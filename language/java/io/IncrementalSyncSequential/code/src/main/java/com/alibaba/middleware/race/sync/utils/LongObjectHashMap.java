package com.alibaba.middleware.race.sync.utils;

import com.koloboke.compile.KolobokeMap;

import java.util.Map;

/**
 * Created by yfu on 6/23/17.
 */
public abstract class LongObjectHashMap<V> implements Map<Long, V> {
    public static <V> LongObjectHashMap<V> withExpectedSize(int expectedSize) {
        return new KolobokeLongObjectHashMap<>(expectedSize);
    }

    public abstract V get(long key);

    public abstract V put(long key, V value);

    public abstract V remove(long key);
}
