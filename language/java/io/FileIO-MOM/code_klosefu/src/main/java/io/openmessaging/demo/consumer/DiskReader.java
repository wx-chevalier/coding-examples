package io.openmessaging.demo.consumer;

import io.openmessaging.Message;
import io.openmessaging.MessageFactory;
import io.openmessaging.demo.DefaultBytesMessage;
import io.openmessaging.demo.DefaultMessageFactory;
import io.openmessaging.demo.serializer.MessageDeserializer;
import io.openmessaging.demo.serializer.SimpleBinarySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yfu on 4/30/17.
 */
public class DiskReader implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DiskReader.class);

    private final MessageFactory messageFactory;
    private final MessageDeserializer messageDeserializer;

    private final List<ConsumerService.PollingListEntry> pollingList;
    private final String storePath;

    @Override
    public void run() {
        logger.info("DiskReader thread is running");

        try {
            for (ConsumerService.PollingListEntry entry : pollingList) {
                SegmentFile segmentFile = new SegmentFile(Paths.get(storePath, entry.fileName));
                List<Message> messages;
                while (!(messages = segmentFile.readPayloads(messageDeserializer)).isEmpty()) {
                    for (Message message : messages) {
                        for (BlockingQueue<Message> bq : entry.blockingQueues) {
                            // Assuming the receiver won't block
                            bq.put(message);
                        }
                    }
                }

                if (entry.queueName != null) {
                    BlockingQueue<Message> bq = entry.blockingQueues.iterator().next();
                    // a message with null payloads means termination
                    bq.put(new DefaultBytesMessage(null));
                }
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted when polling messages", ex);
        }

        logger.info("DiskReader thread exit");
    }

    DiskReader(String storePath, List<ConsumerService.PollingListEntry> pollingList) {
        this.storePath = storePath;
        this.pollingList = pollingList;

        messageDeserializer = new SimpleBinarySerializer();
        messageFactory = new DefaultMessageFactory();
    }
}
