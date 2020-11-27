# (转) Laravel Dependency injection 依赖注入

HTTP 协议是无状态的，web 应用程序如果需要在请求之间存储用户信息，可以通过 COOKIE 或 SESSION :
``` 
$_SESSION['language'] = 'fr';
```

上述代码中，我们将 language 存储在全局变量，因此可以这样获得它：
```
$user_language = $_SESSION['language'];
```

只有在 OOP 开发时才会遇到**依赖注入**，因此假设我们有一个封装 SESSION 的 SessionStorage 类：
```
class SessionStorage
{
    public function __construct($cookieName="PHPSESSID")
    {
        session_name = $cookieName;
        session_start();
    }

    function set($key, $value)
    {
        $_SESSION[$key] = $value;
    }

    function get($key)
    {
        return $_SESSION[$key];
    }

    // ...
}
```

以及一个更高层的类 User ：
```
class User
{
    protected $storage;

    function __construct()
    {
        $this->storage = new SessionStorage();
    }

    function setLanguage($language)
    {
        $this->storage->set('language', $language);
    }

    function getLanguage()
    {
        return $this->storage->get('language');
    }
}
```

这两个类很简单，用起来也方便：
```
$user = new User();
$user->setLanguage('fr');

$user_language = $user->getLanguage();
```

这种方式看起来很完美，但是并不够灵活。比如：现在要修改会话的 COOKIE 名称(默认为 PHPSESSID )，怎么办? 可能会存在一系列方法：

+ 把会话的名称直接写死在 User 类中的构造函数里。
```
class User
{
    protected $storage;

    function __construct()
    {
        $this->storage = new SessionStorage('SESSION_ID');
    }

    // ...
}
```

+ 在 User 类里定义一个常量：
```
class User
{
    protected $storage;

    function __construct()
    {
        $this->storage = new SessionStorage(SESSION_COOKIE_NAME);
    }

    // ...
}

define('SESSION_COOKIE_NAME', 'SESSION_ID');
```

+ 把会话名称作为参数传入 User 类的构造函数：
```
class User
{
    protected $storage;

    function __construct($cookieName)
    {
        $this->storage = new SessionStorage($cookieName);
    }

    // ...
}

$user = new User('SESSION_ID');
```

无论哪种方法都很糟糕：
+ 把会话的 COOKIE 名称写死在构造函数里的话，下一次修改还要去修改 User 类
+ 使用常量的话，User 类的变化将取决于常量的设置
+ 使用参数看起来很灵活，但是把 User 无关的东西掺杂在了构造函数中

### 通过构造函数，把一个外部的 SessionStorage 实例“注入”到 User 实例内部，而不是在 User 实例内部创建 SessionStorage 实例，这就是“依赖注入”。
```
class User{
    protected $storage;

    function __construct($storage)
    {
        $this->storage = $storage;
    }

    // ...
}
```
只需先创建 SessionStorage 实例，再创建 User 实例：
```
$storage = new SessionStorage('SESSION_ID');
$user = new User($storage);
```
用这个方法，配置 SessionStorage 简单，给 User 替换 $storage 也简单。都不需要去修改 User 类，这就实现了解耦。

### **依赖注入**并不限于构造函数。
+ Constructor Injection
```
class User
{
    function __construct($storage)
    {
        $this->storage = $storage;
    }

    // ...
}
```

+ Setter Injection
```
class User
{
    function setSessionStorage($storage)
    {
        $this->storage = $storage;
    }

    // ...
}
```

+ Property Injection 
```
class User
{
    public $sessionStorage;
}

$user->sessionStorage = $storage;
```

作为经验，**Constructor 注入**最适合必须的依赖关系；**Setter Injection**最适合可选的依赖关系，比如缓存一个对象实例。  
现在，大多数现代的 PHP 框架都大量使用依赖注入来提供一组 **去耦** 但 **粘合** 的组件：




&emsp;

[传送门](https://learnku.com/articles/6117/laravel-dependency-injection-dependency-injection-concept-detailed) 