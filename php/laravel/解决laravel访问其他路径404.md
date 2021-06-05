# 解决 laravel 访问除 '/' 路径外其他路径 404 问题

## 问题阐述

项目环境：nginx 服务

在 routes/web.php 下新建一个路由后：

```php
<?php

use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Route::get('/test', function () {
    echo "Hello Laravel8";
});

```

直接访问 `xxx.com/test` 直接 404 了。

## 解决方案

在你的站点配置中加入以下配置，所有的请求将会引导至 index.php 前端控制器：

```conf
location / {
    try_files $uri $uri/ /index.php?$query_string;
}
```