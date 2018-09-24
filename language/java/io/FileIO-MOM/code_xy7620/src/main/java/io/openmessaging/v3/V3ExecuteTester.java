package io.openmessaging.v3;
/**
 * Created by 瑞 on 2017/5/18.
 */
public class V3ExecuteTester {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        V3ProducerTester.main(args);
        System.out.println(System.currentTimeMillis()-start);
        Thread.sleep(5000);
        start=System.currentTimeMillis();
        V3ConsumerTester.main(args);
        System.out.println(System.currentTimeMillis()-start);
        Thread.sleep(1000);
    }
}
