# Nginx 七层负载均衡

## Nginx 七层负载均衡配置指令

### upstream

该指令用来定义一组服务器，它们可以是监听不同端口的服务器，并且也可以是同时监听 TCP 和 Unix socket 的服务器。服务器可以指定不同的权重，默认为1。

| 语法   | upstream name {...} |
| ------ | ------------------- |
| 默认值 | —                   |
| 位置   | http                |

### server

该指令用来指定后端服务器的名称和一些参数，可以使用域名、IP、端口或者 unix socket。

| 语法   | server name [paramerters] |
| ------ | ------------------------- |
| 默认值 | —                         |
| 位置   | upstream                  |

## Nginx 七层负载均衡实现案例

服务端设置

```conf
server {
    listen   9001;
    server_name localhost;
    default_type text/html;
    location /{
    	return 200 '<h1>192.168.200.146:9001</h1>';
    }
}
server {
    listen   9002;
    server_name localhost;
    default_type text/html;
    location /{
    	return 200 '<h1>192.168.200.146:9002</h1>';
    }
}
server {
    listen   9003;
    server_name localhost;
    default_type text/html;
    location /{
    	return 200 '<h1>192.168.200.146:9003</h1>';
    }
}
```

负载均衡器设置

```conf
upstream backend{
	server 192.168.200.146:9091;
	server 192.168.200.146:9092;
	server 192.168.200.146:9093;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

## 负载均衡状态

代理服务器在负责均衡调度中的状态有以下几个：

| 状态         | 概述                              |
| ------------ | --------------------------------- |
| down         | 当前的 server 暂时不参与负载均衡    |
| backup       | 预留的备份服务器                  |
| max_fails    | 允许请求失败的次数                |
| fail_timeout | 经过 max_fails 失败后, 服务暂停的时间 |
| max_conns    | 限制最大的接收连接数              |

### down

```conf
upstream backend{
	server 192.168.200.146:9001 down;
	server 192.168.200.146:9002
	server 192.168.200.146:9003;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

该状态一般会对需要停机维护的服务器进行设置。

### backup

当主服务器不可用时，backup 服务器将用来响应请求。

```conf
upstream backend{
	server 192.168.200.146:9001 down;
	server 192.168.200.146:9002 backup;
	server 192.168.200.146:9003;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

### max_conns

用来设置代理服务器同时活动链接的最大数量，默认为 0，表示不限制。使用该配置可以根据后端服务器处理请求的并发量来进行设置，防止后端服务器被压垮。

### max_fails 和 fail_timeout

max_fail : 设置允许请求代理服务器失败的次数，默认为 1。

fail_timeout : 设置经过 max_fails 失败后，服务暂停的时间，默认是 10 秒。

```conf
upstream backend{
	server 192.168.200.133:9001 down;
	server 192.168.200.133:9002 backup;
	server 192.168.200.133:9003 max_fails=3 fail_timeout=15;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

## 负载均衡策略

Nginx 的 upstream 支持如下六种方式的分配算法，分别是:

| 算法名称   | 说明             |
| ---------- | ---------------- |
| 轮询       | 默认方式         |
| weight     | 权重方式         |
| ip_hash    | 依据 ip 分配方式   |
| least_conn | 依据最少连接方式 |
| url_hash   | 依据 URL 分配方式  |
| fair       | 依据响应时间方式 |

### 轮询

upstream 模块负载均衡默认的策略。每个请求会按时间顺序逐个分配到不同的后端服务器。轮询不需要额外的配置。

```conf
upstream backend{
	server 192.168.200.146:9001;
	server 192.168.200.146:9002;
	server 192.168.200.146:9003;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

### weight 加权(加权轮询)

weight=number : 用来设置服务器的权重，默认为 1，权重数据越大，被分配到请求的几率越大；该权重值，主要是针对实际工作环境中不同的后端服务器硬件配置进行调整的，所以此策略比较适合服务器的硬件配置差别比较大的情况。

```conf
upstream backend{
	server 192.168.200.146:9001 weight=10;
	server 192.168.200.146:9002 weight=5;
	server 192.168.200.146:9003 weight=3;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

### ip_hash

当对后端的多台动态应用服务器做负载均衡时，ip_hash 指令能够将某个客户端 IP 的请求通过哈希算法定位到同一台后端服务器上。这样，当来自某一个 IP 的用户在后端 Web 服务器 A 上登录后，在访问该站点的其他 URL，能保证其访问的还是后端 web 服务器 A。

| 语法   | ip_hash; |
| ------ | -------- |
| 默认值 | —        |
| 位置   | upstream |

```conf
upstream backend{
	ip_hash;
	server 192.168.200.146:9001;
	server 192.168.200.146:9002;
	server 192.168.200.146:9003;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

使用 `ip_hash` 指令无法保证后端服务器的负载均衡，可能导致有些后端服务器接收到的请求多，有些后端服务器接收的请求少，而且设置后端服务器权重等方法将不起作用。

### least_conn

最少连接，把请求转发给连接数较少的后端服务器。轮询算法是把请求平均的转发给各个后端，使它们的负载大致相同；但是，有些请求占用的时间很长，会导致其所在的后端负载较高。这种情况下，`least_conn` 这种方式就可以达到更好的负载均衡效果。

```conf
upstream backend{
	least_conn;
	server 192.168.200.146:9001;
	server 192.168.200.146:9002;
	server 192.168.200.146:9003;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

### url_hash 

按访问 url 的 hash 结果来分配请求，使每个 url 定向到同一个后端服务器，要配合缓存命中来使用。同一个资源多次请求，可能会到达不同的服务器上，导致不必要的多次下载，缓存命中率不高，以及一些资源时间的浪费。而使用 `url_hash`，可以使得同一个 url（也就是同一个资源请求）会到达同一台服务器，一旦缓存住了资源，再此收到请求，就可以从缓存中读取。

```conf
upstream backend{
	hash $request_uri;
	server 192.168.200.146:9001;
	server 192.168.200.146:9002;
	server 192.168.200.146:9003;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

访问如下地址：

```
http://192.168.200.133:8083/a
http://192.168.200.133:8083/b
http://192.168.200.133:8083/c
```

### fair

`fair` 采用的不是内建负载均衡使用的轮换的均衡算法，而是可以根据页面大小、加载时间长短智能的进行负载均衡。

```
upstream backend{
	fair;
	server 192.168.200.146:9001;
	server 192.168.200.146:9002;
	server 192.168.200.146:9003;
}
server {
	listen 8083;
	server_name localhost;
	location /{
		proxy_pass http://backend;
	}
}
```

fair 属于第三方模块实现的负载均衡。需要添加 `nginx-upstream-fair`:

1. 下载 nginx-upstream-fair 模块

[下载地址](https://github.com/gnosek/nginx-upstream-fair)

2. 将下载的文件上传到服务器并进行解压缩

```sh
unzip nginx-upstream-fair-master.zip
```

3. 重命名资源(可选)

```
mv nginx-upstream-fair-master fair
```

4. 使用 `./configure` 命令将资源添加到 Nginx 模块中

```
./configure --add-module=/root/fair
```

5. 编译

```
make
```

编译可能会出现如下错误，ngx_http_upstream_srv_conf_t 结构中缺少default_port：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_7upstream1.png)

#### 解决方案

在 Nginx 的源码中 `src/http/ngx_http_upstream.h`，找到`ngx_http_upstream_srv_conf_s`，在模块中添加添加 `default_port` 属性。

```c
in_port_t	   default_port
```

然后再 make 编译。

6. 更新 Nginx

```sh
mv /usr/local/nginx/sbin/nginx /usr/local/nginx/sbin/nginxold

cd objs
cp nginx /usr/local/nginx/sbin

cd ../
make upgrade
```