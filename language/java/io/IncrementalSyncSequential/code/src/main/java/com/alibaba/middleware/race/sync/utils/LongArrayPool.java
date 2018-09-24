package com.alibaba.middleware.race.sync.utils;

import com.alibaba.middleware.race.sync.Constants;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yfu on 6/25/17.
 */
public class LongArrayPool {

    private static final Logger logger = Logger.SERVER_LOGGER;
    
    private static int arrayLength = - 1;

    public static void setArrayLength(int arrayLength) {
        LongArrayPool.arrayLength = arrayLength;
    }

    public static volatile long[] mem = null;
    
    private static final AtomicInteger allocated = new AtomicInteger();
    
    private int index;
    private int blockEnd;
//    private IntArrayList slots = new IntArrayList(1024);

    public LongArrayPool() {
        if (mem == null) {
            synchronized(allocated) {
                if (mem == null) {
                    mem = new long[Constants.GLOBAL_DATA_BUFFER_LENGTH];
                }
            }
        }
    }

    public int allocate() {
        // Disable slots reusing for performance
//        if (!slots.isEmpty()) {
//            return slots.popInt();
//        }
        if (index + arrayLength > blockEnd) {
            allocateNewBlock();
        }
        int ret = index;
        index += arrayLength;
        return ret;
    }
    
    private void allocateNewBlock() {
        blockEnd = allocated.addAndGet(Constants.GLOBAL_DATA_BUFFER_ALLOCATE_UNIT);
        if (blockEnd > Constants.GLOBAL_DATA_BUFFER_LENGTH) {
            throw new RuntimeException("LongArrayPool out of memory");
        }
        index = blockEnd - Constants.GLOBAL_DATA_BUFFER_ALLOCATE_UNIT;
    }
    
//    public void free(int index) {
//        slots.add(index);
//    }
    
    public static void printPerfLog() {
        logger.info("Allocated: %d/%d bytes", allocated.get(), Constants.GLOBAL_DATA_BUFFER_LENGTH);
    }
}
