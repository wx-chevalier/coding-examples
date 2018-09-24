> 写在前面
1. 赛题答疑联系人(可通过旺旺联系)：万少（手机：15669090790）
2. 开始Coding前请仔细阅读以下内容
3. 外部赛复赛旺旺群：1251809708
4. 该文档根据实际情况会进行更新，选手每次写代码前建议pull下该样例工程，查看README文件的变化



## 重要提示：2017年6月12日，预热赛评测通道临时关闭，评测机器做一些安全加固。
## 再次提醒选手，禁止在JAVA程序中启shell脚本，可允许读的目录只有canal_data下的10份数据文件
## 允许写的目录只有/home/admin/middle目录和自己teamCode对应的结果文件目录，禁止操作其他目录。
## 违反比赛规则的将视情况追究责任


# ================================================== 赛题规则 ============================================================

# 1. 赛题描述
题目主要解决的是数据同步领域范畴：实时增量同步，主要的技术挑战为模拟数据库的主备复制，提供"高效"的实时同步能力。即给定一批固定的增量数据变更信息，程序需要收集增量变更信息，并进行一定的数据重放计算，然后将最终结果输出到给定的目标文件中。增量数据的变更信息为了简化处理，会给出明文的数据，主要包含数据库的insert/update/delete三种类型的数据。具体的增量数据变更信息的数据格式见环境描述部分。数据重放主要是指模拟数据库的insert/update/delete语义，允许使用一些中间过程的存储。

# 2. 环境描述：
## 2.1 整体格式
有2台机器，简称A、B机器，A机器里会保存增量数据文件并且在固定的目录下提供10个文本文件，每个文本文件大概为1GB左右。每个文件有若干条变更信息。每条变更信息的记录由多列构成。文本中每行记录的格式为:

|     binaryId   | timestamp   |schema|table|变更类型|列信息|变更前列值| 变更后列值|列信息|列值|...|
| ------------- |-------------| -------------| -------------| -------------| ------------| -------------| -------------|------------| -------------|-------------| 


## 2.2 格式解释：

a. binaryId： 一个唯一的字符串编号,例子:000001:106

b. timestamp
  * 数据变更发生的时间戳,毫秒精度,例子:1489133349000
  
c. schema/table
  * 数据变更对应的库名和表名
  
d. 变更类型(主要分为I/U/D)
  * I代表insert, U代表update, D代表delete
  
d. 列信息
  * 列信息主要格式为，列名:类型:是否主键
  * 类型主要分为1和2
  * 1代表为数字类型，数字类型范围为0<= x <= 2^64-1
  * 2代表为字符串类型，0<= len <= 65536
  * 是否主键:0或者1 (0代表否，1代表是) 
  * 例1： id:1:1 代表列名为id,类型为数字,是主键
  * 例2： name:2:0 代表列名为name,类型为字符串,非主键
  

e. 列值
 * 主要分为变更前和变更后,NULL代表物理值为NULL(空值),(可不考虑字符串本身为"NULL"的特殊情况)
 * insert变更,只有变更后列值,其变更前列值为NULL,会包含所有的列信息
 * upadate变更,都会有变更前和后的列值,会包含主键和发生变更列的信息(未发生变更过的列不会给出,不是全列信息)
 * delete变革,只有变更前列值,会包含所有的列信息

## 2.3 格式例子
 

实际例子:
 * 000001:106|1489133349000|test|user|I|id:1:1|NULL|102|name:2:0|NULL|ljh|score:1:0|<NULL>|98|
 * 000001:106|1489133349000|test|user|U|id:1:1|102|102|score:1:0|98|95|  //执行了变更update score=95 where id=102


## 2.4 注意事项

1. 每一行代表一条变更数据,注意几个数据变更场景
2. 表的主键值也可能会发生update变更
3. 表的一行记录也可能发生先delete后insert
3. 整个数据变更内容是从零条记录开始构建的，即为任意一条行记录的update之前一定会有对应行的insert语句，delete之前也一定有一条insert,不考虑DDL变更产生列类型变化，也不需要考虑其他异常情况


#3. 程序要求
## 3.1 程序实现

分为server和client两部分：
1. server会传入对应的文本的绝对路径地址，启动后需要开启网络服务，等待client建立链接之后，需要使用push机制主动开始推送数据到client
2. client在启动时会传入server机器地址，主动和server建立链接之后，等待server推送数据，接收到数据后进行数据重放处理，等收完所有数据后，生成最终的结果到给定的目标文件名.

## 3.2 程序校验
结果校验，会传入一批数据(格式为:schema+table+pk)，程序要按顺序返回每个主键对应记录的所有列的最终值.时间计算，会计算从程序启动到最后返回结果的时间差值

## 3.3 使用语言
JAVA

# 4. 排名规则

在结果校验100%正确的前提下，按照总耗时进行排名，耗时越少排名越靠前，时间精确到毫秒。

# ================================ 如何模拟赛题数据  ===================================

1. 从阿里云代码仓库下载[canal(复赛模拟数据版)](https://code.aliyun.com/wanshao/canal.git)。canal是一款开源的mysql实时数据解析工具，在github的地址为[canal(开源版)](https://github.com/alibaba/canal)
2. 自己安装好mysql，并且建好相关的schema和table
3. 根据[QuickStart](https://github.com/alibaba/canal/wiki/QuickStart)启动canal server
4. 启动[canal(复赛模拟数据版)](https://code.aliyun.com/wanshao/canal.git)中的SimpleCanalClientTest这个客户端程序(建议直接在IDE中启动)
5. 对mysql数据库进行一些DML操作，触发binlog变更，默认在/Users/wanshao/work/canal_data/canal.txt路径下会生成符合赛题要求的变更数据

PS：
1. 修改默认路径请更改AbstractCanalClientTest.storeChangeToDisk()方法
2. canal中转化数据的代码请看：analyseEntryAndFlushToFile()方法
3. canal中解析生成赛题数据时，现在只有int类型换解析成"数字类型"，其他的类型都会解析成"字符串类型"
4. 生成大量数据的时候请使用canal项目里面的startup.sh里面的JVM参数启动CanalLauncher和SimpleCanalClientTest，避免GC问题


为了方便选手测试，也提供了生产好的数据和答案(用的钉钉的网盘)，选手可以下载，其中数据文件可以通过split命令自行分割成10个小文件来测试
- [测试文件](https://space.dingtalk.com/c/ggHaACQwZmE1ZDc3MC03MzcwLTQ1NjAtYWI4Mi00MTU4YjBlMTQxNDUCzhn0eXw)

另外选手可以使用如下的存储过程来生成数据：

```
CREATE TABLE student(
  id INT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(10) NOT NULL,
  last_name VARCHAR(10) NOT NULL,
  sex VARCHAR(5) NOT NULL,
  score INT NOT NULL,
  PRIMARY KEY (`id`)
);


/**增加学生数据的存储过程-- **/
DROP PROCEDURE IF EXISTS add_student;  
DELIMITER //
    create PROCEDURE add_student(in num INT)
    BEGIN
        DECLARE rowid INT DEFAULT 0;
        DECLARE firstname CHAR(1);
        DECLARE name1 CHAR(1);
        DECLARE name2 CHAR(1);
        DECLARE lastname VARCHAR(3) DEFAULT '';
        DECLARE sex CHAR(1);
        DECLARE score INT DEFAULT 0;
        DECLARE updateFirstName  CHAR(1);
        DECLARE updateLastName CHAR(1);
        DECLARE updateScore INT DEFAULT 0;
        SET @exedata = "";
        WHILE rowid < num DO
            SET firstname = SUBSTRING('赵钱孙李周吴郑王林杨柳刘孙陈江阮侯邹高彭徐',FLOOR(1+21*RAND()),1); 
            SET name1 = SUBSTRING('一二三四五六七八九十甲乙丙丁静景京晶名明铭敏闵民军君俊骏天田甜兲恬益依成城诚立莉力黎励',floor(1+43*RAND()),1); 
            SET name2 = SUBSTRING('一二三四五六七八九十甲乙丙丁静景京晶名明铭敏闵民军君俊骏天田甜兲恬益依成城诚立莉力黎励',floor(1+43*RAND()),1); 
            SET sex=SUBSTRING('男女',floor(1+2*RAND()),1);
            SET score= FLOOR(40 + (RAND() *60));
            SET lastname=SUBSTRING('一二三四五六七八九十甲乙丙丁静景京晶名明铭敏闵民军君俊骏天田甜雨恬益依娥我他刚人发上乐',floor(1+43*RAND()),1);
            SET rowid = rowid + 1;
            IF ROUND(RAND())=0 THEN 
            SET lastname =name1;
            END IF;
            IF ROUND(RAND())=1 THEN
            SET lastname = CONCAT(name1,name2);
            END IF;
            IF length(@exedata)>0 THEN
            SET @exedata = CONCAT(@exedata,',');
            END IF;
            SET @exedata=concat(@exedata,"('",firstname,"','",lastname,"','",sex,"','",score,"')");
            IF rowid%10000=0
            THEN 
                SET @exesql =concat("insert into student(first_name,last_name,sex,score) values ", @exedata);
                prepare stmt from @exesql;
                execute stmt;
                DEALLOCATE prepare stmt;
                SET @exedata = "";
            END IF;
        END WHILE;
        
        
        IF length(@exedata)>0 
        THEN
            SET @exesql =concat("insert into student(first_name,last_name,sex,score) values ", @exedata);
            prepare stmt from @exesql;
            execute stmt;
            DEALLOCATE prepare stmt;
        END IF; 
    END //
DELIMITER ;


/**更新学生数据的存储过程-- **/
DROP PROCEDURE IF EXISTS update_student;  
DELIMITER //
    create PROCEDURE update_student(in num INT)
    BEGIN
		DECLARE rowid INT DEFAULT 0;
        DECLARE randomNum INT DEFAULT 0;
        WHILE rowid < num DO
			SET randomNum =  FLOOR(10 + (RAND() *1000));
			SET rowid = rowid + 1;
			update student set score=randomNum where id>0;
        END WHILE;
    END //
DELIMITER ;


/**删除学生数据的存储过程-- **/
DROP PROCEDURE IF EXISTS delete_student;  
DELIMITER //
    create PROCEDURE delete_student(in num INT)
    BEGIN
		DECLARE rowid INT DEFAULT 0;
        WHILE rowid < num DO
			SET rowid = rowid + 1;
			delete from student where id mod 2=0;
        END WHILE;
    END //
DELIMITER ;



/**调用方式 PS:选手可以修改存储过程中的where条件来做不同的变更**/
call add_student(10);
call update_student(10);
call delete_student(10);
```



# ========================= 评测程序如何工作 ============================================
概要说明：评测程序也分为Server和Client，请留意

1. 从天池拉取选手git地址
2. 对选手代码在评测程序的server端进行编译打包
3. 将选手代码从评测程序的server端拷贝到评测程序的client端
4. 在评测程序server端记录开始时间startTime
5. 通过shell脚本在评测程序的server端启动选手的server程序
6. server端评测程序通过HTTP请求通知client端评测程序去启动选手的client端程序(会传给client端一个serverIp)

```
# 启动选手server，并且传入相关
java $JAVA_OPS -cp $jarPath com.alibaba.middleware.race.sync.Server $schema $tableName $start $end
# 启动选手client
java $JAVA_OPS -cp $jarPath com.alibaba.middleware.race.sync.Client
```

7. client端评测程序等待client端程序运行结束(退出JVM)后，到指定目录拿选手的最终结果和标准结果进行比对，并且将结果(结果正确、超时、结果错误)返回给server端评测程序
8. server端评测程序得到结果，如果结果有效，记录结束时间endTime。如果结果无效则直接将相关错误信息之间返回给天池系统。
10. server端评测程序强制kill选手的server端进程
11. server端评测程序将最终的间隔finalTime=(endTime-starttime)上报给天池系统，由天池进行排名。天池上的costTime单位是毫秒


注意点：
1. 选手的server端程序由server端的评测程序来kill
2. client端的评测程序需要选手自己控制在得到最终结果后停止，否则会有超时问题


# ============================= 如何获取评测日志 ===================================
1. 超时时间： server端不做超时处理，client端超时时间为5分钟
2. 日志处理：
    - 请将日志写入指定的日志目录：/home/admin/logs/${teamCode}/，这里的teamCode请替换成自己的唯一teamCode，此外请不要透露自己的teamCode给别人哦。teamCode目录服务器会自己建，自己不用再创建。
3. 如何获取自己运行的日志：
    - 选手每次提交的程序运行的gc日志以及符合上面命名规范的日志，评测程序才会将其反馈给选手。
    - 选手的日志请命名为server-custom.log和client-custom.log，否则不会上传到OSS
    - GC日志的名称为：gc_client.log或者gc_server.log
    - 评测日志名称为：server-assesment-INFO.log或者client-assessment-INFO.log
    - 选手可以通过地址：http://middle2017.oss-cn-shanghai.aliyuncs.com/${teamCode}/server.log.tar.gz或者client.log.tar.gz来获取日志
    - 日志已经做了上传大小的限制，限制为10K





# ================================= 如何使用Demo ================================
Demo基于netty实现了简单的客户端和服务端程序。
1. Server: 负责接收评测系统给其的输入(通过args参数)，并且将解析好的数据交给Client。每次提交评测会给定4个参数，作为一组输入，保存在main的args对象里
2. Client: 启动后根据评测系统给其的serverIp来启动，启动后接受Server的信息，并且将最终结果写入到指定结果文件目录

```
Server端Program arguments示例（4个参数）：
middleware student 100 200

Client端Program arguments示例：
127.0.0.1

```

3. Constants: 比赛时候凡是需要写文件的地方，必须写到指定目录，对相关目录进行了定义。写出的目录路径都包含${teamcode}，千万不能遗漏。
teamcode是识别选手的唯一标示，评测程序会从选手teamcode相关目录下读取选手的结果文件，并且清理垃圾文件。

```
    // 赛题数据
    String DATA_HOME = "/home/admin/canal_data";
    // 结果文件目录(client端会用到)
    String RESULT_HOME = "/home/admin/sync_results/${teamcode}";
    // 中间结果目录（client和server都会用到）
    String MIDDLE_HOME = "/home/admin/middle/${teamcode}";
    //结果文件的命名
    //String RESULT_FILE_NAME = "Result.rs";
    
    PS： 中间结果一定要写到指定目录，否则评测程序将不会对其清理。写到非法目录，然后在下次评测的时候直接读取中间结果来提升成绩的视为违规。请注意！
```

4. Demo仅做演示用，选手可以自由选择其他通信框架，自己按照自己的方式来处理通信。



必读的注意点：
1. 写出中间结果文件一定要写指定目录
2. 写出结果文件一定要用指定的名字
3. 结果文件要在Client端写到指定目录
4. Client和Server的类名必须是"Client"和"Server"，否则评测程序无法正常启动选手的程序
5. 评测程序给server的参数，第一个参数是schema名字，第二个参数是table名字，第三个参数和第四个参数表征查询的主键范围。具体可以查看Demo
6. 构建工程必须保证构件名字为sync，最后得到的jar为sync-1.0.jar，建议使用Demo里面的assembly.xml，用mvn clean assembly:assembly -Dmaven.test.skip=true命令打包。
7. 结果文件的格式可以使用SQL:select * into outfile 'student.txt' from student来获得。默认每一列都是以tab分隔，每一行都以'\n'来换行（包括最后一行）
8. 变更信息的10个数据文件命名为： 1.txt、2.txt、3.txt、4.txt、5.txt、6.txt、7.txt、8.txt、9.txt、10.txt


结果文件格式例子如下，每列分别代表ID、名称、城市、性别

```
1	李雷   杭州  男
2   韩梅梅 北京  女
```


# =========================================== 比赛环境说明 =========================================

预热赛的环境原本采用集团内部物理机，但是最终经过出题组评估，从集团安全性角度出发改用阿里云上的ECS。机器配置是16核32G。
数据文件全部放在内存文件系统。正式比赛也会采用ECS的机器，配置相同。具体CPU、内存读写速度可以参考FAQ。

重要提示：
1. canal_data中的数据文件全部是从内存加载的
2. 如果写中间结果，写的middle目录是用的阿里云SSD云盘。读写性能在110MB/s左右。





# =========================================== FAQ =========================================

> 环境相关

1. JDK采用什么版本？

```
java version "1.7.0_80"
Java(TM) SE Runtime Environment (build 1.7.0_80-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.80-b11, mixed mode)
```

2. 磁盘信息？ 答：数据文件全部放在内存，从内存加载8G数据时的性能如下。



```
# 写性能
$ sudo  time dd if=/dev/zero of=/home/admin/myram/test  bs=8k count=1000000
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 5.15673 s, 1.6 GB/s
0.08user 5.07system 0:05.15elapsed 100%CPU (0avgtext+0avgdata 832maxresident)k
0inputs+0outputs (0major+249minor)pagefaults 0swaps

# 读性能
$ sudo time dd if=/home/admin/myram/test  of=/dev/null bs=8k count=1000000
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 2.02892 s, 4.0 GB/s
0.07user 1.95system 0:02.02elapsed 99%CPU (0avgtext+0avgdata 832maxresident)k
0inputs+0outputs (0major+249minor)pagefaults 0swaps



```

3. CPU信息? 其中一个核的信息如下

```
processor	: 15
vendor_id	: GenuineIntel
cpu family	: 6
model		: 79
model name	: Intel(R) Xeon(R) CPU E5-2682 v4 @ 2.50GHz
stepping	: 1
microcode	: 0x1
cpu MHz		: 2499.996
cache size	: 40960 KB
physical id	: 0
siblings	: 16
core id		: 15
cpu cores	: 16
apicid		: 15
initial apicid	: 15
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ss ht syscall nx pdpe1gb rdtscp lm constant_tsc rep_good nopl eagerfpu pni pclmulqdq ssse3 fma cx16 pcid sse4_1 sse4_2 x2apic movbe popcnt tsc_deadline_timer aes xsave avx f16c rdrand hypervisor lahf_lm abm 3dnowprefetch fsgsbase tsc_adjust bmi1 hle avx2 smep bmi2 erms invpcid rtm rdseed adx smap xsaveopt
bogomips	: 4999.99
clflush size	: 64
cache_alignment	: 64
address sizes	: 46 bits physical, 48 bits virtual
power management:
```


4. 最大文件打开数是多少？

```
$ulimit -a
core file size          (blocks, -c) 0
data seg size           (kbytes, -d) unlimited
scheduling priority             (-e) 0
file size               (blocks, -f) unlimited
pending signals                 (-i) 31214
max locked memory       (kbytes, -l) 64
max memory size         (kbytes, -m) unlimited
open files                      (-n) 65535
pipe size            (512 bytes, -p) 8
POSIX message queues     (bytes, -q) 819200
real-time priority              (-r) 0
stack size              (kbytes, -s) 8192
cpu time               (seconds, -t) unlimited
max user processes              (-u) 4096
virtual memory          (kbytes, -v) unlimited
file locks                      (-x) unlimited
```

5. 是否开启超线程？答：开启


6. 启动选手的Client和Server采用怎样的JVM配置？

```
-XX:InitialHeapSize=3221225472 -XX:MaxDirectMemorySize=209715200 -XX:MaxHeapSize=3221225472 -XX:MaxNewSize=1073741824 -XX:MaxTenuringThreshold=6 -XX:NewSize=1073741824 -XX:OldPLABSize=16 -XX:OldSize=2147483648 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedOops -XX:+UseConcMarkSweepGC -XX:+UseParNewGC
```


> 规则相关

1. 哪些属于违规行为，会取消成绩甚至参赛资格? 

```
选手依靠自己的算法和程序，通过程序的运算得到正确答案一般都是不会违规的。
通过一些hack的方式而不是依靠程序算法本身来获取成绩，视为无效。
选手如果对某些行为是否违规不确定，可以联系我。当然相关内容我是不会透露给别的选手的。
```

2. 是否可以使用别的语言或者在JAVA中使用shell脚本？

```
不可以。之所以不支持别的语言主要原因是：
1. JAVA属于比较好上手的语言，其语法也能迅速掌握，如果能用其他语言实现自己的算法，换成JAVA也是比较容易的
2. 中间件部门内大量使用JAVA，从用人角度来说，也希望通过比赛挖掘相关的人才
3. 开发评测工具的人手有限，如果多语言的话，还要考虑C++、C、python、go、ruby等语言，代价较高
4. 限定使用一种语言，相对来说更加公平，屏蔽了不同语言之间的影响
```

3. 可以随意使用三方库吗？

```
可以。选手可以随意选择自己所需要的三方库
```

4. 不写到指定目录是否可以？

```
不可以！！评测程序会对指定目录下的选手中间结果清理。选手将中间结果写入非法目录，评测程序将不会及时对其进行清理。下次选手运行时可能会
```

5. 可以使用堆外内存吗？

```
可以，不过只能使用200MB.禁止使用超过200MB的堆外内存
```

6. 数据文件是按照时间排序的吗？

```
是的，1.txt中的时间最早，10.txt文件中的变更信息时间最晚。同一个文件内也是最前面的行时间最早
```


7. 内存是多少？

```
采用的ECS配额的内存是32G，但是选手只允许使用3G的堆内内存和200M有限的堆外内存。
```

8. 结果输出顺序咋样？

```
列顺序按照第一次insert时的列顺序，行的顺序按照主键的顺序。如果范围内某主键的记录被删除了，就不用输出。
```

9. 网络能力咋样？

```
在客户端执行iperf传输1G数据的结果（每秒传输100MB）：
[ ID] Interval       Transfer     Bandwidth
[  3]  0.0- 1.0 sec   182 MBytes  1.53 Gbits/sec
[  3]  1.0- 2.0 sec   103 MBytes   865 Mbits/sec
[  3]  2.0- 3.0 sec  91.0 MBytes   763 Mbits/sec
[  3]  3.0- 4.0 sec   100 MBytes   843 Mbits/sec
[  3]  4.0- 5.0 sec  91.0 MBytes   763 Mbits/sec
[  3]  5.0- 6.0 sec  98.9 MBytes   829 Mbits/sec
[  3]  6.0- 7.0 sec  98.1 MBytes   823 Mbits/sec
[  3]  7.0- 8.0 sec   104 MBytes   876 Mbits/sec
[  3]  8.0- 9.0 sec  97.6 MBytes   819 Mbits/sec
[  3]  9.0-10.0 sec  97.6 MBytes   819 Mbits/sec
[  3]  0.0-10.2 sec  1.04 GBytes   879 Mbits/sec
```

10. 结果中列的顺序怎样？

```
第一次插入的时候列信息是完整的，按照第一次插入时候的列顺序来输出即可
```

11. 是否可以针对数据集的特征做优化？

```
已知公开条件均可以利用，可以利用有限的日志信息做一些数据特征探索。
```

12. 比赛的输入会换吗?

```
会的，比赛有多套输入，会随机选择
```

13. 主键是数字类型确定吗?

```
确定的，主键是数字类型
```

14. 预热赛和正式赛有什么区别？

```
预热赛和正式赛的比赛数据不同。
```

15. 会不会有原本是查询范围外的主键通过update变成查询范围内的？

```
会有
```


16. 预热赛的答案能否提供?

```
可以提供，为了让选手更好的调试代码，解决问题。预热赛的答案提供如下：
下载地址：https://space.dingtalk.com/c/ggHaACQzMTYwZjhlMi01Zjk5LTRmODMtODM2ZC1jYWFlNjlkYzg1ZWYCzhoZeDw
```

17. client端程序连不上server怎么办？

```
选手server端程序启动后，评测程序马上通知client端的评测程序启动选手的client端程序，此时如果server端程序还没有完全起来，就会导致连不上。
因此，选手需要自己对client端程序做重试逻辑，或者干脆sleep一段时间（一般15秒肯定起来了，当然还是建议做重试，避免浪费时间）
```

18. 可以逆序读取吗？

```
对于比赛数据，只允许顺序读取一次，读取的时候单线程读。
之所以做这样的决定，是因为我们这道赛题的工程背景本身就是实时数据同步。
对于一个Binlog文件内的数据的实时解析，本身也就是流式的。大家可以从流式计算的角度来理解本赛题
希望选手能在这个规则下进行算法优化。这样的算法也能服务于真正的场景。

```