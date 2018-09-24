package io.openmessaging.demo;

import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.PullConsumer;
import io.openmessaging.demo.consumer.ConsumerService;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class DefaultPullConsumer implements PullConsumer {
    private KeyValue properties;
    private BlockingQueue<Message> blockingQueue;
    private ConsumerService consumerService;
    private boolean pollingStarted = false;

    public DefaultPullConsumer(KeyValue properties) {
        this.properties = properties;
        this.consumerService = ConsumerService.getInstance(properties);
    }

    @Override public KeyValue properties() {
        return properties;
    }

    @Override public synchronized Message poll() {
        if (blockingQueue == null) {
            return null;
        }
        if (!pollingStarted) {
            pollingStarted = true;
            consumerService.startPolling();
        }
        try {
            // Workaround here: use an message with null body as a termination signal
            DefaultBytesMessage message = (DefaultBytesMessage) blockingQueue.take();
            if (message.getBody() == null) return null;
            return message;
        } catch (InterruptedException ex) {
            throw new RuntimeException("Interrupted when polling messages", ex);
        }
    }

    @Override public Message poll(KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void ack(String messageId) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void ack(String messageId, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public synchronized void attachQueue(String queueName, Collection<String> topics) {
        if (blockingQueue != null) {
            throw new ClientOMSException("You have already attached to a queue");
        }
        blockingQueue = consumerService.attachQueue(queueName, topics);
    }
}
