package io.openmessaging.v1;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.MessageHeader;
import io.openmessaging.PullConsumer;
import io.openmessaging.demo.ClientOMSException;
import io.openmessaging.demo.MessageStore;
import io.openmessaging.tester.Constants;
import util.ThreadLock;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author XF
 *　拉取消费者的默认实现。会实例多个。<p>
 * 主要有两个方法：1、绑定queue和topic 2、获取下一个message对象。
 * <p>按题目要求，一个消费者线程实例化一个本对象。
 */
public class V1PullConsumer implements PullConsumer {
    private KeyValue properties;
    private List<String> bucketList = new ArrayList<>();
    
    //磁盘中文件数组,文件数
    static File[] files;
    static int fileNum ;
    
    //记录偏移，读第几个bucket中的第几个message
    int bucketOffset = -1;
    int messageOffset = 0;
    
    //记录当前读的list的引用
    List<BytesMessage> currentList = null; 
    
    /**
     * 存储所有的数组，放于内存中.
     * 测试ConcurrentHashMap，只能对1/10,跟直接用HashMap几乎没区别。
     * 测试Hashtable，对19/20。
     * 所以暂时考虑使用HashMap，外部控制同步。
     */
    static HashMap<String, List<BytesMessage>> data = null;
//    static ConcurrentHashMap<String, List<BytesMessage>> data = new ConcurrentHashMap<String, List<BytesMessage>>(170);
//    static Hashtable<String, List<BytesMessage>> data = new Hashtable<String, List<BytesMessage>>(170);
    
    //当前该读磁盘中文件在files中索引，使用AtomicInteger避免同步
    static AtomicInteger index ;
    
    //记录已经读完的文件，所有文件都读完了才能开始消费
    static AtomicInteger finshed ;
    
    //线程锁，可以传入参数，设定几个线程可以同时运行
    static int threadNum = 4; 
    private static ThreadLock lock = new ThreadLock(threadNum);  
    
    static Object obj = new Object();
    
    
    public V1PullConsumer(KeyValue properties) {
        this.properties = properties;
        /**
         * 暂未考虑同步问题。现在测试例使用反射，没有并发，所以测试问题没发现问题。
         * files为null代表是新的测试例,为避免本次测试影响下次需要初始化各个属性。
         */
        if(files == null){
        	String path = properties.getString("STORE_PATH");
            files = new File(path).listFiles();
            fileNum = files.length;
            index = new AtomicInteger(0);
            finshed = new AtomicInteger(0);
            data = new HashMap<String, List<BytesMessage>>(170);
        }
    }


    @Override public KeyValue properties() {
        return properties;
    }


    /**
     * 读完文件后不需要同步,也不需要轮询,挨个读。
     * 读文件后的程序正确(在单线程读文件后，多线程poll程序正确)
     */
    boolean isReaded = false;

    public Message poll() {
    	//每个线程都应该先读文件，或者阻塞住等待读文件完毕
    	if(isReaded == false){
    		readFile();
    		isReaded = true;
    		//将files赋为null，下个测试例才知道是新的测试例，重新初始化各个属性
    		files = null;
    	}
    	
    	//可能某个queue/topic一个message都没有，滚动到下一个
    	while(currentList ==null  && bucketOffset<bucketList.size()-1 ){
    		++bucketOffset;
    		currentList = data.get(bucketList.get(bucketOffset));
    	}
    	
    	if(currentList ==null){
    		return null;
    	}
    	
    	BytesMessage message = currentList.get(messageOffset);
    	if(messageOffset < currentList.size()-1){
    		messageOffset++;
    	}else {
//    		bucketOffset++;
    		currentList = null;
    		messageOffset = 0;
    	}
    	return message;
    }

    @Override public Message poll(KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void ack(String messageId) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void ack(String messageId, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }
   
    /**
     * 不需要同步，每个线程启动一个Consumer。
     * <P>对于消费者来说，queue和topic是没有区分的，不需要区分。
     * <p>不在这里获得所有消息queue和topic的集合，就不用考虑同步。
     */
    @Override
    public void attachQueue(String queueName, Collection<String> topics){
    	
    	bucketList.add(queueName);
    	bucketList.addAll(topics);
    	/**
    	 * 如果这里执行读文件，因为测试例使用反射所以是单线程的，只有main线程。
    	 * 单线程情况下结果正确，性能较差。
    	 */
//    	if(isReaded == false){
//    		readFile();
//    		isReaded = true;
//    	}
    }
    
    long listTime = 0;
    long totalTime = 0;
    //读入文件
    public void readFile(){
    	
    	/**
    	 * 这里加锁，只是为了控制几个线程可以取得同步资源。与数据的同步无关。
    	 * 测试发现，不加锁更快。。推测线程切换的开销比阻塞唤醒的小。
    	 * 但是不同测试例可能有不同的效果，以后再测。
    	 */
//    	lock.lock();
    	
    	try{
//    		System.out.println(Thread.currentThread().getName()+"获得锁 :"+(new Date()));
    		int fileIndex = index.getAndIncrement();
        	while(fileIndex < fileNum){
//        		System.out.println(Thread.currentThread().getName()+"获得文件"+files[fileIndex]);
        		File file = files[fileIndex];
        		
        		//输入流使用buffer（没有指定buffer大小，后面可调整），性能提升3倍
        		FileInputStream input = new FileInputStream(file);
//        		ObjectInputStream ois = new ObjectInputStream(input);
        		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(input) );
        		
        		//先不考虑同步试试
        		BytesMessage message = (BytesMessage) ois.readObject();
        		while(message != null){
        			long l = System.currentTimeMillis();
        			
            		KeyValue headers = message.headers();
            		String queueOrTopic = null;
            		if((queueOrTopic = headers.getString(MessageHeader.QUEUE)) == null){
            			queueOrTopic = headers.getString(MessageHeader.TOPIC);
            		}
            		
            		//如果不做同步处理，偶尔有错（数据错误，或者空指针异常）
            		List<BytesMessage> list = null;
            		if(!data.containsKey(queueOrTopic)){
            			synchronized (obj) {
							if(!data.containsKey(queueOrTopic)){
								list = new ArrayList<BytesMessage>(10000);
								data.put(queueOrTopic, list);
							}else{
								list = data.get(queueOrTopic);
							}
						}
            		}else{
            			list = data.get(queueOrTopic);
            		}
            		 
            		//不对list做任何同步处理，只有前1/10的数据能对
            		synchronized(list){
            			list.add(message);
            		}
            		listTime += System.currentTimeMillis() - l; 
            		//因为不确定多少个对象，最后会读到一个空，必须捕捉异常
            		try{
            			message = (BytesMessage) ois.readObject();
            			totalTime += System.currentTimeMillis() - l; 
            		}catch (EOFException | ClassNotFoundException e){
//        				System.out.println("文件终止！");
            			//关闭io，删除该文件
            			ois.close();
            			file.delete();
            			
            			//已读文件数+1
            			finshed.incrementAndGet();
        				break;
        			}
        		}
//        		System.out.println(Thread.currentThread().getName()+" "+files[fileIndex].getName()+" 读取完毕！组装list耗时(ms): "+listTime+", 反序列化耗时(ms): "+(totalTime-listTime));
        		fileIndex = index.getAndIncrement();
        	}
        	/**
        	 * 所有读文件的线程都读入完了才能进入消费阶段。
        	 * 如果所有文件都被读了，但是其他线程某个文件还没读完，则自旋等待。
        	 * 已读文件数等于所有文件数时，代表已经读完
        	 */
        	int num = finshed.get();
        	while(num < fileNum){
        		//暂停当前线程，把执行机会让出来。
        		 Thread.yield();
        		num = finshed.get();
        	}
    	}catch(IOException | ClassNotFoundException e){
    		e.printStackTrace();
		}finally{
//			System.out.println(Thread.currentThread().getName()+"释放锁 :"+(new Date()));
//    		lock.unlock();
    	}
    }
}
