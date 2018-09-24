package com.alibaba.middleware.race.sync;

import com.alibaba.middleware.race.sync.server.Solution;
import com.alibaba.middleware.race.sync.utils.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 服务器类，负责push消息到client Created by wanshao on 2017/5/25.
 */
public class Server {
    private static final Logger logger = Logger.SERVER_LOGGER;

    private static Solution solution;
    private static Thread solutionThread;

    public static void main(String[] args) throws Exception {
        initProperties();
        printInput(args);

        solution = new Solution(args[0], args[1], Long.parseLong(args[2]), Long.parseLong(args[3]));
        solutionThread = new Thread(solution);

        solutionThread.start();
        solution.startMonitorDaemon();

        listenAndPush(5527);

        solutionThread.join();
        solution.stopMonitorDaemon();

        logger.info("Goodbye!");
    }

    /**
     * 打印赛题输入 赛题输入格式： schemaName tableName startPkId endPkId，例如输入： middleware student 100 200
     * 上面表示，查询的schema为middleware，查询的表为student,主键的查询范围是(100,200)，注意是开区间 对应DB的SQL为： select * from middleware.student where
     * id>100 and id<200
     */
    private static void printInput(String[] args) {
        logger.info("schema:" + args[0]);
        logger.info("table:" + args[1]);
        logger.info("start:" + args[2]);
        logger.info("end:" + args[3]);
    }

    /**
     * 初始化系统属性
     */
    private static void initProperties() {
        System.setProperty("middleware.test.home", Constants.TESTER_HOME);
        System.setProperty("middleware.teamcode", Constants.TEAMCODE);
        System.setProperty("app.logging.level", Constants.LOG_LEVEL);
    }


    private static void listenAndPush(int port) throws IOException, InterruptedException {

        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress(port));
            logger.info("Listening: %s", serverSocket);

            try (SocketChannel socket = serverSocket.accept()) {
                logger.info("Client connected: %s", socket);

                socket.configureBlocking(false);
                solution.outputResult(socket);
            }
        }

    }
}
