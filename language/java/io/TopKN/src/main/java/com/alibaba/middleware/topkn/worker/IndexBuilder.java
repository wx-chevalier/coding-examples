package com.alibaba.middleware.topkn.worker;

import com.alibaba.middleware.topkn.Constants;
import com.alibaba.middleware.topkn.Logger;

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yfu on 7/15/17.
 */
public class IndexBuilder implements Runnable {

    private static final Logger logger = Logger.WORKER_LOGGER;

    private static File currentFile;
    private static final AtomicLong currentOffset = new AtomicLong();
    private static int currentFileNo;

    static {
        nextDataFile();
    }
    
    private static synchronized FileSegment getNextSegment() {
        if (currentOffset.get() >= currentFile.length()) {
            synchronized (currentOffset) {
                if (currentOffset.get() >= currentFile.length()) {
                    boolean hasNext = nextDataFile();
                    if (!hasNext) return null;
                }
            }
        }
        long prevOffset = currentOffset.getAndAdd(Constants.WORKER_SEGMENT_SIZE);
        return new FileSegment(currentFile, prevOffset, prevOffset + Constants.WORKER_SEGMENT_SIZE);
    }
    
    private static synchronized boolean nextDataFile() {
        while (++currentFileNo <= 10) {
            String fileName = String.format("split%d.txt", currentFileNo);
            currentFile = new File(Constants.DATA_DIR + fileName);
            if (currentFile.exists()) {
                currentOffset.set(0);
                logger.info("Reading file: " + fileName);
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        try {
            FileSegment segment;
            while ((segment = getNextSegment()) != null) {
                readSegment(segment);
            }
            logger.info("exited");
        } catch (Throwable ex) {
            logger.error("exception caught during IndexBuilder running", ex);
        }
    }
    
    private byte[] readBuffer;
    
    private void readSegment(FileSegment segment) throws IOException {

        if (readBuffer == null) { // Lazy allocation
            readBuffer = new byte[Constants.WORKER_SEGMENT_SIZE + 300];
        }
        
        int pos = 0;
        int limit;
        try (RandomAccessFile raf = new RandomAccessFile(segment.getFile(), "r")) {
            raf.seek(segment.getOffset());
            limit = raf.read(readBuffer, 0, readBuffer.length);
        }
        
        int t;
        
        if (segment.getOffset() != 0) { // Is not first segment in file
            while (readBuffer[pos++] != '\n') ;
        }
        
        while (pos + segment.getOffset() <= segment.getNextOffset() && pos < limit) {

            int startPosition = pos;
            
            int byte1 = 0;
            int byte2 = 0;
            int byte3 = 0;

            byte1 = readBuffer[pos++];
            assert byte1 != '\n';
            if ((t = readBuffer[pos++]) != '\n') {
                byte2 = t;
                if ((t = readBuffer[pos++]) != '\n') {
                    byte3 = t;
                    while (readBuffer[pos++] != '\n') ;
                }
            }
            
            int len = pos - startPosition - 1; // -1 because '\n'

            Bucket.bucketCount.incrementAndGet(Bucket.encode(len, byte1, byte2, byte3));
        }
        
        
    }
}
