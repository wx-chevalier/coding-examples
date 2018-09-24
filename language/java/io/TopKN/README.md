>写在前面: 
> 1. 赛题答疑联系人：万少(tel:15669090790)
> 2. 在开始coding前请仔细阅读以下内容




# 1. 题目背景: 模拟阿里双十一的分布式数据库的核心技术
  * 题目主要解决的是NewSQL领域中使用最频繁的一个场景:分页排序，其对应的SQL执行为order by id limit k,n. 
  * 主要的技术挑战为"分布式"的策略，赛题中使用多个文件模拟多个数据分片

# 2. 题目描述

## 2.1 题目内容
 给定一批数据，求解按顺序从小到大，顺序排名从第k下标序号之后连续的n个数据 (类似于数据库的order by asc + limit k,n语义)
  * top(k,3)代表获取排名序号为k+1,k+2,k+3的内容,例:top(10,3)代表序号为11、12、13的内容,top(0,3)代表序号为1、2、3的内容
  * 需要考虑k值几种情况，k值比较小或者特别大的情况，比如k=1,000,000,000
  * 对应k,n取值范围： 0 <= k < 2^63 和 0 < n < 100
  * 数据全部是文本和数字结合的数据，排序的时候按照整个字符串的长度来排序。例如一个有序的排序如下(相同长度情况下ascii码小的排在前面)：
  
  例：
  a
  ab
  abc
  def
  abc123
  
  例如给定的k,n为(2,2)，则上面的数据需要的答案为:
  abc
  def

## 2.2 语言限定
限定使用JAVA语言

# 3. 环境描述和程序要求
有3台机器，简称A、B、C机器，A和B机器主要保存待计算的数据文本，B机器主要为top(k,n)发起者和结果输出者
  * A和B机器上会在固定的目录下各提供5个文本文件(两机器数据各不干扰)，每个文本大概为1GB左右，文本格式: 
    * 纯字符串文本 (文本长度, 0 < len < 128)
    * 每一行代表一条数据记录
    
  * 程序需要分为worker、master两部分，A、B机器部署worker，c机器部署master
    * worker会提供相应的接口来接收top中k和n的具体值、同时需要返回对应的结果
    * master会提供相应的接口来接收文件的绝对路径，允许利用server进行CPU计算和多server之间网络通讯
    * 语言为JAVA，整个运行环境均为XXXX
  * 程序校验
    * 结果校验，会按顺序传递5组k,n值
    * 在所有5轮结果校验通过后(每次k,n参数会不同,k的值会相对比较分散),计算5轮处理时间的总耗时，总耗时越短越好。


# 4. 示例工程说明

用户从阿里云git仓库下载比赛的示例工程，并且实现TopknMaster和TopknWorker当中的类

阿里云内部赛git仓库地址： https://code.aliyun.com/wanshao/topkn_final

TopknMaster: 作为SocketServer监听5527和5528端口，接收TopknWorker的请求。TopknMaster会接收比赛的输入，一组k,n的值。
当请求过来的时候，将k,n的值传递给TopknWorker并且在相应端口上等待worker的结果。处理worker传递的结果数据的方法是processResult方法。

TopknWorker: 作为SocketClient向服务端发起请求，获取k,n的值，并且在其中的processAndSendResult方法进行处理，并且将处理结果返回给服务端。
worker已经设置成可以自动重连。

DataGeneratorTopkn: 比赛生成随机数据的生成器，方便选手自己生成数据测试。生成的数据可以采用shell脚本利用split、sort来得到正确答案。






PS：
1. 选手可以不使用示例工程的代码，但是请确保类名固定为TopknMaster（表示C机器）和TopknWorker(表示A、B机器)
2. 请确保k,n参数的值从main方法的args中按顺序获取
3. TopknMaster中的main接收两个args参数，第一个值为k，第二个值为n
4. TopknWorker中的main接收两个args参数，第一个值为master所在机器的ip，第二个值需要访问的端口。这些参数校验程序会给出的。其中端口必然是5527或者5528

# 5. 数据文件说明

1. 文件个数：10
2. 每个文件大小：1G
3. 文件内容：由字符和数字随机组成，每一行的字符串代表一条数据记录。其大小以其长度来判断，如果长度相同，则比较最左端字符的ascii码
4. 每一行字符串的长度为 [1,128] （数字在Long值范围内均匀分布）
5. 数据文件的命名严格按照规则命名。命名规则："splitX.txt" ，其中X的范围是[1,10]
6. 数据文件存放目录为/home/admin/final24/topkn-datafiles/
7. 数据文件全部放在内存文件系统中

PS: 数据文件均放在worker所在机器的指定目录下，按照指定的命名规则命名。请确保提交的代码从指定路径下以正确的文件名读取文件。否则，将导致算法的校验逻辑失败。


# 6. 结果文件说明
1. 结果文件命名规则：X.rs ，X表示轮次的编号，取值为[1,5]。比赛进行五轮测试，每轮测试都需要生成轮次对应的结果文件，即5轮结束时候
需要生成1.rs、2.rs、3.rs、4.rs、5.rs总共5个文件
2. 结果文件输出目录：/home/admin/final24/topkn-resultfiles/teamCode


PS: 
1. 结果文件的命名和输出必须严格符合赛题要求，否则会影响程序的校验和排名。以上teamCode目录是选手参赛时的teamCode，每个人不同，请留意。
2. **结果文件中的每一行记录后面都必须跟一个换行符(\n)，因为最后比对结果用的是MD5，你少个换行符(\n)可能比对也会失败，切记！**

# 7. 测试环境描述
测试环境为相同的24核物理机，内存为47GB，磁盘使用不做限制(一般不建议选手产生超过10G的中间结果文件)。选手可以使用的JVM堆大小为3G。

PS:
1. 选手的代码执行时，JAVA_OPTS="-XX:InitialHeapSize=3221225472 -XX:MaxDirectMemorySize=209715200 -XX:MaxHeapSize=3221225472 -XX:MaxNewSize=1073741824 -XX:MaxTenuringThreshold=6 -XX:NewSize=1073741824 -XX:OldPLABSize=16 -XX:OldSize=2147483648 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedOops -XX:+UseConcMarkSweepGC -XX:+UseParNewGC"
2. 不准使用堆外内存。




# 8. 程序校验逻辑
1. 参赛选手给出git地址
2. 结果校验和排名程序每天会从提交的git地址上拉取代码进行结果验证和排名
3. 校验程序会给出随机的k值和n值，作为TopknMaster进程启动后的输入。其中k的取值范围是[0,TOTAL_LINE_NUM-100]， n的取值范围是(0,100]。其中TOTAL_LINE_NUM表示所有数据文件的总行数。参赛程序所需的数据文件
在结果校验程序所在服务器会随机生成。
4. 校验程序在master所在机器启动TopknMaster类的main方法，并且给出k和n的值，
5. 校验程序拷贝选手的代码到A、B两台worker机器上，启动TopknWorker类的main方法
6. 选手的TopknWorker请求TopknMaster并且获取k和n的值进行处理，处理后的结果再发送给TopknMaster,在master端产生对应轮次的结果文件x.rs
7. 重复执行第4至第6步，进行5轮，使得在master所在的机器产生5个结果文件
8. 校验程序将用户处理得到的5个结果文件和"标准答案"文件一一做MD5校验，如果全部一致，则认为算法正确，计算耗时并且统计排名。


![pic](https://img.alicdn.com/tfs/TB1IYxgQXXXXXbaXVXXXXXXXXXX-360-307.png)


PS:
1. 调用选手的程序是通过脚本"java -cp $yourJarPath com.alibaba.middleware.race.TopknMaster $k $n" 这样的形式来调用的。
该脚本会调用5次，当然每次的k,n都是随机的。JVM的启动和停止也会算在耗时里面请注意。
2. 校验程序本身通过http请求通知worker机器上的校验程序启动选手的TopknWorker，因此本身也会有一些开销。如果像示例代码一样，master和worker基本都是
什么都不做，跑完5次整个链路，约有9秒多的耗时。这个是测试几次的数据。


# 9. 排名规则

在结果校验100%正确的前提下，按照总耗时进行排名，耗时越少排名越靠前，时间精确到毫秒



# 10. 可以使用的类的约定

* 公平起见，仅允许依赖JavaSE 7 包含的标准库。此外不允许使用nio包下的file channel等使用堆外内存的方法，以及其他使用堆外内存的类。
* 标准IO的类都可以使用，里面涉及nio的FileChannel不可以用
* 不允许使用Unsafe类



# 11. 关于选手中间结果的补充

1. 比赛过程中，选手可以自由利用磁盘的空间。中间结果在进行选手的5轮测试的时候，均可以使用，不会被清空。选手的5轮测试结束后，选手的中间结果会被清空，下次评测使用中间结果需要重新生成。程序校验结束后，所有中间结果全部清空。中间结果请输出到指定目录：/home/admin/final24/middle/teamCode下。

2. 请不要投机取巧将中间结果写入到非指定目录，然后在下一次再校验的时候去读取，从而来改进自己的比赛成绩。校验程序不会特地去清理写在其他目录选手产生的临时、中间结果文件，但是工作人员会不定时去服务器上抽查。

3. 请大家诚信为本，靠自己真正的实力赢得比赛。比赛后续还有答辩，所以投机取巧是不可行哦。


# 12. 代码提交相关的补充

代码提交评测的步骤：
1. 选手将自己的代码提交到[阿里云code](https://code.aliyun.com)上，并且在自己的私人项目添加项目成员"middlewarerace2017"，并将其角色设置为：develeoper
2. 选手在天池系统上通过设置自己的代码git仓库地址（此时会生成teamCode，该teamCode会在很多地方用到，例如写指定的中间文件目录、结果文件目录等）
3. 选手点击提交按钮，评测机器会自己拉取选手的代码，build选手的代码，然后运行相应的jar。
4. 如果选手代码的结果是正确的，则消耗掉一次评测机会，选手总共有30次评测机会。评测系统下载选手代码失败、编译构建失败，不消耗评测次数；如果代码运行超时或者得到的答案错误，将消耗一次评测机会，请大家提交代码前做好本地的验证工作！

PS：
1.  评测系统使用maven assemble插件构建选手程序。建议选手本地先运行"mvn clean assembly:assembly -Dmaven.test.skip=true" 命令，确保能正确构建再提交
2.  不要忘记添加指定成员为develeoper
3.  teamCode在正式开放代码提交入口之前，是无法获得的。等天池开放代码提交入口后，选手设定自己的git地址后，会获得唯一的teamCode

# 13. 代码评测相关的补充
1. 超时时间： 一次提交，会接收5轮输入。第一轮处理的超时时间为5分钟，后面四轮的超时时间为1分钟。意味着每位选手最多9分钟的处理时间。
2. 日志处理：
    - 选手可以使用样例工程中的日志框架，注意改成自己的teamCode
    - 日志命名严格按照样例工程中logback.xml中的配置
3. 如何获取自己运行的日志：
    - 选手可以通过地址：http://middle2017.oss-cn-shanghai.aliyuncs.com/${teamCode}/server.log.tar.gz或者client.log.tar.gz来获取日志
    - 以上打包了运行期间的tsar信息、gc日志和评测日志。另外由于技术实现原因，TopknMaster端不提供gc日志。




# 15. 违规行为的补充说明
选手凡是利用赛题漏洞，或者通过破解评测程序等手段获取的成绩均无效。例如：
1. 日志记录正确的结果，下一次评测直接读取正确结果
2. 通过随机种子推算比赛所使用的数据文件内容
3. 中间结果故意写到非指定目录，下次直接读取中间结果
4. 其他一切推演评测所用的数据文件、利用提前推测出来的k、n值或者写入非指定目录来提升自己成绩的行为
5. 使用违规的类，堆外内存、Unsafe类等



# 16. FAQ补充

#### 1. 请大家在向天池系统提交代码前，仔细检查自己的代码，避免由于程序问题影响天池的校验程序。一些恶意行为的话，可能会被追究责任哟。例如在非指定目录写入大量文件把天池评测系统磁盘撑爆、编写恶意代码破坏天池系统正常评测等等。

#### 2. 是否可以使用堆外内存？答：不可以，仅在堆内内存处理数据。例如MappedByteBuffer、DirectByteBuffer这些涉及堆外内存的类不可以使用。

#### 3. 是否可以使用JAVA以外的语言？ 答：不可以，JNI调用、shell脚本调用都不行

#### 4. 最大文件打开数是多少？

```
$ulimit -a
core file size          (blocks, -c) 0
data seg size           (kbytes, -d) unlimited
scheduling priority             (-e) 0
file size               (blocks, -f) unlimited
pending signals                 (-i) 386774
max locked memory       (kbytes, -l) unlimited
max memory size         (kbytes, -m) unlimited
open files                      (-n) 655350
pipe size            (512 bytes, -p) 8
POSIX message queues     (bytes, -q) 819200
real-time priority              (-r) 0
stack size              (kbytes, -s) 10240
cpu time               (seconds, -t) unlimited
max user processes              (-u) 386774
virtual memory          (kbytes, -v) unlimited
file locks                      (-x) unlimited

```

#### 5. CPU信息怎样的？答：24核，其中1个核的信息如下：

```
processor	: 23
vendor_id	: GenuineIntel
cpu family	: 6
model		: 45
model name	: Intel(R) Xeon(R) CPU E5-2430 0 @ 2.20GHz
stepping	: 7
cpu MHz		: 2199.853
cache size	: 15360 KB
physical id	: 1
siblings	: 12
core id		: 5
cpu cores	: 6
apicid		: 43
initial apicid	: 43
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic mtrr pge mca cmov pat pse36 clflush dts acpi mmx fxsr sse sse2 ss ht tm pbe syscall nx pdpe1gb rdtscp lm constant_tsc arch_perfmon pebs bts rep_good xtopology nonstop_tsc aperfmperf pni pclmulqdq dtes64 ds_cpl vmx smx est tm2 ssse3 cx16 xtpr pdcm dca sse4_1 sse4_2 x2apic popcnt aes xsave avx lahf_lm arat epb xsaveopt pln pts dts tpr_shadow vnmi flexpriority ept vpid
bogomips	: 4399.41
clflush size	: 64
cache_alignment	: 64
address sizes	: 46 bits physical, 48 bits virtual
power management:
```

#### 6. 磁盘信息怎样的？

```
$sudo fdisk -l /dev/sda5

Disk /dev/sda5: 424.0 GB, 423999045632 bytes
255 heads, 63 sectors/track, 51548 cylinders
Units = cylinders of 16065 * 512 = 8225280 bytes
Sector size (logical/physical): 512 bytes / 4096 bytes
I/O size (minimum/optimal): 4096 bytes / 4096 bytes
Disk identifier: 0x00000000




```

#### 7. k值超过总行数的情况要考虑吗？答: 不需要考虑，k+n的值肯定是小于等于总行数的

#### 8. 生成的数据是否会重复，是否需要去重？答：不需要去重，生成数据的方法可以参考样例工程中的数据生成方法

#### 9. JDK版本详情：

```
java version "1.7.0_80"
Java(TM) SE Runtime Environment (build 1.7.0_80-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.80-b11, mixed mode)
```

#### 10. 可以使用多线程吗？答：可以

#### 11. 可以用java的Runtime.execute和ProcessBuilder.start等方法来启动LINUX的守护进程吗？答：不可以


#### 12. 可以使用nio吗？答：可以，但是不允许使用file channel等会开辟堆外内存的方法

#### 13. 生成的数据会重复吗？答：有可能会重复，但是不需要做去重。

#### 14. 是否开启了超线程？答：已经开启

#### 15. 内存文件系统读写速度和SSD读写速度怎样？

```
# ---------------------------- 内存文件系统 ------------------------
# 写性能
$sudo  time dd if=/dev/zero of=/home/admin/final24/myram/test.txt  bs=8k count=1000000
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 5.31966 s, 1.5 GB/s
0.08user 5.23system 0:05.32elapsed 99%CPU (0avgtext+0avgdata 3792maxresident)k
0inputs+0outputs (0major+364minor)pagefaults 0swaps

# 读性能
$sudo time dd if=/home/admin/final24/myram/test.txt  of=/dev/null bs=8k count=1000000
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 2.1377 s, 3.8 GB/s
0.07user 2.06system 0:02.13elapsed 99%CPU (0avgtext+0avgdata 3760maxresident)k
0inputs+0outputs (0major+362minor)pagefaults 0swaps

# ---------------------------- SSD系统 ------------------------
# 写性能
$sudo  time dd if=/dev/zero of=/data/test  bs=8k count=1000000
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 18.6796 s, 439 MB/s
0.19user 15.03system 0:18.80elapsed 80%CPU (0avgtext+0avgdata 3760maxresident)k
1152inputs+16000000outputs (1major+360minor)pagefaults 0swaps
# 读性能
$sudo time dd if=/dev/sda5  of=/dev/null bs=8k count=1000000
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 25.6534 s, 319 MB/s
0.13user 7.93system 0:25.65elapsed 31%CPU (0avgtext+0avgdata 3792maxresident)k
16000320inputs+0outputs (1major+362minor)pagefaults 0swaps
```

#### 16. 评测会清空系统缓存吗？答：每次运行选手的程序前都会用命令"sudo sysctl -w vm.drop_caches=3"清空系统缓存

#### 17. 评测的时候出现assembly错误怎么办？

```
1. 参考样例工程，确保使用如下的构件名：
    <groupId>com.alibaba.middleware.race</groupId>
    <artifactId>limitkndemo</artifactId>
    <version>1.0</version>
    <name>limitkndemo</name>
2. 确保本地使用mvn clean assembly:assembly -Dmaven.test.skip=true命令测试正确

3. 确保你的阿里云code项目已经添加项目成员"middlewarerace2017"，并将其角色设置为：develeoper

4. 在阿里云代码提交的时候，确保git地址的正确性

推荐做法： 直接下载样例工程，把自己的TopKN主类搞进去
```

#### 18. 评测的时候出现找不到结果文件怎么办？答：参考第六条，确保自己的结果文件命名正确，并且在指定目录下。好多漏掉teamCode的。另外保证构件名字正确，参考上面。

#### 19. 那些行为消耗评测次数？答： 结果超时、结果正确、生成结果并且比对答案的时候错误。PS：编译出错、由于目录原因找不到指定结果文件都不消耗评测次数

#### 20. 比赛使用的数据文件每次都不一样吗？答： 由于生成数据文件比较耗时，为了加速评测效率，整个比赛期间都是使用一份数据文件的。但是请选手尽量不要做数据硬编码的事情降低算法的工程价值。

#### 21. 哪里获取teamCode? 答:在天池提交代码处设置自己的git地址，即可在下方好看到自己的teamcode

#### 22. 排行榜啥时候更新？ 答：已经调整为每小时更新一次


#### 23. sudo blockdev --getra /dev/sda的值为多少？ 256


#### 24. 哪些情况消耗评测次数？1. 结果不正确 2. 结果正确 3. 结果超时 4. 找不到结果文件 （ 因此请选手提交代码前一定要注意自己的代码准确性）

#### 25. 磁盘的并发IO性能如何？答： 采用8个进程并发读写性能如下

```

# 测试用脚本
for i in `seq 8`; do sudo time dd if=/dev/zero  of=/data/test$i  bs=8k count=1000000  & done; wait;


# 测试写性能（8个进程写8个文件，每个文件写8.2G）
vm.drop_caches = 3
^@^@1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 180.778 s, 45.3 MB/s
0.29user 65.77system 3:00.77elapsed 36%CPU (0avgtext+0avgdata 3792maxresident)k
640inputs+16000000outputs (0major+364minor)pagefaults 0swaps
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 181.109 s, 45.2 MB/s
0.27user 66.48system 3:01.11elapsed 36%CPU (0avgtext+0avgdata 3760maxresident)k
544inputs+16000000outputs (1major+360minor)pagefaults 0swaps
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 181.114 s, 45.2 MB/s
0.31user 66.78system 3:01.11elapsed 37%CPU (0avgtext+0avgdata 3776maxresident)k
312inputs+16000000outputs (0major+362minor)pagefaults 0swaps
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 181.116 s, 45.2 MB/s
 0.29user 66.31system 3:01.11elapsed 36%CPU (0avgtext+0avgdata 3760maxresident)k
 856inputs+16000000outputs (1major+360minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 181.247 s, 45.2 MB/s
 0.30user 66.18system 3:01.24elapsed 36%CPU (0avgtext+0avgdata 3776maxresident)k
 448inputs+16000000outputs (0major+362minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 181.322 s, 45.2 MB/s
 0.30user 66.06system 3:01.32elapsed 36%CPU (0avgtext+0avgdata 3792maxresident)k
 656inputs+16000000outputs (0major+364minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 181.323 s, 45.2 MB/s
 0.28user 66.55system 3:01.32elapsed 36%CPU (0avgtext+0avgdata 3760maxresident)k
 376inputs+16000000outputs (0major+361minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 181.338 s, 45.2 MB/s
 0.29user 66.78system 3:01.33elapsed 36%CPU (0avgtext+0avgdata 3760maxresident)k
 448inputs+16000000outputs (1major+360minor)pagefaults 0swaps
 
 
 
 # 测试读性能（8个进程读8个8.2G的文件）
 vm.drop_caches = 3
 ^@^@1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 133.928 s, 61.2 MB/s
 0.12user 4.06system 2:13.92elapsed 3%CPU (0avgtext+0avgdata 3792maxresident)k
 16000000inputs+0outputs (0major+363minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 133.948 s, 61.2 MB/s
 0.12user 4.20system 2:13.94elapsed 3%CPU (0avgtext+0avgdata 3760maxresident)k
 16000000inputs+0outputs (0major+361minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 133.962 s, 61.2 MB/s
 0.12user 4.16system 2:13.96elapsed 3%CPU (0avgtext+0avgdata 3760maxresident)k
 16000000inputs+0outputs (0major+361minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 133.988 s, 61.1 MB/s
 0.13user 4.19system 2:13.98elapsed 3%CPU (0avgtext+0avgdata 3760maxresident)k
 16000000inputs+0outputs (0major+362minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 133.989 s, 61.1 MB/s
 0.12user 4.08system 2:13.99elapsed 3%CPU (0avgtext+0avgdata 3792maxresident)k
 16000104inputs+0outputs (1major+362minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 134.006 s, 61.1 MB/s
 0.11user 4.15system 2:14.00elapsed 3%CPU (0avgtext+0avgdata 3792maxresident)k
 16000000inputs+0outputs (0major+364minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 134.012 s, 61.1 MB/s
 0.10user 4.11system 2:14.01elapsed 3%CPU (0avgtext+0avgdata 3776maxresident)k
 16000000inputs+0outputs (0major+363minor)pagefaults 0swaps
 1000000+0 records in
 1000000+0 records out
 8192000000 bytes (8.2 GB) copied, 134.014 s, 61.1 MB/s
 0.10user 4.11system 2:14.01elapsed 3%CPU (0avgtext+0avgdata 3792maxresident)k
 16000000inputs+0outputs (0major+363minor)pagefaults 0swaps
```


#### 网络IO和延迟情况如何：

```
采用qperf的测试结果如下（一分钟）：
带宽：941MB/s
网络延迟：28.8us
```