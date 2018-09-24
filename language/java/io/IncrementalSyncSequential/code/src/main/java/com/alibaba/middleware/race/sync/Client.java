package com.alibaba.middleware.race.sync;

import com.alibaba.middleware.race.sync.utils.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by wanshao on 2017/5/25.
 */
public class Client {

    private static final Logger logger = Logger.CLIENT_LOGGER;

    private final Path resultPath = Paths.get(Constants.RESULT_HOME, Constants.RESULT_FILE_NAME);

    public static void main(String[] args) throws Exception {
        initProperties();
        logger.info("Client started");
        String serverIP = args[0];
        Client client = new Client();

        while (true) {
            try {
                client.connect(serverIP, Constants.SERVER_PORT);
                return;
            } catch (IOException ex) {
                Thread.sleep(100);
            }
        }
    }

    /**
     * 初始化系统属性
     */
    private static void initProperties() {
        System.setProperty("middleware.test.home", Constants.TESTER_HOME);
        System.setProperty("middleware.teamcode", Constants.TEAMCODE);
        System.setProperty("app.logging.level", Constants.LOG_LEVEL);
    }

    /**
     * 连接服务端
     */
    public void connect(String host, int port) throws IOException {
        try (SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(host, port))) {
            logger.info("Connected to server: %s", socketChannel);

            ByteBuffer buffer = ByteBuffer.allocate(8 * 1024 * 1024);

            try (FileChannel fileChannel = FileChannel.open(resultPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, StandardOpenOption.READ)) {

                int total = 0;
                int n;
                while ((n = socketChannel.read(buffer)) != -1) {
                    buffer.flip();
                    fileChannel.write(buffer);
                    buffer.clear();
                    total += n;
                }

                logger.info("Result received.  size = %d", total);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
