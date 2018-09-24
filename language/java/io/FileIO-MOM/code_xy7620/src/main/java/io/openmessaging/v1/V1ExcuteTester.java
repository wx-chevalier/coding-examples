package io.openmessaging.v1;

/**
 * @author XF
 * 调用官方的两个测试例修改版，直接执行用控制台日志查看结果
 */
public class V1ExcuteTester {

	public static void main(String[] args) throws Exception {
		//不能循环执行，没有清空内存
//		for(int i=0; i<1; i++){
//			V1ProducerTester.main(args);
//			Thread.sleep(5000);
//			V1ConsumerTester.main(args);
//			Thread.sleep(1000);
//		}
		System.out.println((byte)'1');
	}
}
