package io.openmessaging.demo;

import java.io.Serializable;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;

/**
 * @author XF
 * 实现BytesMessage接口，包含三个属性，及相应的设置、获取方法
 * <p>KeyValue header、 KeyValue properties、byte[] body
 */
public class DefaultBytesMessage implements BytesMessage{

    private KeyValue headers = new DefaultKeyValue();
    //注意这里没有初始化，如果没有这个属性那么为null，消费者读文件不用新建对象。
    private KeyValue properties;
    private byte[] body;

    /**
     * 唯一的构造方法
     * @param body
     */
    public DefaultBytesMessage(byte[] body) {
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
        headers.put(key, value);
        return this;
    }

    @Override public Message putHeaders(String key, long value) {
        headers.put(key, value);
        return this;
    }

    @Override public Message putHeaders(String key, double value) {
        headers.put(key, value);
        return this;
    }

    @Override public Message putHeaders(String key, String value) {
        headers.put(key, value);
        return this;
    }

    @Override public Message putProperties(String key, int value) {
        if (properties == null) properties = new DefaultKeyValue();
        properties.put(key, value);
        return this;
    }

    @Override public Message putProperties(String key, long value) {
        if (properties == null) properties = new DefaultKeyValue();
        properties.put(key, value);
        return this;
    }

    @Override public Message putProperties(String key, double value) {
        if (properties == null) properties = new DefaultKeyValue();
        properties.put(key, value);
        return this;
    }

    @Override public Message putProperties(String key, String value) {
        if (properties == null) properties = new DefaultKeyValue();
        properties.put(key, value);
        return this;
    }
    public void clean(){
    	this.headers = new DefaultKeyValue();
    	this.properties = null;
    }
}
