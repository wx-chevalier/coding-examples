package com.alibaba.middleware.topkn.worker;

import com.alibaba.middleware.topkn.Constants;
import com.alibaba.middleware.topkn.Logger;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by yfu on 7/15/17.
 */
public class QueryExecutor implements Runnable {

    private static final Logger logger = Logger.WORKER_LOGGER;

    private final int lowerBound;
    private final int upperBound;
    private final ByteBuffer buffer;

    private final int lengthLowerBound;
    private final int lengthUpperBound;

    public QueryExecutor(int lowerBound, int upperBound, ByteBuffer buffer) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.buffer = buffer;

        lengthLowerBound = Bucket.getLen(lowerBound);
        lengthUpperBound = Bucket.getLen(upperBound);
    }

    private static File currentFile;
    private static long currentOffset;
    private static int currentFileNo;

    static {
        nextDataFile();
    }

    private static synchronized FileSegment getNextSegment() {
        long fileSize = currentFile.length();
        if (currentOffset >= fileSize) {
            boolean hasNext = nextDataFile();
            if (!hasNext) return null;
        }
        assert currentOffset < fileSize;
        long prevOffset = currentOffset;
        currentOffset += Constants.WORKER_SEGMENT_SIZE;
        return new FileSegment(currentFile, prevOffset, currentOffset);
    }

    private static synchronized boolean nextDataFile() {
        while (++currentFileNo <= 10) {
            String fileName = String.format("split%d.txt", currentFileNo);
            currentFile = new File(Constants.DATA_DIR + fileName);
            if (currentFile.exists()) {
                currentOffset = 0;
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
        
        RandomAccessFile raf = new RandomAccessFile(segment.getFile(), "r");
        raf.seek(segment.getOffset());
        int limit = raf.read(readBuffer, 0, readBuffer.length);
        int pos = 0;
        
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

            if (len < lengthLowerBound || len > lengthUpperBound) {
                continue; // Avoid most calls to Bucket.encode (which is expensive)
            }
            
            int bucketKey = Bucket.encode(len, byte1, byte2, byte3);
            
            if (lowerBound <= bucketKey && bucketKey <= upperBound) {
                synchronized (buffer) {
                    raf.seek(startPosition + segment.getOffset());
                    raf.read(buffer.array(), buffer.position(), len + 1);
                    buffer.position(buffer.position() + len + 1);
                }
            }
        }
        
        raf.close();
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
