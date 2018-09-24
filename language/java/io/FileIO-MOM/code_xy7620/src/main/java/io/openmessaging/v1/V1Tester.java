package io.openmessaging.v1;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.MessageHeader;
import io.openmessaging.Producer;
import io.openmessaging.PullConsumer;
import io.openmessaging.demo.DefaultBytesMessage;
import io.openmessaging.demo.DefaultKeyValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;

/**
 * @author XF version1<p>
 * 自己实现的测试类，比赛时会有其他测试类。
 * <p>应该都是调用DefaultProducer与DefaultPullConsumer 及其他接口方法。
 */
public class V1Tester {


    public static void main(String[] args) {
        final KeyValue properties = new DefaultKeyValue();
//      properties.put("STORE_PATH", "/home/admin/test"); //实际测时利用 STORE_PATH 传入存储路径,进行持久化
        properties.put("STORE_PATH", "src/main/resources/store");
        String store = properties.getString("STORE_PATH");
        File file = new File(store);
        if(!file.isDirectory()){
        	file.mkdirs();
        }

        //参数
        final int bucketNum = 200;
        final int queueNum = 100;
        final int messageNum = 300000;
        final int threadNum = 20;
        
        //构造测试数据 message header。
        final List<String> bucketList = new ArrayList<String>(bucketNum);
        for(int i=0; i<bucketNum/2; i++){
        	bucketList.add("QUEUE"+i);
        	bucketList.add("TOPIC"+i);
        }
        
        //如果要验证消息顺序以及正确性，那么应该由测试程序将每个线程的发送消息存下来。
        final HashMap<String, List<BytesMessage>> originalData= new HashMap<String, List<BytesMessage>>(threadNum*4/3+1);
        
        //预防多线程同时写，先将key存下来。如果使用线程池，名字是固定的pool-1-thread-i
        for(int i=0; i<threadNum;i++){
        	originalData.put("Thread"+i, new ArrayList<BytesMessage>());
        }
        
        long start = System.currentTimeMillis();
        //这个测试程序的测试逻辑与实际评测相似，但注意这里是单线程的，实际测试时会是多线程的，并且发送完之后会Kill进程，再起消费逻辑
        ExecutorService pool = Executors.newFixedThreadPool(threadNum);
        for(int i=0; i<threadNum; i++){
        	
        	Runnable r = new Runnable(){
        		@Override 
        		public void run() {
        			//操作的都是测试例中originalData，防止生产者线程被中断
        			String name = Thread.currentThread().getName();
        			List<BytesMessage> messageList = originalData.get(name);
        			if(messageList == null){
        				messageList = new ArrayList<BytesMessage>(messageNum);
        			}
        			originalData.put(name, messageList);
        			Random r = new Random(47);
        			Producer producer = new V1Producer(properties);
        			
        			//构造测试数据 消息主体字符串
    				String str = "kljsalkjfoashnujiohniozhjxocifgjasijf";
    				StringBuilder sb =new StringBuilder();
    				for(int i=0; i<100; i++){
    					sb.append(str);
    				}
    				String s = sb.toString();
    				s = "";
        			/**
        			 * 构造测试数据 每个线程的message对象。（每个线程的所有消息主体应该是独立的，不考虑同步）
        			 * 发送消息。会强行kill Producer进程。推测消息会很多，中途中断
        			 */    				
        			for(int j=0; j<messageNum; j++){
        				//检测中断标志位，手动退出。后面程序应有原子性。
        				if(Thread.interrupted() == true) {
        					System.out.println(Thread.currentThread().getName()+"中断,发送消息："+j);
        					return;
        				}
        				//消息主体
        				String body = name+" message "+j+s;
        				int index = r.nextInt(bucketNum);
        				BytesMessage bucket = null; 
        				if(index%2 == 0){
        					bucket =  producer.createBytesMessageToQueue(bucketList.get(index), body.getBytes());
        				}else{
        					bucket =producer.createBytesMessageToTopic(bucketList.get(index), body.getBytes());
        				}
    					messageList.add(bucket);
    					//发送消息
    					producer.send(bucket);
        			}
        			System.out.println(Thread.currentThread().getName()+"所有消息发送完毕！即中断失败！");
        		}
        	};
        	//使用线程池，对线程命名无效。名字是固定的pool-1-thread-i
        	pool.execute(new Thread(r, "Thread"+i));
        }
        
        //T1时间后，kill所有生产者线程
        long end = System.currentTimeMillis();
        long T1 = end - start;
        while(T1 < 10000){
        	T1 = System.currentTimeMillis() - start;
        }
        
        //这样中断，需要在程序中检测中断标志位来中断。因为程序中没有sleep/wait等
        pool.shutdownNow();
        
        //将原始数据，按queue/topic组装.这里为为了测试，实际测试怎样做并不影响，因为这段不计入时间。
        HashMap<String, List<BytesMessage>> adjustedData= new HashMap<String, List<BytesMessage>>(bucketNum*4/3+1); 
        Iterator<String> it = originalData.keySet().iterator();
        while(it.hasNext()){
        	String key = it.next();
        	List<BytesMessage> list = originalData.get(key);
        	for(int i=0; i<list.size(); i++){
        		BytesMessage message = list.get(i);
        		KeyValue header = message.headers();
        		String bucket = header.getString(MessageHeader.TOPIC);
        		if(bucket == null){
        			bucket = header.getString(MessageHeader.QUEUE);
        		}
        		List<BytesMessage> buckets = adjustedData.get(bucket);
        		if(buckets == null){
        			buckets = new ArrayList<BytesMessage>();
        			adjustedData.put(bucket, buckets);
        		}
        		buckets.add(message);
        	}
        }
        
        //T2计时开始，启动消费者线程
        start = System.currentTimeMillis();
        
        
        for(int i=0; i<queueNum; i++){
        	
        }
    


    }
}
