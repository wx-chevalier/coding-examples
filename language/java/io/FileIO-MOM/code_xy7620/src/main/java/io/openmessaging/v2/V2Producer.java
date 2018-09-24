package io.openmessaging.v2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.openmessaging.BatchToPartition;
import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.MessageFactory;
import io.openmessaging.MessageHeader;
import io.openmessaging.Producer;
import io.openmessaging.Promise;
import io.openmessaging.demo.ClientOMSException;
import io.openmessaging.demo.DefaultMessageFactory;
import io.openmessaging.demo.MessageStore;
import util.Message2Bytes;

/**
 * @author XF
 * 生产者默认实现<p>
 * 主要有两个方法:生产消息和发送消息。
 * 此类需要在构造器中初始化信息，如果测试类使用反射，将出现问题。获取线程名时都是主线程。
 */
public class V2Producer  implements Producer {
    private MessageFactory messageFactory = new DefaultMessageFactory();

    private KeyValue properties;
    
    //序列化输出对象，每个生产者持有一个.自己测试时记得关闭，不然没法删除持有的文件。实测时kill进程，没有这个问题
    //private FileOutputStream fos = null;
    private ZipOutputStream zos = null;
    //private MappedByteBuffer mapBuffer = null;

    //这里我需要一个超级大的cache
    private int cacheSize=1024*1024*1024;

    //private ZipOutputStream zos=null;
    RandomAccessFile memoryMappedFile=null;

    MappedByteBuffer out=null;
    
    private static int count = 0;
    /**
     * 初始化对象时，初始化io。并一直持有ObjectOutputStream oos。
     * @param properties
     */
    public V2Producer(KeyValue properties) {
        this.properties = properties;
        String path = properties.getString("STORE_PATH");
        String fileName = count+++".txt";
        File file = new File(path, fileName);
        if(!file.getParentFile().isDirectory()){
        	file.getParentFile().mkdirs();
        }
        if(!file.isFile()){
        	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        try {
            memoryMappedFile=new RandomAccessFile(file,"rw");
            out=memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE,0,cacheSize);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


    @Override public BytesMessage createBytesMessageToTopic(String topic, byte[] body) {
        return messageFactory.createBytesMessageToTopic(topic, body);
    }

    @Override public BytesMessage createBytesMessageToQueue(String queue, byte[] body) {
        return messageFactory.createBytesMessageToQueue(queue, body);
    }

    @Override public void start() {

    }

    /**
     * 在这里就可以释放，不需要再添加新的接口方法
     * 关闭io资源，让持有的文件释放
     */
    @Override public void shutdown() {
        try {
            out.clear();
            memoryMappedFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public KeyValue properties() {
        return properties;
    }

    /**
     * 默认send方法<p>
     * 检查topic与queue有且仅有一个，将它与对象再传给{@link MessageStore}
     * 直接序列化对象。
     */
    @Override public void send(Message message) {
        if (message == null) throw new ClientOMSException("Message should not be null");
        out.put(Message2Bytes.toBytes((BytesMessage)message));
    }

    @Override public void send(Message message, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public Promise<Void> sendAsync(Message message) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public Promise<Void> sendAsync(Message message, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void sendOneway(Message message) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public void sendOneway(Message message, KeyValue properties) {
        throw new UnsupportedOperationException("Unsupported");
    } 


	@Override
	public BatchToPartition createBatchToPartition(String partitionName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public BatchToPartition createBatchToPartition(String partitionName, KeyValue properties) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}
}
