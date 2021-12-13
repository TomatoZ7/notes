# Nginx 四层负载均衡

Nginx在 1.9 之后，增加了一个 stream 模块，用来实现四层协议的转发、代理、负载均衡等。stream 模块的用法跟 http 的用法类似，允许我们配置一组 TCP 或者 UDP 等协议的监听，然后通过 `proxy_pass` 来转发我们的请求，通过 `upstream` 添加多个后端服务，实现负载均衡。

四层协议负载均衡的实现，一般都会用到 LVS、HAProxy、F5 等，要么很贵要么配置很麻烦，而 Nginx 的配置相对来说更简单，更能快速完成工作。

## 添加 stream 模块的支持

Nginx 默认是没有编译这个模块的，需要使用到 stream 模块，那么需要在编译的时候加上 `--with-stream`。

1. 将原有 /usr/local/nginx/sbin/nginx 进行备份

2. 拷贝 nginx 之前的配置信息

3. 在 nginx 的安装源码进行配置指定对应模块 `./configure --with-stream`

4. 通过 `make` 进行编译

5. 将 objs 下面的 nginx 移动到 /usr/local/nginx/sbin 下

6. 在源码目录下执行 `make upgrade` 进行升级，这个可以实现不停机添加新模块的功能

## Nginx 四层负载均衡的指令

### stream

该指令提供在其中指定流服务器指令的配置文件上下文。和 http 指令同级。

| 语法   | stream { ... } |
| ------ | -------------- |
| 默认值 | —              |
| 位置   | main           |

### upstream指令

该指令和 http 的 `upstream` 指令是类似的。

## Nginx 四层负载均衡配置实例

```
stream {
    upstream redisbackend {
            server 192.168.200.146:6379;
            server 192.168.200.146:6378;
    }
    upstream tomcatbackend {
            server 192.168.200.146:8080;
    }
    server {
            listen  81;
            proxy_pass redisbackend;
    }
    server {
            listen	82;
            proxy_pass tomcatbackend;
    }
}
```