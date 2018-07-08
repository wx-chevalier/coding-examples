WxMatchDroid
============

WxMatchDroid can be used to analyze text ,such as pattern match.It integrated with classic algorithm such as KMP、Sunday、AC。WxMatchDroid can be an optional for REGEXP while much faster and easy than it.
What’s MatcherDroid
　　因为不爽正则表达式复杂的语法，较低的中文支持度，不是很好的效率，所以自己写了一个类正则表达式的自动匹配/提取机。目前测试来看，MatcherDroid相较于正则表达式，有更易使用的语法规范、大数据下更高的执行效率以及正好的中文支持。
　　鉴于本人能力非常有限，希望各位大神发现什么问题或者有什么新的功能需求可以随时联系我，很乐意效劳。邮箱：384924552@qq.com
　　MatcherDroid是在字符串搜索/比较的基础上，最大化的使用剪枝与复用的思路，建立一个高效的匹配搜索引擎，其提高效率的三大方式：
　　（1）剪枝：尽可能地减少运算/迭代次数。
　　（2）复用：最大程度地避免重复运算，尽可能地用有限的空间换有限的时间。
　　（3）优化代码：尽可能地避免冗余运算，使用高效运算符替代低效运算符。
　　
　　MatcherDroid的关键技术点为：
　　（1）匹配机首先使用锚来搜索模串在匹配文本中的可能开始下标，本质上就是多模式匹配问题。这里使用AC算法对模串集合进行优化，自动调度遍历（Java String类型中自带的indexOF方法）/KMP/Sunday算法来搜索模串在匹配文本中可能的下标列表。其中遍历适合于较短文本，较简单模串的情况，KMP适用于较短文本，较复杂模串的情况，Sunday适用于较长文本，较复杂模串的情况。
　　（2）匹配机构造了规则队列与源输入串两个动态匹配队列，构造成了一个类自动机的迭代机制，尽可能地使用剪枝的方法减少迭代次数同时每一次匹配动作尽可能地匹配较长的文本。
　　
术语
锚	将规则中规定的必然出现的字符串/字符串集合称为锚，MatcherDroid首先搜索锚在匹配文本中的下标，然后从下标位置开始前向或者后向匹配。
【注】：对于规则字符串中可能存在多个单配/通配字符串的情况时，因为在实际测试中MatchDroid用于锚下标搜索的时间远大于匹配耗费的时间，目前默认只选择最长的单配字符串（第一优先级）或者字符串数目最少的多配字符串集合作为锚。可能在长文本中设置多锚可以提高效率，如果有任何问题请及时交流。
字符项	单配字符:如 ab、张二狗、- 这样匹配特定单一字符串的输入字符串。规则中所有的单配字符需要放置在()中。
多配字符串集合：输入的一个HashSet集合中包含有一个或多个的单配字符。
    规则中HashSet集合名需要放置在[]中。
通配字符：如%这种可以匹配任意中文字符的匹配字符。注意，通配字符不需要放置在()中。
规则项	任何一个“[字符项][重复次数]”的组合会被解析为一个规则项，譬如：
(张二狗){1,1}就是一个规则项。
内置关键字与元字符
%	匹配所有中文字符。
()	定义输入的单配字符串。
[]	定义需要输入的多配字符串的集合名称。
{m,n}	定义输入的字符项的重复次数，省略则默认为1;
关键字的数目还是很少的，懒得用那么多的转义字符。
MatcherDroid可选参数
IsGreedy（默认true）	是否进行贪婪匹配。比较如下：
关键字：张%{1,2}二
匹配句式：张二二二二 
贪婪匹配结果：张二二二
非贪婪匹配结果：张二二
AcEnabled（默认true）	多模串匹配时是否使用AhoCorasick算法进行优化。
algorithm
（默认为CollectionUtil）	选定单模串匹配算法，可选:
CollectionUtil：String自带的indexOf方法，适用于短文本、简单模串。
KMP：适用于短文本、复杂模串。
Sunday：适用于长文本。
setAnchor(int Pos)	自行设定哪一个规则项为锚，注意，规则项中若存在单配字符串则不能选择多配字符串作为锚。

运行原理
规则编译
　　将输入的规则按照从左到右的顺序提取成一个个规则项然后放入内置的规则项匹配队列中，基本数据结构如下所示：

锚定位
首先在输入的匹配文本中搜索锚的位置，然后从锚位置开始前向或者后向匹配。
锚为单配字符串
KMP	
Sunday	
CollectUtil	

锚为多配字符串
AC	
N次单模串匹配	

动态匹配
　　设置了两个动态指针，初始化分别指向锚所在的输入字符串的当前位置与锚所对应的匹配规则项的当前位置。如果锚所在的规则项不是规则匹配队列首，则自动前向匹配。如果锚在规则匹配队列首或者前向匹配完成之后，会自动开始从锚位置后向匹配，当发现一个匹配项后会自动将结果加入到一个静态的结果集变量中。

实例与效率对比
姓名提取：单配字符串使用示例
使用步骤
Step 1 : 创建MatchDroid实例。
MatcherDroid cre = new MatcherDroid();

Step 2 : 编译规则
String reg = "(尊敬的)%{2,3}";
　　这是MatcherDroid规则，对应正则表达式的规则是："尊敬的.{2,3}"，由此可以看出MatchDroid可以更好地支持中文。下面使用compiled函数进行规则编译。
cre.compiled(reg,null);

Step 3 : 匹配机可选参数设定
设定单模串匹配算法：KMP、Sunday或者String自带的IndexOf，这里使用KMP作为示范。
　　cre.setAlgorithm(Algorithm.KMP);

Step 4 : 进行模式匹配
String str_input = "尊敬的张二狗11你好，尊敬的成吉思汗，尊敬的张1234";
Result_Set rs = cre.matcher(str_input);
System.out.println(rs.getGroup());
匹配的结果是：[尊敬的张二狗, 尊敬的成吉思]
效率比较
数据集：9335862条40~120个字符的文本数据
	MatchDroid	正则
String reg = "(尊敬的)%{2,3}";	33s	40s
String reg = "(尊敬的会员)%{2,3}(我的名字是二狗)";	67s	183s

由上可见，越是复杂的规则MatchDroid的效率越高。
车牌号码提取：多配字符串使用示例
Step 1 : 创建MatchDroid实例。
MatcherDroid cre = new MatcherDroid();

Step 2 : 编译规则
车牌号对应的正则表达式规则是：
"[苏沪浙京鲁皖豫闽鄂湘粤赣津冀晋黑吉辽渝川云贵桂琼陕甘青宁新蒙藏]{1}[A-Z]{1}[\\-]{1}[A-Z_0-9]{5}"
则将[苏~藏]、[A~Z]、[A~Z_0~9]这三个多配字符串转化为HashSet数据集
String reg = "[hs_p]{1}[En_Set]{1}(-){0,1}[En_Num_Set]{5}";
这里的hs_p是存放[苏~藏]这些字符的HashSet数据集，En_Set是内置的存放所有英文字符的数据集，En_Num_Set是内置的存放所有数字与英文字符的数据集。
cre.compiled("[hs_p]{1}[En_Set]{1}(-){0,1}[En_Num_Set]{5}",hm);
这里的hm是一个HashMap<String, HashSet<String>>结构体，String表示多配字符串数据集名，HashSet<String>存放实际数据。

Step 3 : 匹配机可选参数设定
设定锚规则，这里选择[hs_p]这个多配字符串集合所在的规则项作为锚规则。
　　cre.setAnchor(0);

Step 4 : 进行模式匹配
str_input = "张二狗苏F--5FAF6苏F-FFFFF甘A97671王下邀月甘G---8786F熊";
rs = cre.matcher(str_input);
System.out.println(rs.getGroup());
结果是：
[甘A97671, 苏F-FFFFF]
效率比较
	MatchDroid	正则	MatchDroid
(AC优化)
String reg = "[hs_p]{1}[En_Set]{1}(-){1}[En_Num_Set]{5}";	3783ms	95362ms	
String reg = "[hs_p]{1}[En_Set]{1}(-){0,1}[En_Num_Set]{5}";	32s	75s	22788ms
由此可见，
设定优质的锚字符可以几何级的提高效率。上表中第一条与第二条规则的区别在于’-’这个字符是否一定出现。
在多模式匹配中，使用AC算法可以大幅度提高效率。
附录：
单模串搜索算法效率比较

IndexOf/KMP/Sunday	较长文本	较短文本
较简单模串		72/80/107
较复杂模串	2381ms/4500ms/1032ms	109/99/102
如何提高MatchDroid的效率
任何一个MatchDroid规则只需要调用一次compiled函数，matcher函数可以无限调用。千万不要将compiled函数放在循环体内部，这非常耗费时间。
选定优质的锚字符：尽可能是文本中出现次数少的单配/多配字符串。