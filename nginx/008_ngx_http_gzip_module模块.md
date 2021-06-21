# ngx_http_gzip_module 模块

## Gzip 模块配置指令

### 1、gzip : 该指令用于开启或者关闭 gzip 功能

| 语法   | gzip on\|off;             |
| ------ | ------------------------- |
| 默认值 | gzip off;                 |
| 位置   | http、server、location... |

> 注意：只有该指令为打开状态，下面的指令才有效果。

### 2、gzip_types : 可以根据响应页的 MIME 类型选择性地开启 Gzip 压缩功能

| 语法   | gzip_types mime-type ...; |
| ------ | ------------------------- |
| 默认值 | gzip_types text/html;     |
| 位置   | http、server、location    |

所选择的值可以从 `mime.types` 文件中进行查找，也可以使用 `*` 代表所有。（一般不建议这么做，全部压缩对 CPU 压力太大）

### 3、gzip_comp_level : 用于设置 Gzip 压缩程度

级别从 1-9，1 表示要是程度最低，但是效率最高，9 刚好相反，压缩程度最高，但是效率最低最费时间。

| 语法   | gzip_comp_level level; |
| ------ | ---------------------- |
| 默认值 | gzip_comp_level 1;     |
| 位置   | http、server、location |

```
http{
	gzip_comp_level 6;
}
```

### 4、gzip_vary : 用于设置使用 Gzip 进行压缩发送是否携带 `Vary:Accept-Encoding` 响应头信息

主要是告诉接收方，所发送的数据经过了 Gzip 压缩处理。

| 语法   | gzip_vary on\|off;     |
| ------ | ---------------------- |
| 默认值 | gzip_vary off;         |
| 位置   | http、server、location |

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_gzip1.png)

### 5、gzip_buffers : 用于处理请求压缩的缓冲区数量和大小

| 语法   | gzip_buffers number size;  |
| ------ | -------------------------- |
| 默认值 | gzip_buffers 32 4k\|16 8k; |
| 位置   | http、server、location     |

number : 指定 Nginx 服务器向系统申请缓存空间个数。

size : 每个缓存空间的大小。

gzip_buffers 主要实现的是申请 number 个每个大小为 size 的内存空间。这个值的设定一般会和服务器的操作系统有关，所以建议此项不设置，使用默认值即可。

### 6、gzip_disable : 针对不同种类客户端发起的请求，可以选择性地开启和关闭 Gzip 功能

| 语法   | gzip_disable regex ...; |
| ------ | ----------------------- |
| 默认值 | —                       |
| 位置   | http、server、location  |

regex : 根据客户端的浏览器标志(user-agent)来设置，支持使用正则表达式。指定的浏览器标志不使用 Gzip。

该指令一般是用来排除一些明显不支持Gzip的浏览器。

```
gzip_disable "MSIE [1-6]\.";
```

### 7、gzip_http_version : 针对不同的 HTTP 协议版本，可以选择性地开启和关闭 Gzip 功能

| 语法   | gzip_http_version 1.0\|1.1; |
| ------ | --------------------------- |
| 默认值 | gzip_http_version 1.1;      |
| 位置   | http、server、location      |

该指令是指定使用 Gzip 的 HTTP 最低版本，该指令一般采用**默认值**即可。

### 8、gzip_min_length : 针对传输数据的大小，可以选择性地开启和关闭 Gzip 功能

| 语法   | gzip_min_length length; |
| ------ | ----------------------- |
| 默认值 | gzip_min_length 20;     |
| 位置   | http、server、location  |

```
nginx 计量大小的单位：bytes[字节] / kb[千字节] / M[兆]
例如: 1024 / 10k|K / 10m|M
```

### 9、gzip_proxied : 设置是否对服务端返回的结果进行 Gzip 压缩

| 语法   | gzip_proxied  off\|expired\|no-cache\|<br/>no-store\|private\|no_last_modified\|no_etag\|auth\|any; |
| ------ | ------------------------------------------------------------ |
| 默认值 | gzip_proxied off;                                            |
| 位置   | http、server、location                                       |

off - 关闭 Nginx 服务器对后台服务器返回结果的 Gzip 压缩

expired - 启用压缩，如果 header 头中包含 `Expires` 头信息

no-cache - 启用压缩，如果 header 头中包含 `Cache-Control:no-cache` 头信息

no-store - 启用压缩，如果 header 头中包含 `Cache-Control:no-store` 头信息

private - 启用压缩，如果 header 头中包含 `Cache-Control:private` 头信息

no_last_modified - 启用压缩，如果 header 头中不包含 `Last-Modified` 头信息

no_etag - 启用压缩，如果 header 头中不包含 `ETag` 头信息

auth - 启用压缩，如果 header 头中包含 `Authorization` 头信息

any - 无条件启用压缩

## Gzip 和 sendfile 共存问题

开启 `sendfile` 以后，在读取磁盘上的静态资源文件的时候，可以减少拷贝的次数，可以不经过用户进程将静态文件通过网络设备发送出去。

但是 Gzip 要想对资源压缩，是需要经过用户进程进行操作的。

可以使用 ngx_http_gzip_static_module 模块的 `gzip_static` 指令来解决两个设置的共存问题。

### Nginx 添加 ngx_http_gzip_static_module 模块

#### 1、查询当前Nginx的配置参数

```sh
nginx -V
```

#### 2、将 nginx 安装目录下 sbin 目录中的 nginx 二进制文件进行更名

```sh
cd /usr/local/nginx/sbin
mv nginx nginxold
```

#### 3、进入 Nginx 的安装目录

```sh
cd /root/nginx/core/nginx-1.16.1
```

#### 4、执行 `make clean` 清空之前编译的内容

```sh
make clean
```

#### 5、使用 configure 来配置参数

这里需要加上第一步的参数 xxx

```sh
./configure xxx --with-http_gzip_static_module
```

#### 6、使用 make 命令进行编译

```sh
make
```

#### 7、将 objs 目录下的 nginx 二进制执行文件移动到 nginx 安装目录下的 sbin 目录中

```sh
mv objs/nginx /usr/local/nginx/sbin
```

#### 8、执行更新命令

```sh
make upgrade
```

### gzip_static : 检查与访问资源同名的 .gz 文件时，response 中以 gzip 相关的 header 返回 .gz 文件的内容。

| 语法   | **gzip_static** on \| off \| always; |
| ------ | ------------------------------------ |
| 默认值 | gzip_static off;                     |
| 位置   | http、server、location               |