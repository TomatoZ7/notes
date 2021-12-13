# Nginx Rewrite 功能

Rewrite 是 Nginx 服务器提供的一个重要基本功能。主要的作用是用来实现 URL 的重写。

> Nginx 服务器的 Rewrite 功能的实现依赖于 PCRE 的支持，因此在编译安装 Nginx 服务器之前，需要安装 PCRE 库。Nginx 使用的是 ngx_http_rewrite_module 模块来解析和处理 Rewrite 功能的相关配置。

## Rewrite 规则

### set : 用来设置一个新的变量

| 语法   | set $variable value; |
| ------ | -------------------- |
| 默认值 | —                    |
| 位置   | server、location、if |

variable : 变量的名称，该变量名称要用 `$` 作为变量的第一个字符，且不能与 Nginx 服务器预设的全局变量同名。

value : 变量的值，可以是字符串、其他变量或者变量的组合等。

#### Rewrite 常用全局变量

| 变量               | 说明                                                         |
| ------------------ | ------------------------------------------------------------ |
| $args              | 变量中存放了请求URL中的请求指令。比如 `http://192.168.200.133:8080?arg1=value1&args2=value2` 中的 `arg1=value1&arg2=value2`，功能和 `$query_string` 一样 |
| $http_user_agent   | 变量存储的是用户访问服务的代理信息(如果通过浏览器访问，记录的是浏览器的相关版本信息) |
| $host              | 变量存储的是访问服务器的 `server_name` 值                        |
| $document_uri      | 变量存储的是当前访问地址的 URI。比如 `http://192.168.200.133/server?id=10&name=zhangsan` 中的 `/server`，功能和 `$uri` 一样 |
| $document_root     | 变量存储的是当前请求对应 `location` 的 `root` 值，如果未设置，默认指向 `Nginx` 自带 `html` 目录所在位置 |
| $content_length    | 变量存储的是请求头中的 `Content-Length` 的值                     |
| $content_type      | 变量存储的是请求头中的 `Content-Type` 的值                       |
| $http_cookie       | 变量存储的是客户端的 cookie 信息，可以通过 `add_header Set-Cookie cookieName=cookieValue` 来添加 cookie 数据 |
| $limit_rate        | 变量中存储的是 Nginx 服务器对网络连接速率的限制，也就是 Nginx 配置中对 limit_rate 指令设置的值，默认是 0，不限制 |
| $remote_addr       | 变量中存储的是客户端的 IP 地址                                 |
| $remote_port       | 变量中存储了客户端与服务端建立连接的端口号                   |
| $remote_user       | 变量中存储了客户端的用户名，需要有认证模块才能获取           |
| $scheme            | 变量中存储了访问协议                                         |
| $server_addr       | 变量中存储了服务端的地址                                     |
| $server_name       | 变量中存储了客户端请求到达的服务器的名称                     |
| $server_port       | 变量中存储了客户端请求到达服务器的端口号                     |
| $server_protocol   | 变量中存储了客户端请求协议的版本，比如"HTTP/1.1"             |
| $request_body_file | 变量中存储了发给后端服务器的本地文件资源的名称               |
| $request_method    | 变量中存储了客户端的请求方式，比如"GET","POST"等             |
| $request_filename  | 变量中存储了当前请求的资源文件的路径名                       |
| $request_uri       | 变量中存储了当前请求的 URI，并且携带请求参数，比如 `http://192.168.200.133/server?id=10&name=zhangsan` 中的 `/server?id=10&name=zhangsan` |

### if : 条件判断

| 语法   | if  (condition){...} |
| ------ | -------------------- |
| 默认值 | —                    |
| 位置   | server、location     |

condition 为判定条件，可以支持以下写法：

#### 1、变量名

如果变量名对应的值为空或者是 0，if 都判断为 false,其他条件为 true。

#### 2、= 和 !=

满足条件为 true，不满足为 false。

```
if ($request_method = POST){
	return 405;
}
```

> 注意：此处字符串不需要添加引号。

#### 3、正则表达式

变量与正则表达式之间使用 `~`，`~*`，`!~`，`!~*`来连接。

`~` 代表匹配正则表达式过程中区分大小写。

`~*` 代表匹配正则表达式过程中不区分大小写。

`!~` 和 `!~*` 刚好和上面取相反值，如果匹配上返回 false，匹配不上返回 true。

```
if ($http_user_agent ~ MSIE){
	#$http_user_agent的值中是否包含MSIE字符串，如果包含返回true
}
```

> 注意：正则表达式字符串一般不需要加引号，但是如果字符串中包含 "}" 或者是 ";" 等字符时，就需要把引号加上。

#### 4、-f 和 !-f

```
if (-f $request_filename){
	#判断请求的文件是否存在
}
if (!-f $request_filename){
	#判断请求的文件是否不存在
}
```

#### 5、-d 和 !-d : 判断请求的目录是否存在

#### 6、-e 和 !-e : 判断请求的目录或者文件是否存在

#### 7、-x 和 !-x : 判断请求的文件是否可执行

### break

该指令用于中断当前相同作用域中的其他 Nginx 配置。与该指令处于同一作用域的 Nginx 配置中，位于它前面的指令配置生效，位于后面的指令配置无效。

`break` 还有另外一个功能是终止当前的匹配并把当前的 URI 在本 location 进行重定向访问处理。

| 语法   | break;               |
| ------ | -------------------- |
| 默认值 | —                    |
| 位置   | server、location、if |

### return

该指令用于完成对请求的处理，直接向客户端返回响应状态代码。在 `return` 后的所有 Nginx 配置都是无效的。

| 语法   | return code [text];<br/>return code URL;<br/>return URL; |
| ------ | -------------------------------------------------------- |
| 默认值 | —                                                        |
| 位置   | server、location、if                                     |

code : 为返回给客户端的 HTTP 状态代理。可以返回的状态代码为 0~999 的任意 HTTP 状态代理。

text : 为返回给客户端的响应体内容，支持变量的使用。

URL : 为返回给客户端的 URL 地址。

### rewrite

该指令通过正则表达式的使用来改变URI。可以同时存在一个或者多个指令，按照顺序依次对URL进行匹配和处理。

| 语法   | rewrite regex replacement [flag]; |
| ------ | --------------------------------- |
| 默认值 | —                                 |
| 位置   | server、location、if              |

regex : 用来匹配 URI 的正则表达式

replacement : 匹配成功后，用于替换 URI 中被截取内容的字符串。如果该字符串是以 `http://` 或者 `https://` 开头的，则不会继续向下对 URI 进行其他处理，而是直接返回重写后的 URI 给客户端。

flag : 用来设置 rewrite 对 URI 的处理行为，可选值有如下：

+ last
+ break
+ redirect (302)
+ permanent (301)

### rewrite_log : 配置是否开启URL重写日志的输出功能

| 语法   | rewrite_log on\|off;       |
| ------ | -------------------------- |
| 默认值 | rewrite_log off;           |
| 位置   | http、server、location、if |

开启后，URL 重写的相关日志将以 notice 级别输出到 error_log 指令配置的日志文件汇总。