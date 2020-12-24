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

### Part3. Binding Abstract & Concert Classes (绑定抽象类和具体类)
绑定还可以用在抽象类：
```
$container->bind(MyAbstract::class, MyConcreteClass::class);
```

或者继承的类中：
```
$container->bind(MysqlDatabase::class, CustomMysqlDatabase::class);
```

### Part4. 自定义绑定
如果类中需要一些附加的配置项，可以把 `bind()` 方法中的第二个参数换成 `Closure(闭包函数)`:
```
$container->bind(Database::class, function (Container $container) {
    return new MysqlDatabase(MYSQL_HOST, MYSQL_PORT, MYSQL_USER, MYSQL_PASS);
});
```

闭包也可用于定制 具体类 的实例化方式：
```
$container->bind(GitHub\Client::class, function (Container $container) {
    $client = new GitHub\Client();
    $client->setEnterpriseUrl(GITHUB_HOST);
    return $client;
})
```

### Part5. Resolving Callbacks(回调)
可用 `resolving()` 方法来注册一个 callback(回调函数) ，而不是直接覆盖之前的 **绑定** 。这个函数会在绑定的类解析完成后调用。
```
$container->bind(GitHub\Client::class, function ($client, Container $container) {
    $client->setEnterpriseUrl(GITHUB_HOST);
});
```

如果有一大堆 callbacks ,他们全部都会被调用。对于 **接口** 和 **抽象类** 也可以这么用：
```
$container->resolving(Logger::class, function (Logger $logger) {
    $logger->setLevel('debug');
});

$container->resolving(FileLogger::class, function (FileLogger $logger) {
    $logger->setFilename('logs/debug.log');
});

$container->bind(Logger::class, FileLogger::class);

$logger = $container->make(Logger::class);
```

更厉害的是，还可以注册成 [什么类解析完之后都调用]:
```
$container->resolving(function ($object, Container $container) {
    // ...
})
```
但这个估计只有 logging 和 debugging 才会用到。

### Part6. Extending a Class(扩展一个类)
使用 `extend()` 方法，可以封装一个类然后返回一个不同的对象(装饰模式)：
```
$container->extend(APIClient::class, function ($client, Container $container) {
    return new APIClientDecorator($client);
});
```
注意：这两个类要实现相同的 **接口** ，不然用类型提示的时候会出错：
```
interface Getable
{
    public function get();
}
```
```
class APIClient implements Getable
{
    public function get()
    {
        return 'yes';
    }
}
```
```
class APIClientDecorator implements Getable
{
    private $client;

    public function __construct(APIClient $client)
    {
        $this->client = $client;
    }

    public function get()
    {
        return 'no';
    }
}
```
```
class User
{
    private $client;

    public function __construct(Getable $client)
    {
        $this->client = $client;
    }
}
```
```
$container->extend(APIClient::class, function ($client, Container $container) {
    return new APIClientDecorator($client);
});
//
$container->bind(Getable::class, APIClient::class);

// 此时 $instance 的 $client 属性已经是 APIClientDecorator 类型了
$instance = $container->make(User::class);
```

### Part7. 单例
使用 `bind()` 方法绑定后，每次解析时都会新实例化一个对象(或重新调用闭包)，如果想获取 **单例**，则用 `singleton()` 方法代替 `bind()`:
```
$container->singleton(Cache::class, RedisCache::class);
```

绑定单例 **闭包**
```
$container->singleton(Database::class, function (Container $container) {
    return new MySQLDatabase('localhost', 'testdb', 'user', 'pass');
});
```

绑定 **具体类** 的时候，不需要第二个参数：
```
$container->singleton(MySQLDatabase::class);
```

在每种情况下，**单例** 对象将在第一次需要时创建，然后在后续重复使用。  
如果你已经有一个 **实例** 并且想重复使用，可以用 `instance()` 方法。Laravel 就是用这种方法确保每次获取到的都是同一个 Container 实例：
```
$container->instance(Container::class, $container);
```