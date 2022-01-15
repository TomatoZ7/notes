# leetcode 第三题：无重复字符的最长子串

给定一个字符串，请你找出其中不含有重复字符的 **最长子串** 的长度。

### 示例1
```
输入: s = "abcabcbb"
输出: 3 
解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
```

### 示例2
```
输入: s = "bbbbb"
输出: 1
解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
```

### 示例3
```
输入: s = "pwwkew"
输出: 3
解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
     请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
```

### 示例4
```
输入: s = ""
输出: 0
```

## 解法1：双指针 + HashSet

利用双指针遍历字符串，同时用到了集合 `set` ，当左指针向右移动时，则删除原来的元素，当右指针向右移动，则添加所在元素。

```java
public int lengthOfLongestSubstring(String s) {
    if (s.length() == 0) {
        return 0;
    }

    Set<Character> subSet = new HashSet<Character>();
    int left = 0;   // 左指针
    int right = 0; // 右指针
    int len = s.length();
    int max = 0;

    while (right < len && left < len) {
        while (right < len && !subSet.contains(s.charAt(right))) {
            subSet.add(s.charAt(right));
            ++right;
        }

        max = Math.max(max, right - left);

        subSet.remove(s.charAt(left));
        ++left;
    }

    return max;
}
```

### 复杂度分析
+ 时间复杂度：O(N²)
+ 空间复杂度：O(1)，因为存取的是字符集的个数，本题中的字符集可以是所有 ASCII 码在 [0, 128) 内的字符，所以为 O(1)。

## 解法2：双指针 + HashMap

遍历字符串并将字符存入 HashMap 中，判断字符是否存在于 HashMap ,如果存在，则移动左指针到已存在的值 value + 1 处。

```java
public int lengthOfLongestSubstring(String s) {
    if (s.length() == 0) {
        return 0;
    }

    int max = 0;
    int left = 0;
    int len = s.length();
    HashMap<Character, Integer> mapTable = new HashMap<Character, Integer>();

    for (int right = 0; right < len; ++right) {
        if (mapTable.containsKey(s.charAt(right))) {
            left = Math.max(left, mapTable.get(s.charAt(right)) + 1);
        }

        mapTable.put(s.charAt(right), right);
        max = Math.max(max, right - left + 1);
    }
    
    return max;
}
```

### 复杂度分析
+ 时间复杂度：O(N)
+ 空间复杂度：O(N)