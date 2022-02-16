# Laravel-modules

## 1.简介
`nwidart/laravel-modules` 是一个 `Laravel` 包，通过模块化能够管理大型 `Laravel` 应用。 一个模块就像一个 `Laravel` 框架，包含控制器、模型、视图等。 该扩展包在 `Laravel 5` 中得到支持和测试。


## 2.版本要求

+ >= PHP7.1

## 3.安装&配置

### 3.1 Composer Download

请先确保安装了 `composer`。执行命令：

```shell
composer require nwidart/laravel-modules
```

### 3.2 添加服务提供者（非必须）

在 `config/app.php` 中添加以下服务提供者：

```php
'providers' => [
    Nwidart\Modules\LaravelModulesServiceProvider::class,
]
```

添加别名：

```php
'aliases' => [
    'Module' => Nwidart\Modules\Facades\Module::class,
]
```

以上两步是进行服务注册，必须要进行添加。不过在 `laravel5.5` 版本之后可以不添加也能运行，这得益于 `laravel5.5` 提供的 [包自动发现机制](https://learnku.com/articles/4901/laravel-55-supports-packet-discovery-automatically)。

### 3.3 发布软件包配置

执行命令：

```shell
php artisan vendor:publish --provider="Nwidart\Modules\LaravelModulesServiceProvider"
```

执行完后，在 `config` 文件夹下会生成一个 `modules.php` 文件，这个是模块开发的配置文件，你可以在这里面进行配置。

### 3.4 添加自动加载

默认情况下，模块类不会自动加载。 您可以使用 `psr-4` 自动加载模块。

`composer.json`：

```json
{   
    "autoload": {     
        "psr-4": {       
            "App\\": "app/",
            "Modules\\": "Modules/"
        }
    }
}
```

### 3.5 配置文件

配置文件 `config/modules.php`：

```php
return [
    /**
     * 生成模块时默认的命名空间
     */
    'namespace' => 'Modules',
    
    /**
     * 生成模块时的一些配置
     */
    'stubs' => [···],
    
    /**
     * 配置模块的路径、模块内文件结构和路径
     */
    'paths' => [···],
    
    /**
     * 默认是不开启的，一旦开启，将会在 paths 路径数组里寻找模块
     */
    'scan' => [
        'enabled' => false,
        'paths' => [
            base_path('vendor/*/*'),
        ],
    ],
    
    /**
     * 模块下 composer.json 的配置文件
     */
    'composer' => [···],
    
    /**
     * 如果你有很多模块，最好缓存这些信息
     */
    'cache' => [···],
    
    /**
     * 决定包需要注册哪些自定义命名空间。 
     * 如果设置为 false，则包将不会处理其注册。     
     */
    'register' => [···]
];
```

## 4.基本操作

### 4.1 创建模块

执行如下命令创建模块：

```shell
php artisan module:make <module-name>
```

`module-name` 是开发者定义的模块名，如：

```shell
php artisan module:make Blog
```

也支持同一命令创建多个模块：

```shell
php artisan module:make Blog User Auth
```

默认情况下，该命令会自动添加诸如控制器、`seed` 类、服务提供者等。如果开发者不需要这些，可以通过添加 `--plain` 标识来创建一个普通的模块：

```shell
php artisan module:make Blog --plain
# 或者
php artisan module:make Blog -p
```

### 4.2 文件结构

```
app/
bootstrap/
vendor/
Modules/
    ├── RecordCenter/       
        ├── Config/       
        ├── Console/       
        ├── Database/   
            ├── factories/        
            ├── Migrations/           
            ├── Seeders/       
        ├── Entities/       
        ├── Http/           
            ├── Controllers/           
            ├── Middleware/           
            ├── Requests/           
            ├── routes.php     
        ├── Providers/           
            ├── RecordCenterServiceProvider.php       
        ├── Resources/ 
            ├── assets/          
            ├── lang/           
            ├── views/    
        ├── Tests/       
        ├── composer.json       
        ├── module.json       
        ├── start.php
```

## 5.问题记录

由于 `laravel-modules` 运行时依赖于外部 `Laravel` 项目，所以外部的配置读取、`ORM` 映射等跟在外部 `Laravel` 上开发无差异，这里更多的关注于 `laravel-modules` 内部。

### 5.1 路由

路由文件默认位于模块下 `Http/routes.php`。

注意 `Laravel` 的路由文件 `api.php` 默认会拼接上 `api` 前缀，在这里并不会。

### 5.2 config 文件

开发者在 `Config` 目录下新建配置文件，则需要在 `Providers/BlogServiceProvider.php` 中注册该文件：

```php
/**
 * Boot the application events.
 *
 * @return void
 */
public function boot()
{
    $this->registerConfig();
    ...
}

/**
 * Register config.
 *
 * @return void
 */
protected function registerConfig()
{
    $this->publishes([
        __DIR__.'/../Config/config.php' => config_path('blog.php'),
    ], 'config');
    $this->mergeConfigFrom(
        __DIR__.'/../Config/config.php', 'blog'
    );
    // 注册 author
    $this->mergeConfigFrom(
        __DIR__.'/../Config/author.php', 'author'
    );
}
```

在代码中读取 `author.php` 中的配置：

```php
config('author.api_domain');
```

### 5.3 Command

`Command` 类一般放在 `Console` 中，它同样需要注册。

以录制中心为例，在 `Providers/RecordCenterServiceProvider.php` 中：

```php
/**
 * Boot the application events.
 *
 * @return void
 */
public function boot()
{
    $this->registerCommands();
    ...
}

/**
 * 命令行进程注册
 *
 * @return void
 */
public function registerCommands()
{
    $this->commands([
        \Modules\Blog\Console\BlogFilterCommand::class,
        ......
    ]);
}
```

## 7.参考

[Document](https://nwidart.com/laravel-modules/v6/introduction)
[nWidart/laravel-modules - Github](https://github.com/nWidart/laravel-modules)
[Laravel 模块化开发 - LearnKu](https://learnku.com/articles/6153/laravel-modular-development)
