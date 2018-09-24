package com.alibaba.middleware.race.sync.utils;

/**
 * Created by yfu on 6/17/17.
 */
public class ReadUtils {

    public static int skipNext(byte[] buffer, int pos, char b) {
        while (buffer[pos++] != b) ;
        return pos;
    }

    public static byte peek(byte[] buffer, int pos) {
        return buffer[pos];
    }
}
