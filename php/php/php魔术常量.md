# PHP 8 个魔术常量

PHP 向它运行的任何脚本提供了大量的预定义常量。

不过很多常量都是由不同的扩展库定义的，只有在加载了这些扩展库时才会出现，或者动态加载后，或者在编译时已经包括进去了。

有八个魔术常量它们的值随着它们在代码中的位置改变而改变。

### \_\_LINE\_\_

文件中的当前行号。

```php
echo '这是第' . __LINE__ . '行';    // 这是第1行
```

### \_\_FILE\_\_

文件的完整路径和文件名。如果用在被包含文件中，则返回被包含的文件名。

```php
// test.php
$test_file = __FILE__;
```

```php
// main.php
require_once('./test.php');

echo __FILE__;      // /var/www/phpunit/main.php

echo $test_file;    // /var/www/phpunit/test.php
```

### \_\_DIR\_\_

```php
// main.php
echo __DIR__;       // /var/www/phpunit
```

### \_\_FUNCTION\_\_

返回函数被定义时的名字，区分大小写。

```php
function test() {
    echo '函数名为：' . __FUNCTION__;
}
test();             // 函数名为：test
```

### \_\_CLASS\_\_

返回类被定义时的名字，类名包括其被声明的作用区域，如 `Foo\Bar`。

自 PHP5.4 起，`__CLASS__` 对 trait 也起作用。当用在 trait 方法中时，`__CLASS__` 是调用 trait 方法的类的名字。

```php
namespace PHPUnit;

class Test
{
    public $class_name = __CLASS__;
}

$test = new Test();
echo $test->class_name;     // PHPUnit\Test
```

### \_\_TRAIT\_\_

与上述类似，返回 trait 定义时的名字，包括作用区域。


### \_\_METHOD\_\_

类的方法名，包括作用区域和类名(区分大小写)。

```php
namespace PHPUnit;

class Test
{
    public function methodTest()
    {
        echo __METHOD__;
    }

    public function functionTest()
    {
        echo __FUNCTION__;
    }
}
$test = new Test();

$test->methodTest();        // PHPUnit\Test::methodTest
echo PHP_EOL;
$test->functionTest();      // functionTest
```

### \_\_NAMESPACE\_\_

返回命名空间的名称(区分大小写)。

```php
namespace PHPUnit\User;

echo __NAMESPACE__;         // PHPUnit\User
```