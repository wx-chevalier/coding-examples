package io.openmessaging.v6;

import io.openmessaging.*;
import io.openmessaging.demo.DefaultBytesMessage;
import io.openmessaging.tester.Constants;
import util.ThreadLock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将header中topic/queue的value改为hashMap读取，减少String，但是官方测试没通过
 * @author XF 拉取消费者的默认实现。会实例多个。
 *         <p>
 *         主要有两个方法：1、绑定queue和topic 2、获取下一个message对象。
 *         <p>
 *         按题目要求，一个消费者线程实例化一个本对象。
 */
public class V6PullConsumerNoStr implements PullConsumer {
	private KeyValue properties;

	private List<String> bucketList = new ArrayList<>();
	//listOffset中key个数
	int bucketSize = 0;

	// 磁盘中文件数组,文件数
	static File[] files;
	static int fileNum;
	String fileName = null;
	static String[] fileNames = null;


	// 记录偏移，读第几个bucket中的第几个message
	int bucketOffset = -1;
	int messageOffset = 0;

	// 记录当前读的list的引用
	List<BytesMessage> currentList = null;

	/**
	 * 存储所有的数组，放于内存中. 测试ConcurrentHashMap，只能对1/10,跟直接用HashMap几乎没区别。
	 * 测试Hashtable，对19/20。 所以暂时考虑使用HashMap，外部控制同步。
	 * 存储对象： <queue/topic, <fileName, List<BytesMessage>>>,
	 * fileName实际代表了不同生产者线程，因为不同考虑不同生产者的顺序，这样可以避免同步
	 */
	static HashMap<String, List<DefaultBytesMessage>> data = null;

	//存储所有的queue/topic,让header中的queue和topic不用新建String
	static HashMap<Integer ,String> allQueueOrTopic = new HashMap<Integer ,String>(256);

	/**
	 * 所有线程对于不同topic，对于不同消费者的偏移
	 * 存储对象： <queue/topic,  <consumerId, offset>>
	 * 这样是为了知道某个queue/topic被消费的情况，可以及时释放内存
	 */
	static HashMap<String,  HashMap<Integer, Integer>> allOffset = null;

	//本地偏移，存topic
	HashMap<String,  Integer> listOffset = null;
	
	HashMap<String, Integer> queueOffset = null;

	// 当前该读磁盘中文件在files中索引，使用AtomicInteger避免同步
	static AtomicInteger index;

	// 记录已经读完的文件个数
	static AtomicInteger finshed;

	// 记录已经读完的文件个数,因为没有同步，所以文件读完了，还需要再让所有线程读一次，再次检测到读完了才能进行相应操作
	static HashMap<String, Boolean> isFileFinshed;

	// 线程锁，可以传入参数，设定几个线程可以同时运行
	static int threadNum = 4;
	private static ThreadLock lock = new ThreadLock(threadNum);

    //作为一个buffer的大小，放入整数个对象，后面移动指针
    private static final int bytesSize = 102400;

	static final String[] headerBank = { MessageHeader.MESSAGE_ID, MessageHeader.TOPIC, MessageHeader.QUEUE,
			MessageHeader.BORN_TIMESTAMP, MessageHeader.BORN_HOST, MessageHeader.STORE_TIMESTAMP,
			MessageHeader.STORE_HOST, MessageHeader.START_TIME, MessageHeader.STOP_TIME, MessageHeader.TIMEOUT,
			MessageHeader.PRIORITY, MessageHeader.RELIABILITY, MessageHeader.SEARCH_KEY,
			MessageHeader.SCHEDULE_EXPRESSION, MessageHeader.SHARDING_KEY, MessageHeader.SHARDING_PARTITION,
			MessageHeader.TRACE_ID };

	MappedByteBuffer mbb;
	//存放本线程消费的queue的MappedByteBuffer
	MappedByteBuffer queueMbb;
	//存放所有线程消费的topic的MappedByteBuffer
	static HashMap<String, MappedByteBuffer> allTopicMbb;
	
	static HashMap<String, AtomicBoolean> isReading ;

	//标识消费者，因为测试线程用的反射，所以在构造器中赋值。
	static int count = 0;
	final int consumerId;
	static String path ;

	public V6PullConsumerNoStr(KeyValue properties) throws IOException {
//		System.out.println("DefaultPullConsumer start:"+Thread.currentThread().getName());
		this.properties = properties;
		/**
		 * 暂未考虑同步问题。现在测试例使用反射，没有并发，所以测试问题没发现问题。
		 * files为null代表是新的测试例,为避免本次测试影响下次需要初始化各个属性。
		 */
		if (files == null) {
			path = properties.getString("STORE_PATH");
			files = new File(path).listFiles();
			index = new AtomicInteger(0);
			finshed = new AtomicInteger(0);
			isFileFinshed = new HashMap<>(128);
//			latch = new CountDownLatch(fileNum);
			data = new HashMap<String, List<DefaultBytesMessage>>(256);
			allOffset = new HashMap<String,  HashMap<Integer, Integer>>(256);
			allTopicMbb = new HashMap<String, MappedByteBuffer>(128);
			isReading = new HashMap<String, AtomicBoolean>(128);
			cleanOff = new HashMap<String, Integer>(128);
			
		}
		consumerId = count++;
//		listOffset = new HashMap<String, HashMap<String, Integer>>(256);
//		System.out.println("DefaultPullConsumer end:"+Thread.currentThread().getName());
	}

	@Override
	public KeyValue properties() {
		return properties;
	}

	/**
	 * 先消费
	 * 直到消费不到了，读入文件。
	 * 文件数可能比线程数多。
	 */
	boolean isReadedFinished = false;
	boolean lastRead = false;
	int cleanTimes = 0;
	//保存上次读到的list和offset，快速查找，查找不到则进行普通查找
	List<DefaultBytesMessage> nextList = null;
	int nextOff = 0;
	String nextTopic = null;
	int nextSize = 0;
	
	//存放各个topic的清理偏移
	static HashMap<String, Integer> cleanOff;
	
	
	long readTime = 0;
	String queue = null;
	
	//对于每个queue，一次读入多少对象放于list
	static int readThreshold = 100;
	int localReadThreshold = 100;
	int queueOff = readThreshold;
	List<DefaultBytesMessage> queueList = new ArrayList<DefaultBytesMessage>(readThreshold);
	boolean isReadQueueFinshed = false;
	boolean isQueueFinshed = false;
	int queueNum = 0;
	public Message poll() {
		try{
			//queue
			if(!isQueueFinshed){
				while (true) {
					if (queueOff != localReadThreshold) {
						System.out.println(Thread.currentThread().getName()+"消费queue个数: "+queueNum++);
						return queueList.get(queueOff++);
						
					} else {
						if (isReadQueueFinshed) {
							// 读queue完毕
							System.out.println(queue+" 消费完毕！");
							isQueueFinshed = true;
							
							//清理queueList
							queueList = null;
							
							//清理mapBuffer
							queueMbb = null;
							
							break;

						} else {
							//如果queue文件读完，将读到的数目设置为localReadThreshold，并将isQueueFinshed置为true
							readQueue();
							queueOff = 0;
						}
					}
				}	
			}
//			else{
//				return null;
//			}
			
			//topic fast-search
			if(nextOff < nextSize){
				Message m = nextList.get(nextOff);
				//需要检测，多线程最后一个可能获得null。
				if(m != null){
					//off+1。暂时不存入allOffset，快速查找失败后再存
					nextOff++;
					
					return m;
				}
			}
			//fast-search 失败后统一存入偏移
			allOffset.get(nextTopic).put(consumerId, nextOff);
			
			while(true){
				//topic normal-search
				for (int i = 0; i < bucketSize; i++) {
					//这几个变量均保存下来，下次快速查找，不用再一步步求得
					nextTopic = bucketList.get(i);
					nextList = data.get(nextTopic);
					nextSize = nextList.size();
					nextOff = allOffset.get(nextTopic).get(consumerId);
					
					if(nextOff < nextSize){
						Message m = nextList.get(nextOff);
						//需要检测，多线程最后一个可能获得null。
						if(m != null){
							//off+1。暂时不存入allOffset，快速查找失败后再存
							nextOff++;
							return m;
						}
					}

				}
				//此topic获得对象失败,尝试获得mapBuffer读文件.
				for(int i = 0; i < bucketSize; i++){
					//这几个变量均保存下来，下次快速查找，不用再一步步求得
					nextTopic = bucketList.get(i);
					nextList = data.get(nextTopic);
					try{
					nextOff = allOffset.get(nextTopic).get(consumerId);
					}
					catch(Exception e){
						System.out.println(nextTopic+" "+Thread.currentThread().getName()+" 异常！ ");
						e.printStackTrace();
						System.out.println();
					}
					//文件是否已经读完
					if (isFileFinshed.get(nextTopic)) {
						if(nextOff < nextList.size()){
							//读完了，本线程还没消费完,快速查找消费完
							return nextList.get(nextOff++);
							
						}else{
							//所有的消费都完成了，清除本线程该topic的所有信息
//							System.out.println(nextTopic+" "+Thread.currentThread().getName()+" 消费完毕! ");
							allOffset.get(nextTopic).remove(consumerId);
							if(allOffset.get(nextTopic).size() == 0){
								data.remove(nextTopic);
							}
							bucketList.remove(i);
							bucketSize--;
							if(bucketSize == 0) return null;
							i--;
							continue;
						}
					} 
	    			//文件没读完，开始尝试读入。能把值更改为true的才获得mapBuffer，并开始读。否则等待下次send
	    			if(isReading.get(nextTopic).compareAndSet(false, true)){
//	    				System.out.println(key+" "+Thread.currentThread().getName()+" readyForRead 获得写对象成功！ ");
	    				
	    				mbb = allTopicMbb.get(nextTopic);
	    				if(readTopic(nextTopic)){
	    					//文件没读完
	    					Message m = nextList.get(nextOff);
	    					nextSize = nextList.size();
	    					//不需要检测，本线程再读文件，可以正确获得
//	    					if(m != null){
	    					isReading.get(nextTopic).set(false);
	    					nextOff++;
	    					return m;
//	    					}
	    				}else{
	    					//文件读完了，做相应处理
	    					isReading.get(nextTopic).set(false);
	    					isFileFinshed.put(nextTopic, true);
	    					
	    					//释放清除相应的mapBuffer
	    					
	    					//移除bucketList中这个topic，topic剩余的对象由快速查找消费完
	    					nextSize = nextList.size();
	    					if(nextOff < nextSize){
	    						return nextList.get(nextOff++);
	    					}
	    					
	    				}
	    			}
				}
			}
			
			
		}catch(Exception e){ //读取异常时结束本线程
			System.out.println(Thread.currentThread().getName()+" 异常如下：");
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Message poll(KeyValue properties) {
		throw new UnsupportedOperationException("Unsupported");
	}

	@Override
	public void ack(String messageId) {
		throw new UnsupportedOperationException("Unsupported");
	}

	@Override
	public void ack(String messageId, KeyValue properties) {
		throw new UnsupportedOperationException("Unsupported");
	}

	/**
	 * 不需要同步，每个线程启动一个Consumer。
	 * <P>
	 * 对于消费者来说，queue和topic是没有区分的，不需要区分。
	 * <p>
	 * 不在这里获得所有消息queue和topic的集合，就不用考虑同步。
	 * 这里获取了所有要消费的queue和topic。data
	 * 另topic需多次消费，需维持一个变量。allOffset
	 * 本消费者要消费的消息偏移。 listOffset
	 */
	
	@Override
	public void attachQueue(String queueName, Collection<String> topics) {
//		System.out.println("attachQueue start:"+Thread.currentThread().getName());
		/**
		 * queue
		 */
		queue = queueName;
		File file = new File(path, queue);
		FileChannel fc;
		try {
			fc = new FileInputStream(file).getChannel();
			queueMbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			queueMbb.get(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//初始化queueList放入对象，后面不用再做空检测
		for(int i=0; i<readThreshold; i++){
			queueList.add(new DefaultBytesMessage(null));
		}
		/**
		 * topic
		 */
		Iterator<String> it =  topics.iterator();
		while(it.hasNext()){
			String topicName = it.next();
			bucketList.add(topicName);
//			allQueueOrTopic.put(topicName, topicName);		
			if(!data.containsKey(topicName)){
				//init data  4kw/100
				data.put(topicName, new ArrayList<DefaultBytesMessage>(400000));
				//init allOffset
				HashMap<Integer, Integer> hm1 = new HashMap<Integer, Integer>();
				hm1.put(consumerId, 0);
				allOffset.put(topicName, hm1);
				cleanOff.put(topicName, 0);
				isFileFinshed.put(topicName, false);
				isReading.put(topicName, new AtomicBoolean());
				
    			//init Topic MappedByteBuffer
    	        File topicFile = new File(path, topicName);
//    	        System.out.println(topicFile.getAbsoluteFile());
				try {
					FileChannel topicFc = new FileInputStream(file).getChannel();
					mbb = topicFc.map(FileChannel.MapMode.READ_ONLY, 0, topicFc.size());
					allTopicMbb.put(topicName, mbb);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				allOffset.get(topicName).put(consumerId, 0);
			}
		}
		nextTopic = bucketList.get(0);
		bucketSize = bucketList.size();
	}


	/**
	 * 每次调用本方法读入bytesSize大小中的对象
	 * (肯定是整数个对象，由生产者保证)
	 */
	byte[] b = new byte[bytesSize];
	int readFileTimes = 0;
	
	// 通过字节数组下标index来控制读入的进度
	int bytesIndex = 0;
	
	public boolean readTopic(String topic) {
//		System.out.println(Thread.currentThread().getName()+"读文件 "+topic+" 第"+readFileTimes+++" 次");
		try {
			// 通过字节数组下标index来控制读入的进度
			int bytesIndex = 0;
			//上次清理到cleanFloor
			int cleanFloor = cleanOff.get(topic);
			//所有线程读到的最小的偏移
			HashMap<Integer, Integer> topicOff = allOffset.get(topic);
			Iterator<Integer> it = topicOff.values().iterator();
			int cleanCeiling = nextOff;
			int temp ;
			while(it.hasNext()){
				temp = it.next();
				if(temp < cleanCeiling){
					cleanCeiling = temp;
				}
			}

			try {
				mbb.get(b);
//				while(b[0] == 0){
//					mbb.get(b);
//					System.out.println(topic+"读为空    pos："+mbb.position());
//				}
			} catch (BufferUnderflowException e) {
				System.out.println(Thread.currentThread().getName()+"  读完了 ");
				return false;
			}
			while (bytesIndex != -1 && bytesIndex < bytesSize ) {
				// Message
				DefaultBytesMessage message = null;
				if(cleanFloor < cleanCeiling){
					message = (DefaultBytesMessage) nextList.get(cleanFloor);
					message.clean();
					
					nextList.set(cleanFloor++, null);
				}else{
					message = new DefaultBytesMessage(null);
				}
				/**
				 * headers
				 */
				int headerSize = b[bytesIndex++];
				if(headerSize == 0 ){
					cleanOff.put(topic, cleanFloor);
//					System.out.println(Thread.currentThread().getName()+"放偏移 "+topic+"  "+cleanFloor);
					if(bytesIndex == 1){
//						System.out.println(Thread.currentThread().getName()+" header = 0 文件 "+topic+" 读完了, Num:"+nextList.size());
//						System.out.println(topic+"读为空    pos："+mbb.position());
						return false;
					}else{
//						System.out.println(Thread.currentThread().getName()+" header = 0 本轮完毕");
						return true;
					}
				}
				for (int i = 0; i < headerSize; i++) {
					// header编号一个字节
					int seq = b[bytesIndex++] - 97;
					String header = headerBank[seq];

					// 获取header对应的value
					int start = bytesIndex;
					// 直到找到分割符 ；到字节数组最后时，需要将之前的存下来，并且读入新的字节数组
					while (b[bytesIndex] != 59) {
						bytesIndex++;
					}
					String value = new String(b, start, bytesIndex - start);
					bytesIndex++;

					message.putHeaders(header, value);
				}
				message.putHeaders(MessageHeader.TOPIC, topic);
				/**
				 * properties
				 */
				int proSize = b[bytesIndex++];
				/**
				 * 属性个数不为0时时查找。 与上方查找header的value方法一致，一直向后直到分隔符。
				 * 到字节数组最后时，需要将之前的存下来，并且读入新的字节数组
				 */
				if (proSize != 0) {

					for (int i = 0; i < proSize; i++) {
						// 找key，直到找到分割符 ；
						int start = bytesIndex;
						while (b[bytesIndex] != 59) {
							bytesIndex++;
						}
						String key = new String(b, start, bytesIndex - start);
						bytesIndex++;

						// 找value，直到找到分割符 ；
						start = bytesIndex;
						while (b[bytesIndex] != 59) {
							bytesIndex++;
						}
						String value = new String(b, start, bytesIndex - start);
						bytesIndex++;
						message.putProperties(key, value);
					}
				}
				/**
				 * body len1;body 一直读直到分隔符为body的长度
				 */
				// 找len1，直到找到分割符 ；
				int bodyLen = ((b[bytesIndex++] & 0xff) << 16) + ((b[bytesIndex++] & 0xff) << 8) + ((b[bytesIndex++] & 0xff)); 

				// body

				int tempS = bytesIndex;
				bytesIndex = bytesIndex + bodyLen;
				byte[] body = Arrays.copyOfRange(b, tempS, bytesIndex);
				message.setBody(body);
				nextList.add(message);
				
				if(bytesIndex == bytesSize) {
					cleanOff.put(topic, cleanFloor);
//					System.out.println(Thread.currentThread().getName()+"放偏移 "+topic+"  "+cleanFloor);
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName()+"异常，信息如下!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	int readNum = 0;
	public boolean readQueue(){
		try {
			//一次读入readThreshold个对象，可以复用对象
			for(int ri=0; ri<readThreshold; ri++){  //ri:readIndex
				// Message
				DefaultBytesMessage message = queueList.get(ri);
				message.clean();
				/**
				 * headers
				 */

				int headerSize = b[bytesIndex++];
				while(headerSize == 0 ){
					if(bytesIndex == 1){
//						System.out.println(Thread.currentThread().getName()+" header = 0 文件读完了:"+queue);
						isReadQueueFinshed = true;
						localReadThreshold = ri;
						return true;
					}else{
//						System.out.println(Thread.currentThread().getName()+" header = 0 本轮完毕");
						queueMbb.get(b);
						bytesIndex = 0;
						headerSize = b[bytesIndex++];
					}
				}
//				System.out.println(Thread.currentThread().getName()+" 读入个数："+readNum++);
				for (int i = 0; i < headerSize; i++) {
					// header编号 一个字节
					int seq = b[bytesIndex++] - 97;
					String header = headerBank[seq];

					// 获取header对应的value
					int start = bytesIndex;
					// 直到找到分割符 ；到字节数组最后时，需要将之前的存下来，并且读入新的字节数组
					while (b[bytesIndex] != 59) {
						bytesIndex++;
					}
					String value = new String(b, start, bytesIndex - start);
					bytesIndex++;
					
					message.putHeaders(header, value);
				}
				message.putHeaders(MessageHeader.QUEUE, queue);

				/**
				 * properties
				 */
				int proSize = b[bytesIndex++];

				/**
				 * 属性个数不为0时时查找。 与上方查找header的value方法一致，一直向后直到分隔符。
				 * 到字节数组最后时，需要将之前的存下来，并且读入新的字节数组
				 */
				if (proSize != 0) {

					for (int i = 0; i < proSize; i++) {
						// 找key，直到找到分割符 ；
						int start = bytesIndex;
						while (b[bytesIndex] != 59) {
							bytesIndex++;
						}
						//可以观察下这里的值，尝试不用新建String
						String key = new String(b, start, bytesIndex - start);
						bytesIndex++;

						// 找value，直到找到分割符 ；
						start = bytesIndex;
						while (b[bytesIndex] != 59) {
							bytesIndex++;
						}
						String value = new String(b, start, bytesIndex - start);
						bytesIndex++;
						message.putProperties(key, value);
					}
				}
				/**
				 * body len1;body 一直读直到分隔符为body的长度
				 */
				// 找bodyLen，直到找到分割符 ；
				int bodyLen = ((b[bytesIndex++] & 0xff) << 16) + ((b[bytesIndex++] & 0xff) << 8) + ((b[bytesIndex++] & 0xff)); 
//				int bodyLen = b[bytesIndex++]-48;
//				if(bodyLen != 0){
//					while (b[bytesIndex] != 59) {
//						bodyLen = bodyLen*10 + b[bytesIndex++] -48;
//					}					
//				}
//				bytesIndex++;
				
				// body
				int tempS = bytesIndex;
				bytesIndex = bytesIndex + bodyLen;
				byte[] body = Arrays.copyOfRange(b, tempS, bytesIndex);
				message.setBody(body);
				
				if(bytesIndex == bytesSize) {
					queueMbb.get(b);
					bytesIndex = 0;
				}
			}
		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName()+"异常，信息如下!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
