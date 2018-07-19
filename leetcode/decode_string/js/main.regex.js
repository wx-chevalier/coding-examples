/*
 * @file 文件描述
 * @description 详细说明
 * 
 * @author 王下邀月熊 <384924552@qq.com>
 * 
 * Created Date: Sun, 2018-07-15 22:18:07
 * 
 * Last Modified: Sun, 2018-07-15 22:22:24
 * Last Modified By: 王下邀月熊 <384924552@qq.com>
 * 
 * This code is licensed under the MIT License.
 */
export const decodeString = str => {
  return str.replace(
    /(\d)\[([a-z]+((\d)\[([a-z]+)\])*)\]/gi,
    (matched, $0, $1, $2, $3, $4) => {
      $1 = $2 ? $4.repeat(+$3) : $1;
      return $0 * $1;
    }
  );
};
