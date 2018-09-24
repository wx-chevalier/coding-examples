package com.alibaba.middleware.race.sync.utils;

import com.koloboke.collect.map.LongIntMap;
import com.koloboke.compile.KolobokeMap;

/**
 * Created by yfu on 6/23/17.
 */
public abstract class LongIntHashMap implements LongIntMap {
    public static LongIntHashMap withExpectedSize(int expectedSize) {
        return new KolobokeLongIntHashMap(expectedSize);
    }

    public abstract int get(long key);

    public abstract int put(long key, int value);

    public abstract int remove(long key);

    @Override
    public final int defaultValue() {
        return -1;
    }
}
