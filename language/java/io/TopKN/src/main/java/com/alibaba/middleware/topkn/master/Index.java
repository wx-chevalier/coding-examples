package com.alibaba.middleware.topkn.master;

import com.alibaba.middleware.topkn.Constants;
import com.alibaba.middleware.topkn.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by yfu on 7/15/17.
 */
public class Index {

    private static Logger logger = Logger.MASTER_LOGGER;

    public static final String INDEX_FILE_NAME = Constants.MIDDLE_DIR + "index.bin";
    
    static {
        File middleDir = new File(Constants.MIDDLE_DIR);
        if (!middleDir.exists()) {
            middleDir.mkdirs();
        }
    }

    public static final int[] bucketCount = new int[Constants.BUCKET_SIZE];

    public static class IndexAccumulator implements Runnable {

        @Override
        public void run() {
            try {
                for (int i = 1; i < bucketCount.length; i++) {
                    bucketCount[i] += bucketCount[i - 1];
                }
                logger.info("Index data accumulated.");
            } catch (Throwable ex) {
                logger.error("exception caught in IndexAccumulator", ex);
            }
        }
    }


    public static final Thread indexSaverThread = new Thread(new IndexSaver());

    public static class IndexSaver implements Runnable {
        @Override
        public void run() {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                File indexFile = new File(INDEX_FILE_NAME);
                try (FileOutputStream fos = new FileOutputStream(indexFile)) {
                    for (int value : bucketCount) {
                        buffer.putInt(value);
                        if (!buffer.hasRemaining()) {
                            buffer.flip();
                            fos.write(buffer.array(), 0, buffer.limit());
                            buffer.clear();
                        }
                    }
                    buffer.flip();
                    fos.write(buffer.array(), 0, buffer.limit());
                    buffer.clear();
                }

                logger.info("Index data written to disk");
            } catch (Throwable ex) {
                logger.error("exception caught in IndexSaver", ex);
            }
        }
    }

}
