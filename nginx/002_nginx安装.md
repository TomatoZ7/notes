# nginx 安装的三种方式

以 1.16.1 为例。

## 一、简单安装

需提前准备好环境：

```sh
yum install -y gcc pcre pcre-devel zlib zlib-devel openssl openssl-devel
```

### 1、进入官网查找需要下载版本的链接地址，然后使用 wget 命令进行下载

```sh
wget http://nginx.org/download/nginx-1.16.1.tar.gz
```

### 2、建议大家将下载的资源进行包管理

```sh
mkdir -p nginx/core
mv nginx-1.16.1.tar.gz nginx/core
```

### 3、解压缩

```sh
tar -xzf nginx-1.16.1.tar.gz
```

### 4、进入资源文件中，发现 configure

```sh
./configure
```

configure 文件是 Nginx 的自动脚本程序。运行 configure 自动脚本一般会完成两项工作：

1. 检查环境，根据环境检查结果生成 C 代码；

2. 生成编译代码需要的 Makefile 文件。

### 5、编译

```sh
make
```

### 6、安装

```sh
make install
```

## 二、通过 yum 安装

### 1、安装 yum-utils

```sh
sudo yum install -y yum-utils
```

### 2、添加 yum 源文件

通过 `vim /etc/yum.repos.d/nginx.repo` 创建新文件并把一下代码段复制进去保存：

```
[nginx-stable]
name=nginx stable repo
baseurl=http://nginx.org/packages/centos/$releasever/$basearch/
gpgcheck=l
enabled=l
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true

[nginx-mainline]
name=nginx mainline repo
baseurl=http://nginx.org/packages/mainline/centos/$releasever/$basearch/
gpgcheck=l
enabled=0
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true
```

默认下载稳定版，如需主线版则执行命令切换：

```sh
sudo yum-config-manager --enable nginx-mainline
```

### 3、安装 Nginx

```
sudo yum install nginx
```

## 三、复杂安装

这种方式和简单的安装配置不同的地方在第一步，通过 `/confiqure` 来对编译参数进行设置，需要我们手动来指定。那么都有哪些参数可以进行设置，接下来我们进行一个详细的说明。

PATH : 是和路径相关的配置信息

with : 是启动模块，默认是关闭的

without : 是关闭模块，默认是开启的

我们先来认识一些简单的路径配置已经通过这些配置来完成一个简单的编译。

```
--prefix=PATH : 指向Nginx的安装目录，默认值为 `/usr/local/nginx`

--sbin-path=PATH : 指向(执行)程序文件(nginx)的路径，默认值为 `<prefix>/sbin/nginx`

--modules-path=PATH : 指向Nginx动态模块安装目录，默认值为 `<prefix>/modules`

--conf-path=PATH : 指向配置文件(nginx.conf)的路径,默认值为 `<prefix>/conf/nginx.conf`

--error-log-path=PATH : 指向错误日志文件的路径，默认值为 `<prefix>/logs/error.log`

--http-log-path=PATH : 指向访问日志文件的路径，默认值为 `<prefix>/logs/access.log`

--pid-path=PATH : 指向Nginx启动后进行ID的文件路径，默认值为 `<prefix>/logs/nginx.pid`

--lock-path=PATH : 指向Nginx锁文件的存放路径,默认值为 `<prefix>/logs/nginx.lock`
```

要想使用可以通过如下命令：

```sh
./configure--prefix=/usr/local/nginx \
--sbin-path=/usr/local/nginx/sbin/nginx \
--modules-path=/usr/local/nginx/modules \
--conf-path=/usr/local/nginx/conf/nginx.conf \
--error-log-path=/usr/local/nginx/logs/error.log \
--http-log-path=/usr/local/nginx/logs/access.log \
--pid-path=/usr/local/nginx/logs/nginx.pid \
--lock-path=/usr/local/nginx/logs/nginx.lock
```

在使用上述命令之前，需要将之前服务器已经安装的nginx进行卸载，卸载的步骤分为三步骤:

1. 需要将nginx的进程关闭

```sh
./nginx-s stop
```

2. 将安装的nginx进行删除

```sh
rm -rf/usr/local/nginx
```

3. 将安装包之前编译的环境清除掉

```sh
make clean
```
