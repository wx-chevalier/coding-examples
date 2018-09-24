package io.openmessaging.demo.consumer;

import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.demo.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yfu on 4/30/17.
 */
public class ConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    private volatile static ConsumerService instance;

    private static final int MODE_INITIALIZING = 0;
    private static final int MODE_POLLING = 1;
    
    private volatile int mode = MODE_INITIALIZING;

    private Map<String, BlockingQueue<Message>> queues = new HashMap<>();
    private Map<String, Collection<BlockingQueue<Message>>> topics = new HashMap<>();
    
    private final String storePath;
    
    private DiskReader diskReader;
    private Thread diskReaderThread;
    
    public static ConsumerService getInstance(KeyValue keyValue) {
        if (instance == null) {
            String storePath = keyValue.getString("STORE_PATH");
            synchronized (ConsumerService.class) {
                if (instance == null) {
                    if (!storePath.endsWith("/")) {
                        storePath += "/";
                    }
                    instance = new ConsumerService(storePath);
                }
            }
        }
        return instance;
    }

    private ConsumerService(String storePath) {
        this.storePath = storePath;
    }

    // attachQueue takes queue name and topic names
    // returns a BlockingQueue where messages could be consumed from
    public synchronized BlockingQueue<Message> attachQueue(String queue, Collection<String> topics) {
        if (mode != MODE_INITIALIZING) {
            throw new RuntimeException("Cannot attach queue after polling started");
        }
        BlockingQueue<Message> blockingQueue = new ArrayBlockingQueue<>(Constants.CONSUMING_QUEUE_CAPACITY);
        queues.put(queue, blockingQueue);
        for (String topic: topics) {
            if (!this.topics.containsKey(topic)) {
                this.topics.put(topic, new ArrayList<>());
            }
            this.topics.get(topic).add(blockingQueue);
        }
        return blockingQueue;
    }
    
    public void startPolling() {
        if (mode == MODE_INITIALIZING) {
            synchronized (this) {
                if (mode == MODE_INITIALIZING) {
                    logger.info("Enter into MODE_POLLING");
                    mode = MODE_POLLING;
                    
                    // When entering MODE_POLLING, start the daemon thread
                    diskReader = new DiskReader(storePath, getPollingList());
                    diskReaderThread = new Thread(diskReader);
                    diskReaderThread.start();
                }
            }
        }
    }
    
    static class PollingListEntry {
        final String fileName;
        final Collection<BlockingQueue<Message>> blockingQueues;
        final String topicName;
        final String queueName;

        PollingListEntry(String fileName, Collection<BlockingQueue<Message>> blockingQueues, String topicName, String queueName) {
            this.fileName = fileName;
            this.blockingQueues = blockingQueues;
            this.topicName = topicName;
            this.queueName = queueName;
        }
    }
    
    // filename - several blockingQueues
    private List<PollingListEntry> getPollingList() {
        List<PollingListEntry> results = new ArrayList<>(topics.size() + queues.size());

        // TODO: sort results (topics) by attached queues
        for (Map.Entry<String, Collection<BlockingQueue<Message>>> entry : topics.entrySet()) {
            results.add(new PollingListEntry(entry.getKey(), entry.getValue(), entry.getKey(), null));
        }
        
        for (Map.Entry<String, BlockingQueue<Message>> entry : queues.entrySet()) {
            results.add(new PollingListEntry(entry.getKey(), Collections.singletonList(entry.getValue()), null, entry.getKey()));
        }
        return results;
    }
    
}
