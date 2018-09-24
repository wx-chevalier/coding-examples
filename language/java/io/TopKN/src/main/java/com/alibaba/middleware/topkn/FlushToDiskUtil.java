package com.alibaba.middleware.topkn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * 写出文件的工具类
 * Created by wanshao on 2017/6/5.
 */
public class FlushToDiskUtil {

    public static void main(String[] args) {

    }

    public static void flushToDisk(byte[] resultBytes,String filePath) {
        File destFile = new File(filePath);

        FileChannel fileInputChannel = null;
        try {
            if(!destFile.exists()){
                destFile.createNewFile();
            }

            //获取可写的file channel；使用FileInputStream是只读
            RandomAccessFile raf   = new RandomAccessFile(destFile,"rw");
            //设置指针位置为文件末尾
            long fileLength = raf.length();
            raf.seek(fileLength);
            fileInputChannel = raf.getChannel();

            //映射的字节数
            long size = resultBytes.length;
            MappedByteBuffer buf = fileInputChannel.map(MapMode.READ_WRITE, fileInputChannel.position(), size);
            buf.put(resultBytes);
            buf.force();
            fileInputChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
