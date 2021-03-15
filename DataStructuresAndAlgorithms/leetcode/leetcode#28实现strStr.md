# leetcode 第二十八题：实现 strStr()

实现 `strStr()` 函数。

给定一个 haystack 字符串和一个 needle 字符串，在 haystack 字符串中找出 needle 字符串出现的第一个位置 (从0开始)。如果不存在，则返回  -1。

### 示例1
```
输入: haystack = "hello", needle = "ll"
输出: 2
```

### 示例2
```
输入: haystack = "aaaaa", needle = "bba"
输出: -1
```

### 说明

当 needle 是空字符串时，我们应当返回什么值呢？这是一个在面试中很好的问题。

对于本题而言，当 needle 是空字符串时我们应当返回 0 。这与C语言的 `strstr()` 以及 Java的 `indexOf()` 定义相符。

## 解法1：比较子串

```java
class Solution {
    public int strStr(String haystack, String needle) {
        if (needle.length() == 0) {
            return 0;
        }

        int i = 0;
        int len = needle.length();

        while ( i < haystack.length() - len + 1 ) {
            if (haystack.substring(i, i+len).equals(needle)) {
                return i;
            }else{
                i++;
            }
        }

        return -1;
    }
}
```

### 复杂度分析

时间复杂度：O((N-L)L)，其中 N 是字符串 haystack 的长度，L 是字符串 needle 的长度，比较字符串的复杂度为 L,总共需要比较 (N-L) 次。
空间复杂度：O(1)

## 解法2：双指针

解法1的缺陷主要是比较次数过多，每个子串都需要比较一次，针对这个缺陷，我们利用双指针进行优化。

当 haystack 子串和 needle 子串的第一个字符相等时才进入比较流程，而一旦不相等，则跳出比较流程，并将 haystack 指针回溯子串的第一个字符的下一位。

```java
class Solution {
    public int strStr(String haystack, String needle) {
        if (needle.length() == 0) {
            return 0;
        }

        int len = needle.length();
        int n = haystack.length();

        for (int i = 0; i < n - len + 1; i++ ) {
            if ( haystack.charAt(i) == needle.charAt(0) ) {
                int t = i;
                for (int j = 0; j < len; j++, t++) {
                    if ( haystack.charAt(t) != needle.charAt(j) ) {
                        break;
                    }

                    if (j == len - 1 && haystack.charAt(t) == needle.charAt(j) ) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }
}
```

### 复杂度分析

时间复杂度：最优时间复杂度为 O(N)，最坏时间复杂度为 O((N-L)L)
空间复杂度：O(1)

## 解法3：Rabin Karp - 常数复杂度 (来自 leetcode 官方)

[传送门](https://leetcode-cn.com/problems/implement-strstr/solution/shi-xian-strstr-by-leetcode/)