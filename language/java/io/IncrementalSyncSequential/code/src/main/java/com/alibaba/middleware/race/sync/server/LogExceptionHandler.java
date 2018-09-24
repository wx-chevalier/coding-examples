package com.alibaba.middleware.race.sync.server;

import com.alibaba.middleware.race.sync.utils.Logger;
import com.lmax.disruptor.ExceptionHandler;

/**
 * Log exceptions in Disruptor to logger and exit immediately
 * 
 * Created by yfu on 6/22/17.
 */
public class LogExceptionHandler implements ExceptionHandler<Object> {

    private final static Logger logger = Logger.SERVER_LOGGER;

    @Override
    public void handleEventException(Throwable ex, long sequence, Object object) {
        logger.error("Exception caught", ex);
        System.exit(-1);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        logger.error("Exception caught when start", ex);
        System.exit(-1);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        logger.error("Exception caught when shutdown", ex);
        System.exit(-1);
    }

}
