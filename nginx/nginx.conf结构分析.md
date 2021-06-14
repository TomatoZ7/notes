# nginx.conf 结构分析

nginx.conf 配置文件中默认有三大块：全局块、events 块、http 块。

http 块中可以配置多个 server 块，每个 server 块又可以配置多个 location 块。

## 全局块

### user 指令

#### user : 用于配置运行 Nginx 服务器的 worker 进程的用户和用户组。

| 语法   | user user [group] |
| ------ | ----------------- |
| 默认值 | nobody            |
| 位置   | 全局块            |

#### 1、创建一个用户

```sh
useradd www
```

#### 2、修改 nginx.conf 的 user 属性

```conf
user www
```

#### 3、创建 `/home/www/html/index.html` 页面，内容如下：

> 如果直接放到 root 目录下，会报 403。

```html
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
<p><em>I am WWW</em></p>
</body>
</html>
```

#### 4、修改 nginx.conf 

```conf
location / {
	root   /home/www/html;
	index  index.html index.htm;
}
```

#### 5、启动访问

综上所述，使用 user 指令可以指定启动运行工作进程的用户及用户组，这样对于系统的权限访问控制的更加精细，也更加安全。

### work process 指令

#### master_process : 用来指定是否开启工作进程

| 语法   | master_process on\|off; |
| ------ | ----------------------- |
| 默认值 | master_process on;      |
| 位置   | 全局块                  |

#### worker_process : 用于配置 Nginx 生成工作进程的数量

这个是Nginx服务器实现并发处理服务的关键所在。

理论上来说 workder process 的值越大，可以支持的并发处理量也越多，但事实上这个值的设定是需要受到来自服务器自身的限制，建议将该值和**服务器 CPU 的内核数**保存一致。

| 语法   | worker_processes     num/auto; |
| ------ | ------------------------------ |
| 默认值 | 1                              |
| 位置   | 全局块                         |

如果将 worker_processes 设置成 2，则会看到如下内容:

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_conf1.png)

### 其他指令

#### daemon : 设定 Nginx 是否以守护进程的方式启动。

> 守护式进程是 linux 后台执行的一种服务进程，特点是独立于控制终端，不会随着终端关闭而停止。

| 语法   | daemon on\|off; |
| ------ | --------------- |
| 默认值 | daemon on;      |
| 位置   | 全局块          |

#### pid : 用来配置 Nginx 当前 master 进程的进程号 ID 存储的文件路径。

| 语法   | pid file;                       |
| ------ | ------------------------------ |
| 默认值 | /usr/local/nginx/logs/nginx.pid |
| 位置   | 全局块                          |

该属性可以通过 `./configure --pid-path=PATH` 来指定。

#### error_log : 用来配置 Nginx 的错误日志存放路径

| 语法   | error_log  file [日志级别];     |
| ------ | ------------------------------- |
| 默认值 | error_log logs/error.log error; |
| 位置   | 全局块、http、server、location  |

该属性可以通过 `./configure --error-log-path=PATH` 来指定。

其中日志级别的值有：debug|info|notice|warn|error|crit|alert|emerg

翻译过来为：测试|信息|通知|警告|错误|临界|警报|紧急

建议设置的时候不要设置成 info 以下的等级，因为会带来大量的磁盘 I/O 消耗，影响 Nginx 的性能。


## events 块

#### accept_mutex : 用来设置 Nginx 网络连接序列化

| 语法   | accept_mutex on\|off; |
| ------ | --------------------- |
| 默认值 | accept_mutex on;      |
| 位置   | events                |

这个配置主要可以用来解决常说的"惊群"问题。

> "惊群" 大致意思是在某一个时刻，客户端发来一个请求连接，Nginx 后台是以多进程的工作模式，也就是说有多个 worker 进程会被同时唤醒，但是最终只会有一个进程可以获取到连接，如果每次唤醒的进程数目太多，就会影响 Nginx 的整体性能。

如果将上述值设置为 on(开启状态)，将会对多个 Nginx 进程接收连接进行序列号，一个个来唤醒接收，就防止了多个进程对连接的争抢。

#### multi_accept : 用来设置是否允许同时接收多个网络连接

| 语法   | multi_accept on\|off; |
| ------ | --------------------- |
| 默认值 | multi_accept off;     |
| 位置   | events                |

如果 multi_accept 被禁止了，nginx 一个工作进程只能同时接受一个新的连接。否则，一个工作进程可以同时接受所有的新连接(实际开发中建议打开)。

#### worker_connections : 用来配置单个 worker 进程最大的连接数

| 语法   | worker_connections number; |
| ------ | -------------------------- |
| 默认值 | worker_commections 512;    |
| 位置   | events                     |

这里的连接数不仅仅包括和前端用户建立的连接数，而是包括所有可能的连接数。另外，number值不能大于操作系统支持打开的最大文件句柄数量。

#### use : 用来设置 Nginx 服务器选择哪种事件驱动来处理网络消息

| 语法   | use  method;   |
| ------ | -------------- |
| 默认值 | 根据操作系统定  |
| 位置   | events         |

注意：此处所选择事件处理模型是 Nginx 优化部分的一个重要内容，method的可选值有 select/poll/epoll/kqueue 等，之前在准备 centos 环境的时候，我们强调过要使用 linux 内核在 2.6 以上，就是为了能使用 epoll 函数来优化 Nginx。

另外这些值的选择，我们也可以在编译的时候使用

`--with-select_module`、`--without-select_module`、

`--with-poll_module`、`--without-poll_module` 来设置是否需要将对应的事件驱动模块编译到 Nginx 的内核。


## http 块

### 定义 MIME-Type

我们都知道浏览器中可以显示的内容有 HTML、XML、GIF 等种类繁多的文件、媒体等资源，浏览器为了区分这些资源，就需要使用 MIME Type。所以说 MIME Type 是网络资源的媒体类型。Nginx 作为 web 服务器，也需要能够识别前端请求的资源类型。

#### default_type : 用来配置 Nginx 响应前端请求默认的 MIME 类型。

| 语法   | default_type mime-type;   |
| ------ | ------------------------- |
| 默认值 | default_type text/plain； |
| 位置   | http、server、location    |

在 default_type 之前还有一句 `include mime.types`, include 之前我们已经介绍过，相当于把 mime.types 文件中 MIME 类型与相关类型文件的文件后缀名的对应关系加入到当前的配置文件中。

### 自定义服务日志

Nginx 中日志的类型分 access.log、error.log。

access.log : 用来记录用户所有的访问请求。

error.log : 记录 Nginx 本身运行时的错误信息，不会记录用户的访问请求。

Nginx 服务器支持对服务日志的格式、大小、输出等进行设置，需要使用到两个指令，分别是 `access_log` 和 `log_format` 指令。

1. access_log : 用来设置用户访问日志的相关属性。

| 语法   | access_log path[format[buffer=size]] |
| ------ | ------------------------------------ |
| 默认值 | access_log logs/access.log combined; |
| 位置   | `http`, `server`, `location`         |

2. log_format : 用来指定日志的输出格式。

| 语法   | log_format name [escape=default\|json\|none] string....; |
| ------ | -------------------------------------------------------- |
| 默认值 | log_format combined "...";                               |
| 位置   | http                                                     |

> access_log 后的 combined 应与 log_format 后的 combined 保持一致才能生效。

### 其他配置指令

#### sendfile : 用来设置 Nginx 服务器是否使用 sendfile() 传输文件，该属性可以大大提高 Nginx 处理静态资源的性能。

| 语法   | sendfile on\|off；     |
| ------ | ---------------------- |
| 默认值 | sendfile off;          |
| 位置   | http、server、location |

#### keepalive_timeout : 用来设置长连接的超时时间。

> 为什么要使用 keepalive？
> 
> 我们都知道 HTTP 是一种无状态协议，客户端向服务端发送一个 TCP 请求，服务端响应完毕后断开连接。
> 
> 如果客户端向服务端发送多个请求，每个请求都需要重新创建一次连接，效率相对来说比较低，使用 keepalive 模式，可以告诉服务器端在处理完一个请求后保持这个 TCP 连接的打开状态，若接收到来自这个客户端的其他请求，服务端就会利用这个未被关闭的连接，而不需要重新创建一个新连接，提升效率。
> 
> 但是这个连接也不能一直保持，这样的话，连接如果过多，也会是服务端的性能下降，这个时候就需要我们进行设置其的超时时间。

| 语法   | keepalive_timeout time; |
| ------ | ----------------------- |
| 默认值 | keepalive_timeout 75s;  |
| 位置   | http、server、location  |

#### keepalive_requests : 用来设置一个keep-alive连接使用的次数

| 语法   | keepalive_requests number; |
| ------ | -------------------------- |
| 默认值 | keepalive_requests 100;    |
| 位置   | http、server、location     |

## server 块和 http 块

server 块和 http 块都是重点内容，因为不想篇幅过长，所以会在另一个地方进行解读。