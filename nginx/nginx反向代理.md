# nginx 反向代理

Nginx 反向代理模块的指令是由 `ngx_http_proxy_module` 模块进行解析，该模块在安装 Nginx 的时候已经自己加装到 Nginx 中了。

## Nginx 反向代理的配置语法

### proxy_pass

该指令用来设置被代理服务器地址，可以是主机名称、IP 地址加端口号形式。

| 语法   | proxy_pass URL; |
| ------ | --------------- |
| 默认值 | —               |
| 位置   | location        |

URL : 为要设置的被代理服务器地址，包含传输协议(`http`,`https://`)、主机名称或 IP 地址加端口号、URI 等要素。

#### 举例

```
proxy_pass http://www.baidu.com
```

以 `/` 结尾的差异：

```
location /server {
    proxy_pass http://192.168.200.146;  # 访问 http://192.168.200.146/server/index.html

    proxy_pass http://192.168.200.146/;  # 访问 http://192.168.200.146/index.html;
}
```

### proxy_set_header

该指令可以更改 Nginx 服务器接收到的客户端请求的请求头信息，然后将新的请求头发送给代理的服务器。

| 语法   | proxy_set_header field value;                                |
| ------ | ------------------------------------------------------------ |
| 默认值 | proxy_set_header Host $proxy_host;<br/>proxy_set_header Connection close; |
| 位置   | http、server、location                                       |

需要注意的是，如果想要看到结果，必须在被代理的服务器上来获取添加的头信息。

#### 举例

被代理服务器： [192.168.200.146]

```
server {
    listen 8080;
    server_name localhost;
    default_type text/plain;
    return 200 $http_username;
}
```

代理服务器： [192.168.200.133]

```
server {
    listen 8080;
    server_name localhost;
    location /server {
        proxy_pass http://192.168.200.146:8080/;
        proxy_set_header username TOM;
    }
}
```

### proxy_redirect

该指令用来重置头信息中的 `Location` 和 `Refresh` 的值。

| 语法   | proxy_redirect redirect replacement;<br/>proxy_redirect default;<br/>proxy_redirect off; |
| ------ | ------------------------------------------------------------ |
| 默认值 | proxy_redirect default;                                      |
| 位置   | http、server、location                                       |

#### 举例

被代理服务器： [192.168.200.146]

```
server {
    listen 8081;
    server_name localhost;
    if (!-f $request_filename){
    	return 302 http://192.168.200.146;
    }
}
```

代理服务器： [192.168.200.133]

```
server {
    listen 8081;
    server_name localhost;
    location / {
		proxy_pass http://192.168.200.146:8081/;
		proxy_redirect http://192.168.200.146 http://192.168.200.133;
	}
}
```

#### 该指令的几组选项

```
proxy_redirect redirect replacement
```

redirect : 目标，location 的值

repalcement : 要替换的值

```
proxy_redirect default
```

将 location 块的 uri 变量作为 replacement

将 proxy_pass 变量作为 redirect 进行替换

```
proxy_redirect off
```