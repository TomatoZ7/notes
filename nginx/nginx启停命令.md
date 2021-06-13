# Nginx 启停命令

对于 Nginx 的启停在 linux 系统中也有很多种方式，我们本次介绍两种方式：

1. Nginx 服务的信号控制

2. Nginx 的命令行控制

## 一、Nginx服务的信号控制

Nginx默认采用的是多进程的方式来工作的，当将Nginx启动后，我们通过 `ps -ef | grep nginx` 命令可以查看到如下内容：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_ss1.png)

从上图中可以看到,Nginx 后台进程中包含一个 master 进程和多个 worker 进程，master 进程主要用来管理 worker 进程，包含接收外界的信息，并将接收到的信号发送给各个 worker 进程，监控 worker 进程的状态，当 worker 进程出现异常退出后，会自动重新启动新的 worker 进程。而 worker 进程则是专门用来处理用户请求的，各个 worker 进程之间是平等的并且相互独立，处理请求的机会也是一样的。Nginx 的进程模型，我们可以通过下图来说明下：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_ss2.png)

作为管理员，只需要通过给 master 进程发送信号就可以来控制 Nginx,这个时候我们需要有两个前提条件，一个是要操作的 master 进程，一个是信号。

（1）要想操作 Nginx 的 master 进程，就需要获取到 master 进程的进程号 ID。获取方式简单介绍两个：

1. 通过 `ps -ef | grep nginx`；

2. 在 Nginx 目录下 使用 `more` 或者 `cat` 查看 `./logs/nginx.pid` 即可。

（2）信号

| 信号     | 作用                                                   |
| -------- | ----------------------------------------------------- |
| TERM/INT | 立即关闭整个服务                                        |
| QUIT     | "优雅"地关闭整个服务                                    |
| HUP      | 重读配置文件并使用服务对新配置项生效                      |
| USR1     | 重新打开日志文件，可以用来进行日志切割                    |
| USR2     | 平滑升级到最新版的nginx                                 |
| WINCH    | 所有子进程不在接收处理新连接，相当于给work进程发送QUIT指令 |

调用命令为 `kill -signal PID`

> signal：即为信号；PID 即为获取到的 master 线程 ID。

1. 发送 TERM/INT 信号给 master 进程，会将 Nginx 服务立即关闭。

```sh
kill -TERM PID / kill -TERM `cat /usr/local/nginx/logs/nginx.pid`
kill -INT PID / kill -INT `cat /usr/local/nginx/logs/nginx.pid`
```

2. 发送 QUIT 信号给 master 进程，master 进程会控制所有的 work 进程不再接收新的请求，等所有请求处理完后，在把进程都关闭掉。

```sh
kill -QUIT PID / kill -TERM `cat /usr/local/nginx/logs/nginx.pid`
```

3. 发送 HUP 信号给 master 进程，master 进程会把控制旧的 work 进程不再接收新的请求，等处理完请求后将旧的 work 进程关闭掉，然后根据 nginx 的配置文件重新启动新的 work 进程。

```sh
kill -HUP PID / kill -TERM `cat /usr/local/nginx/logs/nginx.pid`
```

4. 发送 USR1 信号给 master 进程，告诉 Nginx 重新开启日志文件。

```sh
kill -USR1 PID / kill -TERM `cat /usr/local/nginx/logs/nginx.pid`
```

5. 发送 USR2 信号给 master 进程，告诉 master 进程要平滑升级，这个时候，会重新开启对应的 master 进程和 work 进程，整个系统中将会有两个 master 进程，并且新的 master 进程的 PID 会被记录在 `/usr/local/nginx/logs/nginx.pid`，而之前的旧的 master 进程 PID 会被记录在 `/usr/local/nginx/logs/nginx.pid.oldbin` 文件中，接着再次发送 QUIT 信号给旧的 master 进程，让其处理完请求后再进行关闭。

```sh
kill -USR2 PID / kill -USR2 `cat /usr/local/nginx/logs/nginx.pid`
```

```sh
kill -QUIT PID / kill -QUIT `cat /usr/local/nginx/logs/nginx.pid.oldbin`
```

6. 发送 WINCH 信号给 master 进程,让 master 进程控制不让所有的 work 进程在接收新的请求了，请求处理完后关闭 work 进程。注意 master 进程不会被关闭掉。

```sh
kill -WINCH PID /kill -WINCH`cat /usr/local/nginx/logs/nginx.pid`
```

## 二、Nginx 的命令行控制

此方式是通过 Nginx 安装目录下的 sbin 下的可执行文件 nginx 来进行 Nginx 状态的控制，我们可以通过 `nginx -h` 来查看都有哪些参数可以用：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_ss3.png)

-?和-h:显示帮助信息

-v:打印版本号信息并退出

-V:打印版本号信息和配置信息并退出

-t:测试 nginx 的配置文件语法是否正确并退出

-T:测试 nginx 的配置文件语法是否正确并列出用到的配置文件信息然后退出

-q:在配置测试期间禁止显示非错误消息

-s:signal 信号，后面可以跟 ：

    stop[快速关闭，类似于 TERM/INT 信号的作用]

    quit[优雅的关闭，类似于 QUIT 信号的作用] 

    reopen[重新打开日志文件类似于 USR1 信号的作用] 

    reload[类似于 HUP 信号的作用]

-p:prefix，指定 Nginx 的 prefix 路径(默认为: /usr/local/nginx/)

-c:filename,指定 Nginx 的配置文件路径(默认为: conf/nginx.conf)

-g:用来补充 Nginx 配置文件，向 Nginx 服务指定启动时应用全局的配置