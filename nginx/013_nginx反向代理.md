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

## Nginx 反向代理系统调优

反向代理值 Buffer 和 Cache。

Buffer 翻译过来是**缓冲**，Cache 翻译过来是**缓存**。

相同点 : 两种方式都是用来提供 IO 吞吐效率，都是用来提升 Nginx 代理的性能。

不同点 : 

缓冲主要用来解决不同设备之间数据传递速度不一致导致的性能低的问题，缓冲中的数据一旦此次操作完成后，就可以删除。

缓存主要是备份，将被代理服务器的数据缓存一份到代理服务器。这样的话，客户端再次获取相同数据的时候，就只需要从代理服务器上获取，效率较高，缓存中的数据可以重复使用，只有满足特定条件才会删除.

### Proxy Buffer 相关指令

#### proxy_buffering : 开启或者关闭代理服务器的缓冲区

| 语法   | proxy_buffering on\|off; |
| ------ | ------------------------ |
| 默认值 | proxy_buffering on;      |
| 位置   | http、server、location   |

#### proxy_buffers : 指定单个连接从代理服务器读取响应的缓存区的个数和大小。

| 语法   | proxy_buffers number size;                |
| ------ | ----------------------------------------- |
| 默认值 | proxy_buffers 8 4k \| 8K;(与系统平台有关) |
| 位置   | http、server、location                    |

number : 缓冲区的个数

size : 每个缓冲区的大小，缓冲区的总大小就是 number*size

#### proxy_buffer_size : 设置从被代理服务器获取的第一部分响应数据的大小。保持与 proxy_buffers 中的 size 一致即可，当然也可以更小。

| 语法   | proxy_buffer_size size;                     |
| ------ | ------------------------------------------- |
| 默认值 | proxy_buffer_size 4k \| 8k;(与系统平台有关) |
| 位置   | http、server、location                      |

#### proxy_busy_buffers_size : 该指令用来限制同时处于 BUSY 状态的缓冲总大小

| 语法   | proxy_busy_buffers_size size;    |
| ------ | -------------------------------- |
| 默认值 | proxy_busy_buffers_size 8k\|16K; |
| 位置   | http、server、location           |

#### proxy_temp_path : 当缓冲区存满后，仍未被 Nginx 服务器完全接受，响应数据就会被临时存放在磁盘文件上，该指令设置文件路径

| 语法   | proxy_temp_path  path;      |
| ------ | --------------------------- |
| 默认值 | proxy_temp_path proxy_temp; |
| 位置   | http、server、location      |

注意path最多设置三层。  

#### proxy_temp_file_write_size : 设置磁盘上缓冲文件的大小。

| 语法   | proxy_temp_file_write_size size;    |
| ------ | ----------------------------------- |
| 默认值 | proxy_temp_file_write_size 8K\|16K; |
| 位置   | http、server、location              |

### Proxy Buffer 配置实例

```
proxy_buffering on;
proxy_buffers 4 32k;
proxy_busy_buffers_size 64k;
proxy_temp_file_write_size 64k;
```