package io.openmessaging.demo.producer;

import io.openmessaging.Message;
import io.openmessaging.demo.Constants;
import io.openmessaging.demo.serializer.MessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

class SegmentFile {
    private static final Logger logger = LoggerFactory.getLogger(SegmentFile.class);
    
    private final Path path;
    private final ArrayBlockingQueue<Message> queue;
    private final FileChannel channel;
    private MappedByteBuffer buffer;
    
    private long currentMapStart = 0;
    private long currentMapEnd = Constants.FILE_ALLOCATE_SIZE;

    SegmentFile(Path path) {
        // Ensure the parent directory exists
        path.getParent().toFile().mkdirs();

        this.path = path;
        this.queue = new ArrayBlockingQueue<>(Constants.MAX_BATCH_SIZE);
        try {
            // TODO: try StandardOpenOption.APPEND
            channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
            buffer = channel.map(FileChannel.MapMode.READ_WRITE, currentMapStart, currentMapEnd);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to open FileChannel", ex);
        }
    }

    void putQueue(Message message) throws InterruptedException {
        queue.put(message);
    }

    // writeDisk cannot be called in multi-threads
    void writeDisk(MessageSerializer messageSerializer) {
        List<Message> payloads = new ArrayList<>(queue.size() + 10);

        final int count = queue.drainTo(payloads);
        logger.debug("Pop payloads from writing queue  count={} file=\"{}\"", count, path.toString());

        final int position = buffer.position();
        try {
            messageSerializer.write(buffer, payloads);
        } catch (BufferOverflowException ex) {
            buffer.position(position);
            logger.warn("Preallocated buffer size not enough");
            allocateNextBlock();
        }

        logger.debug("Wrote payload into mmap buffer  size={} file=\"{}\"", buffer.position() - position, path.toString());

        if (buffer.remaining() < Constants.FILE_ALLOCATE_MARGIN) {
            allocateNextBlock();
        }
    }

    private void allocateNextBlock() {
        final int position = buffer.position();
        final int droppedPages = position / 4096;
        final int newPosition = position % 4096;

        currentMapStart += droppedPages * 4096;
        currentMapEnd += Constants.FILE_ALLOCATE_SIZE;

        try {
            buffer = channel.map(FileChannel.MapMode.READ_WRITE, currentMapStart, currentMapEnd - currentMapStart);
            buffer.position(newPosition);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to reallocate mmap buffer", ex);
        }
    }

    int getQueueSize() {
        return queue.size();
    }

    int getQueueCapacity() {
        return Constants.MAX_BATCH_SIZE;
    }
}