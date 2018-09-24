package io.openmessaging.demo.producer;

import io.openmessaging.Message;
import io.openmessaging.demo.serializer.MessageSerializer;
import io.openmessaging.demo.serializer.SimpleBinarySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by yfu on 4/29/17.
 */
public class DiskWriter implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DiskWriter.class);

    private final String storePath;
    private final ConcurrentMap<String, SegmentFile> files = new ConcurrentHashMap<>();
    private final MessageSerializer messageSerializer;
    
    private volatile boolean closing = false;

    DiskWriter(String storePath) {
        this.storePath = storePath;

        // TODO: Add more compressing method
        messageSerializer = new SimpleBinarySerializer();
    }

    @Override
    public void run() {
        logger.info("DiskWriter thread is running");
        boolean written;
        do {
            written = false;
            int maxQueueSize = 0;
            SegmentFile maxQueueFile = null;
            for (Map.Entry<String, SegmentFile> entry : files.entrySet()) {
                SegmentFile file = entry.getValue();
                final int queueSize = file.getQueueSize();
                if (queueSize == file.getQueueCapacity()) {
                    file.writeDisk(messageSerializer);
                    written = true;
                } else if (queueSize > maxQueueSize) {
                    maxQueueFile = file;
                    maxQueueSize = queueSize;
                }
            }
            if (!written && maxQueueFile != null) {
                maxQueueFile.writeDisk(messageSerializer);
                written = true;
            }
        } while (!closing || written);
    }

    void writeAsync(String fileName, Message message) throws InterruptedException {
        if (!files.containsKey(fileName)) {
            synchronized (this) {
                if (!files.containsKey(fileName)) {
                    logger.info("New file created  name={}", fileName);
                    SegmentFile file = new SegmentFile(Paths.get(storePath, fileName));
                    files.put(fileName, file);
                }
            }
        }
        
        files.get(fileName).putQueue(message);

        logger.debug("Put payload into queue  file={}", fileName);
    }
    
    void finish() {
        closing = true;
    }
}
