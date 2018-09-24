package io.openmessaging.demo.serializer;

import io.openmessaging.Message;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by yfu on 4/29/17.
 */
public interface MessageSerializer {
    // write a list of payloads into buffer
    // throws BufferOverflowException if buffer is overflowed
    void write(ByteBuffer buffer, List<Message> messages) throws BufferOverflowException;
}
