# (转) Laravel Container(容器) 深入理解 (下)

在 [Laravel Container (容器) 概念详解 (上)](https://github.com/TomatoZ7/notes-of-tz/blob/master/php/laravel/laravel_container_first.md) 中介绍了 Dependency Injection Container(容器) 的基本概念，本文将深入探讨一下 Laravel 的 Container 。

Laravel 中实现的 Inversion of Control（IoC）/ Dependency Injection（DI） Container 非常强大，而文档中却没有详细叙述。

> 本文示例基于 laravel5.5，其他版本类似。

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

## Container 的技能们
### 基本用法，用 type hint(类型提示) 注入依赖：
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

