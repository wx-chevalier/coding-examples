package io.openmessaging.demo;

import java.io.Serializable;
import java.util.Arrays;
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
public class DefaultBytesMessageProducer2 implements BytesMessage{
    private  static KeyValue headers = new DefaultKeyValue();
    //注意这里没有初始化，如果没有这个属性那么为null，消费者读文件不用新建对象。
    private  static KeyValue properties = new DefaultKeyValue();
	
	static final HashMap<String, Byte> seq = new HashMap<String, Byte>(32);
	
	static{
		//使用字符而不是整数，从字符串转换成字节只需要1字节
		seq.put(MessageHeader.MESSAGE_ID, (byte) 97);
//		seq.put(MessageHeader.TOPIC, "b");
//		seq.put(MessageHeader.QUEUE, "c");
		seq.put(MessageHeader.BORN_TIMESTAMP, (byte) 100);
		seq.put(MessageHeader.BORN_HOST, (byte) 101);
		seq.put(MessageHeader.STORE_TIMESTAMP, (byte) 102);
		seq.put(MessageHeader.STORE_HOST, (byte) 103);
		seq.put(MessageHeader.START_TIME, (byte) 104);
		seq.put(MessageHeader.STOP_TIME, (byte) 105);
		seq.put(MessageHeader.TIMEOUT, (byte) 106);
		seq.put(MessageHeader.PRIORITY, (byte) 107);  
		seq.put(MessageHeader.RELIABILITY, (byte) 108);
		seq.put(MessageHeader.SEARCH_KEY, (byte) 109);
		seq.put(MessageHeader.SCHEDULE_EXPRESSION, (byte) 110);
		seq.put(MessageHeader.SHARDING_KEY, (byte) 111);
		seq.put(MessageHeader.SHARDING_PARTITION, (byte) 112);
		seq.put(MessageHeader.TRACE_ID, (byte) 113);
	}
	
//	static final String sem = ";";
	
    private byte[] body;
    
    public byte[] headerBytes = new byte[100];
    //第一个字节是属性个数
    public int headerIndex = 1;
    int headerBytesSize = 100;
    
    
    public byte[] proBytes ;
  //第一个字节是属性个数
    public int proIndex = 1;
    int proBytesSize = 100;
    
    /**
     * 唯一的构造方法
     * @param body
     */
    public DefaultBytesMessageProducer2(byte[] body) {
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
//        headersBiulder.append(seq.get(key));
//        headersBiulder.append(value);
//        headersBiulder.append(sem);
//        headerSize++;
        return this;
    }

    @Override public Message putHeaders(String key, long value) {
    	putHeaders(key, String.valueOf(value));
//        headersBiulder.append(seq.get(key));
//        headersBiulder.append(value);
//        headersBiulder.append(sem);
//        headerSize++;
        return this;
    }

    @Override public Message putHeaders(String key, double value) {
    	putHeaders(key, String.valueOf(value));
//        headersBiulder.append(seq.get(key));
//        headersBiulder.append(value);
//        headersBiulder.append(sem);
//        headerSize++;
        return this;
    }
    
    static int isFirst = 150;
    
    @Override public Message putHeaders(String key, String value) {
//    	if(isFirst-- > 0){
//    		System.out.println(Thread.currentThread().getName()+" putHeaders key:"+key+" value:"+value);
//    	}

    	byte[] valueBytes = value.getBytes();
        int valueLen = valueBytes.length;
        //header一个字节，结尾分号一个字节
        int len = 2 + valueLen;
        //如果当前数组存不下，数组扩容
        if(headerBytesSize - headerIndex < len){
        	headerBytesSize = len + headerBytesSize + 100;
        	System.out.println(Thread.currentThread().getName()+" 数组扩容为："+ headerBytesSize+" 放入"+value);
        	byte[] newBytes = new byte[headerBytesSize];
        	System.arraycopy(headerBytes, 0, newBytes, 0, headerIndex);
        	headerBytes = newBytes;
        }
    	headerBytes[headerIndex++] = seq.get(key);
    	System.arraycopy(valueBytes, 0, headerBytes, headerIndex, valueLen);
    	headerIndex += valueLen;
    	// ;
    	headerBytes[headerIndex++] = 59;
    	//headerSize
    	headerBytes[0] = (byte) (headerBytes[0]+1);
        
        return this;
    }

    @Override public Message putProperties(String key, int value) {
    	putProperties(key, String.valueOf(value));
//        if (prosBiulder == null) prosBiulder = new StringBuilder(sem);
////        if (properties == null) properties = new DefaultKeyValue();
//        prosBiulder.append(key);
//        prosBiulder.append(sem);
//        prosBiulder.append(value);
//        prosBiulder.append(sem);
//        proSize++;
        return this;
    }

    @Override public Message putProperties(String key, long value) {
    	putProperties(key, String.valueOf(value));
//        if (prosBiulder == null) prosBiulder = new StringBuilder(sem);
////        if (properties == null) properties = new DefaultKeyValue();
//        prosBiulder.append(key);
//        prosBiulder.append(sem);
//        prosBiulder.append(value);
//        prosBiulder.append(sem);
//        proSize++;
        return this;
    }

    @Override public Message putProperties(String key, double value) {
    	putProperties(key, String.valueOf(value));
//        if (prosBiulder == null) prosBiulder = new StringBuilder(sem);
////        if (properties == null) properties = new DefaultKeyValue();
//        prosBiulder.append(key);
//        prosBiulder.append(sem);
//        prosBiulder.append(value);
//        prosBiulder.append(sem);
//        proSize++;
        return this;
    }

    @Override public Message putProperties(String key, String value) {
//    	if(isFirst-- > 0){
//    		System.out.println(Thread.currentThread().getName()+" putProperties key:"+key+" value:"+value);
//    	}

        if (proBytes == null) {
        	proBytes = new byte[100];
        }
        byte[] keyBytes = key.getBytes();
        int keyLen = keyBytes.length;
        byte[] valueBytes = value.getBytes();
        int valueLen = valueBytes.length;
        int len = keyLen + valueLen + 2;
        //如果当前数组存不下，数组扩容
        if(proBytesSize - proIndex < len){
        	proBytesSize = len + proBytesSize + 100 ;
        	System.out.println(Thread.currentThread().getName()+" 数组扩容为："+ headerBytesSize+" 放入"+value);
        	byte[] newBytes = new byte[proBytesSize];
        	System.arraycopy(proBytes, 0, newBytes, 0, proIndex);
        	proBytes = newBytes;
        }
        //key
    	System.arraycopy(keyBytes, 0, proBytes, proIndex, keyLen);
    	proIndex += keyLen;
    	proBytes[proIndex++] = 59;
    	
    	//value
    	System.arraycopy(valueBytes, 0, proBytes, proIndex, valueLen);
    	proIndex += valueLen;
    	proBytes[proIndex++] = 59;
    	
    	//Size
    	proBytes[0] = (byte)(proBytes[0]+1);
    	if(proBytes[0] > 100){
    		System.out.println(Thread.currentThread().getName()+" pro 大于100！");
    	}
        return this;
    }
    public void clean(){
//    	if(headerIndex > 99){
//    		headerBytes = new byte[100];
//    		headerBytesSize = 100;
//    	}else{
//    		headerBytes[0] = 0;
//    	}
		headerBytes = new byte[100];
		headerBytesSize = 100;   	
    	headerIndex = 1;
    	proBytes = null;
    	proIndex = 1;
    	proBytesSize = 100;
     }
}
