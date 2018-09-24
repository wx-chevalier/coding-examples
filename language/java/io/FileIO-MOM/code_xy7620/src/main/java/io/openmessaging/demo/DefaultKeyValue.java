package io.openmessaging.demo;

import io.openmessaging.KeyValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 用HashMap<String, Object>实现KeyValue接口，每个对象包含一个map
 */
public class DefaultKeyValue implements KeyValue {

    private final Map<String, Object> kvs = new HashMap<>();
    @Override
    public KeyValue put(String key, int value) {
        kvs.put(key, value);
        return this;
    }

    @Override
    public KeyValue put(String key, long value) {
        kvs.put(key, value);
        return this;
    }

    @Override
    public KeyValue put(String key, double value) {
        kvs.put(key, value);
        return this;
    }

    @Override
    public KeyValue put(String key, String value) {
        kvs.put(key, value);
        return this;
    }

    @Override
    public int getInt(String key) {
        return (Integer)kvs.getOrDefault(key, 0);
    }

    @Override
    public long getLong(String key) {
        return (Long)kvs.getOrDefault(key, 0L);
    }

    @Override
    public double getDouble(String key) {
        return (Double)kvs.getOrDefault(key, 0.0d);
    }

    @Override
    public String getString(String key) {
        return (String)kvs.getOrDefault(key, null);
    }

    @Override
    public Set<String> keySet() {
        return kvs.keySet();
    }

    @Override
    public boolean containsKey(String key) {
        return kvs.containsKey(key);
    }

	public int size() {
		// TODO Auto-generated method stub
		return kvs.size();
	}

	public Set<Entry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		return kvs.entrySet();
	}


}
