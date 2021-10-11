# laravel 连接 MongoDB

## 1 开启 php 扩展

[https://github.com/TomatoZ7/notes-of-tz/blob/master/php/php/extension/mongodb.md](https://github.com/TomatoZ7/notes-of-tz/blob/master/php/php/extension/mongodb.md)

## 2 下载 jenssegers/laravel-mongodb 包

```shell
> composer require jenssegers/laravel-mongodb="3.8.x"
```

我当前的 `laravel` 版本：`"laravel/framework": "^8.40"`。

更多版本对应关系可参考：[https://github.com/jenssegers/laravel-mongodb#belongstomany-and-pivots](https://github.com/jenssegers/laravel-mongodb#belongstomany-and-pivots)

## 3 配置

如果没有自动加载包，需要添加 `provider`：

```php
Jenssegers\Mongodb\MongodbServiceProvider::class,
```

在 `config/database.php` 里的 `connections` 添加先关配置：

```shell
'mongodb' => [
    'driver' => 'mongodb',
    'host' => env('DB_HOST', '127.0.0.1'),
    'port' => env('DB_PORT', 27017),
    'database' => env('DB_DATABASE', 'homestead'),
    'username' => env('DB_USERNAME', 'homestead'),
    'password' => env('DB_PASSWORD', 'secret'),
    'options' => [
        // here you can pass more settings to the Mongo Driver Manager
        // see https://www.php.net/manual/en/mongodb-driver-manager.construct.php under "Uri Options" for a list of complete parameters that you can use

        'database' => env('DB_AUTHENTICATION_DATABASE', 'admin'), // required with Mongo 3+
    ],
],
```

## 4 测试

创建 `app/Models/Mongo/Test.php`:

```php
<?php

namespace App\Models\Mongo;

use Jenssegers\Mongodb\Eloquent\Model;

class Test extends Model
{
    protected $connection = 'mongodb';      // 有些版本可忽略，这里我使用的版本不可忽略

    protected $collection = 'test';
}
```

接着创建测试控制器：

```php
<?php

namespace App\Http\Controllers\Test;

use App\Http\Controllers\Controller;

class TestController extends Controller
{
    public function test()
    {
        // echo "<h1>Hello Laravel</h1>";

        $count = Test::query()->count();

        var_dump($count);
    }
}
```