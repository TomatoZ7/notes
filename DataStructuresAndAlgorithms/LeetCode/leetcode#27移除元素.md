# leetcode 第二十七题：移除元素

给你一个数组 `nums` 和一个值 `val`，你需要 原地 移除所有数值等于 `val` 的元素，并返回移除后数组的新长度。

不要使用额外的数组空间，你必须仅使用 `O(1)` 额外空间并 [原地](https://baike.baidu.com/item/%E5%8E%9F%E5%9C%B0%E7%AE%97%E6%B3%95) 修改输入数组。

元素的顺序可以改变。你不需要考虑数组中超出新长度后面的元素。

### 说明

为什么返回数值是整数，但输出的答案是数组呢?

请注意，输入数组是以「引用」方式传递的，这意味着在函数里修改输入数组对于调用者是可见的。

你可以想象内部操作如下:

```java
// nums 是以“引用”方式传递的。也就是说，不对实参作任何拷贝
int len = removeElement(nums, val);

// 在函数里修改输入数组对于调用者是可见的。
// 根据你的函数返回的长度, 它会打印出数组中 该长度范围内 的所有元素。
for (int i = 0; i < len; i++) {
    print(nums[i]);
}
```

### 示例1
```
输入：nums = [3,2,2,3], val = 3
输出：2, nums = [2,2]
解释：函数应该返回新的长度 2, 并且 nums 中的前两个元素均为 2。你不需要考虑数组中超出新长度后面的元素。例如，函数返回的新长度为 2 ，而 nums = [2,2,3,3] 或 nums = [2,2,0,0]，也会被视作正确答案。
```

### 示例2
```
输入：nums = [0,1,2,2,3,0,4,2], val = 2
输出：5, nums = [0,1,3,0,4]
解释：函数应该返回新的长度 5, 并且 nums 中的前五个元素为 0, 1, 3, 0, 4。注意这五个元素可为任意顺序。你不需要考虑数组中超出新长度后面的元素。
```

### 提示

+ `0 <= nums.length <= 100`
+ `0 <= nums[i] <= 50`
+ `0 <= val <= 100`

## 解法1：双指针

可类比 [leetcode 第 26 题：删除排序数组的重复项](https://github.com/TomatoZ7/notes-of-tz/blob/master/DataStructuresAndAlgorithms/leetcode/leetcode%2326%E5%88%A0%E9%99%A4%E6%8E%92%E5%BA%8F%E6%95%B0%E7%BB%84%E7%9A%84%E9%87%8D%E5%A4%8D%E9%A1%B9.md)。

```java
class Solution {
    public int removeElement(int[] nums, int val) {
        if (nums.length == 0){
            return 0;
        }

        int i = 0;

        for (int j = 0; j < nums.length; j++) {
            if (nums[j] != val) {
                nums[i] = nums[j];
                i++;
            }
        }

        return i;
    }
}
```

### 复杂度分析

时间复杂度：O(N)
空间复杂度：O(1)

## 解法2：双指针 ———— 当要删除的元素比较少时

### 思路

现在考虑数组包含很少的要删除的元素的情况。例如，`num=[1，2，3，5，4], Val=4`。之前的算法会对前四个元素做不必要的复制操作。另一个例子是 `num=[4，1，2，3，5], Val=4`。似乎没有必要将 `[1，2，3，5]` 这几个元素左移一步，因为问题描述中提到元素的顺序可以更改。

### 算法

当我们遇到 `nums[i] = val` 时，我们可以将当前元素与最后一个元素进行交换，并释放最后一个元素。这实际上使数组的大小减少了 1。

请注意，被交换的最后一个元素可能是您想要移除的值。但是不要担心，在下一次迭代中，我们仍然会检查这个元素。

```java
class Solution {
    public int removeElement(int[] nums, int val) {
        int i = 0;
        int n = nums.length;
        while (i < n) {
            if (nums[i] == val) {
                nums[i] = nums[n - 1];
                n--;
            } else {
                i++;
            }
        }
        return n;
    }
}
```

### 复杂度分析

时间复杂度：O(N)
空间复杂度：O(1)