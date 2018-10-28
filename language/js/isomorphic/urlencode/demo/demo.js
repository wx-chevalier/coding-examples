/**
 * Created by apple on 16/9/8.
 */

var urlencode = require("../urlencode");

var urlencodeDOM = require("../urlencode").dom;


//测试长字符串编码时间
var str = "";

for (i = 0; i < 100000; i++) {
  str += "王下邀月熊";
}

console.time("基于Pure JavaScript的编解码");

urlencode.decode(urlencode(str, "gbk"), "gbk");

console.timeEnd("基于Pure JavaScript的编解码");


console.time("基于DOM的编解码");

urlencodeDOM(str, "gbk").then(function (data) {

  //测试解码
  urlencodeDOM.decode(data, "gbk").then(function (data) {
    console.timeEnd("基于DOM的编解码")
  })
});

//测试短时间内多次编码
var timerPure = 0;

var timerDOM = 0;

str = "王下邀月熊";

for (i = 0; i < 1000; i++) {

  var start = new Date();

  urlencode.decode(urlencode(str, "gbk"), "gbk");

  timerPure += new Date().getMilliseconds() - start.getMilliseconds();

  (function test(start) {

    urlencodeDOM(str, "gbk").then(function (data) {

      //测试解码
      urlencodeDOM.decode(data, "gbk").then(function (data) {
        timerDOM += new Date().getMilliseconds() - start.getMilliseconds();

        console.log("timerDOM:" + timerDOM);

      })
    });

  })(new Date());

}
console.log("timerPure:" + timerPure);

// 基于Pure JavaScript的编解码: 526.27ms
// 基于DOM的编解码: 155.32ms


