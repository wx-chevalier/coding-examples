/*
 * @file 文件描述
 * @description 详细说明
 * 
 * @author 王下邀月熊 <384924552@qq.com>
 * 
 * Created Date: Sun, 2018-07-15 22:18:26
 * 
 * Last Modified: Sun, 2018-07-15 22:44:31
 * Last Modified By: 王下邀月熊 <384924552@qq.com>
 * 
 * This code is licensed under the MIT License.
 */
const reg = /(\d*)\[([a-z]*)\]/i;

export const decodeStr = str => {
  const match = reg.exec(str);

  // 无匹配则返回
  if (!match) {
    return str;
  }

  // 进行字符串匹配生成
  let repeatedStr = '';
  const [matchedStr, num, repeatStr] = match;

  Array.from({
    length: parseInt(num, 10)
  }).forEach(() => {
    repeatedStr += repeatStr;
  });

  // 进行递归提取
  return decodeStr(str.replace(matchedStr, repeatedStr));
};
