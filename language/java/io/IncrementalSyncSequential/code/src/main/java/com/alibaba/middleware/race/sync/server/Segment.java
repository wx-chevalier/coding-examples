package com.alibaba.middleware.race.sync.server;

import com.alibaba.middleware.race.sync.Constants;
import com.alibaba.middleware.race.sync.utils.PromisePool;
import com.lmax.disruptor.EventFactory;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;

/**
 * Created by yfu on 6/20/17.
 */
final class Segment {

    int no; // For debug (not necessary)
    
    ArrayList<MappedByteBuffer> mappedBuffers = new ArrayList<>(3);
    
    int size; // -1 means DONE
    byte[] data = new byte[Constants.SEGMENT_SIZE_WITH_MARGIN];;
    boolean isBegin;

    int[] count;
    byte[][] opcodes;
    int[][] offsets;
    long[][] keys;
    int[][] promises;
    
    private boolean allocated = false;

    void ensureAllocated() {
        if (allocated) return;

        count = new int[Constants.WORKER_NUM];
        opcodes = new byte[Constants.WORKER_NUM][];
        offsets = new int[Constants.WORKER_NUM][];
        keys = new long[Constants.WORKER_NUM][];
        promises = new int[Constants.WORKER_NUM][];
        
        for (int i = 0; i < Constants.WORKER_NUM; i++) {
            opcodes[i] = new byte[Constants.SEGMENT_MAX_EVENTS_NUM_PER_BUCKET];
            offsets[i] = new int[Constants.SEGMENT_MAX_EVENTS_NUM_PER_BUCKET];
            keys[i] = new long[Constants.SEGMENT_MAX_EVENTS_NUM_PER_BUCKET];
            promises[i] = new int[Constants.SEGMENT_MAX_EVENTS_NUM_PER_BUCKET];
        }
        
        allocated = true;
    }

    int addUpdateKeySrc(long key, PromisePool promisePool) {
        int bucket = hash(key);
        int n = count[bucket]++;
        opcodes[bucket][n] = Task.TASK_UPDATE_PK_SRC;
        keys[bucket][n] = key;
        int promise = promisePool.newPromise(bucket);
        promises[bucket][n] = promise;
        return promise;
    }

    void addUpdateKeyDst(int pos, long key, int promise) {
        int bucket = hash(key);
        int n = count[bucket]++;
        opcodes[bucket][n] = Task.TASK_UPDATE_PK_DST;
        offsets[bucket][n] = pos;
        keys[bucket][n] = key;
        promises[bucket][n] = promise;
    }

    void addUpdate(int pos, long key) {
        int bucket = hash(key);
        int n = count[bucket]++;
        opcodes[bucket][n] = Task.TASK_UPDATE;
        offsets[bucket][n] = pos;
        keys[bucket][n] = key;
    }

    void addInsert(int pos, long key) {
        int bucket = hash(key);
        int n = count[bucket]++;
        opcodes[bucket][n] = Task.TASK_INSERT;
        offsets[bucket][n] = pos;
        keys[bucket][n] = key;
    }

    void addDelete(long key) {
        int bucket = hash(key);
        int n = count[bucket]++;
        opcodes[bucket][n] = Task.TASK_DELETE;
        keys[bucket][n] = key;
    }
    
    void clear() {
        for (int i = 0; i < Constants.WORKER_NUM; i++) {
            count[i] = 0;
        }
    }
    
    static byte hash(long key) {
        return (byte) ((key >> 5) % Constants.WORKER_NUM);
    }

    public static final EventFactory<Segment> EVENT_FACTORY = new EventFactory<Segment>() {
        @Override
        public Segment newInstance() {
            return new Segment();
        }
    };
    
}


