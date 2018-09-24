package io.openmessaging.v6;

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

public class V6ProducerTester {

    static Logger logger = LoggerFactory.getLogger(V6ProducerTester.class);
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
                Class producerClass = Class.forName("io.openmessaging.v6.V6Producer");
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
        Random r = new Random(47);
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
                    message.putProperties("PRO_OFFSET", "PRODUCER5_719");
                    message.putProperties("jdljknw", "ys0yz2b");
                    message.putProperties("jdlrvo9", "ys06a3l");
                    
                    if(sendNum % 10 == 0 && sendNum < 500){
                    	message.putHeaders(MessageHeader.MESSAGE_ID, "1828oz6vk46cuupruzgmdh1okq55i7sh5wozy3sejk47vsv2xt4rh9aq5cjevpcv9m0893a7mlop27y1mqkltvy4f2yl1jjbz0ce2p8shsep31avbar64p3j8nw5vaplkqyr8w1qo47ljxa1pp1nqrlmqgfettpc82d2ox2b3fh45w7ynui18v12q9u4ow4lrzafdr6rar2ae5lnjr3aizrr09qnn75r4cjmcy3to5hgtcvcw2m7if7so2tvnvpblaincanybhiwm5i8agt9idsh6al5vvo8c45atw2p5293e0d340iyebnremgbsr5f3wen8btzw6qhoqybkozgt0724ag2lbh740gykvwvibyitazlka4yz4u9cloey809xl4bd0jasuzicd83h3v7xo5s9ogj4tlcwb8ycr5gi27uxua5jj99qn3t18pm62hpmadrxrzg0d5oldd6mdruzpetuupl8wo8710rf48qapptr6dri3g4v27mdp2hz2x0ba4ce7pv4nndg1h337bemrdsjn77nw91eztzujyz5zgvvqnh678xflfuikpjngg04g9wbj705vvn9bxgq4a4n0pr29r6a79k70iqi34o7aovogwek3u7jvjlia2p1e5rrqdo6e8r1q7ujrn3dtphvxe0pjlobddygbsummbdk0spjh734h3mqs78ak45uanzt8t6av9k8vicxpzb4dh1vj0t7xvrj7882nsyn1w0is5u3ymx9sv2pjkyj0pcisqnsllo080bifzbi9q1m82x355uvcwqa2ohjb62l28yh2uacp3nnjevsf7e999h1m1yfv4fzrwkxls7lq0aiv6borgy1xd7g93p8lxrnxtspi24os2rndvy5x6nu67t8fxnenb2se3j31o3v6dphh3xo9xvkkjjmlog3o18w977f34g8crw7dfimwzwiybdscaputs1ols31ujy34bwg529yqroajttca4mnzlztagwc");
                    	message.putHeaders(MessageHeader.START_TIME, queueOrTopic);
                    	message.putProperties("PRO_OFF", "1828oz6vk46cuupruzgmdh1okq55i7sh5wozy3sejk47vsv2xt4rh9aq5cjevpcv9m0893a7mlop27y1mqkltvy4f2yl1jjbz0ce2p8shsep31avbar64p3j8nw5vaplkqyr8w1qo47ljxa1pp1nqrlmqgfettpc82d2ox2b3fh45w7ynui18v12q9u4ow4lrzafdr6rar2ae5lnjr3aizrr09qnn75r4cjmcy3to5hgtcvcw2m7if7so2tvnvpblaincanybhiwm5i8agt9idsh6al5vvo8c45atw2p5293e0d340iyebnremgbsr5f3wen8btzw6qhoqybkozgt0724ag2lbh740gykvwvibyitazlka4yz4u9cloey809xl4bd0jasuzicd83h3v7xo5s9ogj4tlcwb8ycr5gi27uxua5jj99qn3t18pm62hpmadrxrzg0d5oldd6mdruzpetuupl8wo8710rf48qapptr6dri3g4v27mdp2hz2x0ba4ce7pv4nndg1h337bemrdsjn77nw91eztzujyz5zgvvqnh678xflfuikpjngg04g9wbj705vvn9bxgq4a4n0pr29r6a79k70iqi34o7aovogwek3u7jvjlia2p1e5rrqdo6e8r1q7ujrn3dtphvxe0pjlobddygbsummbdk0spjh734h3mqs78ak45uanzt8t6av9k8vicxpzb4dh1vj0t7xvrj7882nsyn1w0is5u3ymx9sv2pjkyj0pcisqnsllo080bifzbi9q1m82x355uvcwqa2ohjb62l28yh2uacp3nnjevsf7e999h1m1yfv4fzrwkxls7lq0aiv6borgy1xd7g93p8lxrnxtspi24os2rndvy5x6nu67t8fxnenb2se3j31o3v6dphh3xo9xvkkjjmlog3o18w977f34g8crw7dfimwzwiybdscaputs1ols31ujy34bwg529yqroajttca4mnzlztagwc");
                    }
                    
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
