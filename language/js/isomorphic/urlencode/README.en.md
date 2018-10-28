# Isomorphic Urlencode

Main motivation for building this Repo is that:
- Enable GBK Encoding by Pure Client-Side JavaScript
- Guarantee isomorphic and seamless migration between test in node and production in browser for fetch

# Usage

The Node implementation is based on [node-urlencode](https://www.npmjs.com/package/urlencode)，so api style is as same as node-urlencode. But the encode in browser is async,so i use Promise as return;

use npm to install:
```
npm install --save isomorphic-urlencode
```

If used in Browser, add this to head of your HTML
```
if (parent._urlEncode_iframe_callback) {

    parent._urlEncode_iframe_callback(location.search.split('=')[1]);

    //直接关闭当前子窗口
    window.close();
}
```

```

var urlencode = require("isomorphic-urlencode");
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

The Browser Version is built on iframe and form, you can refer to this [blog](https://github.com/wxyyxc1992/Web-Frontend-Introduction-And-Best-Practices/blob/master/dom/network/HTTPClient/DOM-URLEncode.md) for more details;