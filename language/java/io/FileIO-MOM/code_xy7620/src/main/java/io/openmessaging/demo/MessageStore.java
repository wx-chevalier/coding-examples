package io.openmessaging.demo;

import io.openmessaging.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XF
 * 消息的中转,生产者发送消息到此，消费者从此获取消息，相当于server <p>
 * 通过getInstance获取实例，单例模式.<p>
 * 含有 put/pullMessage 的方法
 */
public class MessageStore {

    private static final MessageStore INSTANCE = new MessageStore();

    public static MessageStore getInstance() {
        return INSTANCE;
    }

    /**
     * 消息桶<p>
     * key:bucket(topic或者queue)  --  value:对应的message list.<p>
     * (使用ArrayList是因为按偏移取，需要有序)
     */
    private Map<String, ArrayList<Message>> messageBuckets = new HashMap<>();

    /**
     * 用来记录当前queue被消费到哪里了<p>
     * key:queue  --  value: HashMap< bucket, offset> <p>
     * 这样看来bucket像是topic。
     */
    private Map<String, HashMap<String, Integer>> queueOffsets = new HashMap<>();

    /**
     * 将message存入相应bucket(topic/queue)的list中，同步方法。<p>
     * 对每个queue或者topic都有一个list存放它的message。
     * @param bucket topic/queue
     * @param message
     */
    public synchronized void putMessage(String bucket, Message message) {
        if (!messageBuckets.containsKey(bucket)) {
            messageBuckets.put(bucket, new ArrayList<>(1024));
        }
        ArrayList<Message> bucketList = messageBuckets.get(bucket);
        bucketList.add(message);
    }

    /**
     * 通过queue与bucket获取下一个要消费的message。
     * <p>获取bucket下一个message的偏移offset，用offset从bucketList中获取message。
     * @param queue
     * @param bucket queue自身或者绑定的topics中的一个。
     */
   public synchronized Message pullMessage(String queue, String bucket) {
        ArrayList<Message> bucketList = messageBuckets.get(bucket);
        if (bucketList == null) {
            return null;
        }
        HashMap<String, Integer> offsetMap = queueOffsets.get(queue);
        if (offsetMap == null) {
            offsetMap = new HashMap<>();
            queueOffsets.put(queue, offsetMap);
        }
        int offset = offsetMap.getOrDefault(bucket, 0);
        if (offset >= bucketList.size()) {
            return null;
        }
        Message message = bucketList.get(offset);
        offsetMap.put(bucket, ++offset);
        return message;
   }
}
