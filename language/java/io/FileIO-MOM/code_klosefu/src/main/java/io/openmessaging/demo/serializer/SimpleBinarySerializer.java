package io.openmessaging.demo.serializer;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.demo.DefaultBytesMessage;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by yfu on 5/21/17.
 */
public final class SimpleBinarySerializer implements MessageSerializer, MessageDeserializer {

    private final static int MAX_MESSAGE_LENGTH = 100 * 1024;

    private final byte[] buf = new byte[MAX_MESSAGE_LENGTH];

    @Override
    public void write(ByteBuffer buffer, List<Message> messages) throws BufferOverflowException {
        for (Message message : messages) {
            write(buffer, message);
        }
    }

    private void write(ByteBuffer buffer, Message message) throws BufferOverflowException {
        // TODO: only support BytesMessage currently
        byte[] body = ((BytesMessage) message).getBody();
        buffer.putInt(body.length);
        buffer.put(body);

        write(buffer, message.headers());
        write(buffer, message.properties());
    }

    private void write(ByteBuffer buffer, KeyValue keyValue) throws BufferOverflowException {
        if (keyValue == null) {
            buffer.putInt(0);
            return;
        }
        Set<String> keySet = keyValue.keySet();
        buffer.putInt(keySet.size());
        for (String key : keySet) {
            write(buffer, key);
            // TODO: only support String currently
            write(buffer, keyValue.getString(key));
        }
    }

    private void write(ByteBuffer buffer, String string) {
        byte[] valueBytes = string.getBytes();
        buffer.putInt(valueBytes.length);
        buffer.put(valueBytes);
    }

    @Override
    public List<Message> read(ByteBuffer buffer, int count) throws BufferUnderflowException {
        List<Message> messages = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Message message = readMessage(buffer);
            if (message == null) break;
            messages.add(message);
        }
        return messages;
    }

    private Message readMessage(ByteBuffer buffer) throws BufferUnderflowException {
        int bodySize = buffer.getInt();
        if (bodySize == 0) return null;
        
        byte[] body = new byte[bodySize];
        buffer.get(body);
        BytesMessage message = new DefaultBytesMessage(body);

        int numHeaders = buffer.getInt();
        for (int i = 0; i < numHeaders; i++) {
            String key = readString(buffer);
            String value = readString(buffer);
            message.putHeaders(key, value);
        }

        int numProperties = buffer.getInt();
        for (int i = 0; i < numProperties; i++) {
            String key = readString(buffer);
            String value = readString(buffer);
            message.putProperties(key, value);
        }
        return message;
    }

    private String readString(ByteBuffer buffer) throws BufferUnderflowException {
        int length = buffer.getInt();
        buffer.get(buf, 0, length);
        return new String(buf, 0, length);
    }
}
