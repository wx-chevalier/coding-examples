package io.openmessaging.demo.consumer;

import io.openmessaging.Message;
import io.openmessaging.demo.Constants;
import io.openmessaging.demo.serializer.MessageDeserializer;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Created by yfu on 4/30/17.
 */
class SegmentFile {
    private final Path path;
    private final FileChannel channel;
    private final MappedByteBuffer buffer;
    
    SegmentFile(Path path) {
        this.path = path;
        try {
            channel = FileChannel.open(path, StandardOpenOption.READ);
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to open FileChannel", ex);
        }
    }
    
    List<Message> readPayloads(MessageDeserializer messageDeserializer) {
        try {
            return messageDeserializer.read(buffer, Constants.MAX_BATCH_SIZE);
        } catch (BufferUnderflowException ex) {
            return null;
        }
    }
}
