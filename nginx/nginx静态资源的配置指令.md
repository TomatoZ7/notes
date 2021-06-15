# Nginx 静态资源的配置指令

静态资源指在服务器端真实存在并且能直接拿来展示的一些文件，比如常见的 html 页面、css 文件、js 文件、图片、视频等资源。

## listen : 用来配置监听端口

| 语法   | listen address[:port] [default_server]...;<br/>listen port [default_server]...; |
| ------ | ------------------------------------------------------------ |
| 默认值 | listen *:80 \| *:8000                                        |
| 位置   | server                                                       |

listen 的设置比较灵活，我们通过几个例子来把常用的设置方式熟悉下：

```
listen 127.0.0.1:8000;  // listen localhost:8000 监听指定的IP和端口
listen 127.0.0.1;       // 监听指定IP的所有端口
listen 8000;            // 监听指定端口上的连接
listen *:8000;          // 监听指定端口上的连接
```

`default_server` 属性是标识符，用来将此虚拟主机设置成默认主机。所谓的默认主机指的是如果没有匹配到对应的 `address:port`，则会默认执行。如果不指定默认使用的是第一个 server。

```
server{
	listen 8080;
	server_name 127.0.0.1;
	location /{
		root html;
		index index.html;
	}
}

# 显示定义一个 default server
server{
	listen 8080 default_server;
	server_name localhost;
	default_type text/plain;
	return 444 'This is a error request';
}
```


## server_name : 用来设置虚拟主机服务名称

| 语法   | server_name  name ...;<br/>name可以提供多个中间用空格分隔 |
| ------ | ------------------------------------------------------ |
| 默认值 | server_name  "";                                        |
| 位置   | server                                                  |

关于 `server_name` 的配置方式有三种，分别是：

### 精确匹配

如：

```
server {
	listen 80;
	server_name www.baidu.cn baidu.cn;
	...
}
```

> 注意如果是本地配置，需要在 host 文件添加映射关系如：
>
> 127.0.0.1 www.baidu.cn
>
> host 位置 :  
> windows : C:\Windows\System32\drivers\etc
> centos : /etc/hosts

### 使用 通配符* 配置

`server_name` 中支持通配符 "*",但需要注意的是通配符不能出现在域名的中间，只能出现在首段或尾段，如：

```
server {
	listen 80;
	server_name  *.baidu.cn	www.baidu.*;
	# www.baidu.cn abc.baidu.cn www.baidu.cn www.baidu.com
	...
}
```

下面的配置就会报错

```
server {
	listen 80;
	server_name  www.*.cn www.baidu.c*
	...
}
```

### 使用正则表达式配置

`server_name` 中可以使用正则表达式，并且使用 `~` 作为正则表达式字符串的开始标记。

常见的正则表达式

| 代码  | 说明                                                     |
| ----- | ------------------------------------------------------- |
| ^     | 匹配搜索字符串开始位置                                    |
| $     | 匹配搜索字符串结束位置                                    |
| .     | 匹配除换行符\n之外的任何单个字符                           |
| \     | 转义字符，将下一个字符标记为特殊字符                       |
| [xyz] | 字符集，与任意一个指定字符匹配                             |
| [a-z] | 字符范围，匹配指定范围内的任何字符                         |
| \w    | 与以下任意字符匹配 A-Z a-z 0-9 和下划线,等效于[A-Za-z0-9_] |
| \d    | 数字字符匹配，等效于[0-9]                                 |
| {n}   | 正好匹配n次                                              |
| {n,}  | 至少匹配n次                                              |
| {n,m} | 匹配至少n次至多m次                                        |
| *     | 零次或多次，等效于{0,}                                    |
| +     | 一次或多次，等效于{1,}                                    |
| ?     | 零次或一次，等效于{0,1}                                   |

配置如下：

```
server{
        listen 80;
        server_name ~^www\.(\w+)\.com$;
        default_type text/plain;
        return 200 $1  $2 ..;
}
```

> $1 代表 () 第一个匹配的内容，如 www.baidu.com 匹配的 $1 就是 baidu
>
> 注意 ~ 后面不能加空格，括号可以取值

### 匹配执行顺序

使用通配符或者正则的时候可能会出现一个域名多个匹配的情况，具体的顺序如下：

```
No1 : 准确匹配 server_name (exact_success)

No2 : 通配符在开始时匹配 server_name 成功 (wildcard_before_success)

No3 : 通配符在结束时匹配 server_name 成功 (wildcard_after_success)

No4 : 正则表达式匹配 server_name 成功 (regex_success)

No5 : 被默认的 default_server 处理，如果没有指定默认找第一个 server (default_server)
```


## location : 用来设置请求的URI

| 语法   | location [  =  \|   ~  \|  ~*   \|   ^~   \|@ ] uri{...} |
| ------ | -------------------------------------------------------- |
| 默认值 | —                                                        |
| 位置   | server,location                                          |

uri 变量是待匹配的请求字符串，nginx 服务器在搜索匹配 `location` 的时候，先使用不包含正则表达式进行匹配，找到一个匹配度最高的一个，然后在通过包含正则表达式的进行匹配，如果能匹配到直接访问，匹配不到，就使用刚才匹配度最高的那个location来处理请求。

### 不带符号，要求必须以指定模式开始

```
server {
	listen 80;
	server_name 127.0.0.1;
	location /abc{
		default_type text/plain;
		return 200 "access success";
	}
}
```

以下访问都是正确的

http://192.168.200.133/abc

http://192.168.200.133/abc?p1=TOM

http://192.168.200.133/abc/

http://192.168.200.133/abcdef

### = : 用于不包含正则表达式的 uri 前，必须与指定的模式精确匹配

```
server {
	listen 80;
	server_name 127.0.0.1;
	location =/abc{
		default_type text/plain;
		return 200 "access success";
	}
}
```

可以匹配到

http://192.168.200.133/abc

http://192.168.200.133/abc?p1=TOM

匹配不到

http://192.168.200.133/abc/

http://192.168.200.133/abcdef

### ~ : 用于表示当前 uri 中包含了正则表达式，并且区分大小写

### ~* : 用于表示当前 uri 中包含了正则表达式，并且不区分大小写

换句话说，如果uri包含了正则表达式，需要用上述两个符合来标识

```
server {
	listen 80;
	server_name 127.0.0.1;
	location ~^/abc\w${
		default_type text/plain;
		return 200 "access success";
	}
}
server {
	listen 80;
	server_name 127.0.0.1;
	location ~*^/abc\w${
		default_type text/plain;
		return 200 "access success";
	}
}
```

### ^~ : 用于不包含正则表达式的 uri 前，功能和不加符号的一致，唯一不同的是，如果模式匹配，那么就停止搜索其他模式了