package io.openmessaging.v6;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmessaging.BatchToPartition;
import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.MessageHeader;
import io.openmessaging.Producer;
import io.openmessaging.Promise;
import io.openmessaging.demo.DefaultBytesMessage;
import io.openmessaging.demo.DefaultKeyValue;
import io.openmessaging.demo.DefaultMessageFactory;
import io.openmessaging.demo.MessageStore;
import sun.misc.Cleaner;

/**
 * @author XF
 * 生产者默认实现<p>
 * 主要有两个方法:生产消息和发送消息。
 * 此类需要在构造器中初始化信息，如果测试类使用反射，将出现问题。获取线程名时都是主线程。
 */
/**
 * @author XF
 * 此版本控制了一定大小的list才开始写，重复利用对象.
 */
public class V6ProducerUseBuilder  implements Producer {
//	static Logger logger = LoggerFactory.getLogger(V6ProducerUseBuilder.class);
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
    private DefaultMessageFactory messageFactory = new DefaultMessageFactory();

    private KeyValue properties;
    
    //序列化输出对象，每个生产者持有一个.自己测试时记得关闭，不然没法删除持有的文件。实测时kill进程，没有这个问题
//    private MappedByteBuffer mapBuffer = null;
    
    FileChannel channel;
    
    //注意与bytesSize要整除，100M，10个topic发会放不下
    long size = 1024*1024*100;
    
    //作为一个buffer的大小，放入整数个对象，后面移动指针
    private static final int bytesSize = 102400;
    
//    private int nowLimit = bytesSize;
    
    static String path ;
    /**
     * 初始化对象时，初始化io。并一直持有ObjectOutputStream oos。
     * @param properties
     */
    public V6ProducerUseBuilder(KeyValue properties) {

        producerId = count++;
        //对于所有线程只需要初始化一次
        if(data == null){
            this.properties = properties;
            path = properties.getString("STORE_PATH");
            data = new HashMap<String, HashMap<Integer, List<DefaultBytesMessage>>>(256);
        }

    }
	//标识消费者，因为测试线程用的反射，所以在构造器中赋值。
	static int count = 0;
	final int producerId;
	/**
	 * 存储所有的数组，放于内存中. 
	 * 存储对象： <queue/topic, <producerId, List<BytesMessage>>>,
	 * producerId实际代表了不同生产者线程，因为不同考虑不同生产者的顺序，这样可以避免同步
	 */
	static HashMap<String, HashMap<Integer, List<DefaultBytesMessage>>> data = null;
	
	//对于每个list，有多少个值时放进磁盘,
	static int writeThreshold = 10;
	
	//代表每个mappedByteBuffer，成功将值从false设置为true则可以使用
	static HashMap<String, AtomicBoolean> isReading = new HashMap<String, AtomicBoolean>(256);
    
	//新建mmap时加锁
    static Object mapLock = new Object();
    static HashMap<String, MappedByteBuffer> allMapBuffer = new HashMap<String, MappedByteBuffer>(256);
    
    //存储本线程对于所有消息的存储偏移
    HashMap<String, Integer> storeOff = new HashMap<>(256);
    
    //存储所有的queue/topic，flush时使用
    static List<String> allQueueAndTopic = new ArrayList<String>(100);
    
    
    //存这次新建对象的存储的list/queueOrTopic，方便send时使用
    HashMap<Integer, List<DefaultBytesMessage>> hm1 = null;
    List<DefaultBytesMessage> list = null;
    String queueOrTopic = null;
    int off = 0;
    @Override public BytesMessage createBytesMessageToTopic(String topic, byte[] body) {
    	//为每个topic，建立MappedByteBuffer，需要同步。
    	//init data
    	if((hm1 = data.get(topic)) == null){
    		//后可尝试用isReading优化，不加锁
        	synchronized (mapLock) {
        		if((hm1 = data.get(topic)) == null){
        			isReading.put(topic, new AtomicBoolean(false));
        			allQueueAndTopic.add(topic);
        			//init data
        			hm1 = new HashMap<Integer, List<DefaultBytesMessage>>();
        			data.put(topic, hm1);
        			
        			//init MappedByteBuffer
        	        File file = new File(path, topic);
        	        	try {
        					file.createNewFile();
        					channel= FileChannel.open(Paths.get(path+"/"+topic), StandardOpenOption.READ,StandardOpenOption.WRITE);
        					MappedByteBuffer mapBuffer = channel.map(MapMode.READ_WRITE, 0, size);
        					allMapBuffer.put(topic, mapBuffer);
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        		}
    		}   		
    	}
    	
    	if((list = hm1.get(producerId)) == null){
    		//初始化为writeThreshold+1个元素
    		list = new ArrayList<>(writeThreshold*2);
    		hm1.put(producerId, list);   
    		storeOff.put(topic, 0);
    	}
    	//获取以前的message对象，清理,避免新建对象
    	if(!storeOff.containsKey(topic)){
    		storeOff.put(topic, 0);
    		off = 0;
    	}else{
    		off = storeOff.get(topic);
    	}
    	DefaultBytesMessage m = null;
    	if(off < list.size()-1){
    		m =  list.get(off);
    		m.clean();
    		m.setBody(body);
    	}else{
    		m = new DefaultBytesMessage(body);
    		list.add(m);
    	}
    	off++;
    	storeOff.put(topic, off);
    	//存这次新建对象的存储的list/queueOrTopic，方便send时使用.对象中就不用存这个属性了
    	queueOrTopic = topic;
    	return m;
    }

    @Override public BytesMessage createBytesMessageToQueue(String queue, byte[] body) {
    	//为每个queue，建立MappedByteBuffer，需要同步。其他线程可以已经执行了这一步
    	if((hm1 = data.get(queue)) == null){
        	synchronized (mapLock) {
        		if((hm1 = data.get(queue)) == null){
        			
        			isReading.put(queue, new AtomicBoolean(false));
        			allQueueAndTopic.add(queue);
        			//init data
        			hm1 = new HashMap<Integer, List<DefaultBytesMessage>>();
        			data.put(queue, hm1);
        			
        			//init MappedByteBuffer
        	        File file = new File(path, queue);
        	        	try {
        					file.createNewFile();
        					channel= FileChannel.open(Paths.get(path+"/"+queue), StandardOpenOption.READ,StandardOpenOption.WRITE);
        					MappedByteBuffer mapBuffer = channel.map(MapMode.READ_WRITE, 0, size);
        					allMapBuffer.put(queue, mapBuffer);
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        		}
    		}   		
    	}
    	if((list = hm1.get(producerId)) == null){
    		list = new ArrayList<>(writeThreshold*2);
    		hm1.put(producerId, list);
    	}
    	//获取以前的message对象，清理,避免新建对象
    	if(!storeOff.containsKey(queue)){
    		storeOff.put(queue, 0);
    		off = 0 ;
    	}else{
    		off = storeOff.get(queue);
    	}
    	DefaultBytesMessage m = null;
    	if(off < list.size()){
    		m =  list.get(off);
    		m.clean();
    		m.setBody(body);
    	}else{
    		m = new DefaultBytesMessage(body);
    		list.add(m);
    	}
    	off++;
    	storeOff.put(queue, off);
    	//存这次新建对象的存储的list/queueOrTopic，方便send时使用
    	queueOrTopic = queue;
    	return m;
    }

    @Override public void start() {

    }

    @Override public void shutdown() {

    }

    @Override public KeyValue properties() {
        return properties;
    }

    /**
     * 默认send方法<p>
     * 检查topic与queue有且仅有一个，将它与对象再传给{@link MessageStore}
     * 直接序列化对象。
     */
    static HashSet<String> all = new HashSet<String>();
    
    //存储本线程准备好写入但是还未写的list
    HashMap<String, List<DefaultBytesMessage>> readyForRead = new HashMap<String, List<DefaultBytesMessage>>();
   
    @Override public void send(Message message) {
    	//处理以前没有发送完留下的对象
    	if( readyForRead.size() > 0){
    		Iterator<Entry<String, List<DefaultBytesMessage>>> it = readyForRead.entrySet().iterator();
    		Entry<String, List<DefaultBytesMessage>> e = null;
    		String key = null;
    		while(it.hasNext()){
    			e = it.next();
    			key =  e.getKey();
    			//能把值更改为true的才获得mapBuffer，并开始写。否则等待下次send
    			if(isReading.get(key).compareAndSet(false, true)){
    				mapBuffer = allMapBuffer.get(key);
    				sendList(e.getValue(), storeOff.get(key));
    				isReading.get(key).set(false);
    				it.remove();
    				storeOff.put(key, 0);
    				
    			}
    		}
    	}
    	/**
    	 * 当存储偏移达到writeThreshold时，写该list. off为下一个要写的数组下标
    	 * 偏移大于writeThreshold时，肯定已经在readyForRead中了。
    	 * 偏移小于于writeThreshold时，不用处理
    	 */
    	if(off == writeThreshold){
    		//能把值更改为true的才获得mapBuffer，并开始写。否则等待下次send
			if(isReading.get(queueOrTopic).compareAndSet(false, true)){
				mapBuffer = allMapBuffer.get(queueOrTopic);
				sendList(list , off);
				isReading.get(queueOrTopic).set(false);
				storeOff.put(queueOrTopic, 0);
				
			}else{
				//需要清理，但是获得buffer失败，偏移+1，尝试放入readyForRead
				readyForRead.put(queueOrTopic, list);
			}
    	}
    }
    /**
     * 先将message转换成字节数组，发送
     * @param message
     */
    MappedByteBuffer mapBuffer = null;
    
    /**
     * 直接发送，不用byte数组先存下来
     * @param message
     */
    StringBuilder builder = new StringBuilder("1");
    Set<String> keyS = null;
    Set<String> ks = null;
    Iterator<String> it = null;
    String key;
    DefaultKeyValue headers;
    DefaultKeyValue pros ;
    static String sem = ";";
    static String zeroSem = "0;";
//    int writeNum = 0;
//    static HashMap<String, HashMap<Integer, Integer>> num = new HashMap<String, HashMap<Integer, Integer>>();
    /**
     * 将某个list由已经设置好的mapBuffer发送
     * @param list
     * @param off  list的后面有可能是以前的数据，不能根据size判断，传入下一个要写的下标
     */
    public void sendList(List<DefaultBytesMessage> list, int off){
    	DefaultBytesMessage message = null;
    	int nowLimit = (mapBuffer.position()/bytesSize + 1)*bytesSize;
    	//off为最后一个的下标
        for(int i=0; i<off; i++){
        	//给headerSize留一个字节的位置
        	builder.delete(1, builder.length());
        	message = list.get(i);
        	
    		//headers
    		headers = (DefaultKeyValue) message.headers();
    		
    		//第一个字节,header属性个数
    		for(Entry<String, Object> e: headers.entrySet()){
    			builder.append(seq.get(e.getKey()));
    			builder.append(e.getValue());
    			//分隔符分号，不能在上面直接用+连接，避免新建String
    			builder.append(sem);
    		}
    		
    		//properties,因为个数，key都不确定所以都需要分隔符
    		pros = (DefaultKeyValue) message.properties();
    		
    		if(pros != null){
    			//String格式："数量;key;主体;key;主体;……"
    			builder.append(pros.size());
    			builder.append(sem);
        		for(Entry<String, Object> e: pros.entrySet()){
    				builder.append(e.getKey());
    				builder.append(sem);
    				builder.append(e.getValue());
    				builder.append(sem);
        		}
    		}else{
    			builder.append(zeroSem);
    		}
    		
    		//body String格式： "len1;body"
    		byte[] body = ((BytesMessage) message).getBody();
    		int bodyLen = body.length;
    		builder.append(bodyLen);
    		builder.append(sem);
    		
    		//header+properties+body总长度 转换字节
    		byte[] bytes =  builder.toString().getBytes();
    		//第一个字节放headerSize，前面预留出来的位置
    		bytes[0] = (byte) headers.size();
    		int proLen = bytes.length;
    		
    		int bytesLen =bodyLen + proLen;

			int position = mapBuffer.position();
			
			//为了方便读，在bytesSize大小的一个字节数组中存整数个对象
			if (nowLimit - position < bytesLen) {
				mapBuffer.position(nowLimit);
				nowLimit += bytesSize;
			}
			mapBuffer.put(bytes);
			mapBuffer.put(body);
		}
        int lastIndex = list.size()-1;
        while(lastIndex > writeThreshold*2){
        	list.remove(lastIndex--);
        }
    }

    @Override public void send(Message message, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public Promise<Void> sendAsync(Message message) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public Promise<Void> sendAsync(Message message, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void sendOneway(Message message) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void sendOneway(Message message, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    } 


	@Override
	public BatchToPartition createBatchToPartition(String partitionName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public BatchToPartition createBatchToPartition(String partitionName, KeyValue properties) {
		// TODO Auto-generated method stub
		return null;
	}
	
	static AtomicInteger index = new AtomicInteger();
	
	@Override
	public void flush() {
		// TODO Auto-generated method stub
		//解决list中剩余没有写入的
//		System.out.println(Thread.currentThread().getName()+" 开始flush");
		String key = null;
		Entry<String, Integer> e = null;
		Iterator<Entry<String, Integer>> it = null;
		while(storeOff.size() > 0){
			it = storeOff.entrySet().iterator();
			while(it.hasNext()){
				e = it.next();
				key = e.getKey();
				//能把值更改为true的才获得mapBuffer，并开始写。否则等待下次send
				if(isReading.get(key).compareAndSet(false, true)){
					mapBuffer = allMapBuffer.get(key);
					sendList(data.get(key).get(producerId), e.getValue());
					isReading.get(key).set(false);
					it.remove();
				}
			}
		}
	}
}
