# leetcode 第七题：整数反转

给你一个 32 位的有符号整数 x ，返回 x 中每位上的数字反转后的结果。

如果反转后整数超过 32 位的有符号整数的范围 [−2^31,  2^31 − 1] ，就返回 0。

假设环境不允许存储 64 位整数（有符号或无符号）。

### 示例1
```
输入：x = 123
输出：321
```

### 示例2
```
输入：x = -123
输出：-321
```

### 示例3
```
输入：x = 120
输出：21
```

### 示例4
```
输入：x = 0
输出：0
```

## 思路

反转整数可以类比于反转字符串（但肯定不是把整数转为字符串进行反转）

我们需要不断弹出 x 的最后一位数字，并将它加入 res 的后面。最后 res 与 x 形成反转。

要在没有辅助堆栈/数组的帮助下 弹出/推入 数字，我们可以使用数学方法。

```java
// pop
pop = x % 10;
x /= 10;

// push
temp = res * 10 + pop;
res = temp;
```

但是，这种方法可能会导致 `temp = res * 10 + pop` 溢出。

所以我们需要做出判断

+ 当 x 为正数时：

1. 如果 `temp = res * 10 + pop` 结果溢出，则 `temp >= INTMAX / 10` 必为真
2. 如果 `res > INTMAX / 10` ,那么 `temp = res * 10 + pop` 一定会溢出
3. 如果 `res == INTMAX / 10` ,那么只要 pop > 7, `temp = res * 10 + pop` 就会溢出

+ 同理，当 x 为负数时：

1. 如果 `temp = res * 10 + pop` 结果溢出，则 `res <= INTMIN / 10` 必为真
2. 如果 `res < INTMIN / 10` ,则 `temp = res * 10 + pop` 一定溢出
3. 如果 `res == INTMIN / 10` ,那么只要 pop < -8，`temp = res * 10 + pop` 就会溢出

## 解

```java
public int reverse(int x) {
    int res = 0;

    while (x != 0) {
        int pop = x % 10;

        if (res > Integer.MAX_VALUE / 10 || (res == Integer.MAX_VALUE / 10 && pop > 7)) {
            return 0;
        }

        if (res < Integer.MIN_VALUE / 10 || (res == Integer.MIN_VALUE / 10 && pop < -8)) {
            return 0;
        }

        res = res * 10 + pop;
        x /= 10;
    }

    return res;
}
```

## 复杂度分析
时间复杂度：O(log(x)), 大概需要循环 log10(x) 次。  
空间复杂度：O(1)

## 其他

关于 2^31 最后一位是多少，可以用 数学归纳法 进行推导

如果实在无法推导，也可以将 `pop > 7` 改为 `pop > Integer.MAX_VALUE % 10`, `pop < -8` 改为 `pop < Integer.MIN_VALUE % 10`

除此之外，我也发现了下面这段有趣的**解法**：

```java
public int reverse(int x) {
    int ans = 0;
	while (x != 0) {
		if ((ans * 10) / 10 != ans) {
			ans = 0;
			break;
		}
		ans = ans * 10 + x % 10;
		x = x / 10;
	}
	return ans;
}
```

不得不说 `ans * 10) / 10 != ans` 这个写法很棒

但是 `ans * 10` 如果溢出，java虚拟机内部实际上是进行了数值类型提升，即溢出时，用long类型数据暂时存储，最后通过变窄转换，保留低32位数值得到 `(ans * 10) / 10 != ans`

与题目规定的 **假设环境不允许存储 64 位整数** 不符