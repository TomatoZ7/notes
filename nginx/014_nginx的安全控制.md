# nginx 的安全控制

## 安全隔离

通过代理分开了客户端到应用程序服务器端的连接，实现了安全措施。在反向代理之前设置防火墙，仅留一个入口供代理服务器访问。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_security1.png)

## 如何使用SSL对流量进行加密

Nginx要想使用 SSL，需要满足一个条件即需要添加一个模块 `--with-http_ssl_module`，而该模块在编译的过程中又需要 OpenSSL 的支持。

### 增量添加 --with-http_ssl_module 模块

1. 将原有 /usr/local/nginx/sbin/nginx 进行备份

2. 拷贝 nginx 之前的配置信息，可通过 nginx -V 来查看

3. 在nginx的安装源码进行配置指定对应模块  ./configure --with-http_ssl_module

4. 通过 make 模板进行编译

5. 将 objs 下面的 nginx 移动到 /usr/local/nginx/sbin 下

6. 在源码目录下执行 make upgrade 进行升级，这个可以实现不停机添加新模块的功能

### Nginx 的 SSL 相关指令

#### ssl

该指令用来在指定的服务器开启 HTTPS，可以使用 `listen 443 ssl`，后面这种方式更通用些。

| 语法   | ssl on \| off; |
| ------ | -------------- |
| 默认值 | ssl off;       |
| 位置   | http、server   |

```
server{
	listen 443 ssl;
}
```

#### ssl_certificate : 为当前这个虚拟主机指定一个带有 PEM 格式证书的证书

| 语法   | ssl_certificate file; |
| ------ | --------------------- |
| 默认值 | —                     |
| 位置   | http、server          |

#### ssl_certificate_key : 指定 PEM secret key 文件的路径

| 语法   | ssl_ceritificate_key file; |
| ------ | -------------------------- |
| 默认值 | —                          |
| 位置   | http、server               |

#### ssl_session_cache : 配置用于 SSL 会话的缓存

| 语法   | ssl_sesion_cache off\|none\|[builtin[:size]] [shared:name:size] |
| ------ | ------------------------------------------------------------ |
| 默认值 | ssl_session_cache none;                                      |
| 位置   | http、server                                                 |

off : 禁用会话缓存，客户端不得重复使用会话

none : 禁止使用会话缓存，客户端可以重复使用，但是并没有在缓存中存储会话参数

builtin : 内置 OpenSSL 缓存，仅在一个工作进程中使用

shared : 所有工作进程之间共享缓存，缓存的相关信息用 name 和 size 来指定

#### ssl_session_timeout : 开启 SSL 会话功能后，设置客户端能够反复使用储存在缓存中的会话参数时间

| 语法   | ssl_session_timeout time; |
| ------ | ------------------------- |
| 默认值 | ssl_session_timeout 5m;   |
| 位置   | http、server              |

#### ssl_ciphers : 指出允许的密码，密码指定为 OpenSSL 支持的格式

| 语法   | ssl_ciphers ciphers;          |
| ------ | ----------------------------- |
| 默认值 | ssl_ciphers HIGH:!aNULL:!MD5; |
| 位置   | http、server                  |

#### ssl_prefer_server_ciphers : 该指令指定是否服务器密码优先客户端密码

| 语法   | ssl_perfer_server_ciphers on\|off; |
| ------ | ---------------------------------- |
| 默认值 | ssl_perfer_server_ciphers off;     |
| 位置   | http、server                       |

### 生成证书

#### 使用阿里云/腾讯云等第三方服务进行购买

#### 使用 openssl 生成证书

先要确认当前系统是否有安装 openssl：

```
openssl version
```

生成证书步骤如下：

```
mkdir /root/cert
cd /root/cert
openssl genrsa -des3 -out server.key 1024
openssl req -new -key server.key -out server.csr
cp server.key server.key.org
openssl rsa -in server.key.org -out server.key
openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt
```

### SSL 配置实例

```
server {
    listen       443 ssl;
    server_name  localhost;

    ssl_certificate      server.cert;
    ssl_certificate_key  server.key;

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;

    ssl_ciphers  HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers  on;

    location / {
        root   html;
        index  index.html index.htm;
    }
}
```