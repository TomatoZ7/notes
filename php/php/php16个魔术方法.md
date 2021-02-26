# PHP 16 个魔术方法

## 前言

PHP 中把两个下划线 __ 开头的方法称为**魔术方法**(Magic Methods), 魔术方法包括：

1. __construct(), 类的构造函数
2. __destruct(), 类的析构函数
3. __call(), 在对象调用一个不可访问的方法时调用
4. __callStatic(), 用静态方式调用一个不可访问的方法时调用
5. __get(), 获得一个类的成员变量时调用 
6. __set(), 设置一个类的成员变量时调用 
7. __isset(), 当对不可访问属性调用 isset() 或 empty() 时调用
8. __unset(), 当对不可访问属性调用 unset() 时调用
9. __sleep(), 执行 serialize() 时会先调用这个函数
10. __wakeup(), 执行 unserialize() 时会先调用这个函数
11. __toString(), 类被当成字符串时的回应方法
12. __invoke(), 调用函数的方式调用一个对象时的回应方法
13. __set_state(), 调用 var_export() 导出类时，此静态方法会被调用
14. __clone(), 当对象复制完成时调用
15. __autoload(), 尝试加载未定义的类
16. __debuginfo(), 打印所调试的信息

## 一、 __construct()，类的构造函数

php 中构造方法是对象创建完成后第一个被对象自动调用的方法。在每一个类中都有一个构造方法，如果没有显示地声明它，那么类中都会默认存在一个没有参数且内容为空的构造方法。

1. 构造方法的作用

通常构造方法被用来执行一些有用的初始化任务，如对成员属性在创建对象时赋予初始值。

2. 构造方法在类中的声明格式

```php
function __construct(){
    // 方法体
}
```

3. 在类中声明构造函数需要注意的事项

+ 在同一个类中只能声明一个构造函数，原因是 PHP不支持构造函数重载
+ 构造函数名称以双下划綫开始 `__construct()`

4. 范例
```php
<?php
class Marvel
{
    public $name;
    public $color;
    public $gender;

    /**
     * 显示声明一个构造方法且带参数
     */  
    public function __construct($name = "", $color = "red", $gender = 1){
        $this->name = $name;
        $this->color = $color;
        $this->gender = $gender;
    }

    /**
     * getName 方法
     */
    public function getName(){
        echo "my name is" . $this->name;
    }
}
```

## 二、 __destruct()，类的析构函数

与构造函数相对的就叫析构函数

析构函数允许在销毁一个类之前执行一些操作，如释放结果集，关闭文件等

析构函数是 PHP5 才引进的新内容

1. 析构函数在类中的声明格式

```java
function __destruct() {
    // 方法体
}
```

**注意**：析构函数不能带有任何参数。

2. 析构函数的作用

一般来说，析构函数在 PHP 不是很常用，它属于类中可选择的一部分，通常用来完成一些在对象销毁前的清理任务。

3. 范例
```php
<?php
class Marvel
{
    public $name;
    public $color;
    public $gender;

    /**
     * 显示声明一个构造方法且带参数
     */  
    public function __construct($name = "", $color = "red", $gender = 1){
        $this->name = $name;
        $this->color = $color;
        $this->gender = $gender;
    }

    /**
     * getName 方法
     */
    public function getName(){
        echo "my name is" . $this->name;
    }

    /**
     * 声明一个析构方法
     */  
     public function __destruct() {
         echo "goodbye, I'm" . $this->name;
     }
}
```

当 `$ironman = new Marvel('钢铁侠')` 并 `unset($ironman)` 时，__destruct() 里的 `echo` 会执行。

## 三、 __call()，在对象中调用一个不可访问方法时调用

该方法有两个参数，第一个参数 $function_name 会自动接收不存在的方法名，第二个 $arguments 则以数组的形式接收不存在方法的多个参数。

1. __call() 方法的格式

```php
function __call(string $function_name, array $arguments) {
    // 方法体
}
```

2. __call() 方法的作用

为了避免当调用的方法不存在时产生错误导致程序意外中止，可以使用 __call() 来避免。

该方法在调用的方法不存在时会自动调用，程序仍会继续执行下去。

3. 范例

```php
<?php
class Marvel
{
    /**
     * getName 方法
     */
    public function getName(){
        echo "I'm ironman";
    }

    /**
     * 声明此方法用来处理调用对象中不存在的方法
     */  
     public function __call($func_name, $arguments) {
        echo "你所调用的函数：" . $func_name . "(参数：" ;  // 输出调用不存在的方法名
        print_r($arguments); // 输出调用不存在的方法时的参数列表
        echo ")不存在！<br>\n"; // 结束换行
     }
}
```

```php
$hero = new Marvel();
$hero->setName("Ironman");  // 调用对象中不存在的方法，则自动调用了对象中的 __call() 方法
$hero->setGender(1);
$hero->getName();
```

执行结果：

```txt
你所调用的函数：setName(参数：Array ( [0] => Ironman ) )不存在！

你所调用的函数：setGender(参数：Array ( [0] => 1 ) )不存在！

I'm ironman
```

## 四、 __callStatic()，用静态方式中调用一个不可访问方法时调用

除了注意是用**静态方式**调用之外，其他的均与上述 __call() 函数一致，不再赘述。

## 五、__get(), 获得一个类的成员变量时调用

在 php 面向对象编程中，类的成员属性被设定为 private 后，如果我们试图访问该属性，则会抛出"不能访问私有属性"的错误。为了解决这个问题，我们可以用 __get() 魔术方法。

1. __get() 方法的作用

在程序运行的过程中，通过它可以在类的外部获取私有属性的值。

2. 范例

```php
<?php
class Marvel
{
    private $name;
    private $gender;
    
    public function __construct($name = "", $gender = 1){
        $this->name = $name;
        $this->gender = $gender;
    }

    /**
     * 在类中添加__get()方法，在直接获取属性值时自动调用一次，以属性名作为参数传入并处理
     * @param $property_name
     *
     * @return int
     */
    public function __get($property_name) {
        if ($property_name != "name" || $property_name != "gender"){
            return null;
        }else{
            return $this->property_name;
        }
    }
}
```

```php
$hero = new Marvel("IronMan", 1);
echo $hero->hero_name;  // null
echo $hero->name;   // IronMan
```

## 六、__set()，设置一个成员变量时调用
1. __set() 方法的作用

__set(property, value) 方法用来设置私有属性，给一个未定义的属性赋值时，此方法会被触发，传递的参数是被设置的属性名和值。

2. 范例

```php
class Marvel
{
    private $name;
    private $gender;
    
    public function __construct($name = "", $gender = 1){
        $this->name = $name;
        $this->gender = $gender;
    }

    /**
     * 声明 __set() 方法需要两个参数，直接为私有属性赋值时自动调用，并可以屏蔽一些非法赋值
     * @param $property
     * @param $value
     *
     * @return int
     */
    public function __set($property, $value) {
        if ($property == "gender") {
            if (!in_array($value, [1, 2, 3])) {
                return;
            }
        }
        
        $this->gender = $value;
    }

    public function show() {
        echo "My name is " . $this->name . ", My gender is " . $this->gender;
    }
}

$hero = new Marvel();
$hero->name = "Ironman";    //赋值成功。如果没有__set()，则出错。
$hero->gender = 1;  // 赋值成功
$hero->gender = 7;  // 赋值失败
$hero->show();  // My name is Ironman, My gender is 1
```

[思否](https://segmentfault.com/a/1190000007250604)