# [Isomorphic Urlencode](https://github.com/wxyyxc1992/Web-Frontend-Introduction-And-Best-Practices/blob/master/OpenSource/isomorphic-urlencode/README.md)

> [Here is English Version For README](https://github.com/wxyyxc1992/Web-Frontend-Introduction-And-Best-Practices/blob/master/dom/network/HTTPClient/isomorphic-urlencode/README.en.md)

笔者在[URL编码详解与DOM中GBK编码实践](https://segmentfault.com/a/1190000006861592)一文中介绍了JavaScript中URL编码相关的基础知识，其中没有介绍纯粹的JavaScript编解码方案，笔者后来根据网上的代码完善了下[Isomorphic Urlencode](https://github.com/wxyyxc1992/Web-Frontend-Introduction-And-Best-Practices/blob/master/OpenSource/isomorphic-urlencode/README.md)这个库，并且对JavaScript中三种不同的编解码方案进行了比较。核心的出发点为

- 对于浏览器中版本使用纯粹的前端代码实现GBK编码
- 保证能够在Node与Browser环境下实现无缝对切

可以使用`npm`命令直接安装该依赖:

```
npm install --save isomorphic-urlencode
```

# Usage

## Pure JavaScript

基于纯粹的JavaScript优势在于可以跨平台使用，不过缺陷在于其性能相对较差，另外目前只支持GBK/GB2312编码，不如另外两种可以用于其他编码规范。在使用纯粹的JavaScript中，如果使用UTF8编码，那么直接调用JavaScript内置的`encodeURIComponent`，如果使用GBK编码，那么会使用如下两个函数，其关键代码为:

```
function gbkEncode(str) {
  return str.replace(/./g, function (a) {
    var code = a.charCodeAt(0);
    if (isAscii(code)) {
      return encodeURIComponent(a);
    } else {
      var key = code.toString(16);
      if (key.length != 4)key = ('000' + key).match(/....$/)[0];
      return U2Ghash[key] || a;
    }
  });
}

function gbkDecode(str, callback) {
  return str.replace(/%[0-9A-F]{2}%[0-9A-F]{2}/g, function (a) {
    if (a in G2Uhash) {
      return String.fromCharCode('0x' + G2Uhash[a]);
    } else {
      return a;
    }
  }).replace(/%[\w]{2}/g, function (a) {

    return decodeURIComponent(a);

  });
}

```

## Node

Node版本使用了 [node-urlencode](https://www.npmjs.com/package/urlencode)，其对于UTF8的编解码也是使用了JavaScript内置的`encodeURIComponent`，而对于GBK等其他编码使用了`iconv-lite`这个库:

```
function encode(str, charset) {
  if (isUTF8(charset)) {
    return encodeURIComponent(str);
  }

  var buf = iconv.encode(str, charset);
  var encodeStr = '';
  var ch = '';
  for (var i = 0; i < buf.length; i++) {
    ch = buf[i].toString('16');
    if (ch.length === 1) {
      ch = '0' + ch;
    }
    encodeStr += '%' + ch;
  }
  encodeStr = encodeStr.toUpperCase();
  return encodeStr;
}
```

## DOM

如果你是在浏览器环境中使用,请在HTML文件头部添加:

```
if (parent._urlEncode_iframe_callback) {

    parent._urlEncode_iframe_callback(location.search.split('=')[1]);

    //直接关闭当前子窗口
    window.close();
}
```
注意,上面一段代码是将自身作为iframe的加载地址,因此务必放在HTML文件首部。然后在JS代码中使用:
```
var urlencode = require("isomorphic-urlencode").dom;

urlencode("王下邀月熊").then(function (data) {
  console.log(data);

  //测试解码
  urlencode.decode(data).then(function (data) {
    console.log(data);
  })
});

urlencode("王下邀月熊", "gbk").then(function (data) {
  console.log(data);

  //测试解码
  urlencode.decode(data, "gbk").then(function (data) {
    console.log(data);
  })
});
```


浏览器版本主要基于iframe与form实现,详细原理参考[这篇博客](https://github.com/wxyyxc1992/Web-Frontend-Introduction-And-Best-Practices/blob/master/dom/network/HTTPClient/DOM-URLEncode.md)

# Benchmark

## Pure JavaScript VS DOM

主要的测试代码如下所示，从结果中可见对于大数据串还是使用DOM效率较好，但是基于DOM的解决方案需要嵌入iframe，这个可能造成额外的代码侵入与性能损耗。

```
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
```



## Pure JavaScript VS Node

主要的测试代码如下所示，可以看出基于Node的编解码速度会快于纯粹的JavaScript编解码。

```
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
```



# Test

使用`node node-urlencode.test.js`来测试Node环境下转码。
使用`webpack demo/demo.js demo/demo.dist.js`,然后在浏览器中打开`demo.html`,在Console中可以查看运行结果