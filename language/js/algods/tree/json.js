/*
当我们使用JSON.stringify序列化js对象时，如果对象中有环状结构，则JSON.stringify会报错，例如：

    const obj = {
      foo: {
        bar: 'foo.bar'
      },

      bar: {
        foo: 'bar.foo'
      }
    }

    obj.foo.baz = obj.bar
    obj.bar.fou = obj.foo

    const jsonText = JSON.stringify(obj) // Error
  
以上代码会抛出“Converting circular structure to JSON”异常，说明对象内部存在环状结构。在JS栏中实现一个“环判断”函数checkCircle(obj)，判断输入对象是否有环。
*/

const _toString = Object.prototype.toString;

const checkCircle = (obj, cache = []) => {
  const values = Array.isArray(obj) ? obj : Object.values(obj);

  let result = false;

  values.forEach(value => {
    if (!Array.isArray(value) || _toString.call(value) !== '[object Object]') {
      return;
    }

    if (cache.includes(value)) {
      result = true;
    } else {
      if (checkCircle(value, cache)) {
        result = true;
      }
    }
  });

  return result;
};
