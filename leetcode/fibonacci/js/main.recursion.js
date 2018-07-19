/*
 * @file 斐波那契数列的递归解法
 * @description 详细说明
 * 
 * @author 王下邀月熊 <384924552@qq.com>
 * 
 * Created Date: Thu, 2018-07-19 00:21:12
 * 
 * Last Modified: Thu, 2018-07-19 00:41:24
 * Last Modified By: 王下邀月熊 <384924552@qq.com>
 * 
 * This code is licensed under the MIT License.
 */

export function fibonacci(n) {
  if (n === 0) {
    return 0;
  } else if (n === 1) {
    return 1;
  }

  return fibonacci(n - 1) + fibonacci(n - 2);
}

/** 带缓存的递归 */
export function fibonacciWithMemory(num, memo) {
  memo = memo || {};

  if (memo[num]) return memo[num];
  if (num <= 1) return 1;

  return (memo[num] = fibonacci(num - 1, memo) + fibonacci(num - 2, memo));
}

export function* fibonacciGenerator(n, current = 0, next = 1) {
  if (n === 0) {
    return current;
  }

  yield current;

  yield* fibonacci(n - 1, next, current + next);
}
