package com.alibaba.middleware.topkn.worker;

import java.io.File;

/**
 * 
 * 
 * Created by yfu on 7/15/17.
 */
public class FileSegment {
    
    private final File file;
    private final long offset;
    private final long nextOffset;

    public FileSegment(File file, long offset, long nextOffset) {
        this.file = file;
        this.offset = offset;
        this.nextOffset = nextOffset;
    }

    public File getFile() {
        return file;
    }

    public long getOffset() {
        return offset;
    }

    public long getNextOffset() {
        return nextOffset;
    }
}
