# php 引用计数基本知识

## 引用计数

php 官方手册介绍 php 的每个变量都是存在一个叫做 zval 的容器里面，这个容器不仅包含了这个变量的值和类型，还包含了另外两个重要的信息：

`is_ref` 和 `refcount`

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref4.png)

+ `is_ref` 看名字就应该知道大概和引用相关，它是一个 bool 值，如果这个值是 true 那么代表这是一个引用变量，否则是普通变量。
+ `refcount` 指的是有多少个变量（符号）指向这个 zval 容器。

更直白但不准确的说：

+ `refcount` 多少个变量是一样的用了相同的值，这个数值就是多少。
+ `is_ref` 当 `refcount` 大于 2 的时候，其中一个变量用了地址 `&` 的形式进行赋值，好了，它就变成 1 了。

## 实操

需安装 xdebug 插件并开启。

### 查看内部结构

```php
$name = 'TomatoZ777';
xdebug_debug_zval('name');
```

会得到

```
name: (refcount=1, is_ref=0), string 'TomatoZ777' (length=10)
```

### 增加一个计数

```php
$name = 'TomatoZ777';
$temp_name = $name;
xdebug_debug_zval('name');
```

会得到

```
name: (refcount=2, is_ref=0), string 'TomatoZ777' (length=10)
```

可以看到 `refcount` +1 了。

### 引用赋值

```php
$name = 'TomatoZ777';
$temp_name = &$name;
xdebug_debug_zval('name');
```

会得到

```
name: (refcount=2, is_ref=1), string 'TomatoZ777' (length=10)
```

引用赋值会导致 zval 通过 `is_ref` 来标记是否存在引用的情况。

### 销毁变量

```php
$name = 'TomatoZ777';
$temp_name = $name;
xdebug_debug_zval('name');
unset($temp_name);
xdebug_debug_zval('name');
```

会得到

```
name: (refcount=2, is_ref=0), string 'TomatoZ777' (length=10)
name: (refcount=1, is_ref=0), string 'TomatoZ777' (length=10)
```

`refcount` 计数减 1，说明 `unset` 并非一定会释放内存，当有两个变量指向的时候，并非会释放变量占用的内存，只是 `refcount` 减 1。

### 数组型变量

```php
$name = [
    'a' => 'Tomato',
    'b' => 'Z777'
];
xdebug_debug_zval('name');
```

会得到

```
name: 
(refcount=1, is_ref=0),
array (size=2)
    'a' => (refcount=1, is_ref=0), string 'Tomato' (length=6)
    'b' => (refcount=1, is_ref=0), string 'Z777' (length=4)
```

对于数组来看是一个整体，对于内部的 kv 来看又是分别独立的整体，各自都维护着一套 zval 的 `refcount` 和 `is_ref`。

### 往数组型变量里添加元素

来自官方示例

```php
$a = [
    'meaning' => 'life',
    'number' => 42
];
$a['life'] = $a['meaning'];
xdebug_debug_zval('name');
```

会得到

```
a: (refcount=1, is_ref=0)=array (
    'meaning' => (refcount=2, is_ref=0)='life',
    'number' => (refcount=1, is_ref=0)=42,
    'life' => (refcount=2, is_ref=0)='life'
)
```

key 为 meaning 和 life 的值指向同一个 zval 容器，refcount = 2。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref1.png)

### 往数组里添加自身的引用

```php
$a = ['one'];
$a[] = &$a;
xdebug_debug_zval('a');
```

会得到

```
a: (refcount=2, is_ref=1)=array (
   0 => (refcount=1, is_ref=0)='one',
   1 => (refcount=2, is_ref=1)=...
)
```

`$a` 数组本身指向的容器 `refcount` 变成了 2，因为 `$a` 和 `$a[1]` 指向了这个容器，然而 `$a[1]` 又是 `$a` 的元素，这个元素又引用了 `$a` 本身，这就形成了一个闭环。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref2.png)

如果这个时候执行 `unset($a)`，那么 `$a` 指向的容器 `refcount` -1变成1，这个时候对于我们程序员来说已经不存在有可操作的变量(符号)指向这个容器了，但是 `refcount=1` 会导致 php 引擎不会销毁这个变量。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref3.png)

那么这个容器在内存中就成了"垃圾"，这个时候如果没有垃圾回收机制 GC，那么只有等当前请求结束/脚本自动结束清除了。

但有时候我们需要递归/死循环去执行一些特殊的业务逻辑，这个时候如果有上述情况出现，那么就会导致内存泄漏，消耗很大的内存空间。

所以才需要有 5.3 版本以后新的[垃圾回收机制](https://github.com/TomatoZ7/notes-of-tz/blob/master/php/php/%E5%86%85%E5%AD%98(%E5%9E%83%E5%9C%BE)%E5%9B%9E%E6%94%B6%E6%9C%BA%E5%88%B6.md)。

## PHP7 新改动

从 PHP7 的 NTS 版本开始，普通的赋值将不再计算 `refcount` 的值。

具体分类如下：

1. 对于 null，bool，int 和 double 的类型变量，`refcount` 永远不会计数。

2. 对于对象、资源类型，`refcount` 计数和 PHP5 一致。

3. 对于字符串，未被引用的变量被称为**实际字符串**。而那些被引用的字符串被重复删除(即只有一个带有特定内容的被插入的字符串)并保证在请求的持续时间内存在，所以不需要为它们使用引用计数；如果使用了 opcache，这些字符串将存在于共享内存中，在这种情况下，不能使用引用计数(因为引用计数是非原子的)。

4. 对于数组，未引用的变量被称为`不可变数组`。其数组本身计数与 PHP5 一致，但是数组里面的每个键值对的计数，则按前面三条的规则(即如果是字符串也不在计数)；如果使用 opcache，则代码中的常量数组文字将被转换为不可变数组。再次，这些生活在共享内存，因此不能使用 `refcount`。

### 简单类型的引用计数

我们说的简单类型是指：bool(true/false), null, long, double

#### 赋值

```php
$a = 777;
$b = $a;
xdebug_debug_zval('a');
xdebug_debug_zval('b');
```

输出

```
a: (refcount=0, is_ref=0)=777
b: (refcount=0, is_ref=0)=777
```

赋值后，`$a`, `$b` 是两个独立的 zval 结构，如下：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref5.png)

#### 引用

```php
$a = 777;
$b = &$a;
xdebug_debug_zval('a');
xdebug_debug_zval('b');
```

输出

```
a: (refcount=2, is_ref=1)=777
b: (refcount=2, is_ref=1)=777
```

`$a`, `$b` 都变为**引用类型**，**引用类型**结构如下：

```c
typedef struct _zend_reference  zend_reference;
struct _zend_reference {
    zend_refcounted_h gc;   // 与垃圾回收相关
    zval              val;  // 一个 zval 结构
};
```

在该例子中，php 创建了一个引用类型的结构体，`$a`, `$b` 的 `value.ref` 均指向它：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref6.png)

