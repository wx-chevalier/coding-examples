package com.alibaba.middleware.race.sync.utils;

import com.alibaba.middleware.race.sync.Constants;
import com.alibaba.middleware.race.sync.server.Promise;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static com.alibaba.middleware.race.sync.Constants.ASSERT;

/**
 * Created by yfu on 6/28/17.
 */
public class PromisePool {
    
    /*
    +------------------+--------+---------------+
    |      8 bits      | 4 bits |    20 bits    |
    +------------------+--------+---------------+
    | Promise Array ID | Sender | Promise Index |
    +------------------+--------+---------------+
     */
    
    private static final Logger logger = Logger.SERVER_LOGGER;
    
    private static final AtomicIntegerArray[] volatileArrays = new AtomicIntegerArray[256];
    private static final AtomicInteger arrayCount = new AtomicInteger(0);
    
    private int id = -1;
    private int n = 999999999;

    public static int getData(int promise) {
        int id = promise >>> 24;
        int index = promise & 0xfffff;
        return volatileArrays[id].get(index);
    }

    public static void setData(int promise, int data) {
        int id = promise >>> 24;
        int index = promise & 0xfffff;
        volatileArrays[id].set(index, data);
    }
    
    public int newPromise(int sender) {
        if (n >= Constants.BUCKET_PROMISE_POOL_SIZE) {
            allocate();
        }
        return id << 24 | sender << 20 | (n++);
    }
    
    public static int senderOf(int promise) {
        return promise >>> 20 & 0xf;
    }
    
    private void allocate() {
        n = 0;
        
        id = arrayCount.getAndIncrement();
        if (id >= 256) {
            throw new RuntimeException("Promise pool overflow");
        }
        if (id >= Constants.WORKER_NUM) {
            logger.warn("Promise pool allocate new block");
        }
        
        volatileArrays[id] = new AtomicIntegerArray(Constants.BUCKET_PROMISE_POOL_SIZE);

        try {
            int[] innerArray = (int[]) arrayField.get(volatileArrays[id]);
            Arrays.fill(innerArray, -1);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Failed to initialize PromiseArray");
        }
    }

    private static Field arrayField;
            
    static {
        try {
            arrayField = AtomicIntegerArray.class.getDeclaredField("array");
            arrayField.setAccessible(true);
        } catch (Exception ex) {}
    }
}
