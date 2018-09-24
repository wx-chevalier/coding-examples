package com.alibaba.middleware.race.sync.server;

import com.alibaba.middleware.race.sync.Constants;
import com.alibaba.middleware.race.sync.utils.Logger;
import com.alibaba.middleware.race.sync.utils.LongArrayPool;
import com.lmax.disruptor.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.concurrent.*;

/**
 * Construct the workflow and solve the problem
 * <p>
 * Created by yfu on 6/17/17.
 */
public class Solution implements Runnable {

    private final static Logger logger = Logger.SERVER_LOGGER;
    private final static LogExceptionHandler exceptionHandler = new LogExceptionHandler();

    private final String schemeName;
    private final String tableName;
    private final long startKey;
    private final long endKey;

    private RingBuffer<Segment> ringBuffer;

    private Sequence[] parserSequences;
    private Sequence[] workerSequences = new Sequence[Constants.WORKER_NUM];

    private final ByteBuffer[] resultBuffer = new ByteBuffer[2];

    {
        for (int i = 0; i < resultBuffer.length; i++) {
            resultBuffer[i] = ByteBuffer.allocate(4 * 1024 * 1024);
        }
    }

    private volatile ByteChannel outputChannel;
    private final CountDownLatch outputLatch = new CountDownLatch(1);

    public Solution(String schemeName, String tableName, long startKey, long endKey) {
        this.schemeName = schemeName;
        this.tableName = tableName;
        this.startKey = startKey;
        this.endKey = endKey;
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception ex) {
            logger.error("Exception caught", ex);
        }
    }

    private void doRun() throws InterruptedException, IOException {

        DataModel.setKeyRange(startKey, endKey);

        Parser[] parsers = new Parser[Constants.PARSER_NUM];
        for (int i = 0; i < Constants.PARSER_NUM; i++) {
            parsers[i] = new Parser();
        }

        CountDownLatch workersLatch = new CountDownLatch(Constants.WORKER_NUM);

        Worker[] workers = new Worker[Constants.WORKER_NUM];
        for (int i = 0; i < Constants.WORKER_NUM; i++) {
            workers[i] = new Worker(i, workersLatch);
        }

        ExecutorService executor = Executors.newFixedThreadPool(Constants.WORKER_NUM + Constants.PARSER_NUM + 1);

        logger.info("Constructing RingBuffer ...");
        
        ringBuffer = RingBuffer.createSingleProducer(
                Segment.EVENT_FACTORY,
                Constants.SEGMENT_QUEUE_SIZE,
                new YieldingWaitStrategy());

        SequenceBarrier readerBarrier = ringBuffer.newBarrier();
        WorkerPool<Segment> parserPool = new WorkerPool<>(ringBuffer, readerBarrier, exceptionHandler, parsers);
        parserSequences = parserPool.getWorkerSequences();

        WorkProcessor[] workerWorkProcessors = new WorkProcessor[Constants.WORKER_NUM];

        SequenceBarrier parserBarrier = ringBuffer.newBarrier(parserSequences);
        for (int i = 0; i < workers.length; i++) {
            WorkProcessor<Segment> workProcessor = new WorkProcessor<>(ringBuffer, parserBarrier, workers[i], exceptionHandler, new Sequence());
            workerSequences[i] = workProcessor.getSequence();
            workerWorkProcessors[i] = workProcessor;
        }

        ringBuffer.addGatingSequences(workerSequences);

        logger.info("prepare to start reader");
        Reader reader = new Reader(ringBuffer);
        executor.submit(reader);

        parserPool.start(executor);
        
        for (WorkProcessor workProcessor : workerWorkProcessors) {
            executor.submit(workProcessor);
        }
        
        logger.info("Waiting for workers done ...");
        workersLatch.await();
        logger.info("Workers done!");

        for (WorkProcessor processor : workerWorkProcessors) {
            processor.halt();
        }
        parserPool.halt();

        logger.info("Start collecting results...");
        long startTime = System.nanoTime();

        if (outputChannel == null) {
            logger.warn("Output channel has not been prepared yet. Waiting...");
            while (outputChannel == null) Thread.yield();
        }

        int i = 0;
        ByteBuffer writeBuffer = resultBuffer[i];
        ByteBuffer sendBuffer = resultBuffer[1 - i];

        writeBuffer.clear();
        sendBuffer.limit(0);

        for (long key = startKey + 1; key < endKey; key++) {
            
            int bucketNo = DataModel.getKeyBelongsTo(key);
            if (bucketNo == -1) continue;

            if (sendBuffer.hasRemaining()) {
                outputChannel.write(sendBuffer);
            }

            Result result = DataModel.getResult(key);
            while (result == null) {
                Thread.yield();
                result = DataModel.getResult(key);
            }
            DataModel.setResult(key, null);
            writeBuffer.put(result.buffer, result.offset, result.length);

            if (writeBuffer.remaining() < 1024 * 1024) { // Flip buffer
                if (sendBuffer.remaining() != 0) {
                    logger.warn("Send buffer was not fully consumed. Waiting...");
                    while (sendBuffer.remaining() != 0) outputChannel.write(sendBuffer);
                }

                sendBuffer = (ByteBuffer) writeBuffer.flip();
                writeBuffer = resultBuffer[i = 1 - i];
                writeBuffer.clear();
            }
        }

        if (writeBuffer.position() != 0) {
            sendBuffer = (ByteBuffer) writeBuffer.flip();
            while (sendBuffer.hasRemaining()) outputChannel.write(sendBuffer);
        }

        outputLatch.countDown();

        logger.info("Outputting done! Time cost = %d ms", (System.nanoTime() - startTime) / 1000000);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        logger.info("Executors exited");

        for (Worker worker : workers) {
            worker.printPerfLog();
        }
        LongArrayPool.printPerfLog();
    }

    public void outputResult(ByteChannel outputChannel) throws InterruptedException {
        this.outputChannel = outputChannel;
        outputLatch.await();
    }

    private final Monitor monitor = new Monitor();
    ;

    public void startMonitorDaemon() {
        Thread thread = new Thread(monitor);
        thread.setDaemon(true);
        thread.start();
    }

    public void stopMonitorDaemon() {
        monitor.running = false;
    }

    public class Monitor implements Runnable {

        private boolean running = true;

        @Override
        public void run() {
            try {
                while (running) {
                    try {
                        printMetrics();
                    } catch (Exception ex) {
                    }
                    Thread.sleep(250);
                }
            } catch (Throwable ex) {
                logger.error("Exception caught", ex);
            }
        }

        private void printMetrics() throws InterruptedException {
            long seqReader = ringBuffer.getCursor();
            long[] seqParser = new long[Constants.PARSER_NUM];
            for (int i = 0; i < seqParser.length; i++) {
                seqParser[i] = parserSequences[i].get();
            }
            long[] seqWorker = new long[Constants.WORKER_NUM];
            for (int i = 0; i < seqWorker.length; i++) {
                seqWorker[i] = workerSequences[i].get();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Reader: ").append(seqReader);
            sb.append("  Parsers:");
            for (long s : seqParser) sb.append(' ').append(s);
            sb.append("  Workers:");
            for (long s : seqWorker) sb.append(' ').append(s);

            logger.info(sb.toString());
        }
    }
}
