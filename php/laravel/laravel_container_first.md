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