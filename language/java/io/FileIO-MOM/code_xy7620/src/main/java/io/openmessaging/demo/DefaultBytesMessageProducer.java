package io.openmessaging.demo;

import java.io.Serializable;
import java.util.HashMap;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.MessageHeader;

/**
 * @author XF
 * 实现BytesMessage接口，包含三个属性，及相应的设置、获取方法
 * <p>KeyValue header、 KeyValue properties、byte[] body
 */
public class DefaultBytesMessageProducer implements BytesMessage{
    private static KeyValue headers = new DefaultKeyValue();
    //注意这里没有初始化，如果没有这个属性那么为null，消费者读文件不用新建对象。
    private static KeyValue properties = new DefaultKeyValue();
	
	static final HashMap<String, String> seq = new HashMap<String, String>(32);
	
	static{
		//使用字符而不是整数，从字符串转换成字节只需要1字节
		seq.put(MessageHeader.MESSAGE_ID, "a");
//		seq.put(MessageHeader.TOPIC, "b");
//		seq.put(MessageHeader.QUEUE, "c");
		seq.put(MessageHeader.BORN_TIMESTAMP, "d");
		seq.put(MessageHeader.BORN_HOST, "e");
		seq.put(MessageHeader.STORE_TIMESTAMP, "f");
		seq.put(MessageHeader.STORE_HOST, "g");
		seq.put(MessageHeader.START_TIME, "h");
		seq.put(MessageHeader.STOP_TIME, "i");
		seq.put(MessageHeader.TIMEOUT, "j");
		seq.put(MessageHeader.PRIORITY, "k");  
		seq.put(MessageHeader.RELIABILITY, "l");
		seq.put(MessageHeader.SEARCH_KEY, "m");
		seq.put(MessageHeader.SCHEDULE_EXPRESSION, "n");
		seq.put(MessageHeader.SHARDING_KEY, "o");
		seq.put(MessageHeader.SHARDING_PARTITION, "p");
		seq.put(MessageHeader.TRACE_ID, "q");
	}
	
	static final String sem = ";";
	
    private byte[] body;
    
    //存储header在StringBuilder中，不在放在kv中。首先占用了一个字节是为了存放headerSize。
    public StringBuilder headersBiulder = new StringBuilder(sem);
    public int headerSize  = 0;
    
    public StringBuilder prosBiulder ;
    public int proSize  = 0;  

    /**
     * 唯一的构造方法
     * @param body
     */
    public DefaultBytesMessageProducer(byte[] body) {
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
        headersBiulder.append(seq.get(key));
        headersBiulder.append(value);
        headersBiulder.append(sem);
        headerSize++;
        return this;
    }

    @Override public Message putHeaders(String key, long value) {
        headersBiulder.append(seq.get(key));
        headersBiulder.append(value);
        headersBiulder.append(sem);
        headerSize++;
        return this;
    }

    @Override public Message putHeaders(String key, double value) {
        headersBiulder.append(seq.get(key));
        headersBiulder.append(value);
        headersBiulder.append(sem);
        headerSize++;
        return this;
    }

    @Override public Message putHeaders(String key, String value) {
//    	System.out.println(Thread.currentThread().getName()+" putHeaders key:"+key+" value:"+value+ "  keySet size:"+headers.keySet().size());
        headersBiulder.append(seq.get(key));
        headersBiulder.append(value);
        headersBiulder.append(sem);
        headerSize++;
        return this;
    }

    @Override public Message putProperties(String key, int value) {
        if (prosBiulder == null) prosBiulder = new StringBuilder(sem);
//        if (properties == null) properties = new DefaultKeyValue();
        prosBiulder.append(key);
        prosBiulder.append(sem);
        prosBiulder.append(value);
        prosBiulder.append(sem);
        proSize++;
        return this;
    }

    @Override public Message putProperties(String key, long value) {
        if (prosBiulder == null) prosBiulder = new StringBuilder(sem);
//        if (properties == null) properties = new DefaultKeyValue();
        prosBiulder.append(key);
        prosBiulder.append(sem);
        prosBiulder.append(value);
        prosBiulder.append(sem);
        proSize++;
        return this;
    }

    @Override public Message putProperties(String key, double value) {
        if (prosBiulder == null) prosBiulder = new StringBuilder(sem);
//        if (properties == null) properties = new DefaultKeyValue();
        prosBiulder.append(key);
        prosBiulder.append(sem);
        prosBiulder.append(value);
        prosBiulder.append(sem);
        proSize++;
        return this;
    }

    @Override public Message putProperties(String key, String value) {
//    	if (properties == null) properties = new DefaultKeyValue();
//    	System.out.println(Thread.currentThread().getName()+" putProperties key:"+key+" value:"+value+ "  keySet size:"+properties.keySet().size());
        if (prosBiulder == null) prosBiulder = new StringBuilder(sem);
        prosBiulder.append(key);
        prosBiulder.append(sem);
        prosBiulder.append(value);
        prosBiulder.append(sem);
        proSize++;
        return this;
    }
    public void clean(){
    	headersBiulder.delete(1, headersBiulder.length());
    	headerSize = 0;
    	prosBiulder = null;
    	proSize = 0;
    }
}
