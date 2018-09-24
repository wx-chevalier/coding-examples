package com.alibaba.middleware.race.sync;

import com.alibaba.middleware.race.sync.server.Solution;
import com.alibaba.middleware.race.sync.utils.Logger;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Local Tester
 * 
 * Created by yfu on 6/17/17.
 */
public class LocalTester {
    
    private static final Logger logger = Logger.SERVER_LOGGER;

    public static void main(String args[]) throws InterruptedException {
//        Thread.sleep(10000);
        final long beginTime = System.currentTimeMillis();

        logger.info("Started");

        Solution solution = new Solution(args[0], args[1], Long.parseLong(args[2]), Long.parseLong(args[3]));
        solution.startMonitorDaemon();
        Thread solutionThread = new Thread(solution);
        solutionThread.start();

        Path path = Paths.get(Constants.RESULT_HOME, Constants.RESULT_FILE_NAME);
        path.toFile().delete();
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            solution.outputResult(channel);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        solutionThread.join();
        final long endTime = System.currentTimeMillis();
        System.out.println("Time cost = " + (endTime - beginTime));
    }
}
