package com.alibaba.middleware.race.sync.server;

import java.util.ArrayList;
import java.util.Queue;

/**
 * One result in queried range
 * <p>
 * Created by yfu on 6/17/17.
 */
final class Result implements Comparable<Result> {

    static final Result DONE = new Result(-1, null, -1, -1);

    private final long key;

    final byte[] buffer;
    final int offset;
    final int length;

    public Result(long key, byte[] buffer, int offset, int length) {
        this.key = key;
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public int compareTo(Result o) {
        return Long.signum(key - o.key);
    }
}
