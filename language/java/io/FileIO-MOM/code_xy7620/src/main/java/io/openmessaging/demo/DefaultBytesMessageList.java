package io.openmessaging.demo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;

/**
 * @author XF
 * 实现BytesMessage接口，包含三个属性，及相应的设置、获取方法
 * <p>KeyValue header、 KeyValue properties、byte[] body
 */
public class DefaultBytesMessageList implements BytesMessage{

    private static KeyValue headers = new DefaultKeyValue();
    //注意这里没有初始化，如果没有这个属性那么为null，消费者读文件不用新建对象。
    private static KeyValue properties = new DefaultKeyValue();
    private byte[] body;
    
    public List<String> headerList = new ArrayList<String>();
    public List<String> proList;

    /**
     * 唯一的构造方法
     * @param body
     */
    public DefaultBytesMessageList(byte[] body) {
        this.body = body;
    }
    @Override public byte[] getBody() {
        return body;
    }

    @Override public BytesMessage setBody(byte[] body) {
        this.body = body;
        return this;
    }

    @Override public KeyValue headers() {
        return headers;
    }

    @Override public KeyValue properties() {
        return properties;
    }

    @Override public Message putHeaders(String key, int value) {
    	putHeaders(key, String.valueOf(value));
        return this;
    }

    @Override public Message putHeaders(String key, long value) {
    	putHeaders(key, String.valueOf(value));
        return this;
    }

    @Override public Message putHeaders(String key, double value) {
    	putHeaders(key, String.valueOf(value));
        return this;
    }

    @Override public Message putHeaders(String key, String value) {
        headerList.add(key);
        headerList.add(value);
        return this;
    }

    @Override public Message putProperties(String key, int value) {
    	if (proList == null) proList = new ArrayList<String>();
    	putProperties(key, String.valueOf(value));
        return this;
    }

    @Override public Message putProperties(String key, long value) {
    	if (proList == null) proList = new ArrayList<String>();
    	putProperties(key, String.valueOf(value));
        return this;
    }

    @Override public Message putProperties(String key, double value) {
    	if (proList == null) proList = new ArrayList<String>();
    	putProperties(key, String.valueOf(value));
        return this;
    }

    @Override public Message putProperties(String key, String value) {
        if (proList == null) proList = new ArrayList<String>();
        proList.add(key);
        proList.add(value);
        return this;
    }
    public void clean(){
    	headerList = new ArrayList<String>();
    	proList = null;
    }
}
