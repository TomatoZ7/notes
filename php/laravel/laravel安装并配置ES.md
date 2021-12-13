# laravel 安装并配置 ES

## composer 安装 ES 扩展包

```composer
composer require elasticsearch/elasticsearch “7.12.x” --ignore-platform-reqs
```

## 配置 ES

config/database.php

```php
'elasticsearch' => [
    'hosts' => explode(',',env('ES_HOSTS')),
]
```

.env

```env
ES_HOSTS=127.0.0.1:9200 #默认9200,端口可不写
```

## 初始化 ES 对象，并注入到 laravel 容器中

在 laravel 容器中自定义一个名为 es 的服务对象，通过 ESClientBuilder 以及配置文件中的信息连接到 es，我们可以通过 `app('es')->info()` 查看连接之后的 es 对象信息。

App/Providers/AppServiceProvider

```php
<?php

namespace App\Providers;

use Elasticsearch\ClientBuilder as ElasticClientBuilder;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     *
     * @return void
     */
    public function register()
    {
        // 注册一个名为 es 的单例
        $this->app->singleton('es',function (){
            // 从配置文件读取 Elasticsearch 服务器列表
            $builder =  ElasticClientBuilder::create()->setHosts(config('database.elasticsearch.hosts'));
            // 如果是开发环境
            if (app()->environment()==='local'){
                // 配置日志，Elasticsearch 的请求和返回数据将打印到日志文件中，方便我们调试
                $builder->setLogger(app('log')->driver());
            }
            return $builder->build();
        });
    }

    /**
     * Bootstrap any application services.
     *
     * @return void
     */
    public function boot()
    {
        //
    }
}

```

## 测试

输出 `app('es')->info()` 即可看到 ES 初始信息。

## 开始

### 创建文档

在 elasticsearch-php 中，几乎所有的东西都是由**关联数组**配置的。 REST 端点、文档和可选参数，都可以配制成一个关联数组。

创建一个文档，我们需要指定三部分信息：索引，id 和文档正文。它们都可以通过构造一个键值对形式的关联数组来完成。请求体本身就是一个包含键值对的关联数组。

```php
$params = [
    'index' => 'my_index',
    'id'    => 'my_id',
    'body'  => ['testField' => 'abc']
];

$response = $client->index($params);
print_r($response);
```

返回创建的文档信息

```php
Array
(
    [_index] => my_index
    [_type] => _doc
    [_id] => my_id
    [_version] => 1
    [_seq_no] => 0
    [_primary_term] => 1
    [found] => 1
    [_source] => Array
        (
            [testField] => abc
        )
)
```

## 获取文档

`getSource()` 方法：

```php
$params = [
    'index' => 'my_index',
    'id'    => 'my_id'
];

$source = $client->getSource($params);
print_r($source);
```

返回文档信息

```php
Array
(
    [testField] => abc
)
```

## 搜索

```php
$params = [
    'index' => 'my_index',
    'body'  => [
        'query' => [
            'match' => [
                'testField' => 'abc'
            ]
        ]
    ]
];

$response = $client->search($params);
print_r($response);
```

`search` 的响应跟之前的不太一样，我们可以看到一些额外的信息如 `took`，`timed_out` 等。还有一个 `hits` 数组代表搜索结果。在 `hits` 数组里还有一个 `hits` 数组，其中包含单个搜索结果。

```php
Array
(
    [took] => 33
    [timed_out] =>
    [_shards] => Array
        (
            [total] => 1
            [successful] => 1
            [skipped] => 0
            [failed] => 0
        )

    [hits] => Array
        (
            [total] => Array
                (
                    [value] => 1
                    [relation] => eq
                )

            [max_score] => 0.2876821
            [hits] => Array
                (
                    [0] => Array
                        (
                            [_index] => my_index
                            [_type] => _doc
                            [_id] => my_id
                            [_score] => 0.2876821
                            [_source] => Array
                                (
                                    [testField] => abc
                                )

                        )

                )

        )

)
```

## 删除文档

```php
$params = [
    'index' => 'my_index',
    'id'    => 'my_id'
];

$response = $client->delete($params);
print_r($response);
```

响应：

```php
Array
(
    [_index] => my_index
    [_type] => _doc
    [_id] => my_id
    [_version] => 2
    [result] => deleted
    [_shards] => Array
        (
            [total] => 1
            [successful] => 1
            [failed] => 0
        )

    [_seq_no] => 1
    [_primary_term] => 1
)
```

## 删除索引

```php
$deleteParams = [
    'index' => 'my_index'
];
$response = $client->indices()->delete($deleteParams);
print_r($response);
```

响应：

```php
Array
(
    [acknowledged] => 1
)
```

## 创建索引

前面一起创建的索引配置是默认的，通过这个方法可以自定义一些配置。

```php
$params = [
    'index' => 'my_index',
    'body'  => [
        'settings' => [
            'number_of_shards' => 2,
            'number_of_replicas' => 0
        ]
    ]
];

$response = $client->indices()->create($params);
print_r($response);
```

响应：

```php
Array
(
    [acknowledged] => 1
    [shards_acknowledged] => true
    [index] => "my_index"
)
```

## 参考

[elasticsearch/elasticsearch - Packagist](https://packagist.org/packages/elasticsearch/elasticsearch#user-content-quickstart)