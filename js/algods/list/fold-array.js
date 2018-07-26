/**
 * 实现 convert2Tree 方法将下面数组根据 id 和 parentId 转成树状
    const data = [
        {id: 1, parentId: null, label: "1"},
        {id: 2, parentId: 1, label: "2"},
        ...
    ];
    返回值
    {
        id:"",
        label:"",
        children:[...]
    }
 */

export const convert2Tree = (data, rootId) => {
  const parentMapping = {};

  // 首先建立父子地址映射
  data.forEach(d => {
    const obj = {
      id: d.id,
      label: d.label,
      parentId: d.parentId,
      children: []
    };

    // 如果是首次加入，则进行添加
    if (!parentMapping[d.parentId]) {
      parentMapping[d.parentId] = [obj];
    } else {
      parentMapping[d.parentId].push(obj);
    }
  });

  // 进行广度优先的遍历
  const cascade = [...parentMapping[rootId]];
  let queue = [...parentMapping[rootId]];

  while (queue.length > 0) {
    const cand = queue.shift();

    // 提取出该地址对应的子地址
    cand.children = parentMapping[cand.value];

    if (Array.isArray(cand.children)) {
      // 将子地址添加到待处理队列
      queue = [...queue, ...cand.children];
    }
  }

  return cascade;
};

export const convert2Cascade = () => {};
