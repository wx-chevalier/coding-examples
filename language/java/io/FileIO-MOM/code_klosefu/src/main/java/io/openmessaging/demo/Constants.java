package io.openmessaging.demo;

/**
 * Created by yfu on 5/21/17.
 */
public class Constants {

    public static final int MAX_BATCH_SIZE = 1024; // num of messages

    public static final int FILE_ALLOCATE_SIZE = 16 * 1024 * 1024;  // Must be an integral multiple of 4K (page size)
    public static final int FILE_ALLOCATE_MARGIN = 512 * 1024; // Same as above

    public static final int CONSUMING_QUEUE_CAPACITY = MAX_BATCH_SIZE * 8;
    
    public static final int PRODUCER_WRITE_THREADS = 4;
    
}
