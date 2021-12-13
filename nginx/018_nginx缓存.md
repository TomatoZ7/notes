# nginx 缓存

## 缓存的概念

缓存就是数据交换的缓冲区(称作:Cache)，当用户要获取数据的时候，会先从缓存中去查询获取数据，如果缓存中有就会直接返回给用户，如果缓存中没有，则会发请求从服务器重新查询数据，将数据返回给用户的同时将数据放入缓存，下次用户就会直接从缓存中获取数据。

缓存其实在很多场景中都有用到，比如：

| 场景             | 作用                   |
| ---------------- | ---------------------- |
| 操作系统磁盘缓存 | 减少磁盘机械操作         |
| 数据库缓存       | 减少文件系统的IO操作     |
| 应用程序缓存     | 减少对数据库的查询       |
| Web服务器缓存    | 减少对应用服务器请求次数 |
| 浏览器缓存       | 减少与后台的交互次数     |

缓存的优点：

1. 减少数据传输，节省网络流量，加快响应速度，提升用户体验；

2. 减轻服务器压力；

3. 提供服务端的高可用性；

缓存的缺点：

1. 数据的不一致

2. 增加成本

## Nginx 的 web 缓存服务

Nginx 是从 0.7.48 版开始提供缓存功能。Nginx 是基于 Proxy Store 来实现的，其原理是把 URL 及相关组合当做 Key，在使用 MD5 算法对 Key 进行哈希，得到硬盘上对应的哈希目录路径，从而将缓存内容保存在该目录中。它可以支持任意 URL 连接，同时也支持 404/301/302 这样的非 200 状态码。Nginx 即可以支持对指定 URL 或者状态码设置过期时间，也可以使用 purge 命令来手动清除指定 URL 的缓存。

## Nginx 缓存设置的相关指令

Nginx 的 web 缓存服务主要是使用 `ngx_http_proxy_module` 模块相关指令集来完成。

### proxy_cache_path

该指定用于设置缓存文件的存放路径

| 语法   | proxy_cache_path path [levels=number] <br/>keys_zone=zone_name:zone_size [inactive=time]\[max_size=size]; |
| ------ | ------------------------------------------------------------ |
| 默认值 | —                                                            |
| 位置   | http                                                         |

path : 缓存路径地址，如：`/usr/local/proxy_cache`

`levels=1:2` : 缓存空间有两层目录，第一次是1个字母，第二次是2个字母

举例说明：

cache[key] 通过 MD5 加密以后的值为  0fea6a13c52b4d4725368f24b045ca84

`levels=1:2` : 最终的存储路径为 /usr/local/proxy_cache/4/a8

`levels=2:1:2` : 最终的存储路径为 /usr/local/proxy_cache/84/a/5c

`levels=2:2:2` : 最终的存储路径为 /usr/local/proxy_cache/84/ca/45

keys_zone : 用来为这个缓存区设置名称和指定大小，如：

`keys_zone=cache:200m` 缓存区的名称为 cache，大小为 200 M，1M大概能存储 8000 个 keys。

inactive : 指定缓存的数据多次时间未被访问就将被删除，如：

`inactive=1d` 缓存数据在 1 天内没有被访问就会被删除

max_size : 设置最大缓存空间，如果缓存空间存满，默认会覆盖缓存时间最长的资源，如:

`max_size=20g`

配置实例:

```
http{
	proxy_cache_path /usr/local/proxy_cache keys_zone=cache:200m  levels=1:2:1 inactive=1d max_size=20g;
}
```

### proxy_cache

该指令用来开启或关闭代理缓存，如果是开启则自定使用哪个缓存区来进行缓存。

| 语法   | proxy_cache zone_name\|off; |
| ------ | --------------------------- |
| 默认值 | proxy_cache off;            |
| 位置   | http、server、location      |

zone_name : 指定使用缓存区的名称

### proxy_cache_key

该指令用来设置 web 缓存的 key 值，Nginx 会根据 key 值 MD5 哈希存缓存。

| 语法   | proxy_cache_key key;                              |
| ------ | ------------------------------------------------- |
| 默认值 | proxy_cache_key \$scheme\$proxy_host$request_uri; |
| 位置   | http、server、location                            |

### proxy_cache_valid

该指令用来对不同返回状态码的URL设置不同的缓存时间

| 语法   | proxy_cache_valid [code ...] time; |
| ------ | ---------------------------------- |
| 默认值 | —                                  |
| 位置   | http、server、location             |

如：

```conf
#为 200 和 302 的响应 URL 设置 10 分钟缓存，为 404 的响应 URL 设置 1 分钟缓存
proxy_cache_valid 200 302 10m;
proxy_cache_valid 404 1m;

对所有响应状态码的 URL 都设置 1 分钟缓存
proxy_cache_valid any 1m;
```

### proxy_cache_min_uses

该指令用来设置资源被访问多少次后被缓存

| 语法   | proxy_cache_min_uses number; |
| ------ | ---------------------------- |
| 默认值 | proxy_cache_min_uses 1;      |
| 位置   | http、server、location       |

### proxy_cache_methods

该指令用户设置缓存哪些 HTTP 方法

| 语法   | proxy_cache_methods GET\|HEAD\|POST; |
| ------ | ------------------------------------ |
| 默认值 | proxy_cache_methods GET HEAD;        |
| 位置   | http、server、location               |

默认缓存 HTTP 的 GET 和 HEAD 方法，不缓存 POST 方法。

## Nginx 缓存配置实例

```conf
http{
	proxy_cache_path /usr/local/proxy_cache levels=2:1 keys_zone=nginx_cache:200m inactive=1d max_size=20g;
	upstream backend{
		server 192.168.200.146:8080;
	}
	server {
		listen       8080;
        server_name  localhost;
        location / {
        	proxy_cache nginx_cache;
            proxy_cache_key cache;
            proxy_cache_min_uses 5;
            proxy_cache_valid 200 5d;
            proxy_cache_valid 404 30s;
            proxy_cache_valid any 1m;
            add_header nginx-cache "$upstream_cache_status";
        	proxy_pass http://backend/js/;
        }
	}
}
```

## Nginx缓存的清除

### 方式一 : 删除对应的缓存目录

```
rm -rf /usr/local/proxy_cache/......
```

### 方式二 : 使用第三方扩展模块 ngx_cache_purge

1. 下载 ngx_cache_purge 模块对应的资源包，并上传到服务器。

2. 对资源文件进行解压缩

```sh
tar -zxf ngx_cache_purge-2.3.tar.gz
```

3. 修改文件夹名称，方便后期配置（可选）

```sh
mv ngx_cache_purge-2.3 purge
```

4. 查询 Nginx 的配置参数并拷贝

```sh
nginx -V
```

5. 进入 Nginx 的安装目录，使用 ./configure 进行参数配置

```sh
./configure --add-module=/root/nginx/module/purge
```

6. 使用 `make` 进行编译

7. 将 nginx 安装目录的 nginx 二进制可执行文件备份

```sh
mv /usr/local/nginx/sbin/nginx /usr/local/nginx/sbin/nginxold
```

8. 将编译后的 objs 中的 nginx 拷贝到 nginx 的 sbin 目录下

```sh
cp objs/nginx /usr/local/nginx/sbin
```

9. 使用make进行升级

```sh
make upgrade
```

10. 在 nginx 配置文件中进行如下配置

```conf
server{
	location ~/purge(/.*) {
		proxy_cache_purge nginx_cache cache;
	}
}
```

## Nginx 设置资源不缓存

针对一些经常发生变化的数据，我们需要进行过滤，不尽兴缓存。

### proxy_no_cache

该指令是用来定义不将数据进行缓存的条件。

| 语法   | proxy_no_cache string ...; |
| ------ | -------------------------- |
| 默认值 | —                          |
| 位置   | http、server、location     |

配置实例

```conf
proxy_no_cache $cookie_nocache $arg_nocache $arg_comment;
```

### proxy_cache_bypass

该指令是用来设置不从缓存中获取数据的条件。

| 语法   | proxy_cache_bypass string ...; |
| ------ | ------------------------------ |
| 默认值 | —                              |
| 位置   | http、server、location         |

配置实例

```conf
proxy_cache_bypass $cookie_nocache $arg_nocache $arg_comment;
```

上述两个指令都有一个指定的条件，这个条件可以是多个，并且多个条件中至少有一个不为空且不等于"0",则条件满足成立。

上面给的配置实例是从官方网站获取的，里面使用到了三个变量，分别是 `$cookie_nocache`、`$arg_nocache`、`$arg_comment`：

`$cookie_nocache` : 当前请求的 cookie 中键的名称为 nocache 对应的值

`$arg_nocache` 和 `$arg_comment` : 当前请求的参数中属性名为 nocache 和 comment 对应的值

### 不缓存配置实例

```conf
server{
	listen	8080;
	server_name localhost;
	location / {
		if ($request_uri ~ /.*\.js$){
           set $nocache 1;
        }
		proxy_no_cache $nocache $cookie_nocache $arg_nocache $arg_comment;
        proxy_cache_bypass $nocache $cookie_nocache $arg_nocache $arg_comment;
	}
}
```