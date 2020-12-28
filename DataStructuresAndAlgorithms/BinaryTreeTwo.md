# 二叉查找树

在 [二叉树基础](https://github.com/TomatoZ7/notes-of-tz/blob/master/DataStructuresAndAlgorithms/BinaryTree.md) 中我们已经对树、二叉树以及二叉树的遍历有了基本的认识，本文主要对**二叉查找树**进行探讨。

## 二叉查找树 (Binary Search Tree)
二叉查找树，又名二叉搜索树，是二叉树中最常用的一种数据类型。

二叉查找树是为了实现快速查找而实现的，除此之外，它还支持快速插入、删除一个数据。它是怎么做到的呢？

这些都依赖于二叉查找树的特殊结构：**二叉查找树要求，对于树中的任意一个节点，其左子树的值必须小于当前节点的值，其右子树的值必须大于当前节点的值。**可以参考下面图片进行理解：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/SearchTreeImg.png)

那么二叉树是如何实现快速查找，添加和删除操作的呢？接下来我们对这三个操作来一一探讨一下。

### 1. 二叉查找树的查找操作
首先，我们看查找操作。给定查找值的前提下，我们先取根节点，如果根节点的值刚好等于给定值，则返回；如果大于给定值，则我们在左子树递归查找；如果小于给定值，则在右子树递归查找。如图所示。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/SearchTreeImg2.png)

具体实现的代码可以如下(php)：
```php
class Node
{
    public function __construct($item)
    {
        $this->elem  = $item;
        $this->left  = null;
        $this->right = null;
    }
}
```
```php
class BinarySearchTree
{
    // ...

    public function find($data)
    {
        $cur_node = $this->root;
        while ( !is_null($cur_node->elem) ) {
            if ( $cur_node->elem < $data ) {
                $cur_node = $cur_node->right;
            }else if ( $cur_node->elem > $data ) {
                $cur_node = $cur_node->left;
            }else{
                return $cur_node;
            }
        }
        
        return;
    }
}
```