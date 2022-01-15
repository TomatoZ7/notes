# leetcode 第二十一题：合并两个有序的链表

将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 

### 示例1

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/leetcode%2321.jpg)

```
输入：l1 = [1,2,4], l2 = [1,3,4]
输出：[1,1,2,3,4,4]
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

## 解法1：递归

这里递归用到的思路主要就是比较 l1 和 l2 的 val 大小，再将小的节点链上下一次递归的比较结果，并返回这个小的节点。

需要注意的是如果 l1 或 l2 为空，则直接返回不为空的连表即可。

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if ( l1 == null ) {
            return l2;
        }else if ( l2 == null ) {
            return l1;
        }else if ( l1.val < l2.val ) {
            l1.next = mergeTwoLists(l1.next, l2);
            return l1;
        }else{
            l2.next = mergeTwoLists(l1, l2.next);
            return l2;
        }
    }
}
```

### 复杂度分析

时间复杂度：O(m+n)，其中 n 和 m 分别代表两个链表的长度。因为每次递归都会去掉 l1 或 l2 的头节点(知道至少有一个连表为空)，函数 mergeTwoLists 至多只会调用每个节点一次。因此，时间负责度取决于合并后的链表长度，即 O(m+n)。

空间复杂度：O(n+m)，其中 n 和 m 分别为两个链表的长度。递归调用 mergeTwoLists 函数时需要消耗栈空间，栈空间的大小取决于递归调用的深度。结束递归调用时 mergeTwoLists 函数最多调用 n+m 次，因此空间复杂度为 O(n+m)。

## 迭代

可以类比归并排序合并的步骤，就是用一个新的链表去存 l1 和 l2 节点的较小值，然后更新指针即可。

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode prehead = new ListNode(-1);

        ListNode prev = prehead;

        while (l1 != null && l2 != null) {
            if ( l1.val < l2.val ) {
                prev.next = l1;
                l1 = l1.next;
            }else{
                prev.next = l2;
                l2 = l2.next;
            }
            prev = prev.next;
        }

        prev.next = l1 == null ? l2 : l1;

        return prehead.next;
    }
}
```

### 复杂度分析

时间复杂度：O(m+n)，其中 n 和 m 分别代表两个链表的长度。具体循环与解法1类似。

空间复杂度：O(1)，我们只需要常数空间来存放若干变量。