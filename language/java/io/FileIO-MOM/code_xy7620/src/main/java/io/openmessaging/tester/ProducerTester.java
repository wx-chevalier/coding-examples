package io.openmessaging.tester;

import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.Producer;
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
            //init offsets
            for (int i = 0; i < 10; i++) {
                offsets.put("TOPIC_" + i, 0);
                offsets.put("QUEUE_" + i, 0);
            }

        }

        @Override
        public void run() {
            while (true) {
                try {
                    String queueOrTopic;
                    //queue和topic数量为1:9，都只有10种类型
                    if (sendNum % 10 == 0) {
                        queueOrTopic = "QUEUE_" + random.nextInt(10);
                    } else {
                        queueOrTopic = "TOPIC_" + random.nextInt(10);
                    }
                    Message message = producer.createBytesMessageToQueue(queueOrTopic, (label + "_" + offsets.get(queueOrTopic)).getBytes());
                    logger.debug("queueOrTopic:{} offset:{}", queueOrTopic, label + "_" + offsets.get(queueOrTopic));
                    offsets.put(queueOrTopic, offsets.get(queueOrTopic) + 1);
                    producer.send(message);
                    sendNum++;
                    if (sendNum >= Constants.PRO_MAX) {
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
        Thread[] ts = new Thread[Constants.PRO_NUM];
        for (int i = 0; i < ts.length; i++) {
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
    }
}
