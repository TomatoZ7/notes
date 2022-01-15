# leetcode 第三十五题：搜索插入位置

给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。

你可以假设数组中无重复元素。

### 示例1
```
输入: [1,3,5,6], 5
输出: 2
```

### 示例2
```
输入: [1,3,5,6], 2
输出: 1
```

### 示例3
```
输入: [1,3,5,6], 7
输出: 4
```

### 示例4
```
输入: [1,3,5,6], 0
输出: 0
```

## 解：二分查找法

直接利用二分查找法即可。

```java
class Solution {
    public int searchInsert(int[] nums, int target) {
        int len = nums.length;

        if (nums[0] >= target) return 0;

        if (nums[len-1] < target) return len;

        return search(1, len-2, nums, target);
    }

    public int search(int p, int q, int[] nums, int target) {
        if (p > q) {
            return p;
        }

        int mid = (q + p)  / 2;
        
        if (nums[mid] == target) {
            return mid;
        }else if (nums[mid] < target) {
            return search(mid+1, q, nums, target);
        }else{
            return search(p, mid-1, nums, target);
        }
    }
}
```