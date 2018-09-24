package io.openmessaging.v6;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.MessageHeader;
import io.openmessaging.PullConsumer;
import io.openmessaging.tester.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V6ConsumerTester {

    static Logger logger = LoggerFactory.getLogger(V6ConsumerTester.class);
    //0表示默认;
    static AtomicInteger state = new AtomicInteger(0);
    static String errorMessage = "";

    static class ConsumerTask extends Thread {
        String queue;
        List<String> topics;
        PullConsumer consumer;
        int pullNum;
        Map<String, Map<String, Integer>> offsets = new HashMap<>();
        public ConsumerTask(String queue, List<String> topics) {
            this.queue = queue;
            this.topics = topics;
            init();
        }

        public void init() {
            //init consumer
            try {
                Class kvClass = Class.forName("io.openmessaging.demo.DefaultKeyValue");
                KeyValue keyValue = (KeyValue) kvClass.newInstance();
                keyValue.put("STORE_PATH", Constants.STORE_PATH);
                Class consumerClass = Class.forName("io.openmessaging.v6.V6PullConsumerNoStr");
                consumer = (PullConsumer) consumerClass.getConstructor(new Class[]{KeyValue.class}).newInstance(new Object[]{keyValue});
                if (consumer == null) {
                    throw new InstantiationException("Init Producer Failed");
                }
                consumer.attachQueue(queue, topics);
            } catch (Exception e) {
                logger.error("please check the package name and class name:", e);
            }
            /**
             * init offsets，为了检验正确性和顺序。
             * key为本线程消费的queue/topic，
             * value为对应的线程和该线程发送的消息的偏移(key为线程名, value初始为0)。
             */
            offsets.put(queue, new HashMap<>());
            for (String topic: topics) {
                offsets.put(topic, new HashMap<>());
            }
            for (Map<String, Integer> map: offsets.values()) {
                for (int i = 0; i < Constants.PRO_NUM; i++) {
                	//"PRODUCER_i",即生产者线程名
                    map.put(Constants.PRO_PRE + i, 0);
                }
            }
        }
        long time  = 0;
        @Override
        public void run() {
        	
            while (true) {
                try {
                	long start = System.currentTimeMillis();
                    BytesMessage message = (BytesMessage) consumer.poll();
                    time += System.currentTimeMillis() - start;
                    if (message == null) {
                        break;
                    }
                    String queueOrTopic;
                    if (message.headers().getString(MessageHeader.QUEUE) != null) {
                        queueOrTopic = message.headers().getString(MessageHeader.QUEUE);
                    } else {
                        queueOrTopic = message.headers().getString(MessageHeader.TOPIC);
                    }
                    if (queueOrTopic == null || queueOrTopic.length() == 0) {
                        throw new Exception("Queue or Topic name is empty");
                    }
                    // 消息主体 ：“生产者线程名_偏移” 的字节，重新生成string
                    String body = new String(message.getBody());
                    int index = body.lastIndexOf("_");
                    //生产者线程名
                    String producer = body.substring(0, index);
                    //该线程的该消息对应的偏移
                    int offset = Integer.parseInt(body.substring(index + 1));
                    
                    /**
                     * 检测正确性和顺序。
                     * 每个获得的消息，根据queueOrTopic，再通过producer（生产者线程名），
                     * 就找到偏移（即等于之前的编译+1，因为发送时是累加的），一致则这个消息接受正确。
                     */
                    if (offset != offsets.get(queueOrTopic).get(producer)) {
                        logger.error("Offset not equal expected:{} actual:{} producer:{} queueOrTopic:{}",
                        		offsets.get(queueOrTopic).get(producer), offset, producer, queueOrTopic);
                        System.out.println("消费: "+pullNum);
                        break;
                    } else {
//                        logger.info("Offset  equal expected:{} actual:{} producer:{} queueOrTopic:{}",
//                        		offsets.get(queueOrTopic).get(producer), offset, producer, queueOrTopic);
                        offsets.get(queueOrTopic).put(producer, offset + 1);
                    }
                    pullNum++;
//                    System.out.println(Thread.currentThread().getName()+": "+pullNum);
                } catch (Exception e) {
                    logger.error("Error occurred in the consuming process", e);
                    break;
                }
            }
        }

        public int getPullNum() {
//        	System.out.println(Thread.currentThread().getName()+" 消费 耗时: "+time);
            return pullNum;
        }

    }

    public static void main(String[] args) throws Exception {
        Thread[] ts = new Thread[Constants.CON_NUM];
        long start = System.currentTimeMillis();
        //topic
        Random r= new Random(47);
        
        for (int i = 0; i < ts.length; i++) {
//            ts[i] = new ConsumerTask(Constants.QUEUE_PRE + i,Collections.singletonList(Constants.TOPIC_PRE + i));
            //多个topic
        	List<String> list = new ArrayList<String>();
            int t= r.nextInt(5);
            for(int j=0; j<t+2; j++){
            	String s = Constants.TOPIC_PRE + r.nextInt(100);
            	if(!list.contains(s)){
            		list.add(s);
            	}
            }
        	ts[i] = new ConsumerTask(Constants.QUEUE_PRE + i,list);
        }
        for (int i = 0; i < ts.length; i++) {
            ts[i].start();
        }
//        ts[9].start();

        for (int i = 0; i < ts.length; i++) {
            ts[i].join();
        }
        int pullNum = 0;
        for (int i = 0; i < ts.length; i++) {
            pullNum += ((ConsumerTask)ts[i]).getPullNum();
        }
        long end = System.currentTimeMillis();
        logger.info("Consumer Finished, Cost {} ms, Num {}", end - start, pullNum);
        //程序结束清理磁盘中存储的文件夹

    }
}
