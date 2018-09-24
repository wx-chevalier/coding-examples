package io.openmessaging.demo;

import io.openmessaging.BytesMessage;
import io.openmessaging.MessageFactory;
import io.openmessaging.MessageHeader;

/**
 * 实现MessageFactory接口，通过topic/queue和消息主体body创建消息对象并返回
 * 创建消息
 */
public class DefaultMessageFactory implements MessageFactory {

	DefaultBytesMessage defaultBytesMessage = new DefaultBytesMessage(null);
	
    @Override public BytesMessage createBytesMessageToTopic(String topic, byte[] body) {
        DefaultBytesMessage defaultBytesMessage = new DefaultBytesMessage(body);
        defaultBytesMessage.putHeaders(MessageHeader.TOPIC, topic);
        return defaultBytesMessage;
    }

    @Override public BytesMessage createBytesMessageToQueue(String queue, byte[] body) {
        DefaultBytesMessage defaultBytesMessage = new DefaultBytesMessage(body);
        defaultBytesMessage.putHeaders(MessageHeader.QUEUE, queue);
        return defaultBytesMessage;
    }
    
    public BytesMessage changeBytesMessageToTopic(String topic, byte[] body) {
    	defaultBytesMessage.setBody(body);
    	defaultBytesMessage.clean();
        defaultBytesMessage.putHeaders(MessageHeader.TOPIC, topic);
        return defaultBytesMessage;
    }
    
    public BytesMessage changeBytesMessageToQueue(String queue, byte[] body) {
    	defaultBytesMessage.setBody(body);
    	defaultBytesMessage.clean();
        defaultBytesMessage.putHeaders(MessageHeader.QUEUE, queue);
        return defaultBytesMessage;
    }
}
