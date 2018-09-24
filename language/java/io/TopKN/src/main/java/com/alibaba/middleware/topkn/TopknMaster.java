package com.alibaba.middleware.topkn;

import com.alibaba.middleware.topkn.master.ByteString;
import com.alibaba.middleware.topkn.master.Index;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TopknMaster负责接收题目给定的k,n值，并且将信息发送给TopknWorker1和TopknWorker2 Created by wanshao on 2017/6/29.
 */
public class TopknMaster implements Runnable {

    public TopknMaster(int port) {
        this.port = port;
    }

    // port address of com.alibaba.middleware.topkn.TopknWorker
    private int port;

    // 比赛输入
    private static int k;
    private static int n;

    private static boolean requestingIndex = false;
    private static final CountDownLatch waitingIndexLatch = new CountDownLatch(2);

    private static final SortedMap<ByteString, Integer> result = new TreeMap<>();
    private static int shouldSkip;
    private static final CountDownLatch waitingResult = new CountDownLatch(2);

    private static Logger logger = Logger.MASTER_LOGGER;

    @Override
    public void run() {
        try {
            this.startMasterThread(port);
        } catch (Throwable ex) {
            logger.error("exception caught in master", ex);
        }
    }

    /**
     * 初始化系统属性
     */
    private static void initProperties() {


    }

    public static void main(String[] args) throws Exception {
        logger.info("init some args....");
        initProperties();
        // 获取比赛使用的k,n值
        long kl = Long.valueOf(args[0]);
        if (kl > Integer.MAX_VALUE) {
            logger.error("K exceeds INT_MAX", new Exception("unsupported"));
            return;
        }
        k = (int) kl;
        n = Integer.valueOf(args[1]);

        File indexFile = new File(Index.INDEX_FILE_NAME);
        if (!indexFile.exists()) {
            logger.info("Index file not exist. Will ask workers to build index");
            requestingIndex = true;
        } else {
            logger.info("Loading index data from disk");
            requestingIndex = false;
            
            ByteBuffer readBuffer = ByteBuffer.allocate(Constants.BUCKET_SIZE * 4);
            try (FileInputStream fis = new FileInputStream(indexFile)) {
                int n = fis.read(readBuffer.array(), 0, readBuffer.limit());
                assert n == readBuffer.limit();
                readBuffer.position(n);
                readBuffer.flip();
                readBuffer.asIntBuffer().get(Index.bucketCount);
            }

        }

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new TopknMaster(5527));
        executorService.submit(new TopknMaster(5528));

        logger.info("Port 5527 and 5528 is open for connecting...");

        waitingResult.await();

        logger.info("Result is ready");

        int round = 1;
        File outputFile;
        do {
            outputFile = new File(Constants.RESULT_DIR + (round++) + ".rs");
        } while (outputFile.exists());

        logger.info("Outputting result to file: " + outputFile.getAbsolutePath());
        
        int count = 0;

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {

            OUTPUT_DONE:
            for (Map.Entry<ByteString, Integer> entry : result.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    if (shouldSkip > 0) {
                        shouldSkip--;
                    } else {
                        fos.write(entry.getKey().getData());
                        fos.write('\n');
                        if (++count == n) break OUTPUT_DONE;
                    }
                }
            }

        }
        
        logger.info("Output done. Shutting down...");

        executorService.shutdown();
        executorService.awaitTermination(10000, TimeUnit.MINUTES);

    }

    private void startMasterThread(int port) throws Exception {

        ServerSocketChannel serverSocket;

        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));

        boolean waitingIndexSaver = false;
        
        if (requestingIndex) {

            try (SocketChannel socketChannel = serverSocket.accept()) {
                socketChannel.configureBlocking(true);

                logger.info("Requesting for index data");

                Thread readThread = new Thread(new HandleIndexData(socketChannel));
                readThread.start();
                
                new RequestForIndex(socketChannel).run();
                
                readThread.join();

                logger.info("Index data received");

                waitingIndexLatch.countDown();
                waitingIndexLatch.await();

                synchronized (Index.class) {
                    if (Index.indexSaverThread.getState() == Thread.State.NEW) {
                        new Index.IndexAccumulator().run();
                        Index.indexSaverThread.start();
                        // index saver could be done later
                        waitingIndexSaver = true;
                    }
                }
            }
        }

        int lower = Arrays.binarySearch(Index.bucketCount, k + 1);
        if (lower < 0) lower = -(lower + 1);
        int upper = Arrays.binarySearch(Index.bucketCount, k + n);
        if (upper < 0) upper = -(upper + 1);

        if (lower == 0) {
            shouldSkip = k;
        } else {
            shouldSkip = k - Index.bucketCount[lower - 1];
        }

        // Now we just need to query bucket [lower, upper]

        logger.info("Querying data for worker  port=" + port);

        try (SocketChannel socketChannel = serverSocket.accept()) {
            socketChannel.configureBlocking(true);

            Thread readThread = new Thread(new HandleQueryResult(socketChannel));
            readThread.start();
            
            new RequestForQuery(socketChannel, lower, upper).run();
            
            readThread.join();
        }

        serverSocket.close();

        waitingResult.countDown();

        if (waitingIndexSaver) {
            Index.indexSaverThread.join();
        }
    }

    class HandleIndexData implements Runnable {

        private final int READ_BUFFER_SIZE = 1024;
        private SocketChannel socketChannel;

        public HandleIndexData(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            try {
                handleReceivingIndex();
            } catch (IOException ex) {
                logger.error("exception caught", ex);
            }
        }

        private void handleReceivingIndex() throws IOException {
            logger.info("Reading index data from worker " + socketChannel.getRemoteAddress());
            ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
            while (socketChannel.read(readBuffer) != -1) {
                readBuffer.flip();
                handleIndexResponse(readBuffer);
                readBuffer.clear();
            }
            assert writeIndexPtr == Constants.BUCKET_SIZE;
        }

        private int writeIndexPtr = 0;

        private void handleIndexResponse(ByteBuffer readBuffer) throws IOException {
            IntBuffer intBuffer = readBuffer.asIntBuffer();
            synchronized (Index.bucketCount) {
                while (intBuffer.hasRemaining()) {
                    Index.bucketCount[writeIndexPtr++] += intBuffer.get();
                }
            }
        }
    }

    public class RequestForIndex implements Runnable {

        private static final int WRITE_BUFFER_SIZE = 1024;
        private SocketChannel socketChannel;

        public RequestForIndex(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            try {
                logger.info("Request for index from worker " + socketChannel.getRemoteAddress());
                ByteBuffer sendBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);
                sendBuffer.clear();
                sendBuffer.put((byte) Constants.OP_BUILD_INDEX);
                sendBuffer.flip();
                while (sendBuffer.hasRemaining()) {
                    socketChannel.write(sendBuffer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public class RequestForQuery implements Runnable {

        private static final int WRITE_BUFFER_SIZE = 1024;
        private SocketChannel socketChannel;

        private final int lower;
        private final int upper;

        public RequestForQuery(SocketChannel socketChannel, int lower, int upper) {
            this.socketChannel = socketChannel;
            this.lower = lower;
            this.upper = upper;
        }

        @Override
        public void run() {
            try {
                logger.info("Query range from worker " + socketChannel.getRemoteAddress());
                ByteBuffer sendBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);
                sendBuffer.clear();
                sendBuffer.put((byte) Constants.OP_QUERY_RANGE);
                sendBuffer.putInt(lower);
                sendBuffer.putInt(upper);
                sendBuffer.flip();
                while (sendBuffer.hasRemaining()) {
                    socketChannel.write(sendBuffer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class HandleQueryResult implements Runnable {

        private SocketChannel socketChannel;

        public HandleQueryResult(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            try {
                handleReceivingQueryResult();
            } catch (Throwable ex) {
                logger.error("exception caught", ex);
            }
        }

        private void handleReceivingQueryResult() throws IOException {
            logger.info("Reading query result from worker " + socketChannel.getRemoteAddress());
            
            ByteBuffer readBuffer = ByteBuffer.allocate(Constants.RESULT_BUFFER_SIZE);
            while (socketChannel.read(readBuffer) != -1) ;
            readBuffer.flip();
            while (readBuffer.hasRemaining()) {
                int start = readBuffer.position();
                while (readBuffer.get() != '\n') ;
                int length = readBuffer.position() - start - 1;
                byte[] bytes = Arrays.copyOfRange(readBuffer.array(), start, start + length);
                ByteString byteString = new ByteString(bytes);

                synchronized (result) {
                    if (result.containsKey(byteString)) {
                        result.put(byteString, result.get(byteString) + 1);
                    } else {
                        result.put(byteString, 1);
                    }
                }
            }
            
            logger.info("Reading query result done. from worker " + socketChannel.getRemoteAddress());
        }
    }
}
