package io.openmessaging.v2;

/**
 * @author XF
 * 调用官方的两个测试例修改版，直接执行用控制台日志查看结果
 */
public class V2ExcuteTester {

	public static void main(String[] args) throws Exception {
		//不能循环执行，没有清空内存
//		for(int i=0; i<1; i++){
//			long start = System.currentTimeMillis();
			V2ProducerTester.main(args);
//			System.out.println(System.currentTimeMillis()-start); 
//			Thread.sleep(5000);
//			start=System.currentTimeMillis();
			V2ConsumerTester.main(args);
//			System.out.println(System.currentTimeMillis()-start);
//			Thread.sleep(1000);
//		}
//		byte[] bs = "asdfsa_SDFSF".getBytes();
//		System.out.println(new String(bs,0,9));
//		System.out.println("ab".getBytes()[0]);
//		System.out.println(Arrays.toString("a;b".getBytes()));
//		System.out.println((int)'a');
//		int i=0;
//		i++;
//		System.out.println(++i==1);
	}
}
