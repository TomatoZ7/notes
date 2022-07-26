# linux 部署 laravel

## 步骤

1、`git pull laravel项目`

2、`composer install`

3、新增 nginx 虚拟主机，`root` 指向 `public` 目录。

## 遇到的问题

1、 `composer update` 时报错 `The Process class relies on proc_open, which is not available on your PHP installation.`

打开 php.ini 后，搜索 `disable_functions` 项，看是否禁用了 `proc_open` 函数，如果禁用，移除 `proc_open` 然后退出。

2、 出现 `file_exists(): open_basedir restriction in effect.`

把 `public/.user.ini` 后的 `open_basedir` 后的 public 删除。

3、 出现 `500 | SERVER ERROR`

查看 `.env` 文件是否存在，如果不存在则复制一份。

4、 出现 `No application encryption key has been specified.`

在 laravel 下执行命令 `php artisan key:generate`

5、 `composer update` 时报错 `Your requirements could not be resolved to an installable set of packages.`

原因 : 不匹配composer.json要求的版本。

解决方案：

composer 可以设置忽略版本匹配，

```bash
composer install --ignore-platform-reqs
composer update --ignore-platform-reqs
```

6、访问其他路径404

在 nginx 配置文件加上

```conf
location / {
    try_files $uri $uri/ /index.php?$query_string;
}
```

所有的请求将会引导至 index.php 前端控制器。