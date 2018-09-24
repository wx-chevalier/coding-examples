package io.openmessaging.demo.producer;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.MessageHeader;
import io.openmessaging.demo.ClientOMSException;
import io.openmessaging.demo.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yfu on 4/29/17.
 */
public class ProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

    private volatile static ProducerService instance;
    
    private final List<DiskWriter> diskWriters;
    private final List<Thread> diskWriterThreads;

    private final AtomicInteger waitCount = new AtomicInteger();
    
    /*
    private static final String PATH_PREFIX_TOPIC = "topic/";
    private static final String PATH_PREFIX_QUEUE = "queue/";
    */
    
    public static ProducerService getInstance(KeyValue keyValue) {
        if (instance == null) {
            String storePath = keyValue.getString("STORE_PATH");
            synchronized (ProducerService.class) {
                if (instance == null) {
                    if (!storePath.endsWith("/")) {
                        storePath += "/";
                    }
                    instance = new ProducerService(storePath);
                }
            }
        }
        instance.waitCount.addAndGet(1);
        return instance;
    }
    
    private ProducerService(String storePath) {
        diskWriters = new ArrayList<>(Constants.PRODUCER_WRITE_THREADS);
        diskWriterThreads = new ArrayList<>(Constants.PRODUCER_WRITE_THREADS);
        
        for (int i = 0; i < Constants.PRODUCER_WRITE_THREADS; i++) {
            DiskWriter diskWriter = new DiskWriter(storePath);
            Thread diskWriterThread = new Thread(diskWriter);
            diskWriterThread.start();
            diskWriters.add(diskWriter);
            diskWriterThreads.add(diskWriterThread);
        }
    }
    
    public void send(Message message) throws InterruptedException {
        if (message == null) throw new ClientOMSException("Message should not be null");
        
        String topic = message.headers().getString(MessageHeader.TOPIC);
        String queue = message.headers().getString(MessageHeader.QUEUE);
        if ((topic == null && queue == null) || (topic != null && queue != null)) {
            throw new ClientOMSException(String.format("Queue:%s Topic:%s should put one and only one", true, queue));
        }
        
        String fileName = topic != null ? topic : queue;
        
        int writerIndex = fileName.hashCode() & 0x3; // when PRODUCER_WRITE_THREADS == 4
        diskWriters.get(writerIndex).writeAsync(fileName, message);
    }
    
    public void flush() throws InterruptedException {
        if (waitCount.addAndGet(-1) != 0) {
            return;
        }
        for (DiskWriter diskWriter: diskWriters) {
            diskWriter.finish();
        }
        for (Thread diskWriterThread: diskWriterThreads) {
            diskWriterThread.join();
        }
    }
}
