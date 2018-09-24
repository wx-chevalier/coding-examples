>写在前面: 
> 1. 在开始coding前请仔细阅读以下内容


## 1. 题目背景
支撑阿里双十一的万亿级消息中间件RocketMQ在去年10月进入Apache基金会进行孵化。异步解耦，削峰填谷，让消息中间件在现代软件架构中扮演者举足轻重的地位。天下大势，分久必合，合久必分，软件领域也是如此。市场上消息中间件种类繁多，阿里消息团队在此当口，推出厂商无关的[Open-Messaging](https://openmessaging.github.io)规范，促进消息领域的进行一步发展。

## 2. 题目描述

### 2.1 题目内容
阅读Open-Messaging的接口代码(本工程内除了demo目录外的其他代码)，了解Topic，Queue的基本概念，并实现一个进程内消息引擎。

提示：Topic类似于水坝（蓄积功能，消峰填谷之利器），Queue类似于水渠；每当新建一个Queue的时候，可以选择绑定到几个Topic，类似于水渠从水坝引水；
每个Topic可以被任意多个Queue绑定，这点与现实生活不太一样，因为数据可以多次拷贝；
在发送的时候，可以选择发送到Topic，也可以选择直接发送到Queue；直接发送到Queue的数据只能被对应Queue消费，不能被其他Queue读取到；
在消费的时候，除了要读取绑定的Topic的数据，还要去取直接发送到该Queue的数据；


### 2.2 语言限定
限定使用JAVA语言

## 3.  程序目标

你的coding目标是实现以下接口:

 * Producer的createBytesMessageToTopic(topic, body)
 * Producer的createBytesMessageToQueue(queue, body)
 * Producer的send(message)
 * PullConsumer的attachQueue(queue, topics)
 * PullConsumer的poll()

实现类名字分别叫做:DefaultProducer,DefaultPullConsumer包名均是：io.openmessaging.demo
请参考demo目录，建议是名字不要改，修改实现内容即可
注意，消息内容需要存入磁盘中，并能再次读取出来


## 4.参赛方法说明
1. 在阿里天池找到"中间件性能挑战赛"，并报名参加
2. 在code.aliyun.com注册一个账号，并新建一个仓库名，并将大赛官方账号middlewarerace2017添加为项目成员，权限为reporter
3. fork或者拷贝本仓库的代码到自己的仓库，并实现自己的逻辑
4. 在天池提交成绩的入口，提交自己的仓库git地址，等待评测结果
5. 坐等每天10点排名更新


## 4. 测试环境描述
测试环境为相同的4核虚拟机(暂定)。
1. 机器4核CPU，4G内存；
2. 发送和消费进程的JVM：-Xms2560M -Xmx2560M
3. 本地编译(mvn clean package -U assembly:assembly -Dmaven.test.skip=true),测评程序拷jar的命令为：cp code/target/*.jar code/target/OpenMessagingDemo/OpenMessagingDemo/lib、*.jar $tmplib, 请确保自己的target下有对应目录，以免jar包没拷贝到，而找不到对应的类
4. 评测日志下载地址：http://middle2017.oss-cn-shanghai.aliyuncs.com/{teamcode}.logs.tgz; teamcode为各自队伍的标识；每次评测都会覆盖该日志；

5. 系统限制

```
$ulimit -a
core file size          (blocks, -c) 0
data seg size           (kbytes, -d) unlimited
scheduling priority             (-e) 0
file size               (blocks, -f) unlimited
pending signals                 (-i) 31546
max locked memory       (kbytes, -l) 64
max memory size         (kbytes, -m) unlimited
open files                      (-n) 65535
pipe size            (512 bytes, -p) 8
POSIX message queues     (bytes, -q) 819200
real-time priority              (-r) 0
stack size              (kbytes, -s) 10240
cpu time               (seconds, -t) unlimited
max user processes              (-u) 31546
virtual memory          (kbytes, -v) unlimited
file locks                      (-x) unlimited
```

6. 磁盘信息

```
$sudo fdisk -l /dev/vda

Disk /dev/vda: 268.4 GB, 268435456000 bytes
255 heads, 63 sectors/track, 32635 cylinders
Units = cylinders of 16065 * 512 = 8225280 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disk identifier: 0x000380f4

   Device Boot      Start         End      Blocks   Id  System
/dev/vda1   *           1       32636   262142976   83  Linux
```

7. 磁盘读写速度(供参考)

```
写速度
$sudo  time dd if=/dev/zero of=/home/admin/test  bs=1k count=1000000
1000000+0 records in
1000000+0 records out
1024000000 bytes (1.0 GB) copied, 10.2137 s, 100 MB/s
0.17user 5.65system 0:10.43elapsed 55%CPU (0avgtext+0avgdata 3776maxresident)k
56inputs+2000000outputs (0major+363minor)pagefaults 0swaps
读速度
$sudo time dd if=/dev/vda  of=/dev/null bs=1k count=1000000
1000000+0 records in
1000000+0 records out
1024000000 bytes (1.0 GB) copied, 24.9769 s, 41.0 MB/s
0.09user 2.16system 0:24.98elapsed 9%CPU (0avgtext+0avgdata 3776maxresident)k
2000272inputs+0outputs (0major+362minor)pagefaults 0swaps
```



## 5. 程序校验逻辑

1. 10~20个线程(位于同一进程中)各自独立调用Producer发送消息(每个线程启动一个Producer，每条消息随机发送到某个Topic或者Queue)，持续时间**T1**，请注意把消息数据写入磁盘中
2. 强行kill Producer进程，未写入磁盘的消息都会丢失
3. 10~20个线程(位于同一进程中)独立调用Consumer收取消息(每个线程启动一个Consumer，attach到指定的Queue，不同的Consumer不会attach同一个Queue），验证消息顺序准确性，可靠性，消费持续的时间为**T2**，消费到的总消息数目为**N**
4. 以**N/(t1+t2)**来衡量性能

    PS：请仔细阅读本仓库内Demo代码，尤其是DemoTester

### 5.1 补充说明
1. 测试时，topic和queue的数目大约是100个(其中queue的数目与消费者线程数相等)；
2. 测试时，消息大小不会超过256K；
3. 可靠性是指，消息不能丢失，且消息的内容不能被篡改；在测试消费的时候，会对消息的body,headers,properties的内容进行校验；
4. header与properties中key和value都不会插入null或空值
5. 发送消息时，消息一定要写入磁盘，最简单的组织形式就是，一个topic或者queue一个文件，每条消息一行（这样做性能可能较低，请自行优化设计）

### 5.2 消息顺序的说明
顺序只针对单个topic或者queue，不同topic，不同queue，topic与queue之间都不用考虑顺序

消息产品的一个重要特性是顺序保证，也就是消息消费的顺序要与发送的时间顺序保持一致；在多发送端的情况下，保证全局顺序代价比较大，只要求各个发送端的顺序有保障即可；
举个例子P1发送M11,M12,M13，P2发送M21,M22,M23，在消费的时候，只要求保证  M11,M12,M13(M21,M22,M23)的顺序，也就是说，实际消费顺序为:    
M11,M21,M12,M13,M22,M23 正确；
M11,M21,M22,M12,M13,M23 正确；
M11,M13,M21,M22,M23,M12 错误，M12与M13的顺序颠倒了；  

## 6. 排名规则

在结果校验100%正确的前提下，按照N/(t1 + t2)从高到低来排名


## 7. 第二/三方库规约

* 仅允许依赖JavaSE 8 包含的lib
* 可以参考别人的实现，拷贝少量的代码
* 我们会对排名靠前的代码进行review，如果发现大量拷贝别人的代码，将扣分


