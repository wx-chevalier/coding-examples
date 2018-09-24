package com.alibaba.middleware.race.sync.server;

import com.alibaba.middleware.race.sync.Constants;
import com.alibaba.middleware.race.sync.utils.*;
import com.lmax.disruptor.WorkHandler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

import static com.alibaba.middleware.race.sync.Constants.ASSERT;
import static com.alibaba.middleware.race.sync.Constants.PERF_LOG;
import static com.alibaba.middleware.race.sync.server.DataModel.*;
import static com.alibaba.middleware.race.sync.utils.ByteHelper.printLong;
import static com.alibaba.middleware.race.sync.utils.LongArrayPool.mem;
import static com.alibaba.middleware.race.sync.utils.PromisePool.getData;
import static com.alibaba.middleware.race.sync.utils.PromisePool.senderOf;
import static com.alibaba.middleware.race.sync.utils.PromisePool.setData;
import static com.alibaba.middleware.race.sync.utils.ReadUtils.skipNext;

/**
 * Key bucket holding a subset of keys
 * <p>
 * Created by yfu on 6/17/17.
 */
public class Worker implements WorkHandler<Segment> {
    private static final Logger logger = Logger.SERVER_LOGGER;

    private final int id;

    private final LongIntHashMap table = LongIntHashMap.withExpectedSize(Constants.BUCKET_TABLE_INITIAL_SIZE);

    private final StringStore stringStore = new StringStore();
    private final byte readBuffer[] = new byte[Constants.READ_BUFFER_SIZE];

    private final CountDownLatch shutdownLatch;

    // Blocked UpdatePKDst tasks
    private final ArrayDeque[] blockedQueues = new ArrayDeque[Constants.WORKER_NUM];
    private final LongObjectHashMap<LinkedList<Task>> blockedTable = LongObjectHashMap.withExpectedSize(Constants.BUCKET_BLOCKED_TABLE_INITIAL_SIZE);

    // For performance log, not necessary
    private int maxBlockedKeysNum = 0; 
    private int processedCount = 0;
    private long prepareResultTime;
    private int maxTableSize = 0;

    {
        for (int i = 0; i < blockedQueues.length; i++) {
            blockedQueues[i] = new ArrayDeque<>();
        }
    }

    private LongArrayPool longArrayPool = null;

    public Worker(int id, CountDownLatch shutdownLatch) {
        this.id = id;
        this.shutdownLatch = shutdownLatch;
    }

    @Override
    public void onEvent(Segment segment) throws Exception {
        if (longArrayPool == null) {
            longArrayPool = new LongArrayPool();
        }
        if (segment.size == -1) {
            while (blockedTable.size() != 0) {
                for (int i = 0; i < Constants.WORKER_NUM; i++) {
                    // Manually inline: cleanBlockedTasks(i);
                    @SuppressWarnings("unchecked") ArrayDeque<Task> blockedQueue = blockedQueues[i];
                    while (!blockedQueue.isEmpty() && getData(blockedQueue.peek().promise) != -1) {
                        Task firstBlockedTask = blockedQueue.poll();
                        long key = firstBlockedTask.key;
                        LinkedList<Task> tasksList = blockedTable.remove(key);

                        do {
                            Task task = tasksList.getFirst();
                            if (task.opcode == Task.TASK_UPDATE_PK_DST && getData(task.promise) == -1) {
                                // Oh shit! Blocked again! Put back the remaining blocked tasks
                                blockedTable.put(key, tasksList);
                                @SuppressWarnings("unchecked") ArrayDeque<Task> bq = blockedQueues[senderOf(task.promise)];
                                bq.add(task);
                                return;
                            }
                            applyUnblockedTask(task);
                            tasksList.removeFirst();
                        } while (!tasksList.isEmpty());
                    }
                    // Manually inline ends
                }
            }
            shutdownLatch.countDown();
            prepareResult();
            return;
        }

        final int n = segment.count[id];
        final byte[] opcodes = segment.opcodes[id];
        final long[] keys = segment.keys[id];
        final int[] offsets = segment.offsets[id];
        final int[] promises = segment.promises[id];
        final byte[] buffer = segment.data;

        for (int i = 0; i < n; i++) {
            if (PERF_LOG) processedCount++;

            LinkedList<Task> tasks = blockedTable.get(keys[i]);
            if (tasks != null) {
                ///////////////////////////////////////////////////////////////////////////////////////////////////
                // This key is blocked. Add current task to blocked queue.
                ///////////////////////////////////////////////////////////////////////////////////////////////////
                switch (opcodes[i]) {
                case Task.TASK_INSERT:
                    tasks.add(handleBlockedInsert(buffer, offsets[i], keys[i]));
                    break;
                case Task.TASK_UPDATE:
                    Task task = tasks.getLast();
                    if (ASSERT) assert task.data != -1;
                    parseUpdate(buffer, offsets[i], task.data);
                    break;
                case Task.TASK_UPDATE_PK_SRC:
                    tasks.add(handleBlockedUpdatePKSrc(keys[i], promises[i]));
//                    promises[i] = null;
                    break;
                case Task.TASK_UPDATE_PK_DST:
                    tasks.add(handleBlockedUpdatePKDst(buffer, offsets[i], keys[i], promises[i]));
//                    promises[i] = null;
                    break;
                case Task.TASK_DELETE:
                    tasks.add(handleBlockedDelete(keys[i]));
                    break;
                default:
                    throw new RuntimeException("Invalid opcode");
                }
                
            } else if (opcodes[i] == Task.TASK_UPDATE_PK_DST && getData(promises[i]) == -1) {
                ///////////////////////////////////////////////////////////////////////////////////////////////////
                // This task requires its pair to pass the promised data, but this is not done yet, so block it
                ///////////////////////////////////////////////////////////////////////////////////////////////////
                tasks = new LinkedList<>();
                
                LinkedList<Task> replaced = blockedTable.put(keys[i], tasks);
                if (ASSERT) assert replaced == null;
                
                Task task = handleBlockedUpdatePKDst(buffer, offsets[i], keys[i], promises[i]);
                tasks.add(task);
                
                @SuppressWarnings("unchecked") ArrayDeque<Task> blockedQueue = blockedQueues[senderOf(promises[i])];
                blockedQueue.add(task);

//                promises[i] = null;

                if (PERF_LOG) {
                    maxBlockedKeysNum = blockedTable.size() > maxBlockedKeysNum ? blockedTable.size() : maxBlockedKeysNum;
                }
                
            } else {
                ///////////////////////////////////////////////////////////////////////////////////////////////////
                // This task could be handled immediately
                ///////////////////////////////////////////////////////////////////////////////////////////////////
                switch (opcodes[i]) {
                case Task.TASK_INSERT:
                    handleInsert(buffer, offsets[i], keys[i]);
                    break;
                case Task.TASK_UPDATE:
                    int data = table.get(keys[i]);
                    if (ASSERT) assert data != -1;

                    parseUpdate(buffer, offsets[i], data);
                    break;
                case Task.TASK_UPDATE_PK_SRC:
                    handleUpdatePKSrc(keys[i], promises[i]);
//                    promises[i] = null;
                    break;
                case Task.TASK_UPDATE_PK_DST:
                    handleUpdatePKDst(buffer, offsets[i], keys[i], promises[i]);
//                    promises[i] = null;
                    break;
                case Task.TASK_DELETE:
                    handleDelete(keys[i]);
                    break;
                default:
                    throw new RuntimeException("Invalid opcode");
                }
            }

            // Manually inline: cleanBlockedTasks(i % Constants.WORKER_NUM);
            @SuppressWarnings("unchecked") ArrayDeque<Task> blockedQueue = blockedQueues[i % Constants.WORKER_NUM];
            while (!blockedQueue.isEmpty() && getData(blockedQueue.peek().promise) != -1) {
                Task firstBlockedTask = blockedQueue.poll();
                long key = firstBlockedTask.key;
                LinkedList<Task> tasksList = blockedTable.remove(key);

                do {
                    Task task = tasksList.getFirst();
                    if (task.opcode == Task.TASK_UPDATE_PK_DST && getData(task.promise) == -1) {
                        // Oh shit! Blocked again! Put back the remaining blocked tasks
                        blockedTable.put(key, tasksList);
                        @SuppressWarnings("unchecked") ArrayDeque<Task> bq = blockedQueues[senderOf(task.promise)];
                        bq.add(task);
                        return;
                    }
                    applyUnblockedTask(task);
                    tasksList.removeFirst();
                } while (!tasksList.isEmpty());
            }
            // Manually inline ends
        }
    }

//    private void cleanBlockedTasks(int sender) throws Exception {
//        @SuppressWarnings("unchecked") ArrayDeque<Task> blockedQueue = blockedQueues[sender];
//
//        while (!blockedQueue.isEmpty() && blockedQueue.peek().promise.data != -1) {
//            Task firstBlockedTask = blockedQueue.poll();
//            long key = firstBlockedTask.key;
//            LinkedList<Task> tasks = blockedTable.remove(key);
//
//            while (!tasks.isEmpty()) {
//                Task task = tasks.getFirst();
//                if (task.opcode == Task.TASK_UPDATE_PK_DST && task.promise.data == -1) {
//                    // Oh shit! Blocked again! Put back the remaining blocked tasks
//                    blockedTable.put(key, tasks);
//                    blockedQueue.add(task);
//                    return;
//                }
//                applyUnblockedTask(task);
//                tasks.removeFirst();
//            }
//        }
//    }

    private void applyUnblockedTask(Task task) {
        switch (task.opcode) {
        case Task.TASK_INSERT:
            applyUnblockedInsert(task.key, task.data);
            break;
        case Task.TASK_UPDATE:
            applyUnblockedUpdate(task.key, task.data);
            break;
        case Task.TASK_UPDATE_PK_SRC:
            applyUnblockedUpdatePKSrc(task.key, task.promise);
            break;
        case Task.TASK_UPDATE_PK_DST:
            applyUnblockedUpdatePKDst(task.key, task.data, task.promise);
            break;
        case Task.TASK_DELETE:
            applyUnblockedDelete(task.key);
            break;
        default:
            throw new RuntimeException("Invalid opcode");
        }
    }

    /////////////////////////   Insert   /////////////////////////

    private void handleInsert(byte[] buffer, int pos, long key) {

        int data = longArrayPool.allocate();

        parseInsert(buffer, pos, data);

        int replaced = table.put(key, data);
        if (ASSERT) assert replaced == -1;

        if (PERF_LOG) maxTableSize = table.size() > maxTableSize ? table.size() : maxTableSize;

        if (inRange(key)) {
            DataModel.setKeyBelongsTo(key, id);
        }
    }

    private Task handleBlockedInsert(byte[] buffer, int pos, long key) {

        int data = longArrayPool.allocate();

        parseInsert(buffer, pos, data);

        return new Task(Task.TASK_INSERT, key, -1, data);
    }

    private void applyUnblockedInsert(long key, int data) {

        int replaced = table.put(key, data);
        if (ASSERT) assert replaced == -1;

        if (PERF_LOG) maxTableSize = table.size() > maxTableSize ? table.size() : maxTableSize;

        if (inRange(key)) {
            DataModel.setKeyBelongsTo(key, id);
        }
    }

    private void parseInsert(byte[] buffer, int pos, int data) {
        // Example:
        // ...|I|id:1:1|NULL|890020|first_name:2:0|NULL|柳|last_name:2:0|NULL|君|score:1:0|NULL|68|
        //                          ^ start                                                   end ^

        for (int col = 0; col < columns.size(); col++) {
            pos += columns.getColumnLength(col) + 5; // Skip column info
            pos += 5; // "NULL|".length()

            if (!columns.isText(col)) {
                byte ch;
                long value = 0;
                while ((ch = buffer[pos++]) != '|') {
                    value = value * 10 + (ch - '0');
                }

                mem[data + col] = value;
            } else {
                int len = 0;
                while ((readBuffer[len] = buffer[pos++]) != '|') len++; // Read new value

                long position = stringStore.put(readBuffer, len);
                mem[data + col] = position;
            }
        }
    }

    /////////////////////////   Update   /////////////////////////

    private void applyUnblockedUpdate(long key, int updateData) {
        int data = table.get(key);
        if (ASSERT) assert data != -1;

        mergeUpdate(data, updateData);
    }

    private ByteArray columnName = ByteArray.newEmpty();

    private void parseUpdate(byte[] buffer, int pos, int data) {
        // Example:
        // ...|U|id:1:1|785586|785586|first_name:2:0|高|彭|
        //                            ^ start         end ^

        while (buffer[pos] != '\n') {
            int len = 0;
            while ((readBuffer[len] = buffer[pos++]) != (byte) ':') len++;
            int col = columns.getIndexByLength(len);
            if (col == -1) { // Fall back to getIndex()
                col = columns.getIndex(columnName.wrap(readBuffer, len));
            }

            pos += 5; // Skip other column info (4 added one byte because value has at least one byte)
            pos = skipNext(buffer, pos, '|'); // Skip original value

            if (!columns.isText(col)) {
                byte ch;
                long value = 0;
                while ((ch = buffer[pos++]) != '|') {
                    value = value * 10 + (ch - '0');
                }

                mem[data + col] = value;
            } else {
                len = 0;
                while ((readBuffer[len] = buffer[pos++]) != (byte) '|') len++; // Read new value

                long position = stringStore.put(readBuffer, len);
                mem[data + col] = position;
            }
        }
    }

    /////////////////////   Update PK Src   /////////////////////

    private void handleUpdatePKSrc(long key, int promise) {
        int data = table.remove(key);
        if (ASSERT) assert data != -1;

        setData(promise, data);

        if (inRange(key)) {
            DataModel.setKeyBelongsTo(key, -1);
//            boolean removed = keysInRange.remove(key);
//            if (ASSERT) assert removed;
        }
    }

    private Task handleBlockedUpdatePKSrc(long key, int promise) {
        return new Task(Task.TASK_UPDATE_PK_SRC, key, promise, -1);
    }

    private void applyUnblockedUpdatePKSrc(long key, int promise) {
        int data = table.remove(key);
        if (ASSERT) assert data != -1;

        setData(promise, data);

        if (inRange(key)) {
            DataModel.setKeyBelongsTo(key, -1);
//            boolean removed = keysInRange.remove(key);
//            if (ASSERT) assert removed;
        }
    }

    /////////////////////   Update PK Dst   /////////////////////

    private void handleUpdatePKDst(byte[] buffer, int pos, long key, int promise) {
        int data = getData(promise);
        if (ASSERT) assert data != -1;

        int replaced = table.put(key, data);
        if (ASSERT) assert replaced == -1;

        parseUpdate(buffer, pos, data);

        if (inRange(key)) {
            DataModel.setKeyBelongsTo(key, id);
        }
    }

    private Task handleBlockedUpdatePKDst(byte[] buffer, int pos, long key, int promise) {
        int data = longArrayPool.allocate();
        Arrays.fill(mem, data, data + columns.size(), -1L);

        parseUpdate(buffer, pos, data);

        if (inRange(key)) {
            DataModel.setKeyBelongsTo(key, id);
        }

        return new Task(Task.TASK_UPDATE_PK_DST, key, promise, data);
    }

    private void applyUnblockedUpdatePKDst(long key, int updateData, int promise) {
        int data = getData(promise);
        if (ASSERT) assert data != -1;

        int replaced = table.put(key, data);
        if (ASSERT) assert replaced == -1;

        mergeUpdate(data, updateData);
    }

    /////////////////////////   Delete   /////////////////////////

    private void handleDelete(long key) {
        int removed = table.remove(key);
        if (ASSERT) assert removed != -1;

        if (inRange(key)) {
            DataModel.setKeyBelongsTo(key, -1);
//            boolean removed_ = keysInRange.remove(key);
//            if (ASSERT) assert removed_;
        }
    }

    private Task handleBlockedDelete(long key) {
        return new Task(Task.TASK_DELETE, key, -1, -1);
    }

    private void applyUnblockedDelete(long key) {
        int removed = table.remove(key);
        if (ASSERT) assert removed != -1;

        if (inRange(key)) {
            DataModel.setKeyBelongsTo(key, -1);
//            boolean removed_ = keysInRange.remove(key);
//            if (ASSERT) assert removed_;
        }
    }

    //////////////////////////////////////////////////////////////

    private void prepareResult() throws InterruptedException {
        long startTime = System.nanoTime();

        for (long key = startKey + 1; key < endKey; key++) {
            if (getKeyBelongsTo(key) != id) continue;
            Result result = buildResult(key);
            if (result != null) {
                setResult(key, result);
            }
        }

        if (PERF_LOG) prepareResultTime = (System.nanoTime() - startTime) / 1000000;
    }

    private final ByteBuffer resultBuffer = ByteBuffer.allocate(Constants.RESULT_BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN);

    private Result buildResult(long key) {
        int data = table.get(key);
        if (data == -1) {
            return null;
        }
        int start = resultBuffer.position();
        printLong(key, resultBuffer);
        for (int col = 0; col < columns.size(); col++) {
            resultBuffer.put((byte) '\t');
            if (!columns.isText(col)) {
                printLong(mem[data + col], resultBuffer);
            } else {
                StringStore.get(mem[data + col], resultBuffer);
            }
        }
        resultBuffer.put((byte) '\n');

        return new Result(key, resultBuffer.array(), start, resultBuffer.position() - start);
    }

    public void printPerfLog() {
        if (PERF_LOG) {
            logger.info("maxTableSize: %d maxBlockedKeysNum: %d processedCount: %d prepareResultTime: %d",
                    maxTableSize, maxBlockedKeysNum, processedCount, prepareResultTime);
            if (maxTableSize > Constants.BUCKET_TABLE_INITIAL_SIZE) {
                logger.warn("table exceeds its initial size");
            }
            if (maxBlockedKeysNum > Constants.BUCKET_BLOCKED_TABLE_INITIAL_SIZE) {
                logger.warn("blockedTable exceeds its initial size");
            }
        }
    }

    private static void mergeUpdate(int data, int update) {
        for (int i = 0; i < columns.size(); i++) {
            if (mem[update + i] != -1L) {
                mem[data + i] = mem[update + i];
            }
        }
    }
}
