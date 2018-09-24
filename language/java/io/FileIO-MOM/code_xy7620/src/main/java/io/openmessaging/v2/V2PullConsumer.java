package io.openmessaging.v2;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.Message;
import io.openmessaging.MessageHeader;
import io.openmessaging.PullConsumer;
import io.openmessaging.demo.ClientOMSException;
import io.openmessaging.demo.DefaultBytesMessage;
import io.openmessaging.demo.DefaultKeyValue;
import io.openmessaging.demo.MessageStore;
import io.openmessaging.exception.OMSReadFinshedException;
import io.openmessaging.tester.Constants;
import util.ThreadLock;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author XF 拉取消费者的默认实现。会实例多个。
 *         <p>
 *         主要有两个方法：1、绑定queue和topic 2、获取下一个message对象。
 *         <p>
 *         按题目要求，一个消费者线程实例化一个本对象。
 */
public class V2PullConsumer implements PullConsumer {
	private KeyValue properties;
	private List<String> bucketList = new ArrayList<>();

	// 磁盘中文件数组,文件数
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
	 */
	static HashMap<String, List<BytesMessage>> data = null;

	// 当前该读磁盘中文件在files中索引，使用AtomicInteger避免同步
	static AtomicInteger index;

	// 记录已经读完的文件，所有文件都读完了才能开始消费
	static AtomicInteger finshed;

	// 线程锁，可以传入参数，设定几个线程可以同时运行
	static int threadNum = 4;
	private static ThreadLock lock = new ThreadLock(threadNum);

	static Object obj = new Object();

	static final String[] headerBank = { MessageHeader.MESSAGE_ID, MessageHeader.TOPIC, MessageHeader.QUEUE,
			MessageHeader.BORN_TIMESTAMP, MessageHeader.BORN_HOST, MessageHeader.STORE_TIMESTAMP,
			MessageHeader.STORE_HOST, MessageHeader.START_TIME, MessageHeader.STOP_TIME, MessageHeader.TIMEOUT,
			MessageHeader.PRIORITY, MessageHeader.RELIABILITY, MessageHeader.SEARCH_KEY,
			MessageHeader.SCHEDULE_EXPRESSION, MessageHeader.SHARDING_KEY, MessageHeader.SHARDING_PARTITION,
			MessageHeader.TRACE_ID };

	public V2PullConsumer(KeyValue properties) {
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

	public Message poll() {
		// 每个线程都应该先读文件，或者阻塞住等待读文件完毕
		if (isReaded == false) {
			readFile();
			isReaded = true;
			// 将files赋为null，下个测试例才知道是新的测试例，重新初始化各个属性
			files = null;
		}

		// 可能某个queue/topic一个message都没有，滚动到下一个
		while (currentList == null && bucketOffset < bucketList.size() - 1) {
			++bucketOffset;
			currentList = data.get(bucketList.get(bucketOffset));
		}

		if (currentList == null) {
			return null;
		}

		BytesMessage message = currentList.get(messageOffset);
		if (messageOffset < currentList.size() - 1) {
			messageOffset++;
		} else {
			// bucketOffset++;
			currentList = null;
			messageOffset = 0;
		}
		return message;
	}

	@Override
	public Message poll(KeyValue properties) {
		throw new UnsupportedOperationException("Unsupported");
	}

	@Override
	public void ack(String messageId) {
		throw new UnsupportedOperationException("Unsupported");
	}

	@Override
	public void ack(String messageId, KeyValue properties) {
		throw new UnsupportedOperationException("Unsupported");
	}

	/**
	 * 不需要同步，每个线程启动一个Consumer。
	 * <P>
	 * 对于消费者来说，queue和topic是没有区分的，不需要区分。
	 * <p>
	 * 不在这里获得所有消息queue和topic的集合，就不用考虑同步。
	 */
	@Override
	public void attachQueue(String queueName, Collection<String> topics) {

		bucketList.add(queueName);
		bucketList.addAll(topics);
	}

	long listTime = 0;
	long totalTime = 0;

	// 读入文件
	public void readFile() {

		/**
		 * 这里加锁，只是为了控制几个线程可以取得同步资源。与数据的同步无关。 测试发现，不加锁更快。。推测线程切换的开销比阻塞唤醒的小。
		 * 但是不同测试例可能有不同的效果，以后再测。
		 */
		// lock.lock();
		int read = 0;
		try {
			int fileIndex = index.getAndIncrement();
			while (fileIndex < fileNum) {
				File file = files[fileIndex];
//				System.out.println(Thread.currentThread().getName()+"read: "+file);
				// 输入流使用buffer（没有指定buffer大小，后面可调整），性能提升3倍
				FileInputStream input = new FileInputStream(file);
				// 输入数据读到此字节数组，数组大小需要调试确定
				int bytesSize = 1024*1000;
				BufferedInputStream bis = new BufferedInputStream(input,bytesSize);
//				ZipInputStream zis = new ZipInputStream(new BufferedInputStream(input,bytesSize));
//				ZipEntry bis = zis.getNextEntry();
				/**
				 * 该文件读入过程中发生异常则终止该文件读入，即认为读入完毕。
				 */
				try {

					byte[] b = new byte[bytesSize];

					// 通过字节数组下标index来控制读入的进度
					int bytesIndex = 0;
					// 存储每次读入的字节数，控制循环退出
					int readSize = 0;
					String queueOrTopic = null;

					readSize = bis.read(b);
					while (bytesIndex != -1) {
						// Message
						BytesMessage message = new DefaultBytesMessage(null);

						/**
						 * headers
						 */
						int headerSize = b[bytesIndex];

						/**
						 *  超出读入字节数组范围，置位bytesIndex并且读入下个字节数组。每次移动下标都需要检测。
						 *  由于buffer，所以不能直接确定读入字节的最后一个，所以由readSize控制循环。
						 *  这里检测了readSize < bytesSize，而后文中没有，因为这是对象的第一个字节，
						 *  理论上读对象中间部分字节时，只要对象没读完，肯定还有字节，所以循环退出在这里。
						 */
						if (++bytesIndex == readSize) {
							if(readSize < bytesSize){
								throw new OMSReadFinshedException();
							}
							bytesIndex = 0;
							readSize = bis.read(b);
						}
						for (int i = 0; i < headerSize; i++) {
							// header编号一个字节
							int seq = b[bytesIndex] - 97;
							String header = null;
							header = headerBank[seq];

							if (++bytesIndex == readSize) {
								bytesIndex = 0;
								readSize = bis.read(b);
							}

							// 获取header对应的value
							int start = bytesIndex;
							String pre = "";
							// 直到找到分割符 ；到字节数组最后时，需要将之前的存下来，并且读入新的字节数组
							while (b[bytesIndex] != 59) {
								bytesIndex++;
								if (bytesIndex == readSize) {
									pre += new String(b, start, bytesIndex - start);
									readSize = bis.read(b);
									bytesIndex = 0;
									start = 0;
								}
							}
							String value = pre + new String(b, start, bytesIndex - start);
							if (++bytesIndex == readSize) {
								bytesIndex = 0;
								readSize = bis.read(b);
							}

							// 获得queue/topic
							if (seq == 1 || seq == 2) {
								queueOrTopic = value;
							}
							message.putHeaders(header, value);
						}

						/**
						 * properties
						 */
						byte[] bs = new byte[5];
						int temp = 0;
						// 直到找到分割符 ；
						while (b[bytesIndex] != 59) {
							bs[temp++] = b[bytesIndex];
							bytesIndex++;
							if (bytesIndex == readSize) {
								readSize = bis.read(b);
								bytesIndex = 0;
							}
						}
						if (++bytesIndex == readSize) {
							bytesIndex = 0;
							readSize = bis.read(b);
						}
						int proSize = Integer.valueOf(new String(bs, 0, temp));

						/**
						 * 属性个数不为0时时查找。 与上方查找header的value方法一致，一直向后直到分隔符。
						 * 到字节数组最后时，需要将之前的存下来，并且读入新的字节数组
						 */
						if (proSize != 0) {

							for (int i = 0; i < proSize; i++) {
								// 找key，直到找到分割符 ；
								int start = bytesIndex;
								String pre = "";
								while (b[bytesIndex] != 59) {
									bytesIndex++;
									if (bytesIndex == readSize) {
										pre += new String(b, start, bytesIndex - start);
										readSize = bis.read(b);
										bytesIndex = 0;
										start = 0;
									}
								}
								String key = pre + new String(b, start, bytesIndex - start);
								if (++bytesIndex == readSize) {
									bytesIndex = 0;
									readSize = bis.read(b);
								}
								// 找value，直到找到分割符 ；
								start = bytesIndex;
								pre = "";
								while (b[bytesIndex] != 59) {
									bytesIndex++;
									if (bytesIndex == readSize) {
										pre += new String(b, start, bytesIndex - start);
										readSize = bis.read(b);
										bytesIndex = 0;
										start = 0;
									}
								}
								String value = pre + new String(b, start, bytesIndex - start);
								message.putProperties(key, value);
								if (++bytesIndex == readSize) {
									bytesIndex = 0;
									readSize = bis.read(b);
								}

							}
						}
						/**
						 * body 一直读直到分隔符
						 */
						int start = bytesIndex;
						byte[] body = null;
						while (b[bytesIndex] != 59) {
							bytesIndex++;
							if (bytesIndex == readSize) {
								if (body == null) {
									body = Arrays.copyOfRange(b, start, bytesIndex);
								} else {
									// bytesSize足够大的话，复制数组操作不多
									int bodyLen = body.length;
									body = Arrays.copyOf(body, bodyLen + bytesIndex - start);
									System.arraycopy(b, start, body, bodyLen, bytesIndex - start);
								}
								readSize = bis.read(b);
								bytesIndex = 0;
								start = 0;
							}
						}
						if (body == null) {
							body = Arrays.copyOfRange(b, start, bytesIndex);
						} else {
							// bytesSize足够大的话，复制数组操作不多
							int bodyLen = body.length;
							body = Arrays.copyOf(body, bodyLen + bytesIndex - start);
							System.arraycopy(b, start, body, bodyLen, bytesIndex - start);
						}
						message.setBody(body);
						/**
						 * 至此一个message对象反序列化完成。
						 * 下面将message放于对应list中。
						 */
						// 如果不做同步处理，偶尔有错（数据错误，或者空指针异常）
						List<BytesMessage> list = null;
						if (!data.containsKey(queueOrTopic)) {
							synchronized (obj) {
								if (!data.containsKey(queueOrTopic)) {
									list = new ArrayList<BytesMessage>();
									data.put(queueOrTopic, list);
								} else {
									list = data.get(queueOrTopic);
								}
							}
						} else {
							list = data.get(queueOrTopic);
						}

						// 不对list做任何同步处理，只有前1/10的数据能对
						synchronized (list) {
							list.add(message);
						}
						read++;
						// 处理bytesIndex，准备读下一个message
						if (++bytesIndex == readSize) {
							int i = 0;
							if ((readSize = bis.read(b)) == -1) {
								bytesIndex = -1;
								break;
							}
							bytesIndex = 0;
						}
					}
				} catch (OMSReadFinshedException e) {
					System.out.println("文件读取完毕！");
//					e.printStackTrace();
				} catch (Exception e) {
//					e.printStackTrace();
				}
				// 关闭io，删除该文件
				bis.close();
				input.close();
//				file.delete();

				// 已读文件数+1
				finshed.incrementAndGet();
				fileIndex = index.getAndIncrement();
			}
			/**
			 * 所有读文件的线程都读入完了才能进入消费阶段。 如果所有文件都被读了，但是其他线程某个文件还没读完，则自旋等待。
			 * 已读文件数等于所有文件数时，代表已经读完
			 */
			int num = finshed.get();
			while (num < fileNum) {
				// 暂停当前线程，把执行机会让出来。
				Thread.yield();
				num = finshed.get();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
