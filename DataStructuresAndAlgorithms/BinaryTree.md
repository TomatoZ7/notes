# 二叉树

##  树（Tree）
什么样的数据结构是 **树** ？语言上理解可能比较困难，可以直接看图：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeImage1.jpg)

上图中我画了3棵 **树** ，这些 **树** 都有哪些特征？  
如果反过来看，可以很直观的发现，数据结构 **树** 跟我们现实中的 **树** 十分类似，图中的每一个圆圈我们称为 **节点**，相邻节点之间的连接关系称为 **父子关系**。

如下图所示，A、B节点为父子关系，其中A节点为B节点的**父节点**，B节点是A节点的**子节点**；  
B、C节点的父节点都是同一个节点，所以它们互称**兄弟节点**；  
我们把没有父节点的节点称为**根节点**，也就是图中的节点A；  
我们把没有子节点的节点称为**叶子节点**或**叶节点**，比如图中的G、H、E、F节点。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeImage2.png)

除此之外，树还有三个比较相似的概念：**高度**(Height)、**深度**(Depth)、**层**(Level)。它们的定义是这样的：  
+ 节点的高度： 节点到叶子节点的**最长路径**(边数)
+ 节点的深度： 节点到根节点所经历的边数
+ 节点的层数： 节点的深度 + 1
+ 树的高度： 根节点的高度

根据概念结合图片：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeImage3.png)

要更灵活的记住这三个概念，可以结合我们实际生活的理解：  
**高度** 的计算都是从下往上的，计数的起点为0；  
**深度** 的计算都是从上往下的，计数的起点为0，  
**层** 与深度类似，不过计数起点为1。

## 二叉树(Binary Tree)
树的结构千千万，其中要数 **二叉树** 使用最为广泛。

**二叉树**，顾名思义，就是树的每个节点最多有两个“叉”，也就是两个子节点，分别是**左子节点**和**右子节点**。不过，二叉树并不强制要求每个节点都必须含有两个子节点，有的只有左子节点，有的只有右子节点。下图中画的都是二叉树：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeImage4.png)

其中有两棵树比较特别，分别是中间和右边两棵树：

中间这棵树称为**满二叉树**，它所有的叶子节点都在最底层，除了叶子节点外，其他节点也都包含了左右两个节点。

右边的则是完全二叉树，它所有的叶子节点都在最底下两层，并且最底层的叶子节点都靠左排列，并且除了最后一层，其它层的节点数都要达到最大。

满二叉树很好理解，但是完全二叉树，有的人可能就分不清了。这里画了完全二叉树和非完全二叉树，你们可以比较一下他们的区别：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeImage5.png)

你可能会问，满二叉树的特征非常明显，且容易分辨。但是完全二叉树的特征并不明显，是否有单独提及的必要？为什么完全二叉树最后一层节点必须靠左排列，靠右排列不行吗？

我们带着问题，先来学习**如何表示（或者存储）一颗二叉树**？

想要存储一颗二叉树，我们有两种方法，一种是基于连表或引用的二叉链式存储法，另一种是基于数组的顺序存储法。

首先来看简单、直观的**链式存储法**。如下图所示，每个节点都包含了三个字段，其中一个用来存储节点的数据，另外两个用来存储节点的左右指针。基于这种方式，只要我们拎住根节点，就能将整个树串联起来。这种存储方式比较常用，大部分二叉树代码都是通过这种结构来实现的。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeImage6.png)

我们再来看，基于数组的**顺序存储法**。我们把根节点存放在下标为 i = 1 的位置，那么左子节点就存放在下标为 2 * i = 2 的位置，右子节点就存放在下标为 2 * i + 1 = 3 的位置。以此类推，B节点的左子节点就存放在 2 * i = 2 * 2 = 4 的位置，而右子节点就存放在 2 * i + 1 = 2 * 2 + 1 = 5 的位置。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeImage7.png)

总结一下就是，如果节点X存放在下标为i的位置，那么其父节点就存放在 `floor(i/2)` 的位置上，左右子节点分别存放在 2*i、2\*i+1 的位置上，一般来说，只要我们知道根节点（或任意节点）的存放位置，就可以通过这种方式将 树 还原出来。

基于数组的顺序存储法对于完全二叉树而言只会浪费一个下标为 0 的存储位置，而对于非完全二叉树，浪费位置都是大于1的：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeImage8.png)

所以，当完全二叉树使用基于数组的顺序存储法时，是最节省内存的一种存放方式。他不用像链表那样需要额外存储指针信息，也不用担心像其他二叉树一样会造成空间的浪费，这就是为什么完全二叉树会被单独提及的原因，也是为什么最底层节点都需靠左排列的原因。

## 二叉树的遍历
说完二叉树的概念，接下来我们来看二叉树的遍历，也是面试中的一个高频考点。

如何将一棵树的节点都遍历出来呢？经典的方法有三种：**前序遍历**、**中序遍历**、**后序遍历**。其中，前、中、后序分别表示的是节点与它左右子树节点遍历打印的先后顺序。
+ 前序遍历：对于树中的任意节点来说，先打印这个节点，再打印左子树，最后打印右子树。（根-左-右）
+ 中序遍历：对于树中的任意节点来说，先打印左子树，再打印这个节点，最后打印右子树。（左-根-右）
+ 后序遍历：对于树中的任意节点来说，先打印左子树，再打印右子树，最后打印这个节点。（左-右-根）

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeGif1.gif)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeGif2.gif)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/treeGif3.gif)

实际上，二叉树的前、中、后序遍历就是一个递归的过程。比如前序遍历，先打印根节点，然后递归打印左子树，接着递归打印右子树。

写递归代码的关键，就是递归公式。这里我们给出三种遍历顺序的递归公式：
```php
// 前序遍历
preOrder($node) = print($node->elem)->preOrder($node->left)->preOrder($node->right);

// 中序遍历
inOrder($node) =  inOrder($node->left)->print($node->elem)->inOrder($node->right);

// 后序遍历
postOrder($node) = postOrder($node->left)->postOrder($node->right)->print($node->elem);
```

> 具体的实现代码请看文末。

那么二叉树遍历的时间复杂度是多少？结合 gif 图和递归流程的分析，每一个节点被访问的次数最多为两次。所以遍历的时间复杂度与 n 成正比，根据大O表示法，那么最终时间复杂度就是 O(n)。

## 思考

## 完全二叉树的代码实现
php 版：
```php
// 先声明一个节点类
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
class BinaryTree
{
    public function __construct()
	{
		$this->root = null;
    }
    
    /**
	 * 添加节点
	 *
	 * @param $item 数据值
	 */
	public function add($item)
	{
		$node = new Node($item);

		// 特殊情况：空树
		if ($this->root == null) {
			$this->root = $node;
			return true;
		}

		// 对于一般情况，可以模仿队列这类数据结构
		$queue = [$this->root];
		while ( !empty($queue) ) {
			$cur_node = array_shift($queue);

			if ( is_null($cur_node->left) ) {
				$cur_node->left = $node;
				return true;
			} else {
				array_push($queue, $cur_node->left);
			}

			if ( is_null($cur_node->right) ) {
				$cur_node->right = $node;
				return true;
			} else {
				array_push($queue, $cur_node->right);
			}
		}
    }
    
    /**
	 * 前序遍历
	 *
	 * @param $node 节点
     */
    public function preOrder($node)
    {
    	if ( is_null($this->root) ) {
    		return;
    	}

    	if ( !$node ) {
    		$node = $this->root;
    	}

    	print($node->elem);
    	$this->preOrder($this->left);
    	$this->preOrder($this->right);
    }

    /**
	 * 中序遍历
	 *
	 * @param $node 节点
     */
    public function inOrder($node)
    {
    	if ( is_null($this->root) ) {
    		return;
    	}

    	if ( !$node ) {
    		$node = $this->root;
    	}

    	$this->inOrder($this->left);
    	print($node->elem);
    	$this->inOrder($this->right);
    }

    /**
	 * 后序遍历
	 *
	 * @param $node 节点
     */
    public function postOrder($node)
    {
    	if ( is_null($this->root) ) {
    		return;
    	}

    	if ( !$node ) {
    		$node = $this->root;
    	}

    	$this->postOrder($this->left);
    	$this->postOrder($this->right);
    	print($node->elem);
    }

    /**
	 * 广度优先遍历
     */
    public function breadth()
    {

    }
}
```