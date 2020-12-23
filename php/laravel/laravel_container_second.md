# (转) Laravel Container(容器) 深入理解 (下)

在 [Laravel Container (容器) 概念详解 (上)](https://github.com/TomatoZ7/notes-of-tz/blob/master/php/laravel/laravel_container_first.md) 中介绍了 Dependency Injection Container(容器) 的基本概念，本文将深入探讨一下 Laravel 的 Container 。

Laravel 中实现的 Inversion of Control（IoC）/ Dependency Injection（DI） Container 非常强大，而文档中却没有详细叙述。

> 本文示例基于 laravel5.5，其他版本类似。

&emsp;

## 准备工作
### Dependency Injection 
关于 DI 请看这篇 [Laravel [Dependency injection] 依赖注入](https://github.com/TomatoZ7/notes-of-tz/blob/master/php/laravel/laravel_dependency_injection.md) 。

### 初识 Container
Laravel 中有一大堆访问 Container 实例的姿势，例如最简单的：
```
$container = app();
```

但我们还是关注下 Container 类本身。
> Laravel 官方文档中一般使用 $this->app 代替 $container。它是 Application 类的实例，而 Application 类继承自 Container 类。

### 在 Laravel 之外使用 Illuminate\Container 
```
mkdir container && cd container
composer require illuminate\container 
```
```
// 新建一个 container.php，文件名任取

<?php
include './vendor/autoload.php';
$container = Container::getInstance();
```

&emsp;

## Container 的技能们
### Part1. 基本用法，用 type hint(类型提示) 注入依赖：
只需要在自己类的构造函数中使用 `type hint` 就能实现 DI ：
```
class MyClass
{
    private $dependency;

    public function __construct(AnotherClass $dependency)
    {
        $this->dependency = $dependency;
    }
}
```

接下来用 Container 的 `make` 方法来取代 `new MyClass()` :
```
$instance = $container->make(MyClass::class);
```

Container 会自动实例化依赖的对象，所以它等同于：
```
$instance = new MyClass(new AnotherClass());
```

如果 AnotherClass 也有依赖，那么 Container 会递归注入它所需的依赖。
> Container 使用 [反射](https://www.php.net/manual/zh/book.reflection.php) 来找到并实例化构造函数参数中的那些类，实现起来并不复杂，以后有机会再介绍。

### 实战
下面是 [PHP-DI 文档](https://php-di.org/doc/getting-started.html) 中的一个例子，它分离了 [用户注册] 和 [发邮件] 的过程：
```
class Mailer
{
    public function mail($recipient, $content)
    {
        // Send an email to the recipient
        // ...
    }
}
```
```
class UserManager
{
    private $mailer;

    public function __construct(Mailer $mailer)
    {
        $this->mailer = $mailer;
    }

    public function register($email, $password)
    {
        // 创建新账户
        // ...

        // 给用户邮箱发送 "hello" 邮件
        $this->mailer->mail($email, "Hello And Welcome!");
    }
}
```
```
use Illuminate\Container\Container;

$container = Container::getInstance();

$userManager = $container->make(UserManager::class);
$userManager->register('dave@davejamesmiller.com', 'MySuperSecurePassword!');
```

### Part2. Binding interface to Implementations(绑定接口到实现)
用 Container 可以轻松地写一个接口，然后在运行时实例化一个具体的实例，首先定义接口：
```
interface MyInterface{}
interface AnotherInterface{}
```

然后声明实现这些接口的具体类。下面这个类不但实现了一个接口，还依赖了实现另一个接口的类实例：
```
class MyClass implements MyInterface
{
    private $dependency;

    // 依赖了一个实现 AnotherInterface 接口的类的实例
    public function __construct(AnotherInterface $dependency)
    {
        $this->dependency = $dependency;
    }
}
```

现在用 Container 的 `bind()` 方法来让每个 接口 和实现它的类一一对应起来：
```
$container->bind(MyInterface::class, MyClass::class);
$container->bind(AnotherInterface::class, AnotherClass::class);
```

最后，用 **接口名** 而不是 **类名** 来传给 `make()` ：
```
$instance = $container->make(MyInterface::class);
```

> 如果你忘记绑定它们，会导致一个 fetal error: "Uncaught ReflectionException: Class MyInterface does not exist"。

### 实战
下面是可封装的 Cache 层：
```
interface Cache
{
    public function get($key);
    public function put($key, $value);
}
```
```
class Worker
{
    private $cache;

    public function __construct(Cache $cache)
    {
        $this->cache = $cache;
    }

    public function result()
    {
        // 去缓存里查询
        $result = $this->cache->get('worker);

        if ($result == null){
            // 如果缓存里没有，就去别的地方查询，然后放入缓存中
            $result = do_something_slow();

            $this->cache->put('worker', $result);
        }

        return $result;
    }
}
```
```
use Illuminate\Container\Container;

$container = Container::getInstance();
$container->bind(Cache::class, RedisCache::class);

$result = $container->make(Worker::class)->result();
```
这里用 Redis 做缓存，如果改用其他缓存，只要把 RedisCache 换成别的就行了。