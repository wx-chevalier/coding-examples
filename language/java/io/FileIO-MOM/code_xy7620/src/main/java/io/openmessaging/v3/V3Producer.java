package io.openmessaging.v3;

import io.openmessaging.*;
import io.openmessaging.demo.ClientOMSException;
import io.openmessaging.demo.DefaultMessageFactory;
import util.Message2Bytes;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by 瑞 on 2017/5/10.
 * 主要是尝试使用zip压缩来写输出流，测试效率，使用v2版本进行简单测试时发现使用zipOutputStream速度会变得极快。
 */
public class V3Producer implements Producer {
    private MessageFactory messageFactory=new DefaultMessageFactory();

    private KeyValue keyValue;

    //private ZipOutputStream zos=null;
    RandomAccessFile memoryMappedFile=null;

    MappedByteBuffer out=null;

    //线程发送消息计数器
    private static int count=0;

    //这里我需要一个超级大的cache
    private int cacheSize=1024*1024*1024;

    public V3Producer(KeyValue keyValue){
        this.keyValue=keyValue;
        String path=keyValue.getString("STORE_PATH");
        String fileName=count+++".txt";
        File file=new File(path,fileName);
        if (!file.getParentFile().isDirectory()){
            file.getParentFile().mkdirs();
        }
        if (!file.isFile()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            memoryMappedFile=new RandomAccessFile(file,"rw");
            out=memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE,0,cacheSize);

//            zos=new ZipOutputStream(new FileOutputStream(file));
//            //这里可能有问题的
//            ZipEntry entry=new ZipEntry("0");
//            zos.putNextEntry(entry);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BytesMessage createBytesMessageToTopic(String topic, byte[] body) {
        return messageFactory.createBytesMessageToTopic(topic,body);
    }

    @Override
    public BytesMessage createBytesMessageToQueue(String queue, byte[] body) {
        return messageFactory.createBytesMessageToQueue(queue,body);
    }

    @Override
    public void start() {

    }

    /**
     * 清理资源
     */
    @Override
    public void shutdown() {
        if (out!=null){
            out.clear();
        }
        if (memoryMappedFile!=null){
            try {
                memoryMappedFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public KeyValue properties() {
        return null;
    }

    @Override
    public void send(Message message) {
        if (message==null)throw new ClientOMSException("Message should not be null");
        out.put(Message2Bytes.toBytes((BytesMessage)message));
        //写文件操作
        //zos.write(Message2Bytes.toBytes((BytesMessage) message));
    }

    @Override
    public void send(Message message, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public Promise<Void> sendAsync(Message message) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public Promise<Void> sendAsync(Message message, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void sendOneway(Message message) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void sendOneway(Message message, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public BatchToPartition createBatchToPartition(String partitionName) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public BatchToPartition createBatchToPartition(String partitionName, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}
}
