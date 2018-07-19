import { decodeStr } from './main.recursion';

describe('测试用例', () => {
  it('基准测试', () => {
    const s = 'ab2[c]3[d]2[a]';
    expect(decodeStr(s)).toEqual('abccdddaa');
  });
  it('单字符串测试', () => {
    const s = '2[leetcode]';
    expect(decodeStr(s)).toEqual('leetcodeleetcode');
  });
});
