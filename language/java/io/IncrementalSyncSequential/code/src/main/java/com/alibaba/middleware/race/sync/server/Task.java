package com.alibaba.middleware.race.sync.server;

/**
 * Task
 * 
 * Created by yfu on 6/28/17.
 */
final class Task {
    static final byte TASK_INSERT = 0;
    static final byte TASK_UPDATE = 1;
    static final byte TASK_UPDATE_PK_SRC = 2;
    static final byte TASK_UPDATE_PK_DST = 3;
    static final byte TASK_DELETE = 4;
    
    final byte opcode;
    final long key;
    final int promise;

    final int data;

    public Task(byte opcode, long key, int promise, int data) {
        this.opcode = opcode;
        this.key = key;
        this.promise = promise;
        this.data = data;
    }
}
