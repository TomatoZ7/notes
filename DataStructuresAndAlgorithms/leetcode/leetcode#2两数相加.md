# leetcode 第二题：两数相加

给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。

请你将两个数相加，并以相同形式返回一个表示和的链表。

你可以假设除了数字 0 之外，这两个数都不会以 0 开头。

### 示例1
![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/addtwonumber1.jpg)

```
输入：l1 = [2,4,3], l2 = [5,6,4]
输出：[7,0,8]
解释：342 + 465 = 807.
```

### 示例2
```
输入：l1 = [0], l2 = [0]
输出：[0]
```

### 示例3
```
输入：l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
输出：[8,9,9,9,0,0,0,1]
```

## 解

因为考虑到是**倒叙**的一个整数，所以只需像小学加法一样，从个位(链表头)开始依次相加即可，同时记录进位符。

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
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // 初始化输出链表
        ListNode tail = null;
        ListNode head = null;

        // 初始化进位符
        int carry = 0;
        
        while (l1 != null || l2 != null) {
            int val1 = l1 != null ? l1.val : 0;
            int val2 = l2 != null ? l2.val : 0;

            // 需记录链表头，因为返回的是链表头
            if (head == null) {
                head = tail = new ListNode((val1 + val2)%10);
            }else{
                tail.next = new ListNode((val1 + val2 + carry)%10);
                tail = tail.next;
            }

            carry = (val1 + val2 + carry)/10;

            if (l1 != null) {
                l1 = l1.next;
            }
            if (l2 != null) {
                l2 = l2.next;
            }
        }

        // 结束时进位符大于0，则需要多加一位
        if (carry > 0) {
            tail.next = new ListNode(carry);
        }

        return head;
    }
}
```