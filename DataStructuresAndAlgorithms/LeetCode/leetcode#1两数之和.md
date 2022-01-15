# leetcode 第一题：两数之和
给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出**和**为目标值的那两个整数，并返回它们的数组下标。

你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。

你可以按任意顺序返回答案。

### 示例1
```
输入：nums = [2,7,11,15], target = 9
输出：[0,1]
解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
```

### 示例2
```
输入：nums = [3,2,4], target = 6
输出：[1,2]
```

### 示例3
```
输入：nums = [3,3], target = 6
输出：[0,1]
```

## 解法1：暴力枚举，双循环检索
```java
public int[] twoSum(int[] nums, int target) {
    for (int i = 0;i < nums.length; i++){
        for (int j = i+1; j < nums.length; j++){
            if (nums[i] + nums[j] == target) {
                return new int[]{i, j};
            }
        }
    }
    return new int[0];
}
```

### 复杂度分析
+ 时间复杂度：O(N²)
+ 空间复杂度：O(1)

## 解法2：借助哈希表，减少一次循环，以空间换时间
```java
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    map.put(nums[0], 0);
    for (int i = 1;i < nums.length;i++) {
        if (map.containsKey(target - nums[i])) {
            return new int[]{map.get(target - nums[i]), i};
        }
        map.put(nums[i], i);
    }
    return new int[0];
}
```

### 复杂度分析
+ 时间复杂度：O(N)
+ 空间复杂度：O(N)，主要是哈希表的开销