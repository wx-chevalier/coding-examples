package com.alibaba.middleware.topkn;

import com.alibaba.middleware.topkn.worker.Bucket;
import com.alibaba.middleware.topkn.worker.IndexBuilder;
import com.alibaba.middleware.topkn.worker.QueryExecutor;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Worker1会接收master提供的题目信息，并且得到计算结果后返回给master Created by wanshao on 2017/6/29.
 */
public class TopknWorker {

    private static int masterPort;
    private static String masterHostAddress;
    private static final Logger logger = Logger.WORKER_LOGGER;

    private long k;
    private int n;

    private boolean buildingIndex;

    private int lowerBound;
    private int upperBound;

    public static void main(String[] args) throws Exception {

        masterHostAddress = args[0];
        //需要通过args参数传递，master会开启5527和5528两个端口提供连接
        masterPort = Integer.valueOf(args[1]);

        logger.info("begin to connect " + masterHostAddress + ":" + masterPort);

        //支持重连
        while (true) {
            try {
                new TopknWorker().connect(masterHostAddress, masterPort);
                return;
            } catch (ConnectException e) {
                Thread.sleep(15); // Retry
            }

        }

    }

    public void connect(String host, int port) throws ConnectException {
        SocketChannel socketChannel = null;
        try {

            do {
                socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
                socketChannel.configureBlocking(true);
                logger.info("Connected to server: " + socketChannel);

                buildingIndex = false;

                new ListenOperation(socketChannel).run();

                if (buildingIndex) {
                    new ResponseBuildIndex(socketChannel).run();
                } else {
                    new ResponseQueryRange(socketChannel).run();
                }

                socketChannel.close();

            } while (buildingIndex); // Until buildingIndex == false


        } catch (ConnectException e) {
            throw e; // Will retry
        } catch (Throwable ex) {
            logger.error("exception caught", ex);
        }
    }

    class ResponseBuildIndex implements Runnable {

        private static final int WRITE_BUFFER_SIZE = Constants.BUCKET_SIZE * 4;

        private SocketChannel socketChannel;

        public ResponseBuildIndex(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            try {
                buildIndexAndSend();
            } catch (Throwable ex) {
                logger.error("exception caught", ex);
            }
        }

        private void buildIndexAndSend() throws InterruptedException {

            logger.info("begin to build index (worker-side)");

            ExecutorService executorService = Executors.newFixedThreadPool(Constants.NUM_CORE);

            for (int i = 0; i < Constants.NUM_CORE; i++) {
                executorService.submit(new IndexBuilder());
            }

            logger.info("Building index ...");

            executorService.shutdown();
            executorService.awaitTermination(100000, TimeUnit.MINUTES);

            logger.info("Index built");

            try {
                logger.info("Begin to send index data to master: " + socketChannel.getRemoteAddress());
                ByteBuffer sendBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);
                sendBuffer.clear();
                Bucket.writeToBuffer(sendBuffer);
                sendBuffer.flip();
                while (sendBuffer.hasRemaining()) {
                    socketChannel.write(sendBuffer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ResponseQueryRange implements Runnable {

        private static final int WRITE_BUFFER_SIZE = Constants.BUCKET_SIZE * 4;

        private SocketChannel socketChannel;

        public ResponseQueryRange(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            try {
                queryRangeAndSend();
            } catch (Throwable ex) {
                logger.error("exception caught", ex);
            }
        }

        private void queryRangeAndSend() throws InterruptedException {

            logger.info("begin to query range [lower, upper]");

            ExecutorService executorService = Executors.newFixedThreadPool(Constants.NUM_CORE);

            ByteBuffer outBuffer = ByteBuffer.allocate(Constants.RESULT_BUFFER_SIZE);

            for (int i = 0; i < Constants.NUM_CORE; i++) {
                executorService.execute(new QueryExecutor(lowerBound, upperBound, outBuffer));
            }

            logger.info("Waiting for query threads ...");

            executorService.shutdown();
            executorService.awaitTermination(100000, TimeUnit.MINUTES);

            logger.info("Scanning file done");


            try {
                logger.info("Begin to send query result to master: " + socketChannel.getRemoteAddress());
                outBuffer.flip();
                while (outBuffer.hasRemaining()) {
                    socketChannel.write(outBuffer);
                }
                logger.info("Send query result done");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ListenOperation implements Runnable {

        private static final int READ_BUFFER_SIZE = 1024 * 1024;
        private SocketChannel socketChannel;

        public ListenOperation(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {

            try {
                logger.info("Waiting for operation command from master " + socketChannel.getRemoteAddress());
                ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
                readBuffer.clear();
                int readBytes = socketChannel.read(readBuffer);
                if (readBuffer.remaining() >= 0) {
                    // do something with the result
                    readBuffer.flip();
                    int operation = readBuffer.get();
                    if (operation == Constants.OP_BUILD_INDEX) {
                        buildingIndex = true;
                    } else if (operation == Constants.OP_QUERY_RANGE) {
                        buildingIndex = false;
                        lowerBound = readBuffer.getInt();
                        upperBound = readBuffer.getInt();
                    } else {
                        throw new RuntimeException("unknown operation");
                    }
                    readBuffer.clear();
                }
                logger.info("Reading intput data from master is finished...");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
