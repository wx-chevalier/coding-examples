/**
 * Created by apple on 16/9/8.
 */
var urlencode = require("./urlencode");
var urlencodeNode = require("./node-urlencode");


//测试长字符串编码时间
var str = "";

for (i = 0; i < 1000000; i++) {
  str += "王下邀月熊";
}

console.time("基于Pure JavaScript的编解码");

urlencode.decode(urlencode(str));

urlencode.decode(urlencode(str, "gbk"), "gbk");

console.timeEnd("基于Pure JavaScript的编解码");


console.time("基于NODE的编解码");

urlencodeNode.decode(urlencode(str));

urlencodeNode.decode(urlencode(str, "gbk"), "gbk");

console.timeEnd("基于NODE的编解码");


// 基于Pure JavaScript的编解码: 526.27ms
// 基于DOM的编解码: 155.32ms

//测试短时间内多次编码
var timerPure = 0;

var timerNODE = 0;

str = "王下邀月熊王下邀月熊王下邀月熊王下邀月熊";

for (i = 0; i < 1000; i++) {

  var start = new Date();

  urlencode.decode(urlencode(str));
  urlencode.decode(urlencode(str, "gbk"), "gbk");

  timerPure += new Date().getMilliseconds() - start.getMilliseconds();
}

for (i = 0; i < 1000; i++) {

  var start = new Date();

  urlencodeNode.decode(urlencode(str));
  urlencodeNode.decode(urlencode(str, "gbk"), "gbk");

  timerNODE += new Date().getMilliseconds() - start.getMilliseconds();

}

console.log("timerPure:" + timerPure);

console.log("timerNODE:" + timerNODE);

// 基于Pure JavaScript的编解码: 10932.610ms
// 基于NODE的编解码: 7585.223ms
// timerPure:28
// timerNODE:31


