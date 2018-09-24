package com.alibaba.middleware.race.sync.utils;

import java.nio.ByteBuffer;

/**
 * Helper functions related to byte operation
 * <p>
 * Created by yfu on 6/10/17.
 */
public class ByteHelper {

    public static boolean byteArrayEquals(byte[] expected, byte[] buf, int len) {
        if (len != expected.length)
            return false;
        for (int i = 0; i < len; i++)
            if (expected[i] != buf[i]) return false;
        return true;
    }

    public static boolean byteArrayEquals(byte[] buf1, int len1, byte[] buf2, int len2) {
        if (len1 != len2)
            return false;
        for (int i = 0; i < len1; i++)
            if (buf1[i] != buf2[i]) return false;
        return true;
    }

    public static long parseLong(byte[] buf, int len) {
        boolean minus = false;
        int i = 0;
        if (buf[0] == '-') {
            minus = true;
            i = 1;
        }
        long result = 0;
        for (; i < len; i++) {
            result = result * 10 + (buf[i] - '0');
        }
        return minus ? -result : result;
    }

    public static void printLong(long value, ByteBuffer buffer) {
        if (value < 0) {
            buffer.put((byte) '-');
            value = -value;
        } else if (value == 0) {
            buffer.put((byte) '0');
            return;
        }
        byte[] array = buffer.array();
        int offset = buffer.arrayOffset() + buffer.position();
        int n = 0;
        for (; value > 0; n++) {
            buffer.put((byte) ('0' + value % 10));
            value /= 10;
        }
        for (int i = 0; i < (n >> 1); i++) {
            byte t = array[offset + i];
            array[offset + i] = array[offset + n - 1 - i];
            array[offset + n - 1 - i] = t;
        }
    }
}
