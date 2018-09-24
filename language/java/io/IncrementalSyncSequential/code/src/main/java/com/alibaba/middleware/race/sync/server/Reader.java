package com.alibaba.middleware.race.sync.server;

import com.alibaba.middleware.race.sync.Constants;
import com.alibaba.middleware.race.sync.utils.Logger;
import com.lmax.disruptor.RingBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Load file segment into byte[] from disk
 * <p>
 * Created by yfu on 6/20/17.
 */
public class Reader implements Runnable {
    private static final Logger logger = Logger.SERVER_LOGGER;

    private final RingBuffer<Segment> taskQueue;

    public Reader(RingBuffer<Segment> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        logger.info("started");
        try {
            doRun();
        } catch (Throwable ex) {
            logger.error("Exception caught", ex);
        }
        logger.info("exited");
    }

    private int fileNumber;
    private FileChannel fileChannel;

    private void doRun() {
        nextFile();

        int segmentNo = 0;
        while (true) {
            long sequence = taskQueue.next();
            Segment segment = taskQueue.get(sequence);
            try {
                boolean hasNext = fillSegment(segment);

                if (!hasNext) break;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                segment.no = segmentNo++;
                taskQueue.publish(sequence);
            }
        }

        long sequence = taskQueue.next();
        Segment segment = taskQueue.get(sequence);
        segment.size = -1; // Done
        taskQueue.publish(sequence);
    }

    private boolean fillSegment(Segment segment) throws IOException {
        segment.size = 0;
        segment.isBegin = fileChannel.position() == 0L;
        while (segment.size < Constants.SEGMENT_SIZE) {
            long numBytes;
            if (fileChannel.position() < fileChannel.size()) {
                long remaining = fileChannel.size() - fileChannel.position();
                numBytes = Math.min(Constants.SEGMENT_SIZE_WITH_MARGIN - segment.size, remaining);
                MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, fileChannel.position(), numBytes);
                fileChannel.position(fileChannel.position() + numBytes);
                segment.mappedBuffers.add(buffer);
            } else {
                numBytes = -1;
            }
            if (numBytes == -1) {
                boolean hasNext = nextFile();
                if (hasNext) continue;
                else return false;
            }
            segment.size += numBytes;
        }
        int margin = segment.size - Constants.SEGMENT_SIZE;
        fileChannel.position(fileChannel.position() - margin);
        return true;
    }

    private boolean nextFile() {
        if (fileNumber == 10) {
            return false;
        }
        String fileName = String.format("%d.txt", ++fileNumber);
        try {
            Path path = Paths.get(Constants.DATA_HOME, fileName);
            fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to open " + fileName, ex);
        }
        logger.info("Processing file %s", fileName);
        return true;
    }
}
