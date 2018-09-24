package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import io.openmessaging.BytesMessage;
import io.openmessaging.KeyValue;
import io.openmessaging.MessageHeader;

/**
 * @author XF
 * Message与Byte[]相互转化.
 * 反序列化时需要结合业务逻辑，所以不在这里单独实现。
 */
public class Message2Bytes {
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
	/**
	 * 存body时没有存长度，需要检测每个字节是不是分隔符，改为下面的方法
	 * @param message
	 * @return
	 */
	public static byte[] toBytes(BytesMessage message){
		
		StringBuilder builder = new StringBuilder();
		
		//headers
		KeyValue headers = message.headers();
		
		//第一个字节,header属性个数
//		byte headerSize = (byte) headers.size();
		
//		Iterator<Entry<String, Object>> it = headers.entrySet().iterator();
		Set<String> keyS = headers.keySet();
		Iterator<String> it = keyS.iterator();
		
		//String格式："数量编号主体;编号主体;……"
		while(it.hasNext()){
//			Entry<String, Object> entry = it.next();
//			builder.append(seq.get(entry.getKey()));
//			builder.append(entry.getValue()+";");
			String key = it.next();
			builder.append(seq.get(key));
			builder.append(headers.getString(key)+";");
		}
		
		//properties,因为个数，key都不确定所以都需要分隔符
		KeyValue pros = message.properties();
		if(pros != null){
//			builder.append(pros.size()+";");
//			it = pros.entrySet().iterator();
//			
//			//String格式："数量;key;主体;key;主体;……"
//			while(it.hasNext()){
//				Entry<String, Object> entry = it.next();
//				builder.append(entry.getKey()+";");
//				builder.append(entry.getValue()+";");
//			}
			Set<String> ks = pros.keySet();
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
		
		//header和properties转换字节
		byte[] bytes =  builder.toString().getBytes();
		int len = bytes.length;
		
		//body
		byte[] body = message.getBody();
		int len1 = body.length;
				
		int resultSize = 2 + len + len1;
		byte[] result = new byte[resultSize];
		
		//拼接整个对象的字节数组。第一个字节为header属性个数。
		result[0] = (byte) keyS.size();
		System.arraycopy(bytes, 0, result, 1, len);
		System.arraycopy(body, 0, result, 1+len, len1);
		result[resultSize-1] = 59;
		
		return result;
		
	}
	/**
	 * 考虑测试消息长度有100k，转换body时没有长度需要检测每个字节是不是分隔符，性能较低。
	 * 现将body更改： body; -> len;body
	 * @param message
	 * @return
	 */
    StringBuilder builder = new StringBuilder();
    Set<String> keyS = null;
    Set<String> ks = null;
	public  byte[] toBytesWithBodyLen(BytesMessage message){
		
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
		byte[] body = message.getBody();
		int len1 = body.length;
		builder.append(len1+";");
		
		//header+properties+body长度 转换字节
		byte[] bytes =  builder.toString().getBytes();
		int len = bytes.length;
				
		int resultSize = 1 + len + len1;
		byte[] result = new byte[resultSize];
		
		//拼接整个对象的字节数组。第一个字节为header属性个数。
		result[0] = headerSize;
		System.arraycopy(bytes, 0, result, 1, len);
		System.arraycopy(body, 0, result, 1+len, len1);
		
		return result;
		
	}
}
