package com.alibaba.middleware.topkn.worker;

import com.alibaba.middleware.topkn.Constants;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by yfu on 7/15/17.
 */
public class Bucket {

    public static final AtomicIntegerArray bucketCount = new AtomicIntegerArray(Constants.BUCKET_SIZE);

//    static int encode(int len, int byte1, int byte2, int byte3) {
//        len -= 1; // so that < 128
//        return byte3 + ((byte2 + ((byte1 + (len << 7)) << 7)) << 7);
//    }

    static int encode(int len, int byte1, int byte2, int byte3) {
        len -= 1; // so that < 128
        if (byte1 != 0) byte1 = byte1 >= 'a' ? byte1 - 'a' + 10 : byte1 - '0';
        if (byte2 != 0) byte2 = byte2 >= 'a' ? byte2 - 'a' + 10 : byte2 - '0';
        if (byte3 != 0) byte3 = byte3 >= 'a' ? byte3 - 'a' + 10 : byte3 - '0';
        return byte3 + ((byte2 + ((byte1 + (len * 36)) * 36)) * 36);
    }
    
    static int getLen(int key) {
        return key / (36 * 36 * 36) + 1;
    }

    public static void writeToBuffer(ByteBuffer buffer) {
        int n = bucketCount.length();
        for (int i = 0; i < n; i++) {
            buffer.putInt(bucketCount.get(i));
        }
    }
}
