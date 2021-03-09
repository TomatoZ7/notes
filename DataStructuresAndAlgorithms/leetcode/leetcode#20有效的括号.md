# leetcode 第二十题：有效的括号

给定一个只包括 `'('`，`')'`，`'{'`，`'}'`，`'['`，`']'` 的字符串 s ，判断字符串是否有效。

有效字符串需满足：

1. 左括号必须用相同类型的右括号闭合。
2. 左括号必须以正确的顺序闭合。

### 示例1
```
输入：s = "()"
输出：true
```

### 示例2
```
输入：s = "(]"
输出：false
```

### 示例3
```
输入：s = "([)]"
输出：false
```

### 示例4
```
输入：s = "([)]"
输出：false
```

### 示例5
```
输入：s = "{[]}"
输出：true
```

### 提示

+ `1 <= s.length <= 104`
+ `s` 仅由括号 `'()[]{}'` 组成


## 解：利用栈stack

我们只需遍历字符串，遇到 左括号 则压栈，遇到 右括号 则与栈顶元素比较是否能组合成一对括号，如果能则弹出栈顶元素，如果不能返回 false。

如下图所示：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/leetcode%2320.gif)

为了快速判断括号的类型，我们可以使用哈希表存储每一种括号。哈希表的键为右括号，值为相同类型的左括号。

```java
class Solution {
    public boolean isValid(String s) {
        int len = s.length();
        if (len % 2 == 1) return false;

        Map<String, String> mapTable = new HashMap<String, String>(){{
            put(")", "(");
            put("]", "[");
            put("}", "{");
        }};

        Stack<String> stk = new Stack<String>();

        for (int i = 0; i < len; i++) {
            String str = s.substring(i, i+1);
            if (mapTable.containsKey(str)) {
                if ( stk.empty() || !stk.peek().equals(mapTable.get(str)) ) {
                    return false;
                }
                stk.pop();
            }else{
                stk.push(str);
            }
        }

        return stk.empty();
    }
}
```

### 复杂度分析

时间复杂度：O(n)。
空间复杂度：O(n)。