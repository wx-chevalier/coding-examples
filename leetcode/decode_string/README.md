# 394.Decode String

## 题目

Given an encoded string, return it's decoded string.

The encoding rule is: k[encoded_string], where the encoded_string inside the square brackets is being repeated exactly k times. Note that k is guaranteed to be a positive integer.

You may assume that the input string is always valid; No extra white spaces, square brackets are well-formed, etc.

Furthermore, you may assume that the original data does not contain any digits and that digits are only for those repeat numbers, k. For example, there won't be input like 3a or 2[4].

说明：给定一个编码字符，按编码规则进行解码，输出字符串；编码规则是`count[letter]`，将 letter 的内容 count 次输出，count 是 0 或正整数，letter 是区分大小写的纯字母

```js
s = "3[a]2[bc]", return "aaabcbc".
s = "3[a2[c]]", return "accaccacc".
s = "2[abc]3[cd]ef", return "abcabccdcdcdef".
```

## 分析

可以使用正则表达式提取，或者递归提取。
