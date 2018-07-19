/*
 * @file 斐波那契数列的循环解法
 * @description 详细说明
 * 
 * @author 王下邀月熊 <384924552@qq.com>
 * 
 * Created Date: Thu, 2018-07-19 00:26:22
 * 
 * Last Modified: Thu, 2018-07-19 00:40:13
 * Last Modified By: 王下邀月熊 <384924552@qq.com>
 * 
 * This code is licensed under the MIT License.
 */

export function fibonacci(n) {
  const infinite = !n && n !== 0;
  let current = 0;
  let next = 1;

  while (infinite || n--) {
    [current, next] = [next, current + next];
  }

  return current;
}

/** 循环生成器 */
export function* fibonacciGenerator(n) {
  const infinite = !n && n !== 0;
  let current = 0;
  let next = 1;

  while (infinite || n--) {
    yield current;
    [current, next] = [next, current + next];
  }
}
