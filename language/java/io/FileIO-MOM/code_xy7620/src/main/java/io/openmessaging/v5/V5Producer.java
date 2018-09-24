package io.openmessaging.v5;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
import io.openmessaging.tester.Constants;
import sun.misc.Cleaner;
import util.Message2Bytes;

/**
 * @author XF
 * 生产者默认实现<p>
 * 主要有两个方法:生产消息和发送消息。
 * 此类需要在构造器中初始化信息，如果测试类使用反射，将出现问题。获取线程名时都是主线程。
 */
public class V5Producer  implements Producer {
	static final HashMap<String, String> seq = new HashMap<String, String>(32);
	
	static{
		//使用字符而不是整数，从字符串转换成字节只需要1字节
		seq.put(MessageHeader.MESSAGE_ID, "a");
		seq.put(MessageHeader.TOPIC, "b");
		seq.put(MessageHeader.QUEUE, "c");
		seq.put(MessageHeader.BORN_TIMESTAMP, "d");
		seq.put(MessageHeader.BORN_HOST, "e");
		seq.put(MessageHeader.STORE_TIMESTAMP, "f");
		seq.put(MessageHeader.STORE_HOST, "g");
		seq.put(MessageHeader.START_TIME, "h");
		seq.put(MessageHeader.STOP_TIME, "i");
		seq.put(MessageHeader.TIMEOUT, "j");
		seq.put(MessageHeader.PRIORITY, "k");  
		seq.put(MessageHeader.RELIABILITY, "l");
		seq.put(MessageHeader.SEARCH_KEY, "m");
		seq.put(MessageHeader.SCHEDULE_EXPRESSION, "n");
		seq.put(MessageHeader.SHARDING_KEY, "o");
		seq.put(MessageHeader.SHARDING_PARTITION, "p");
		seq.put(MessageHeader.TRACE_ID, "q");
	}
    private DefaultMessageFactory messageFactory = new DefaultMessageFactory();

    private KeyValue properties;
    
    //序列化输出对象，每个生产者持有一个.自己测试时记得关闭，不然没法删除持有的文件。实测时kill进程，没有这个问题
    private MappedByteBuffer mapBuffer = null;
    
    FileChannel channel;
    //注意与bytesSize要整除
    long size = 1024*1024*1000;
    
    private static int count = 0;
    
    //作为一个buffer的大小，放入整数个对象，后面移动指针
    private static final int bytesSize = 102400;
    
    private int nowLimit = bytesSize;
    
    String path ;
    /**
     * 初始化对象时，初始化io。并一直持有ObjectOutputStream oos。
     * @param properties
     */
    public V5Producer(KeyValue properties) {
        this.properties = properties;
        path = properties.getString("STORE_PATH");
//        bytesSize = properties.getInt("Bytes_Size");
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
//        return messageFactory.createBytesMessageToTopic(topic, body);
//    	if(!all.contains(topic)){
//    		System.out.println("发送topic:"+topic);
//    		all.add(topic);
//    	}
        return messageFactory.changeBytesMessageToTopic(topic, body);
    }

    @Override public BytesMessage createBytesMessageToQueue(String queue, byte[] body) {
//        return messageFactory.createBytesMessageToQueue(queue, body);
        return messageFactory.changeBytesMessageToQueue(queue, body);
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
    static HashSet<String> all = new HashSet<String>();
    @Override public void send(Message message) {
    	
//    	toBytesAndSend(message);   //91s,1亿。在官方111s,103s
//    	toBytes(message);   //96s,1亿。在官方99s,109s
    	toBytesAndSendMany(message); //91s,1亿
    }
    /**
     * 先将message转换成字节数组，发送
     * @param message
     */
    Message2Bytes message2Bytes = new Message2Bytes();
    
    public void toBytes(Message message){
        if (message == null) throw new ClientOMSException("Message should not be null");

		byte[] bytes = message2Bytes.toBytesWithBodyLen((BytesMessage)message);

		/**
		 * mapBuffer的size必须足够大，不然同一个线程的第2个文件会导致顺序问题. 现在程序只考虑一个文件(最大2G).
		 */
		while (true) {
			int position = mapBuffer.position();
			int last = (int) (size - position - 1);
			int bytesLen = bytes.length;
			if (last > bytesLen) {
				// 划分为一个一个的buffer，只存整数的对象，存不下时，移动指针，从下一个buffer开始
				if (nowLimit - position >= bytesLen) {
					 mapBuffer.put(bytes);
				} else {
					mapBuffer.position(nowLimit);
					nowLimit += bytesSize;
					mapBuffer.put(bytes);

				}
				break;
			} else {
				// 只存完整的message
				// mapBuffer.put(bytes, 0, last);

				/**
				 * 暂时先不调用force，也不清理mapBuffer，如果以后测试例内存不够，或者频繁gc再修改
				 */
				// long start = System.currentTimeMillis();
				//// mapBuffer.force();
				// long time = System.currentTimeMillis() - start;
				// System.out.println(Thread.currentThread().getName()+"mapBuffer写入文件
				// time"+time);
				//
				// try {
				// channel.close();
				// String fileName = count+++".txt";
				// File file = new File(path, fileName);
				// if(!file.isFile()){
				// try {
				// file.createNewFile();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// }
				// channel= FileChannel.open(Paths.get(path+"/"+fileName),
				// StandardOpenOption.READ,StandardOpenOption.WRITE);
				// mapBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0,
				// size);
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// time = System.currentTimeMillis() - start;
				// System.out.println(Thread.currentThread().getName()+"创建新mapBuffer
				// time"+time);
			}
		}
    }
    /**
     * 直接发送，不用byte数组先存下来
     * @param message
     */
    StringBuilder builder = new StringBuilder();
    Set<String> keyS = null;
    Set<String> ks = null;
    Iterator<String> it = null;
    String key;
    KeyValue headers;
    KeyValue pros ;
    public void toBytesAndSend(Message message){

        if (message == null) throw new ClientOMSException("Message should not be null");

//		byte[] bytes = Message2Bytes.toBytesWithBodyLen((BytesMessage)message);
        
		builder.delete(0, builder.length());
		//headers
		headers = message.headers();
		//第一个字节,header属性个数
		keyS = headers.keySet();
		byte headerSize = (byte) keyS.size();
		it = keyS.iterator();
		//String格式："数量编号主体;编号主体;……"
		while(it.hasNext()){
			key = it.next();
			builder.append(seq.get(key));
			builder.append(headers.getString(key)+";");
		}
		//properties,因为个数，key都不确定所以都需要分隔符
		pros = message.properties();
		if(pros != null){
			ks = pros.keySet();
			builder.append(ks.size()+";");
			it = ks.iterator();
			
			//String格式："数量;key;主体;key;主体;……"
			while(it.hasNext()){
				key = it.next();
				builder.append(key+";");
				builder.append(pros.getString(key)+";");
			}
		}else{
			builder.append("0;");
		}
		
		//body String格式： "len1;body"
		byte[] body = ((BytesMessage) message).getBody();
		int len1 = body.length;
		builder.append(len1+";");
		
		//header+properties+body总长度 转换字节
		byte[] bytes =  builder.toString().getBytes();
		int len = bytes.length;
		
		int bytesLen = 1 + len1 + len;

		/**
		 * mapBuffer的size必须足够大，不然同一个线程的第2个文件会导致顺序问题. 现在程序只考虑一个文件(最大2G).
		 */
		while (true) {
			int position = mapBuffer.position();
			int last = (int) (size - position - 1);
			if (last > bytesLen) {
				// 划分为一个一个的buffer，只存整数的对象，存不下时，移动指针，从下一个buffer开始
				if (nowLimit - position >= bytesLen) {
					// mapBuffer.put(bytes);
					mapBuffer.put(headerSize);
					mapBuffer.put(bytes);
					mapBuffer.put(body);

				} else {
					mapBuffer.position(nowLimit);
					nowLimit += bytesSize;
					// mapBuffer.put(bytes);
					mapBuffer.put(headerSize);
					mapBuffer.put(bytes);
					mapBuffer.put(body);
				}
				break;
			} else {
				
			}
		}
    }
    /**
     * 将多个消息放在一组，最后调用mapBuffer,暂时还有问题，没调通
     * @param message
     */
    byte[] bs = new byte[bytesSize];
    int byteIndex =0;
    public void toBytesAndSendMany(Message message){

        if (message == null) throw new ClientOMSException("Message should not be null");

//		byte[] bytes = Message2Bytes.toBytesWithBodyLen((BytesMessage)message);
        
        builder.delete(0, builder.length());
        
		//headers
		KeyValue headers = message.headers();
		//第一个字节,header属性个数
		keyS = headers.keySet();
		byte headerSize = (byte) keyS.size();
		Iterator<String> it = keyS.iterator();
		//String格式："数量编号主体;编号主体;……"
		while(it.hasNext()){
			String key = it.next();
			builder.append(seq.get(key));
			builder.append(headers.getString(key)+";");
		}
		//properties,因为个数，key都不确定所以都需要分隔符
		KeyValue pros = message.properties();
		if(pros != null){
			ks = pros.keySet();
			builder.append(ks.size()+";");
			it = ks.iterator();
			
			//String格式："数量;key;主体;key;主体;……"
			while(it.hasNext()){
				String key = it.next();
				builder.append(key+";");
				builder.append(pros.getString(key)+";");
			}
		}else{
			builder.append("0;");
		}
		
		//body String格式： "len1;body"
		byte[] body = ((BytesMessage) message).getBody();
		int len1 = body.length;
		builder.append(len1+";");
		
		//header+properties+body总长度 转换字节
		byte[] bytes =  builder.toString().getBytes();
		int len = bytes.length;
		
		int bytesLen = 1 + len1 + len;

		/**
		 * mapBuffer的size必须足够大，不然同一个线程的第2个文件会导致顺序问题. 现在程序只考虑一个文件(最大2G).
		 */
		while (true) {
			int position = mapBuffer.position();
			int last = (int) (size - position - 1);
			if (last > bytesLen) {
				// 划分为一个一个的buffer，只存整数的对象，存不下时，移动指针，从下一个buffer开始
				if (bytesSize - byteIndex >= bytesLen) {
					bs[byteIndex++] = headerSize;
					System.arraycopy(bytes, 0, bs, byteIndex, len);
					byteIndex += len;
					System.arraycopy(body, 0, bs, byteIndex, len1);
					byteIndex += len1;

				} else {
					if(byteIndex != bytesSize){
						bs[byteIndex] = 0;
					}
					mapBuffer.put(bs);
					mapBuffer.position(nowLimit);
					nowLimit += bytesSize;
					byteIndex = 0;
					bs[byteIndex++] = headerSize;
					System.arraycopy(bytes, 0, bs, byteIndex, len);
					byteIndex += len;
					System.arraycopy(body, 0, bs, byteIndex, len1);
					byteIndex += len1;
				}
				break;
			} else {
				// 只存完整的message

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
		//使用toBytesAndSendMany()方法发送需要最后再put一次
		bs[byteIndex] = 0;
		mapBuffer.put(bs);
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
