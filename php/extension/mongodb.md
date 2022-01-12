# php_mongodb 扩展连接

## 1 Windows

### 1.1 php 版本

运行 `phpinfo()` 可得到相关的一些信息，对于后面选择安装文件有帮助：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/php/images/php_extension_1.jpg)

### 1.2 下载

官网下载地址是：[https://windows.php.net/downloads/pecl/releases/mongodb](https://windows.php.net/downloads/pecl/releases/mongodb)

因为我的 `MongoDB` 版本比较新，所以选较新的扩展版本 1.8.2。

结合上面拿到的 `php` 相关信息，最终选择：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/php/images/php_extension_2.jpg)

### 1.3 配置

下载后解压，将里面的 `php_mongodb.dll` 文件复制到 `php` 的 `ext` 目录下。

由于 `mongodb` 扩展安装需要依赖 `libsasl.dll`，所以要在系统环境变量中加入该目录。该目录一般是 `php` 的根目录。

在 `php.ini` 文件中加入 `extension=php_mongodb.dll`，根据实际情况调整，像我就只需要添加 `extension=mongodb` 即可。

安装完成

## 2 资料

[https://www.php.net/manual/en/mongodb.installation.windows.php](https://www.php.net/manual/en/mongodb.installation.windows.php)

[Windows安装PHP MongoDB扩展](https://www.cnblogs.com/mityaya/p/5454593.html)