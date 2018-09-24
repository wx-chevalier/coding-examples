package io.openmessaging.demo.serializer;

import io.openmessaging.Message;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by yfu on 4/29/17.
 */
public interface MessageDeserializer {
    // read a list of payloads from buffer
    // throws BufferUnderflowException if excepting data but got EOF
    List<Message> read(ByteBuffer buffer, int count) throws BufferUnderflowException;
}
