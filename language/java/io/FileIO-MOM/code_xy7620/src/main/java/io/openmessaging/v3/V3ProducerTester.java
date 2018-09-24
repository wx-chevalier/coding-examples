package io.openmessaging.v3;

import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.Producer;
import io.openmessaging.tester.Constants;
import io.openmessaging.v2.V2ProducerTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 瑞 on 2017/5/10.
 */
public class V3ProducerTester {
    static Logger logger= LoggerFactory.getLogger(V3ProducerTester.class);
    //0 表示默认
    static AtomicInteger state=new AtomicInteger(0);
    static String errorMessage="";

    static class ProducerTask extends Thread{
        String label=Thread.currentThread().getName();
        Random random=new Random();
        Producer producer=null;
        int sendNum=0;
        Map<String,Integer> offsets=new HashMap<>();

        public ProducerTask(String label){
            this.label=label;
            init();
        }

        public void init(){
            try {
                Class kvClass=Class.forName("io.openmessaging.demo.DefaultKeyValue");
                KeyValue keyValue= (KeyValue) kvClass.newInstance();
                keyValue.put("STORE_PATH", Constants.STORE_PATH);
                Class producerClass=Class.forName("io.openmessaging.v3.V3Producer");
                producer = (Producer) producerClass.getConstructor(new Class[]{KeyValue.class}).newInstance(new Object[]{keyValue});
                if (producer==null){
                    throw new InstantiationException("Init Producer Fail");
                }
            } catch (Exception e) {
                logger.error("please check the package name and class name:", e);
            }
            //init offsets,topic0-9,queue0-9
            for (int i = 0; i < 10; i++) {
                offsets.put("TOPIC_" + i, 0);
                offsets.put("QUEUE_" + i, 0);
            }
        }

        public void run(){
            String queueOrTopic;
            Message message;
            while (true){
                if (sendNum%10==0){
                    queueOrTopic="QUEUE"+"_"+random.nextInt(10);
                    message=producer.createBytesMessageToQueue(queueOrTopic,(label+queueOrTopic+" "+offsets.get(queueOrTopic)).getBytes());
                }else {
                    queueOrTopic="TOPIC"+"_"+random.nextInt(10);
                    message=producer.createBytesMessageToTopic(queueOrTopic,(label+queueOrTopic+" "+offsets.get(queueOrTopic)).getBytes());
                }
                /**
                 * 消息主体 ：“生产者线程名_偏移” 的字节.
                 * 对于label（线程名）和queue/topic的组合来说来说，偏移是一直累加的，0、1、2、3……
                 * 即每个线程对于每个queue/topic的消息中偏移都是一个连续的累加的。
                 */
                logger.debug("queueOrTopic:{} offset:{}", queueOrTopic, label + "_" + offsets.get(queueOrTopic));
                offsets.put(queueOrTopic, offsets.get(queueOrTopic) + 1);
                producer.send(message);
                sendNum++;
                if (sendNum >= Constants.PRO_MAX) {
                    //自己测试时记得关闭，不然没法删除持有的文件。实测时kill进程，没有这个问题
                    producer.shutdown();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //程序开始清理磁盘中存储的文件夹
        String path = Constants.STORE_PATH;
//        File file = new File(path);
//        File[] files = file.listFiles();
//        for(int i=0; i<files.length; i++){
//            files[i].delete();
//        }
        Thread[] ts = new Thread[Constants.PRO_NUM];
        for (int i = 0; i < ts.length; i++) {
            //线程名为 "PRODUCER_i"
            ts[i] = new V3ProducerTester.ProducerTask(Constants.PRO_PRE + i);
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
    }
}
