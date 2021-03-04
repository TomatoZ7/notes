# leetcode 第十三题：罗马数字转整数

罗马数字包含以下七种字符: I，V，X，L，C，D 和 M。

| 字符 | 数字 |
| :---: | :---: |
| I | 1 |
| V | 5 |
| X | 10 |
| L | 50 |
| C | 100 |
| D | 500 |
| M | 1000 |

例如， 罗马数字 2 写做 `II` ，即为两个并列的 1。12 写做 `XII` ，即为 `X` + `II` 。 27 写做 `XXVII`, 即为 `XX` + `V` + `II` 。

通常情况下，罗马数字中小的数字在大的数字的右边。但也存在特例，例如 4 不写做 `IIII`，而是 `IV`。数字 1 在数字 5 的左边，所表示的数等于大数 5 减小数 1 得到的数值 4 。同样地，数字 9 表示为 `IX`。这个特殊的规则只适用于以下六种情况：

+ `I` 可以放在 `V` (5) 和 `X` (10) 的左边，来表示 4 和 9。
+ `X` 可以放在 `L` (50) 和 `C` (100) 的左边，来表示 40 和 90。 
+ `C` 可以放在 `D` (500) 和 `M` (1000) 的左边，来表示 400 和 900。

给定一个罗马数字，将其转换成整数。输入确保在 1 到 3999 的范围内。

### 示例1
```
输入: "III"
输出: 3
```

### 示例2
```
输入: "IV"
输出: 4
```

### 示例3
```
输入: "IX"
输出: 9
```

### 示例4
```
输入: "LVIII"
输出: 58
解释: L = 50, V= 5, III = 3.
```

### 示例5
```
输入: "MCMXCIV"
输出: 1994
解释: M = 1000, CM = 900, XC = 90, IV = 4.
```

### 提示
+ `1 <= s.length <= 15`
+ `s` 仅含字符 `('I', 'V', 'X', 'L', 'C', 'D', 'M')`
+ 题目数据保证 `s` 是一个有效的罗马数字，且表示整数在范围 `[1, 3999]` 内
+ 题目所给测试用例皆符合罗马数字书写规则，不会出现跨位等情况。
+ `IL` 和 `IM` 这样的例子并不符合题目要求，49 应该写作 `XLIX`，999 应该写作 `CMXCIX` 。

## 思路

审题我们得知，需要考虑的特殊情况一共就 6 种：`IV`, `IX`, `XL`, `XC`, `CD`, `CM`。

其余的就是单个罗马字符的拼接。

下面介绍性能由低到高的三种解法。

## 解法1：HashMap

我们使用 HashMap 存放 6+7 种情况，然后遍历字符串去 HashMap 里查找。

```java
public int romanToInt(String s) {
    Map<String, Integer> romanTable = new HashMap<String, Integer>();
    romanTable.put("I", 1);
    romanTable.put("V", 5);
    romanTable.put("X", 10);
    romanTable.put("L", 50);
    romanTable.put("C", 100);
    romanTable.put("D", 500);
    romanTable.put("M", 1000);
    romanTable.put("IV", 4);
    romanTable.put("IX", 9);
    romanTable.put("XL", 40);
    romanTable.put("XC", 90);
    romanTable.put("CD", 400);
    romanTable.put("CM", 900);

    int res = 0;
    for (int i = 0; i < s.length();) {
        if (i + 1 < s.length() && romanTable.containsKey(s.substring(i, i+2))) {
            res += romanTable.get(s.substring(i, i+2));
            i += 2;
        }else{
            res += romanTable.get(s.substring(i, i+1));
            i += 1;
        }
    }

    return res;
}
```

### 复杂度分析

时间复杂度：O(N) <br/>
空间复杂度：O(1)

## 解法2：针对解法1的优化，缩减哈希表的读取

我们分析 6 中特殊组合可以得知：

左边的字符总是小于右边的字符,

于是我们可以将 HashMap 中的特殊组合改用判断的方式来实现:

```java
public int romanToInt(String s) {
    Map<String, Integer> romanTable = new HashMap<String, Integer>();
    romanTable.put("I", 1);
    romanTable.put("V", 5);
    romanTable.put("X", 10);
    romanTable.put("L", 50);
    romanTable.put("C", 100);
    romanTable.put("D", 500);
    romanTable.put("M", 1000);

    int res = 0;
    for (int i = 0; i < s.length();) {
        if (i + 1 < s.length() && romanTable.get(s.substring(i, i+1)) < romanTable.get(s.substring(i+1, i+2))) {
            res += romanTable.get(s.substring(i+1, i+2)) - romanTable.get(s.substring(i, i+1));
            i += 2;
        }else{
            res += romanTable.get(s.substring(i, i+1));
            i += 1;
        }
    }

    return res;
}
```

### 复杂度分析

时间复杂度：O(N) <br/>
空间复杂度：O(1)

相比解法1，运行时间少了，但内存损耗不变。