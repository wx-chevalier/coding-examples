package io.openmessaging.v5;

import io.openmessaging.*;
import io.openmessaging.demo.DefaultBytesMessage;
import io.openmessaging.tester.Constants;
import util.ThreadLock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将header中topic/queue的value改为hashMap读取，减少String，但是官方测试没通过
 * @author XF 拉取消费者的默认实现。会实例多个。
 *         <p>
 *         主要有两个方法：1、绑定queue和topic 2、获取下一个message对象。
 *         <p>
 *         按题目要求，一个消费者线程实例化一个本对象。
 */
public class V5PullConsumerModify implements PullConsumer {
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
	static HashMap<String, HashMap<String, List<BytesMessage>>> data = null;

	//存储所有的queue/topic,让header中的queue和topic不用新建String
	static HashMap<Integer ,String> allQueueOrTopic = new HashMap<Integer ,String>(256);

	/**
	 * 存储每个queue或者topic，对于不同生产者，不同消费者的偏移
	 * 存储对象： <queue/topic, <fileName, <consumerId, offset>>>,
	 * 这样是为了知道某个queue/topic被消费的情况，可以及时释放内存
	 */
	static HashMap<String, HashMap<String, HashMap<Integer, Integer>>> allOffset = null;

	HashMap<String, HashMap<String, Integer>> listOffset = null;
	
	HashMap<String, Integer> queueOffset = null;

	// 当前该读磁盘中文件在files中索引，使用AtomicInteger避免同步
	static AtomicInteger index;

	// 记录已经读完的文件个数
	static AtomicInteger finshed;

	// 记录已经读完的文件个数,因为没有同步，所以文件读完了，还需要再让所有线程读一次，再次检测到读完了才能进行相应操作
	static HashMap<String, Integer> isFileFinshed;

	// 线程锁，可以传入参数，设定几个线程可以同时运行
	static int threadNum = 4;
	private static ThreadLock lock = new ThreadLock(threadNum);

	static Object obj = new Object();

    //作为一个buffer的大小，放入整数个对象，后面移动指针
//    private static final int bytesSize = Constants.Bytes_Size;
    private static final int bytesSize = 102400;

//	Method getCleanerMethod = ByteBuffer.getClass().getMethod("cleaner", new Class[0]);

	private static CountDownLatch latch ;

	static final String[] headerBank = { MessageHeader.MESSAGE_ID, MessageHeader.TOPIC, MessageHeader.QUEUE,
			MessageHeader.BORN_TIMESTAMP, MessageHeader.BORN_HOST, MessageHeader.STORE_TIMESTAMP,
			MessageHeader.STORE_HOST, MessageHeader.START_TIME, MessageHeader.STOP_TIME, MessageHeader.TIMEOUT,
			MessageHeader.PRIORITY, MessageHeader.RELIABILITY, MessageHeader.SEARCH_KEY,
			MessageHeader.SCHEDULE_EXPRESSION, MessageHeader.SHARDING_KEY, MessageHeader.SHARDING_PARTITION,
			MessageHeader.TRACE_ID };

	MappedByteBuffer mbb;

	//标识消费者，因为测试线程用的反射，所以在构造器中赋值。
	static int count = 0;
	final int consumerId;


	public V5PullConsumerModify(KeyValue properties) throws IOException {
//		System.out.println("DefaultPullConsumer start:"+Thread.currentThread().getName());
		this.properties = properties;
		/**
		 * 暂未考虑同步问题。现在测试例使用反射，没有并发，所以测试问题没发现问题。
		 * files为null代表是新的测试例,为避免本次测试影响下次需要初始化各个属性。
		 */
		if (files == null) {
			String path = properties.getString("STORE_PATH");
			files = new File(path).listFiles();
			fileNum = files.length;
			fileNames = new String[fileNum];
			index = new AtomicInteger(0);
			finshed = new AtomicInteger(0);
			isFileFinshed = new HashMap<>();
//			latch = new CountDownLatch(fileNum);
			data = new HashMap<String, HashMap<String, List<BytesMessage>>>(256);
			allOffset = new HashMap<String, HashMap<String, HashMap<Integer, Integer>>>(256);
			
			for(int i=0; i<fileNum; i++){
				fileNames[i] = files[i].getName();
//				System.out.println(fileNames[i]);
				isFileFinshed.put(fileNames[i], 2);
			}
		}
		queueOffset = new HashMap<String, Integer>();
		for(int i=0; i<fileNum; i++){
			queueOffset.put(fileNames[i], 0);
		}		
		int fileIndex = index.getAndIncrement();
		//考虑文件数小于线程数
		if(fileIndex < fileNum){
			File file = files[fileIndex];
			fileName = fileNames[fileIndex];
			FileChannel fc = new FileInputStream(file).getChannel();
			mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//			mbb.load();
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
	List<BytesMessage> nextList = null;
	int nextOff = 0;
	String nextFile = null;
	long readTime = 0;
	
	public Message poll() {
		//利用上次保存的lastList进行快速查找，失败则进行普通查找
		if(nextList.size() > nextOff){
			Message m = nextList.get(nextOff);
			//成功
			if(m != null){
				//lastList/nextFile不变，off+1
				nextOff++;
				//如果是queue需要释放内存，本地queue偏移+1
				KeyValue headers = m.headers();
				if(headers.getString(MessageHeader.QUEUE) != null){
					nextList.set(nextOff -1 , null);
					queueOffset.put(nextFile, nextOff);
					
				}else{  //topic,不需要释放内存，全局allOffset的偏移+1
					String topic = headers.getString(MessageHeader.TOPIC);
					allOffset.get(topic).get(nextFile).put(consumerId, nextOff);
				}
				return m;
			}
		}
		while(true){
			//queue
			String bucket = bucketList.get(0);
			HashMap<String, List<BytesMessage>> hm = data.get(bucket);
			for (int j = 0; j < fileNum; j++) {
				String queueFileName = fileNames[j];
				List<BytesMessage> list = hm.get(queueFileName);
				int offset = queueOffset.get(queueFileName);
				if (list.size() > offset) {
					Message m = list.get(offset);
					if (m != null) {
//						offHM.put(queueFileName, offset + 1);
						// queue只有一个消费者访问，读一个即可释放一个.queue不存allOffset中。
						nextOff = offset+1;
						queueOffset.put(queueFileName, nextOff);
//						System.out.println(Thread.currentThread().getName()+" 返回  procedure"+queueFileName+" topic: "+bucket+"  offset: "+offset);
						list.set(offset, null);
						nextList = list;
						nextFile = queueFileName;
						return m;
					}
				}
			}
			//topic			
			for(int i=1; i<bucketSize; i++){
				bucket = bucketList.get(i);
				hm = data.get(bucket);

				for(int j=0; j<fileNum; j++){
					String topicFileName = fileNames[j];
					List<BytesMessage> list = hm.get(topicFileName);
//					HashMap<String, Integer> offHM = listOffset.get(bucket);
					Integer off = allOffset.get(bucket).get(topicFileName).get(consumerId);
					//该文件读完后，这里会变成空
					if(off == null) continue;
					int offset = off;
					if(list.size()>offset){
						/**
						 * 这里注意，因为没有对list加锁，可能获得到空值。
						 * 必须检测， 不然陷入死循环。
						 */
						Message m = list.get(offset);
						if(m != null){
//							offHM.put(topicFileName, offset+1);
							allOffset.get(bucket).get(topicFileName).put(consumerId, offset+1);
//							System.out.println(Thread.currentThread().getName()+" 返回 "+topicFileName+".txt topic: "+bucket+"  offset: "+offset);
							return m;
						}
					}
					int low = off;
					HashMap<String, HashMap<Integer, Integer>> hm1 = allOffset.get(bucket);
					HashMap<Integer, Integer> hm2 = hm1.get(topicFileName);
					if(hm2 == null) continue;
					Collection<Integer> collection = hm2.values();
					if(collection != null ){
						try{
							for(Integer temp : collection){
								if(temp != null && temp < low){
									low = temp;
								}
							}
							int cleanNum = 0;
							for(int m=low-1; m>=0 && list.get(m) != null; m--){
								list.set(m, null);
								cleanNum++;
							}
//							System.out.println("执行清理topic，第 "+cleanTimes+++" 次，共清理文件个数: "+cleanNum);
						}catch(Exception e){
							System.out.println(Thread.currentThread().getName()+" 读collection 空指针异常");
							e.printStackTrace();
						}
					}
					/**
					 * 文件读完时(值为1)，即将清除本线程要消费的相关信息。
					 * 因为没有同步，需要再读一次才能确认该文件内容已经被消费完了。
					 */
					int isFileF = isFileFinshed.get(topicFileName);
					if(isFileF == 1){
						isFileFinshed.put(topicFileName, 0);
					}else if(isFileF == 0){
						//这里是赋为null，仍然可以读出来，只是值为null
						allOffset.get(bucket).get(topicFileName).put(consumerId, null);
					}
				}
			}

			//所有文件读完后，再度一次也完成了，返回null
			if(lastRead == true){
				System.out.println(Thread.currentThread().getName()+"线程所有操作完毕，退出");
				mbb = null;
				System.out.println(Thread.currentThread().getName()+" readFile 耗时: "+readTime);
				return null;
			}
			//本线程没有读文件，即生产者线程比消费者线程少
			if(mbb == null){
				if(finshed.get() == fileNum){
					//所有文件都读取完毕后最后读一次
					lastRead = true;
				}
				Thread.yield();
				continue;
			}
			//仍然没有消费到，读入新的文件
			if(isReadedFinished == false){
				//返回false说明文件已经读完，以后程序不再读，但是线程不会结束。 
				long start = System.currentTimeMillis();
				if(!readFile()){
					finshed.incrementAndGet();
					isReadedFinished = true;
					isFileFinshed.put(fileName, 1);
//					System.out.println(fileName+"读取完毕");
				}
				readTime += System.currentTimeMillis() - start;
			}else{
				//如果文件数大于消费者线程数,即生产者线程大于消费者线程
				int fileIndex = index.getAndIncrement();
				if(fileIndex < fileNum){
					File file = files[fileIndex];
					fileName = file.getName();
					FileChannel fc;
					try {
						fc = new FileInputStream(file).getChannel();
						mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
					} catch (IOException e) {
						e.printStackTrace();
					}
					isReadedFinished = false;
					continue;
				}
				//所有文件读完了
				if(finshed.get() == fileNum){
					//所有文件都读取完毕后最后读一次
					lastRead = true;
					continue;
				}
				Thread.yield();
			}
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
		bucketList.add(queueName);
//		System.out.println(Thread.currentThread().getName()+"添加queue "+queueName);
		//初始化allQueueOrTopic
//		allQueueOrTopic.put(queueName, queueName);
		byte[] bs = queueName.getBytes();
		//类似为每个queue/topic保留一个hash值
		int hash = 1;
		for(int i=0; i<bs.length; i++){
			hash = bs[i] + 31*hash;
		}
		allQueueOrTopic.put(hash, queueName);
		
		
		//本线程要消费的queue的偏移,这里初始化，避免同步
//		HashMap<String, Integer> queueOffset = new HashMap<String, Integer>();
//		for(int i=0; i<fileNum; i++){
//			queueOffset.put(fileNames[i], 0);
//		}
//		listOffset.put(queueName, queueOffset);
		
		/**
		 * 将queue对于fileName再分一次，实际是对于不同生产者线程在分一次.
		 * 这样组装list不用同步,因为不同生产者线程之间消息没有顺序要求。queue肯定是第一次，需要新建放入data.
		 * data，注意与topic区别是 内部HashMap的key也是queueName
		 */
		HashMap<String, List<BytesMessage>> queueHM = new HashMap<String, List<BytesMessage>>();
		
		for(int i=0; i<fileNum; i++){
			ArrayList<BytesMessage> queueList = new ArrayList<BytesMessage>();
			queueHM.put(fileNames[i], queueList);
		}
		data.put(queueName, queueHM);
		nextFile = fileNames[0];
		nextList = queueHM.get(nextFile);
			
		/**
		 * topic
		 */
		Iterator<String> it =  topics.iterator();
		while(it.hasNext()){
			String topicName = it.next();
			bucketList.add(topicName);
			bs = topicName.getBytes();
			//类似为每个queue/topic保留一个hash值
			hash = 1;
			for(int i=0; i<bs.length; i++){
				hash = bs[i] + 31*hash;
			}
			allQueueOrTopic.put(hash, topicName);
			
			//listOffset,同样区分不同fileName(生产者线程)
//			HashMap<String, Integer> myOffset = new HashMap<String, Integer>();
//			for(int i=0; i<fileNum; i++){
//				myOffset.put(fileNames[i], 0);
//			}
//			listOffset.put(topicName, myOffset);
			
			if(!data.containsKey(topicName)){
				System.out.println("包含topic :"+topicName);
				
				//data，注意与queue区别是 内部HashMap的key是fileName
				HashMap<String, List<BytesMessage>> topicHM = new HashMap<String, List<BytesMessage>>();
				for(int i=0; i<fileNum; i++){
					ArrayList<BytesMessage> queueList = new ArrayList<BytesMessage>();
					topicHM.put(fileNames[i], queueList);
				}
				data.put(topicName, topicHM);
//				System.out.println(Thread.currentThread().getName()+"添加topic "+topicName);
				
				//allOffset,对于每个topic，需要将所有文件名加进去
				HashMap<String, HashMap<Integer, Integer>> fileHMOffset = new HashMap<String, HashMap<Integer, Integer>>();
				for(int i=0; i<fileNum; i++){
					HashMap<Integer, Integer> offset = new HashMap<Integer, Integer>();
//					与消费者线程维持的自身的偏移联系起来，只用维护更新listOffset。对Integer没用 
//					offset.put(consumerId,  listOffset.get(topicName).get(fileNames));
					offset.put(consumerId,  0);
					fileHMOffset.put(fileNames[i], offset);
				}
				allOffset.put(topicName, fileHMOffset);	
				
			}else{
				//allOffset
				HashMap<String, HashMap<Integer, Integer>> fileHMOffset = allOffset.get(topicName);
				for(Entry<String, HashMap<Integer, Integer>> entry : fileHMOffset.entrySet()){
					entry.getValue().put(consumerId, 0);
				}
				
			}
		}
		bucketSize = bucketList.size();
//		System.out.println("attachQueue end:"+Thread.currentThread().getName());
	}


	/**
	 * 每次调用本方法读入bytesSize大小中的对象
	 * (肯定是整数个对象，由生产者保证)
	 */
	byte[] b = new byte[bytesSize];
	int readFileTimes = 0;
	public boolean readFile() {
//		System.out.println(Thread.currentThread().getName()+"读文件 第"+readFileTimes+++" 次");
		try {
			// 通过字节数组下标index来控制读入的进度
			int bytesIndex = 0;

			// 使用内存映射时，每次读入字节都是bytesSize，现在每次读入都是整数个对象
			String queueOrTopic = null;

			try {
				mbb.get(b);
			} catch (BufferUnderflowException e) {
				// throw new OMSReadFinshedException();读完了
				System.out.println(Thread.currentThread().getName()+"  读完了 ");
				e.printStackTrace();
				return false;
			}
			boolean isTopicNeeded = true;
			while (bytesIndex != -1 && bytesIndex < bytesSize -1) {
				// Message
				BytesMessage message = new DefaultBytesMessage(null);
				//该topic是否需要放入内存
				isTopicNeeded = true;
				/**
				 * headers
				 */
				int headerSize = b[bytesIndex++];
				if(headerSize == 0 ){
					if(bytesIndex == 1){
						System.out.println(Thread.currentThread().getName()+" header = 0 文件读完了");
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

					String value = null;
					/**
					 * 获得queue/topic，考虑到对象头中，QueueOrTopic大部分是一样的，
					 * 所以用hashMap存一次就行，不用每次都新建，内存爆炸，cms回收不动.
					 * 为了不新建对象，使用hash值获得value对象的引用
					 */
					if (seq == 1 || seq == 2) {

						int hash = 1;
						int temp = 0;
						while ((temp = b[bytesIndex]) != 59) {
							hash = temp + 31*hash;
							bytesIndex++;
						}
						bytesIndex++;
						queueOrTopic = allQueueOrTopic.get(hash);
						if(queueOrTopic == null){
							//如果data没有topic，说明该对象不需要保存 seq = 1
							isTopicNeeded = false;
							continue;
						}
						value = queueOrTopic;
					}else{
						// 获取header对应的value
						int start = bytesIndex;
						// 直到找到分割符 ；到字节数组最后时，需要将之前的存下来，并且读入新的字节数组
						while (b[bytesIndex] != 59) {
							bytesIndex++;
						}
						value = new String(b, start, bytesIndex - start);
						bytesIndex++;
					}
					message.putHeaders(header, value);
				}

				/**
				 * properties
				 */
//				byte[] bs = new byte[5];
//				int temp = 0;
//				// 直到找到分割符 
//				while (b[bytesIndex] != 59) {
//					bs[temp++] = b[bytesIndex];
//					bytesIndex++;
//				}
//				int proSize = Integer.valueOf(new String(bs, 0, temp));
				int proSize = b[bytesIndex++]-48;
				if(proSize != 0){
					while (b[bytesIndex] != 59) {
						proSize = proSize*10 + b[bytesIndex++] -48;
					}					
				}
				bytesIndex++;
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
//				int start = bytesIndex;
//				String pre = null;
//				while (b[bytesIndex] != 59) {
//					bytesIndex++;
//				}
//				String bodyLen = new String(b, start, bytesIndex - start);
//				int len1 = Integer.valueOf(bodyLen);
//				bytesIndex++;
				int len1 = b[bytesIndex++]-48;
				if(len1 != 0){
					while (b[bytesIndex] != 59) {
						len1 = len1*10 + b[bytesIndex++] -48;
					}					
				}
				bytesIndex++;

				// body
				if(isTopicNeeded){
					byte[] body = null;
					int tempS = bytesIndex;
					bytesIndex = bytesIndex + len1;
					body = Arrays.copyOfRange(b, tempS, bytesIndex);
					message.setBody(body);
					
					/**
					 * 至此一个message对象反序列化完成。 下面将message放于对应list中。
					 * 不需要同步
					 */
					HashMap<String, List<BytesMessage>> hm1 = data.get(queueOrTopic);
//					System.out.println("data包含如下key："+Arrays.toString(data.keySet().toArray()));
//					System.out.println("查找topic list"+queueOrTopic+" file: "+fileName);
//					System.out.println("hm1包含如下key："+Arrays.toString(hm1.keySet().toArray()));
					List<BytesMessage> l = hm1.get(fileName);
					l.add(message);
				}else{
					bytesIndex = bytesIndex + len1;
//					System.out.println("没有包含的topic :"+queueOrTopic);
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
