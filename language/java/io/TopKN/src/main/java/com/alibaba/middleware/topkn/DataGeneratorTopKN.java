package com.alibaba.middleware.topkn;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.google.common.primitives.Bytes;


/**
 * Created by wanshao on 2017/6/27.
 */
public class DataGeneratorTopKN {
    public static void main(String[] args) {
        //1万行约628KB,实际写入大小为lineSize*round，这里round是为了分批处理，避免耗尽内存
        int lineSize=10000; //10000
        int round=16000;  //16000
        for(int i=0;i<round;i++){
            byte[] byteArray =  generateCharData(lineSize);
            String filePath = "/Users/yfu/Workspace/code.aliyun.com/fuyufjh/TopKNTestData/data.txt";
            FlushToDiskUtil.flushToDisk(byteArray,filePath);
        }

    }

    /**
     * 生成指定行数的随机字符文本，\n来换行
     * @param lineSize
     */
    private static byte[] generateCharData(long lineSize){
        SecureRandom random = new SecureRandom();
        byte[] byteArray = new byte[0];
        for(int i=0;i<lineSize;i++){
            //650决定了生成的随机字符串位数不超过128，这个是测试得出的值，去模后加上指定数值也是为了个位数时避免只从几个数里面生成
            int randomInt = abs(random.nextInt() % 650+6);
            String lineData = getRandomString(randomInt);
            byteArray = Bytes.concat(byteArray, lineData.getBytes());

        }

        return byteArray;

    }

    private static String getRandomString(int  randomInt){
        SecureRandom random = new SecureRandom();
        return new BigInteger(randomInt, random).toString(36)+"\n";
    }


    public static int abs(int x) throws ArithmeticException {
        if (x == Integer.MIN_VALUE) {
            throw new ArithmeticException("Math.abs(Integer.MIN_VALUE)");
        }
        return Math.abs(x);
    }
}
