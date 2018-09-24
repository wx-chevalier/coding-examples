package io.openmessaging.v3;

import io.openmessaging.*;
import io.openmessaging.demo.DefaultBytesMessage;
import io.openmessaging.exception.OMSReadFinshedException;
import sun.nio.ch.FileChannelImpl;
import util.ThreadLock;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 瑞 on 2017/5/12.
 */
public class V3PullConsumer implements PullConsumer {
    private KeyValue properties;
    private List<String> bucketList = new ArrayList<>();

    //磁盘中的文件数组、文件数
    static File[] files;
    static int fileNum;

    // 记录偏移，读第几个bucket中的第几个message
    int bucketOffset = -1;
    int messageOffset = 0;

    // 记录当前读的list的引用
    List<BytesMessage> currentList = null;

    /**
     * 存储所有的数组，放于内存中. 测试ConcurrentHashMap，只能对1/10,跟直接用HashMap几乎没区别。
     * 测试Hashtable，对19/20。 所以暂时考虑使用HashMap，外部控制同步。
     * 感觉会出现同步方面的需求啊
     */
    static HashMap<String, List<BytesMessage>> data = null;

    // 当前该读磁盘中文件在files中索引，使用AtomicInteger避免同步
    static AtomicInteger index;

    // 记录已经读完的文件，所有文件都读完了才能开始消费，如果内存不够呢？
    static AtomicInteger finshed;

    // 线程锁，可以传入参数，设定几个线程可以同时运行,限制为四个线程是最好的方式吗？
    static int threadNum = 4;
    private static ThreadLock lock = new ThreadLock(threadNum);

    static Object obj = new Object();

    static final String[] headerBank = {MessageHeader.MESSAGE_ID, MessageHeader.TOPIC, MessageHeader.QUEUE,
            MessageHeader.BORN_TIMESTAMP, MessageHeader.BORN_HOST, MessageHeader.STORE_TIMESTAMP,
            MessageHeader.STORE_HOST, MessageHeader.START_TIME, MessageHeader.STOP_TIME, MessageHeader.TIMEOUT,
            MessageHeader.PRIORITY, MessageHeader.RELIABILITY, MessageHeader.SEARCH_KEY,
            MessageHeader.SCHEDULE_EXPRESSION, MessageHeader.SHARDING_KEY, MessageHeader.SHARDING_PARTITION,
            MessageHeader.TRACE_ID};

    public V3PullConsumer(KeyValue properties) {
        this.properties = properties;
        /**
         * 暂未考虑同步问题。现在测试例使用反射，没有并发，所以测试问题没发现问题。
         * files为null代表是新的测试例,为避免本次测试影响下次需要初始化各个属性。
         */
        if (files == null) {
            String path = properties.getString("STORE_PATH");
            files = new File(path).listFiles();
            fileNum = files.length;
            index = new AtomicInteger(0);
            finshed = new AtomicInteger(0);
            //这里是有问题的吧？170是怎么限制的
            data = new HashMap<String, List<BytesMessage>>(170);
        }
    }

    @Override
    public KeyValue properties() {
        return properties;
    }

    /**
     * 读完文件后不需要同步,也不需要轮询,挨个读。
     */
    boolean isReaded = false;

    @Override
    public Message poll() {
        if (!isReaded) {
            readFile();
            isReaded = !isReaded;
            files = null;
        }
        while (currentList == null && bucketOffset < bucketList.size() - 1) {
            ++bucketOffset;
            currentList = data.get(bucketList.get(bucketOffset));
        }
        if (currentList == null) {
            return null;
        }

        BytesMessage message = currentList.get(bucketOffset);
        if (messageOffset < bucketList.size() - 1) {
            messageOffset++;
        } else {
            currentList = null;
            messageOffset = 0;
        }
        return message;
    }

    @Override
    public Message poll(KeyValue properties) {
        return null;
    }

    @Override
    public void ack(String messageId) {

    }

    @Override
    public void ack(String messageId, KeyValue properties) {

    }

    @Override
    public void attachQueue(String queueName, Collection<String> topics) {
        bucketList.add(queueName);
        bucketList.addAll(topics);
    }

    // 读入文件
    public void readFile() {

        /**
         * 这里加锁，只是为了控制几个线程可以取得同步资源。与数据的同步无关。 测试发现，不加锁更快。。推测线程切换的开销比阻塞唤醒的小。
         * 但是不同测试例可能有不同的效果，以后再测。
         */
        int read=0;
        try {
            int fileIndex = index.getAndIncrement();
            MappedByteBuffer buffer=null;
            int bytesSize=1024*1000;

            try {
                while (fileIndex<fileNum){
                    /**
                     * 内存限制为4G，
                     */
                    File file = files[fileIndex];
                    FileInputStream in = new FileInputStream(file);
                    FileChannel channel = in.getChannel();
                    //将数据映射到内存中
                    buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                    byte[] b=new byte[bytesSize];
                    //这里是一次将所有数据都读入内存中，如果内存满的话，就需要连续换页，这个必须考虑的
                    int bytesIndex=0;
                    int readSize=0;
                    String queueOrTopic=null;

                    if(bytesIndex+bytesSize<file.length()){
                        buffer.get(b,bytesIndex,bytesSize);
                    }else {
                        bytesSize=(int)file.length()-bytesIndex;
                    }
                    while (bytesIndex<file.length()){
                        BytesMessage message=new DefaultBytesMessage(null);

                    }



                    finshed.incrementAndGet();
                    fileIndex=index.getAndIncrement();
                }

                int num=finshed.get();
                while (num<fileNum){
                    Thread.yield();
                    num=finshed.get();
                }
            }finally {
                //通过反射的方式释放内存
                try {
                    Method method= FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
                    method.setAccessible(true);
                    method.invoke(FileChannelImpl.class,buffer);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
