package com.alibaba.middleware.race.sync.server;

import com.alibaba.middleware.race.sync.Constants;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed-length String Store (multi-thread)
 * <p>
 * Created by yfu on 6/12/17.
 */
public class StringStore {

    static private final FileChannel channel;
    static private final AtomicInteger allocated = new AtomicInteger(0); // Java only support buffer at most 2GB
    static private volatile MappedByteBuffer fullMapped;

    public static Unsafe unsafe;

    static {
        try {
            unsafe = getUnsafe();
        } catch (Exception ex) {}
    }

    private static final long BYTE_ARRAY_BASE_OFFSET = unsafe.arrayBaseOffset(byte[].class);
    
    static {
        Path path = Paths.get(Constants.MIDDLE_HOME, "strings.bin");
        try {
            channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private MappedByteBuffer fileBuffer;
    private int fileOffset;

    public StringStore() {
        allocateBuffer();
    }

    public long put(byte[] data, int len) {
        if (len <= 7) {
            long value = unsafe.getLong(data, BYTE_ARRAY_BASE_OFFSET);
            return uint64(len) << 56 | (value & 0xffffffffffffffL);
        } else {
            long pos = doPut(data, len);
            return 0xffL << 56 | pos << 32 | len;
        }
    }

    public static void get(long value, ByteBuffer buf) {
        int h = (int) (value >>> 56);
        if (h != 0xff) {
            buf.putLong(value);
            buf.position(buf.position() - 8 + h);
        } else {
            int pos = (int) ((value & 0xffffff00000000L) >>> 32);
            int len = (int) (value & 0xffffffffL);
            doGet(pos, len, buf);
        }
    }

    private int doPut(byte[] data, int len) {
        if (fileBuffer.remaining() < len) {
            allocateBuffer();
        }
        int pos = fileOffset + fileBuffer.position();
        fileBuffer.put(data, 0, len);
        return pos;
    }

    private static void doGet(int pos, int len, ByteBuffer buf) {
        if (fullMapped == null) {
            synchronized (channel) {
                if (fullMapped == null) {
                    try {
                        fullMapped = channel.map(FileChannel.MapMode.READ_ONLY, 0, allocated.get());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        ByteBuffer slice = (ByteBuffer) fullMapped.duplicate().position(pos);
        int bufPos = buf.position();
        slice.get(buf.array(), bufPos, len);
        buf.position(bufPos + len);
    }

    private void allocateBuffer() {
        assert fullMapped == null;
        try {
            fileOffset = allocated.getAndAdd(Constants.STRING_STORE_PAGE_SIZE);
            fileBuffer = channel.map(FileChannel.MapMode.READ_WRITE, fileOffset, Constants.STRING_STORE_PAGE_SIZE);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static long uint64(byte v) {
        return v & 0xffL;
    }

    private static long uint64(int v) {
        return v & 0xffffffffL;
    }

    public static Unsafe getUnsafe() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        return (Unsafe) theUnsafe.get(null);
    }
}
