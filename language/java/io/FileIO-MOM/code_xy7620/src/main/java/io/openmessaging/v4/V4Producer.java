package io.openmessaging.v4;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
import sun.misc.Cleaner;
import util.Message2Bytes;

/**
 * @author XF
 * 生产者默认实现<p>
 * 主要有两个方法:生产消息和发送消息。
 * 此类需要在构造器中初始化信息，如果测试类使用反射，将出现问题。获取线程名时都是主线程。
 */
public class V4Producer  implements Producer {
    private MessageFactory messageFactory = new DefaultMessageFactory();

    private KeyValue properties;
    
    //序列化输出对象，每个生产者持有一个.自己测试时记得关闭，不然没法删除持有的文件。实测时kill进程，没有这个问题
    private MappedByteBuffer mapBuffer = null;
    
    FileChannel channel;
    long size = 1024*1024*1000;
    private static int count = 0;
    String path ;
    /**
     * 初始化对象时，初始化io。并一直持有ObjectOutputStream oos。
     * @param properties
     */
    public V4Producer(KeyValue properties) {
        this.properties = properties;
        path = properties.getString("STORE_PATH");
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
			//使用buffer速度提升10倍，但是最后在buffer的数据会丢失
//			fos = new FileOutputStream(file);
//			fos = new BufferedOutputStream(new FileOutputStream(file),1024) ;
			
        	//使用MappedByteBuffer ，内存映射件
        	long start = System.currentTimeMillis();
			channel= FileChannel.open(Paths.get(path+"/"+fileName), StandardOpenOption.READ,StandardOpenOption.WRITE);
        	mapBuffer = channel.map(MapMode.READ_WRITE, 0, size);
        	long time = System.currentTimeMillis() - start;
//        	System.out.println(Thread.currentThread().getName()+"创建mapBuffer time"+time);
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

    @Override public void shutdown() {

    }

    @Override public KeyValue properties() {
        return properties;
    }

    /**
     * 默认send方法<p>
     * 检查topic与queue有且仅有一个，将它与对象再传给{@link MessageStore}
     * 直接序列化对象。
     */
    Message2Bytes message2Bytes = new Message2Bytes();
    @Override public void send(Message message) {
        if (message == null) throw new ClientOMSException("Message should not be null");

//        	fos.write(Message2Bytes.toBytes((BytesMessage)message));
		        	byte[] bytes = message2Bytes.toBytesWithBodyLen((BytesMessage)message);
//		        	try {
//		        		int channelSize = (int) channel.size();
//						mapBuffer = channel.map(MapMode.READ_WRITE, 0, channelSize+bytes.length);
//						mapBuffer.position(channelSize);
//						mapBuffer.put(bytes);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
		        	/**
		        	 * mapBuffer的size必须足够大，不然同一个线程的第2个文件会导致顺序问题
		        	 */
		        	while(true){
		            	int last = (int) (size - mapBuffer.position() -1);
		            	if(last > bytes.length){
		            		mapBuffer.put(bytes);
		            		break;
		            	}else{
		            		//只存完整的message
//		            		mapBuffer.put(bytes, 0, last);

		            		/**
		            		 * 暂时先不调用force，也不清理mapBuffer，如果以后测试例内存不够，或者频繁gc再修改
		            		 */
		                	long start = System.currentTimeMillis();
//		            		mapBuffer.force();
		                	long time = System.currentTimeMillis() - start;
		                	System.out.println(Thread.currentThread().getName()+"mapBuffer写入文件 time"+time);

		            		try {
								channel.close();
								String fileName = count+++".txt";
						        File file = new File(path, fileName);
						        if(!file.isFile()){
						        	try {
										file.createNewFile();
									} catch (IOException e) {
										e.printStackTrace();
									}
						        }
								channel= FileChannel.open(Paths.get(path+"/"+fileName), StandardOpenOption.READ,StandardOpenOption.WRITE);
								mapBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                	time = System.currentTimeMillis() - start;
		                	System.out.println(Thread.currentThread().getName()+"创建新mapBuffer time"+time);

//		            		
//		            		
		            	}
		        	}
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
		// 先关闭channel
		if (channel != null) {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 再关闭buffer
		try {
			Method getCleanerMethod = mapBuffer.getClass().getMethod("cleaner", new Class[0]);
			getCleanerMethod.setAccessible(true);
			sun.misc.Cleaner cleaner =  (Cleaner) getCleanerMethod.invoke(mapBuffer, new Object[0]);
			cleaner.clean();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
