package util;

import io.openmessaging.BytesMessage;
import io.openmessaging.demo.DefaultBytesMessage;
import io.openmessaging.exception.OMSReadFinshedException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 瑞 on 2017/5/12.
 * 反序列化操作
 */
public class Deserialization {
    private File[] files;
    private int index;
    private String[] headerBank;
    private HashMap<String,List<BytesMessage>> data=null;

    List<BytesMessage> list=new ArrayList<>();

    public Deserialization(File[] files, int index, String[] headerBank, HashMap<String,List<BytesMessage>> map){
        this.files=files;
        this.index=index;
        this.headerBank=headerBank;
        data=map;
    }

    static Object obj=new Object();

    public void doDes() {
        try {
            File file = files[index];
            FileInputStream input = new FileInputStream(file);
            // 输入数据读到此字节数组，数组大小需要调试确定
            int bytesSize = 1024 * 1000;
            BufferedInputStream bis = new BufferedInputStream(input, bytesSize);

            /**
             * 该文件读入过程中发生异常则终止该文件读入，即认为读入完毕。
             */
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
                    if (readSize < bytesSize) {
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
                if (data.containsKey(queueOrTopic)) {
                    synchronized (obj) {
                        if (!data.containsKey(queueOrTopic)) {
                            list = new ArrayList<>();
                            data.put(queueOrTopic, list);
                        } else {
                            list = data.get(queueOrTopic);
                        }
                    }
                } else {
                    list = data.get(queueOrTopic);
                }

                // 不对list做任何同步处理，只有前1/10的数据能对
                /**
                 * 这里的list可能会非常长，对List进行加锁的代价会非常大的
                 */
                synchronized (list) {
                    list.add(message);
                }
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
