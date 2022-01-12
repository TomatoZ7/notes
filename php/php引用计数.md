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

具体如下：

1. 对于 null，bool，int 和 double 的类型变量，`refcount` **永远不会计数**。

2. 对于对象、资源类型，`refcount` 计数和 PHP5 一致。

3. 对于字符串，如果是临时字串，在赋值时会用到引用计数，但如果变量是字符常量，则不会计数。

4. 变量是普通的数组， 赋值时会用到引用计数，变量是 IS_ARRAY_IMMUTABLE 时，赋值不使用引用计数。

### 简单类型的引用计数

我们说的简单类型是指：bool(true/false), null, long, double

#### 赋值

```php
$a = 6.2;
$b = $a;
xdebug_debug_zval('a');
xdebug_debug_zval('b');
```

输出

```
a: (refcount=0, is_ref=0)=6.2
b: (refcount=0, is_ref=0)=6.2
```

赋值后，`$a`, `$b` 是两个独立的 zval 结构，如下：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref5.png)

#### 引用

```php
$a = 6.2;
$b = &$a;
xdebug_debug_zval('a');
xdebug_debug_zval('b');
```

输出

```
a: (refcount=2, is_ref=1)=6.2
b: (refcount=2, is_ref=1)=6.2
```

`$a`, `$b` 都变为**引用类型**，**引用类型**结构如下：

```c++
typedef struct _zend_reference  zend_reference;
struct _zend_reference {
    zend_refcounted_h gc;   // 与垃圾回收相关
    zval              val;  // 一个 zval 结构
};
```

在该例子中，php 创建了一个引用类型的结构体，`$a`, `$b` 的 `value.ref` 均指向它：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref6.png)

### 字符串的引用计数

```php
$h = 'test';
$i = $h;
xdebug_debug_zval('h');
xdebug_debug_zval('i');

$x = 'test' . time();
$y = $x;
xdebug_debug_zval('x');
xdebug_debug_zval('y');
```

输出

```
h: (refcount=0, is_ref=0)='test'
i: (refcount=0, is_ref=0)='test'
x: (refcount=2, is_ref=0)='test1621580318'
y: (refcount=2, is_ref=0)='test1621580318'
```

为什么同样是字符串，有的计算引用计数，而有的却计算了呢？

#### 字符串的类型

`zend_types.h` 中做了如下定义，注意，这个类型并不是记录在 `zval.u1.v.type` 中的，而是记录在 `zval.value->gc.u.flags` 中，主要服务于垃圾回收的。

```c++
/* string flags (zval.value->gc.u.flags) */
#define IS_STR_INTERNED             GC_IMMUTABLE  /* interned string */
#define IS_STR_PERSISTENT           GC_PERSISTENT /* allocated using malloc */
#define IS_STR_PERMANENT            (1<<8)        /* relives request boundary */
```

具体的字串类别见下图(参考《php7底层设计与源码实现》4.3.2 字符串的类别)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref7.png)

其中，内部字串和已知字串，都会存在于php运行的整个周期，不涉及垃圾回收问题，自然也不需要引用计数。

临时字串，只能在虚拟机执行 opcode 时计算出来并动态分配内存存储，需要引用计数。

#### 上述例子解析

由于 `time()` 只能在运行时计算，所以 `'test'.time()` 属于临时子串，赋值后存储情况如下图：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_ref8.png)

因而引用计数为 2。

### 数组的引用计数

#### 不可变数组

php7中引入了不可变数组（immutable array）的概念。一个不可变数组，是由不可变元素构成的，这些元素在编译阶段就可完全解析确定，比如 string, integer, float 等等。引入这种类型主要是为了优化内存。对于不可变数组，规定其初始引用计数为 2。

```php
$a = [1,2.1,'x'];
xdebug_debug_zval('a');

$b = $a;
xdebug_debug_zval('a');
xdebug_debug_zval('b');
```

输出

```s
a: (refcount=2, is_ref=0)=array (0 => (refcount=0, is_ref=0)=1, 1 => (refcount=0, is_ref=0)=2.1, 2 => (refcount=1, is_ref=0)='x')
a: (refcount=3, is_ref=0)=array (0 => (refcount=0, is_ref=0)=1, 1 => (refcount=0, is_ref=0)=2.1, 2 => (refcount=1, is_ref=0)='x')
b: (refcount=3, is_ref=0)=array (0 => (refcount=0, is_ref=0)=1, 1 => (refcount=0, is_ref=0)=2.1, 2 => (refcount=1, is_ref=0)='x')
```

解析：

+ `$a` 为不可变数组，所以引用计数为 2
+ `b=a` 赋值后，两者的 `zval.value.arr` 指向同一个 `zend_array`, 引用计数 +1, 所以 a, b 的引用计数都为 3。

#### 普通数组

如何生成一个普通数组呢？

* 动态生成数组
* 对不可变数组做任何改变(增减元素，改变元素值)

```php
$c = range(1,2);
xdebug_debug_zval('c');

$j = $c;
xdebug_debug_zval('j');
xdebug_debug_zval('c');
```

输出

```s
c: (refcount=1, is_ref=0)=array (0 => (refcount=0, is_ref=0)=1, 1 => (refcount=0, is_ref=0)=2)
j: (refcount=2, is_ref=0)=array (0 => (refcount=0, is_ref=0)=1, 1 => (refcount=0, is_ref=0)=2)
c: (refcount=2, is_ref=0)=array (0 => (refcount=0, is_ref=0)=1, 1 => (refcount=0, is_ref=0)=2)
```

解析：

+ `$c` 是普通数组，所以引用计数为 1
+ `j=c` 赋值后，引用计数 +1, 所以 c, j 的引用计数都为 2。

#### 修改不可变数组元素

```php
$a = ['y', 'x'];
xdebug_debug_zval('a');

print("after b=a\n");
$b = $a;
xdebug_debug_zval('a');
xdebug_debug_zval('b');

print("after change a[0]\n");
$a[0] = 'b';
xdebug_debug_zval('a');
xdebug_debug_zval('b');
```

输出

```s
a: (refcount=2, is_ref=0)=array (0 => (refcount=1, is_ref=0)='y', 1 => (refcount=1, is_ref=0)='x')
after b=a
a: (refcount=3, is_ref=0)=array (0 => (refcount=1, is_ref=0)='y', 1 => (refcount=1, is_ref=0)='x')
b: (refcount=3, is_ref=0)=array (0 => (refcount=1, is_ref=0)='y', 1 => (refcount=1, is_ref=0)='x')
after change a[0]
a: (refcount=1, is_ref=0)=array (0 => (refcount=1, is_ref=0)='b', 1 => (refcount=2, is_ref=0)='x')
b: (refcount=2, is_ref=0)=array (0 => (refcount=1, is_ref=0)='y', 1 => (refcount=2, is_ref=0)='x')
```

解析：

+ 起始 `$a` 是不可变数组，引用计数为 2。
+ `b=a`后，引用计数 +1。
+ 改变 `$a[0]` 后(这里发生了写时拷贝，即 `$a` 完全复制之前的数组，再修改第 0 个元素)，`$a` 成为普通数组，引用计数 -1。`$b` 指向原有的不可变数组，引用计数为 2。

### 类的引用计数

```php
class Demo{
    public $name = "ball";
}

$a = new Demo();
$b = $a;
xdebug_debug_zval('a');
xdebug_debug_zval('b');
```

输出

```s
a: (refcount=2, is_ref=0)=class Demo { public $name = (refcount=2, is_ref=0)='ball' }
b: (refcount=2, is_ref=0)=class Demo { public $name = (refcount=2, is_ref=0)='ball' }
```

## 参考资料

[php7引用计数](https://www.jianshu.com/p/ba99748f0730)  
[一看就懂系列之 由浅入深聊一聊php的垃圾回收机制](https://blog.csdn.net/u011957758/article/details/76864400)    
[深入理解 PHP7 中全新的 zval 容器和引用计数机制](https://www.jb51.net/article/148865.htm)   