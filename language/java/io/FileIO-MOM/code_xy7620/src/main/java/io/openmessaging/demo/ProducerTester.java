package io.openmessaging.demo;

import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.MessageHeader;
import io.openmessaging.Producer;
import io.openmessaging.tester.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerTester {

    static Logger logger = LoggerFactory.getLogger(ProducerTester.class);
    //0表示默认;
    static AtomicInteger state = new AtomicInteger(0);
    static String errorMessage = "";

    

    static class ProducerTask extends Thread {
        String label = Thread.currentThread().getName();
        Random random = new Random();
        Producer producer = null;
        int sendNum = 0;
        Map<String, Integer> offsets = new HashMap<>();
        public ProducerTask(String label) {
            this.label = label;
            init();
        }

        public void init() {
            //init producer
            try {
                Class kvClass = Class.forName("io.openmessaging.demo.DefaultKeyValue");
                KeyValue keyValue = (KeyValue) kvClass.newInstance();
                keyValue.put("STORE_PATH", Constants.STORE_PATH);
                Class producerClass = Class.forName("io.openmessaging.demo.DefaultProducer");
                producer = (Producer) producerClass.getConstructor(new Class[]{KeyValue.class}).newInstance(new Object[]{keyValue});
                if (producer == null) {
                    throw new InstantiationException("Init Producer Failed");
                }
            } catch (Exception e) {
                logger.error("please check the package name and class name:", e);
            }
            //init offsets,topic0-9,queue0-9
            for (int i = 0; i < 10; i++) {
//                offsets.put("TOPIC_" + i, 0);
                offsets.put("QUEUE_" + i, 0);
            }
            for (int i = 0; i < 100; i++) {
                offsets.put("TOPIC_" + i, 0);
            }

        }

        @Override
        public void run() {
            while (true) {
                try {
                    String queueOrTopic;
                    Message message;
                    if (sendNum % 10 == 0) {
                        queueOrTopic = "QUEUE_" + random.nextInt(10);
                        message = producer.createBytesMessageToQueue(queueOrTopic, (label + "_" + offsets.get(queueOrTopic)).getBytes());
                    } else {
                        queueOrTopic = "TOPIC_" + random.nextInt(100);
                        message = producer.createBytesMessageToTopic(queueOrTopic, (label + "_" + offsets.get(queueOrTopic)).getBytes());
                    }
                    //测试多个header，properties
                    message.putHeaders(MessageHeader.SHARDING_KEY, queueOrTopic);
                    message.putHeaders(MessageHeader.BORN_HOST, queueOrTopic);
                    message.putHeaders(MessageHeader.MESSAGE_ID, queueOrTopic);
                    message.putProperties(queueOrTopic, queueOrTopic);
                    message.putProperties(queueOrTopic+sendNum, queueOrTopic);
                    /**
                     * 消息主体 ：“生产者线程名_偏移” 的字节.
                     * 对于label（线程名）和queue/topic的组合来说来说，偏移是一直累加的，0、1、2、3……    
                     * 即每个线程对于每个queue/topic的消息中偏移都是一个连续的累加的。
                     */
//                    Message message = producer.createBytesMessageToQueue(queueOrTopic, (label + "_" + offsets.get(queueOrTopic)).getBytes());
//                    logger.debug("queueOrTopic:{} offset:{}", queueOrTopic, label + "_" + offsets.get(queueOrTopic));
                    //每产生一个消息，该bucket对应的便偏移+1
                    offsets.put(queueOrTopic, offsets.get(queueOrTopic) + 1);
                    producer.send(message);
                    sendNum++;
                    if(sendNum == 3000000){
                    	System.out.println();
                    }
                    if (sendNum >= Constants.PRO_MAX) {
                    	//自己测试时记得关闭，不然没法删除持有的文件。实测时kill进程，没有这个问题
//                    	if(sendNum%20000 == 0){
//                    		System.out.println(Thread.currentThread().getName()+" 发送："+sendNum);
//                    		String log =Thread.currentThread().getName()+" 发送："+sendNum;
//                    		logger.info(log);
//                    	}
                    	producer.flush();
                        break;
                    }
                } catch (Exception e) {
                    logger.error("Error occurred in the sending process", e);
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //程序开始清理磁盘中存储的文件夹
        String path = Constants.STORE_PATH;
        File file = new File(path);
        if(!file.isDirectory()){
        	file.mkdir();
        }
        System.out.println("start!");
        File[] files = file.listFiles();
        for(int i=0; i<files.length; i++){
        	files[i].delete();
        }
        Thread[] ts = new Thread[Constants.PRO_NUM];
        for (int i = 0; i < ts.length; i++) {
        	//线程名为 "PRODUCER_i"
            ts[i] = new ProducerTask(Constants.PRO_PRE + i);
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < ts.length; i++) {
            ts[i].start();
        }
        for (int i = 0; i < ts.length; i++) {
            ts[i].join();
        }
        long end = System.currentTimeMillis();
        logger.info("Produce Finished, Cost {} ms", end - start);
        System.out.println("Produce Finished, Cost ms:  "+( end - start)+"  num:  "+Constants.PRO_MAX*ts.length);
    }
}
