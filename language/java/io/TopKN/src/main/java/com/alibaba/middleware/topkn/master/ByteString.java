package com.alibaba.middleware.topkn.master;

import java.util.Arrays;

/**
 * Comparable wrapper for byte[]
 * 
 * Created by yfu on 7/16/17.
 */
public class ByteString implements Comparable<ByteString> {

    private final byte[] data;

    public ByteString(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public int compareTo(ByteString o) {
        if (data.length != o.data.length) {
            return data.length - o.data.length;
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] < o.data[i]) return -1;
            if (data[i] > o.data[i]) return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteString that = (ByteString) o;

        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return new String(data);
    }
}
