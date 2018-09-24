package io.openmessaging.tester;

public class Constants {

//    public final static String STORE_PATH = System.getProperty("store.path", "/tmp");
//    public final static String STORE_PATH = System.getProperty("store.path", "store");
    public final static String STORE_PATH = System.getProperty("store.path", "src/main/resources/store");
    public final static int PRO_NUM = Integer.valueOf(System.getProperty("pro.num", "10"));
    public final static int CON_NUM = Integer.valueOf(System.getProperty("con.num", "10"));
    public final static String PRO_PRE = System.getProperty("pro.pre","PRODUCER_");
    public final static int PRO_MAX = Integer.valueOf(System.getProperty("pro.max","4000000"));
    public final static String CON_PRE = System.getProperty("con.pre", "CONSUMER_");
    public final static String TOPIC_PRE = System.getProperty("topic.pre", "TOPIC_");
    public final static String QUEUE_PRE = System.getProperty("topic.pre", "QUEUE_");

    //每次放入或者读入到byte[]的大小，必须放整数个对象
//    public final static int Bytes_Size = Integer.valueOf(System.getProperty("bytes.size", "4096"));
}
