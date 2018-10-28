/*
 * @Author: wxyyxc1992
 * @Date:   2016-09-08 20:26:16
 * @Last Modified by:   wxyyxc1992
 * @Last Modified time: 2016-09-08 20:28:39
 */

'use strict';
var Promise = require('es6-promise').Promise;

function urlencode(url, encode) {

  //默认值为utf8
  encode = encode || "utf8";


  return new Promise(function (resolve, reject) {

    if (!window) {
      //如果不是浏览器环境
      throw new Error("Not In Browser");
    }


    if (encode === "utf8") {

      resolve(encodeURIComponent(url));

      return;

    }

    if (encode === "gbk" || encode === "gb2312") {


      //调用GBK进行处理
      gbkEncode(url, function (data) {
        resolve(data)
      });
    }

  });

}

/**
 * @function 对于输入字符串进行GBK编码
 * @param url
 */
function gbkEncode(url, callback) {

  //创建form通过accept-charset做encode
  var form = document.createElement('form');
  form.method = 'get';
  form.style.display = 'none';
  form.acceptCharset = "gbk";

  //创建伪造的输入
  var input = document.createElement('input');
  input.type = 'hidden';
  input.name = 'str';
  input.value = url;

  //将输入框添加到表单中
  form.appendChild(input);
  form.target = '_urlEncode_iframe_';

  document.body.appendChild(form);

  //隐藏iframe截获提交的字符串
  if (!window['_urlEncode_iframe_']) {
    var iframe = document.createElement('iframe');
    //iframe.name = '_urlEncode_iframe_';
    iframe.setAttribute('name', '_urlEncode_iframe_');
    iframe.style.display = 'none';
    iframe.width = "0";
    iframe.height = "0";
    iframe.scrolling = "no";
    iframe.allowtransparency = "true";
    iframe.frameborder = "0";
    iframe.src = 'about:blank';
    document.body.appendChild(iframe);
  }

  //
  window._urlEncode_iframe_callback = callback;

  //设置回调编码页面的地址，这里需要用户修改
  form.action = window.location.href;

  //提交表单
  form.submit();

  //定时删除两个子Element
  setTimeout(function () {
    form.parentNode.removeChild(form);
    iframe.parentNode.removeChild(iframe);
  }, 100)

}

/**
 * @function 进行解码操作
 * @param encodedUrl
 * @param encode
 */
function urldecode(encodedUrl, encode) {
  //默认值为utf8
  encode = encode || "utf8";


  return new Promise(function (resolve, reject) {

    if (!window) {
      //如果不是浏览器环境
      throw new Error("Not In Browser");
    }


    if (encode === "utf8") {

      resolve(decodeURIComponent(encodedUrl));

      return;

    }

    if (encode === "gbk" || encode === "gb2312") {

      //调用GBK进行处理
      gbkDecode(encodedUrl, function (data) {
        resolve(data)
      });
    }

  });
}


/**
 * @function 对于输入的GBK格式的字符串进行解码
 * @param str
 * @param charset
 * @param callback
 */
function gbkDecode(str, callback) {
  window._urlDecodeFn_ = callback;
  var script = document.createElement('script');
  script.id = '_urlDecodeFn_';
  var src = 'data:text/javascript;charset=' + "gbk" + ',_urlDecodeFn_("' + str + '");'
  src += 'document.getElementById("_urlDecodeFn_").parentNode.removeChild(document.getElementById("_urlDecodeFn_"));';
  script.src = src;
  document.body.appendChild(script);
}

module.exports = urlencode;

module.exports.decode = urldecode;

