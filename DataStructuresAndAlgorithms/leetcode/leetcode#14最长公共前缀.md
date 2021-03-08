# leetcode 第十四题：最长公共前缀

编写一个函数来查找字符串数组中的最长公共前缀。

如果不存在公共前缀，返回空字符串 ""。

### 示例1
```
输入：strs = ["flower","flow","flight"]
输出："fl"
```

### 示例2
```
输入：strs = ["dog","racecar","car"]
输出：""
解释：输入不存在公共前缀。
```

### 提示
+ `0 <= strs.length <= 200`
+ `0 <= strs[i].length <= 200`
+ `strs[i]` 仅由小写英文字母组成

## 解法1：横向比较

遍历数组，首先比较出前两位的公共前缀 `prefix`，接着再拿这个前缀一一同后面的字符串进行比较即可，如果获得更长的公共前缀，则覆盖 `prefix`。

注意：如果在比较过程中出现了空字符串，则直接返回空字符串即可。

```java
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        
        int len = strs.length;
        String prefix = strs[0];

        for (int i = 0; i < len; ++i) {
            prefix = commonPrefix(prefix, strs[i]);

            if (prefix == "") {
                break;
            }
        }

        return prefix;
    }

    public String commonPrefix(String str1, String str2) {
        int index = 0;
        int length = Math.min(str1.length(), str2.length());
        
        while (index < length && str1.charAt(index) == str2.charAt(index)) {
            index++;
        }

        return str1.substring(0, index);
    }
}
```

### 复杂度分析

时间复杂度：O(mn)，其中 m 是字符串数组中字符串的平均长度，n 是字符串数组的长度。在最坏的情况下，字符串数组中的字符串的每个字符都会被比较一次。
空间复杂度：O(1)，这里仅用到了常量级别的内存空间。

## 解法2：纵向比较

通过逐个字符比较每个字符串的前缀，一旦出现 “异类” 则立刻返回。

```java
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        
        // 数组的长度
        int len = strs.length;
        // 第一个字符串的长度
        int count = strs[0].length();

        for (int i = 0; i < count; ++i) {
            // 获取到每一个字符
            char prefix = strs[0].charAt(i);
            for (int j = 1; j < len; ++j) {
                // 先判断后面的字符串是否长度溢出，在判断是否相等
                if ( i == strs[j].length() || strs[j].charAt(i) != prefix ) {
                    return strs[0].substring(0, i);
                }
            }
        }

        return strs[0];
    }
}
```

### 复杂度分析

时间复杂度：O(mn)，其中 m 是字符串数组中字符串的平均长度，n 是字符串数组的长度。在最坏的情况下，字符串数组中的字符串的每个字符都会被比较一次。
空间复杂度：O(1)，这里仅用到了常量级别的内存空间。