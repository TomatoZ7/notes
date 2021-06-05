# PHP7 新特性

PHP7 除了在性能方面有了极大的提升外，还添加了很多新的特性，比如太空船操作符、标量类型声明、返回值的类型声明、全局的 throwable 接口、抽象语法树等，下面分别介绍。

## 太空船操作符

太空船操作符用于比较两个表达式。例如，当 `$a` 小于、等于或大于 `$b` 时，它分别返回 -1、0 或 1。比较的原则沿用 PHP 的常规比较规则进行。

```php
// 整数
echo 1 <=> 2;   // -1
echo 1 <=> 1;   // 0
echo 2 <=> 1;   // 1

// 浮点数
echo 1.5 <=> 2.5;   // -1
echo 1.5 <=> 1.5;   // 0
echo 2.5 <=> 1.5;   // 1

// 字符串
echo 'a' <=> 'b';   // -1
echo 'a' <=> 'a';   // 0
echo 'b' <=> 'a';   // 1
```

## 标量类型声明 & 返回值的类型声明

PHP7 可以对下面几种类型的参数做声明：

1. 字符串(string)
2. 整型(int)
3. 浮点型(float)
4. 布尔型(bool)

注意参数类型声明不受制于默认模式和严格模式。默认模式下，当传入的的参数不符合声明类型时，会首先尝试转换类型；而严格模式下，则直接报错。

```php
declare(strict_types=1);    // strict_types=1 表示开启严格模式
function sumOfInts(int ...$ints)
{
    return array_sum($ints);
}

var_dump(sumOfInts(2, '3.1', 4.1));
```

运行结果：

```
Fatal error:  Uncaught TypeError: Argument 2 passed to sumOfInts() must be of the type int, string given...
```

当注释掉第一行代码时，程序才可以正常运行：

PHP 会首先尝试把 `'3.1'` 转为 int 类型的 `3`，然后再执行。(注意：这里的类型转换仅受制于可转换的类型，例如不能把 `a` 转为 int 型)。

修改上面代码，再来看看返回值类型受限制的情况：

```php
declare(strict_types=1);
function sumOfInts(int ...$ints) : int
{
    return array_sum($ints);
}

var_dump(sumOfInts(2, 3, 4));
```

运行结果：

```
int(9)
```

这段代码额外声明了返回值的类型为 int 型。如果返回值的类型不是 int 型，在默认模式下，PHP 会首先尝试转换返回值的类型为 int 型，如果不能转换，则直接报错。

**PHP7.1** 对函数返回值的声明做了扩充，可以定义其返回值为 void，无论是否开启严格模式，只要函数中含有 `return;` 以外的其他 return 语句都会报错。

注意：参数类型不可以是 void。

```php
declare(strict_types=1);
function sumOfInts(int ...$ints) : void
{
    return;
}

var_dump(sumOfInts(2, 3, 4));
```

运行结果：

```
NULL
```

**PHP7.1.0** 对参数类型和返回值类型还有进一步的支持，其类型可以是可空类型，在参数或返回值类型声明前边加上 `?`，表示返回值要么是 null，要么是声明的类型。

```php
declare(strict_types=1);
function test(? int $a) : ? int
{
    return $a;
}
var_dump(test(null));   // NULL
var_dump(test(1));      // 1
var_dump(test('a'));    // ERROR
```

## null 合并操作符

在 PHP7 之前，人们经常会写这样的代码:

```php
$page = isset($_GET['page']) ? $_GET['page'] : 0;
```

PHP7 提供了一个新的语法糖 `??`，如果变量存在且值不为 null，它会返回自身的值，否则返回它的第二个操作数，可以改写成这样：

```php
$page = $_GET['page'] ?? 0;
```

当代码中有连续的三元运算符的时候还可以像下边这样写：

```php
$page = $_GET['page'] ?? $_POST['page'] ?? 0;
```

## 常量数组

在 PHP7 之前是无法通过 `define` 来定义一个常量数组的，PHP7 支持了这个操作。

```php
define('person', [
    'man', 'woman', 'kid'
]);

var_dump(person);   
/*
    array(3) {
        [0]=>
        string(3) "man"
        [1]=>
        string(5) "woman"
        [2]=>
        string(3) "kid"
    }
*/
```

## namespace 批量导入

在 PHP7 之前，如果要导入一个 namespace 下的多个 class，我们需要这样写：

```php
use Space\ClassA;
use Space\ClassB;
use Space\ClassC as C;
```

在 PHP7 中支持批量导入：

```php
use Space\{ClassA, ClassB, ClassC as C}
```

## throwable 接口

在 PHP7 之前，如果代码中有语法错误，或者 fatal error 时，程序会直接报错退出，但是在 PHP7 中有了改变。PHP7 实现了全局 throwable 接口，原来的 Exception 和部分 Error 实现了该接口。这种 Error 可以像 Exception 一样被第一个匹配的 `try / catch` 块捕获。如果没有匹配的 catch 块，则调用异常处理函数进行处理。如果尚未注册异常处理函数，则按照传统方式处理(fatal error)。

Error 类并非继承自 Exception 类，所以不能用 `catch (Exception $e) {...}` 来捕获 Error。可以用 `catch (Error $e) {...}`，或者通过注册异常处理函数 `set_exception_handler()` 来捕获 Error。

```php
try {
    undefindfunc();
}catch (Error $e) {
    var_dump($e);
}

// 或者
set_exception_handler(function ($e) {
    var_dump($e);
});
undefindfunc();
```

## Closure::call()

在 PHP7 之前，我们需要动态地给一个对象添加方法时，可以通过 Closure 来复制一个闭包对象，并绑定到一个 `$this` 对象和类作用域：

```php
class Test {
    private $num = 1;
}

$f = function() {
    return $this->num + 1;
};

$test = $f->bindTo(new Test, 'Test');
echo $test();     // 2
```

在 PHP7 中新添加了 Closure::call()，可以通过 call 来暂时绑定一个闭包对象到 `$this` 对象来调用它：

```php
class Test {
    private $num = 1;
}

$f = function() {
    return $this->num + 1;
};

echo $f->call(new Test);    // 2
```

## intdiv 函数

PHP7 中还增加了一个新的整除函数，在代码中不需要再手动转了：

```php
// var_dump(intval(10 / 3));
var_dump(intdiv(10, 3));
```

## list 的方括号写法

我们知道可以通过 list 来实现解构赋值，如下：

```php
$arr = [1, 2, 3];
list($a, $b, $c) = $arr;
```

PHP7.1.0 对其做了进一步的优化，可以将其写成如下方法：

```php
[$a, $b, $c] = $arr;
```

注意：这里的 [] 并不是数组的意思，只是 list 的简略形式。

## 其他

除了上文这些，PHP7 还有很多其他的改变和特性。例如，foreach 遍历数组时不再修改内部指针、移除了 ASP 和 scriptPHP 标签、移除了 $HTTP_RAW_POST_DATA、匿名类、类常量可见性等。

## 参考

《PHP7 底层设计与源码实现》