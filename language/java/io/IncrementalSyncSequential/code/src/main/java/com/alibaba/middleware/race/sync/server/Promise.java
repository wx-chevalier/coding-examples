package com.alibaba.middleware.race.sync.server;

/**
 * Promise
 * 
 * Created by yfu on 6/28/17.
 */
public final class Promise {
    volatile int data = -1;

    byte sender = -1;
}
