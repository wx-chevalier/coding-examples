package com.alibaba.middleware.race.sync.utils;

import java.util.Arrays;

/**
 * ByteArray is a simple wrapper for byte[], adding equals and hashCode so that it can be used as map key
 * <p>
 * Created by yfu on 6/9/17.
 */
public final class ByteArray {
    private byte[] data;
    private int len;
    private int hashCode;

    private ByteArray(byte[] data, int len) {
        this.data = data;
        this.len = len;
    }

    public int length() {
        return len;
    }

    public static ByteArray copyOf(byte[] buf, int len) {
        byte[] data = Arrays.copyOf(buf, len);
        return new ByteArray(data, len);
    }

    public static ByteArray copyOf(byte[] buf, int off, int len) {
        byte[] data = Arrays.copyOfRange(buf, off, off + len);
        return new ByteArray(data, len);
    }

    public static ByteArray newEmpty() {
        return new ByteArray(null, 0);
    }
    
    public ByteArray wrap(byte[] data, int len) {
        this.data = data;
        this.len = len;
        this.hashCode = 0;
        return this;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ByteArray)) {
            return false;
        }
        ByteArray o = (ByteArray) other;
        return ByteHelper.byteArrayEquals(data, len, o.data, o.len);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = 1;
            for (int i = 0; i < len; i++) {
                result = 31 * result + data[i];
            }
            hashCode = result;
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return new String(data, 0, len);
    }
}
