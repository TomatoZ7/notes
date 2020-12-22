# (转) Laravel Container(容器) 概念详解 (上)
## 首先记住这句话
> 大多数时候，Dependency Injection 并不需要 Container。

只有当你需要管理一大堆具有很多依赖关系的不同对象时，**Container**才会非常有用 (例如框架中)。

[Laravel Dependency Injection (依赖注入) 概念详解](https://github.com/TomatoZ7/notes-of-tz/blob/master/php/laravel/laravel_dependency_injection.md) 中提到：
创建 User 对象需要先创建 SessionStorage 对象。这里有个瑕疵，创建对象时需要提前知道它所有的依赖项：
```
$storage = new SessionStorage('SESSION_ID');
$user = new User($storage);
```

以 Zend_Framework 中 Zend_Mail 库发送邮件为例：
```
$transport = new Zend_Mail_Transport_Smtp('smtp.gmail.com', [
    'auth'  =>  'login',
    'username'  =>  'foo',
    'password'  =>  'bar',
    'ssl'   =>  'ssl',
    'port'  =>  465
]);

$mailer = new Zend_Mail();
$mailer->setDefaultTransport($transport);
```
> 请把这个例子看做一个大系统中的一小部分，因为这种简单的例子当然没必要用 Container 。

&emsp;

Dependency Injection Container 是一个 "知道如何实例化和配置对象" 的对象 (工厂模式的升华)。为了做到这点，它需要知道构造函数的参数以及对象之间的关系。

下面是一个写死的 Zend_Mail 的 Container :
```
class Container
{
    public function getMailerTransport()
    {
        return new Zend_Mail_Transport_Smtp('smtp.gmail.com', [
            'auth'  =>  'login',
            'username'  =>  'foo',
            'password'  =>  'bar',
            'ssl'   =>  'ssl',
            'port'  =>  465
        ]);
    }

    public function getMailer()
    {
        $mailer = new Zend_Mail();
        $mailer->setDefaultTransport($this->getMailTransport());

        return $mailer;
    }
}
```
这个 container 用起来就相当简单了：
```
$container = new Container();
$mailer = $container->getMailer();
```
我们只管向 Container 要 mailer 对象就行，完全不用管 mailer 怎么创建。
创建 mailer 的"杂活"是嵌入在 Container 中的。

Container 通过 getMailTransport() 方法，把 Zend_Mail_Transport_Smtp 这个依赖自动注入到了 Zend_Mail 中。

细心的网友可能发现，这里的 Container 把什么都写死了，我们可以改善一下。
```
class Container
{
    protected $parameters = [];

    public function __construct(array $parameters = [])
    {
        $this->parameters = $parameters;
    }

    public function getMailerTransport()
    {
        return new Zend_Mail_Transport_Smtp('smtp.gmail.com', [
            'auth'  =>  'login',
            'username'  =>  $this->parameters['mailer.username'],
            'password'  =>  $this->parameters['mailer.password'],
            'ssl'   =>  'ssl',
            'port'  =>  465
        ]);
    }

    public function getMailer()
    {
        $mailer = new Zend_Mail();
        $mailer->setDefaultTransport($this->getMailerTransport());

        return $mailer;
    }
}
```

现在就可以随时更改 username 和 password 了：
```
$container = new Container([
    'mailer.username' => 'foo',
    'mailer.password' => 'bar'
]);
$mailer = $container->getMailer();
```

如果需要更改 mailer 类，把类名也当参数传入就行：
```
class Container
{
    // ...

    public function getMailer()
    {
        $class = $this->parameters['mailer.class'];

        $mailer = new Class();
        $mailer->setDefaultTransport($this->getMailTransport());

        return $mailer;
    }
}

$container = new Container([
    'mailer.username' => 'foo',
    'mailer.password' => 'bar',
    'mailer.class'  =>  'Zend_Mail'
]);
$mailer = $container->getMailer();
```

如果想每次获取同一个 mailer 实例，可以用 单例模式 ：
```
class Container()
{
    static protected $shared = [];

    // ... 

    public function getMailer()
    {
        if (isset(self::shared['mailer']))
        {
            return self::shared['mailer'];
        }

        $class = $this->parameters['mailer.class'];

        $mailer = new $class();
        $mailer->setDefaultTransport($this->getDefaultTransport());

        return self::$shared['mailer'] = $mailer;
    }
}
```

这就包含了 Dependency Injection Containers 的基本功能：
+ Container 管理对象实例化到配置的过程
+ 对象本身不知道自己是由 Container 管理的，对 Container 一无所知。

&emsp;

这就是为什么 Container 能够管理任何 PHP 对象。对象使用 DI 来管理依赖关系非常好，但不是必须的。  
Container 很容易实现，但手工维护乱七八糟的对象还是很麻烦，下一篇将介绍 Laravel 中 Container 的实现方式。


[传送门 : Laravel Container (容器) 概念详解 (上)](https://learnku.com/articles/6139/laravel-container-container-concept-detailed-last)
