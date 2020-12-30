# 二叉查找树

在 [二叉树基础](https://github.com/TomatoZ7/notes-of-tz/blob/master/DataStructuresAndAlgorithms/BinaryTreeOne.md) 中我们已经对树、二叉树以及二叉树的遍历有了基本的认识，本文主要对**二叉查找树**进行探讨。

## 二叉查找树 (Binary Search Tree)
二叉查找树，又名二叉搜索树，是二叉树中最常用的一种数据类型。

二叉查找树是为了实现快速查找而实现的，除此之外，它还支持快速插入、删除一个数据。它是怎么做到的呢？

这些都依赖于二叉查找树的特殊结构：**二叉查找树要求，对于树中的任意一个节点，其左子树的值必须小于当前节点的值，其右子树的值必须大于当前节点的值。**可以参考下面图片进行理解：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/SearchTreeImg.png)

那么二叉树是如何实现快速查找，添加和删除操作的呢？接下来我们对这三个操作来一一探讨一下。

&emsp;

### 1. 二叉查找树的查找操作
首先，我们看查找操作。给定查找值的前提下，我们先取根节点，如果根节点的值刚好等于给定值，则返回；如果大于给定值，则我们在左子树递归查找；如果小于给定值，则在右子树递归查找。如图所示。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/SearchTreeImg2.png)

具体实现的代码如下(php)：
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

> 其他具体细节可移步：[传送门](https://github.com/TomatoZ7/notes-of-tz/blob/master/DataStructuresAndAlgorithms/BinaryTreeOne.md)

&emsp;

### 2. 二叉查找树的添加操作
二叉查找树的添加操作和查找操作类似，新插入的数据一般都在叶子节点上，所以我们只需要像查找操作那样从根节点开始，依次对比数据的大小关系，直到满足条件。如图所示。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/SearchTreeImg3.png)

插入的代码如下(php)：
```php
class BinarySearchTree
{
    // ...

    public function add($data)
    {
        if ( is_null($this->root) ) {
            $this->root = new Node($data);
            return true;
        }

        $cur_node = $this->root;
        while ( !is_null($cur_node->elem) ) {
            if ( $cur_node->elem < $data ) {

                if ( is_null($cur_node->right) ) {
                    $cur_node->right = new Node($data);
                    return true;
                }
                $cur_node = $cur_node->right;
                
            }else if ( $cur_node->elem > $data ) {

                if ( is_null($cur_node->left) ) {
                    $cur_node->left = new Node($data);
                    return true;
                }
                $cur_node = $cur_node->left;

            }
        }
    }
}
```

&emsp;

### 3. 二叉查找树的删除操作
二叉查找树的查找、添加都比较简单易懂，删除可能会有些许麻烦。删除的话针对删除节点的子节点个数(0,1,2)，分为三种情况进行分析：  

1. 当删除的节点没有子节点时，我们只需将其父节点指向该节点的指针置空即可，比如删除图中的；  

2. 当删除的节点有且只有一个子节点时(只有左子节点或右子节点)，我们只需将其父节点指向该节点的指针指向该节点的子节点即可。比如删除图中的；  

3. 当删除的节点有两个子节点时，我们需要将其右子树中最小的节点(或者左子树中最大的节点)，替换到要删除的节点的位置即可。比如删除图中的。

![image]()

老规矩，附上删除的代码(php)
```php
class BinarySearchTree
{
    // ...

    public function delete($data)
    {
        if ( is_null($this->root) ) {
            return true;
        }

        $cur_node = $this->root;    // 记录当前节点信息
        $par_node = null;           // 记录父节点信息

        // 查找节点
        while ( !is_null($cur_node) ) {

        }
    }
}
```