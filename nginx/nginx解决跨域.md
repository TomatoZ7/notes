# nginx 解决跨域

两台服务器的通信不满足**同源策略**，就会出现跨域问题。

> 协议、域名(IP)、端口相同即为同源。

## 跨域问题的案例演示

### nginx 的 html 目录下新建一个 a.html

```html
<html>
  <head>
        <meta charset="utf-8">
        <title>跨域问题演示</title>
        <script src="jquery.js"></script>
        <script>
            $(function(){
                $("#btn").click(function(){
                        $.get('http://192.168.200.133:8080/getUser',function(data){
                                alert(JSON.stringify(data));
                        });
                });
            });
        </script>
  </head>
  <body>
        <input type="button" value="获取数据" id="btn"/>
  </body>
</html>
```

### nginx.conf 配置如下内容

```
server{
    listen  8080;
    server_name localhost;
    location /getUser{
            default_type application/json;
            return 200 '{"id":1,"name":"TOM","age":18}';
    }
}

server{
	listen 	80;
	server_name localhost;
	location /{
		root html;
		index index.html;
	}
}
```

### 访问测试

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_cross_domain1.png)

## 解决方案

使用 `add_header` 指令，添加一些响应头信息。

解决跨域问题，需要添加两个头信息： `Access-Control-Allow-Origin`,`Access-Control-Allow-Methods`。

Access-Control-Allow-Origin : 直译过来是允许跨域访问的源地址信息，可以配置多个(多个用逗号分隔)，也可以使用 `*` 代表所有源。

Access-Control-Allow-Methods : 直译过来是允许跨域访问的请求方式，值可以为 `GET` `POST` `PUT` `DELETE`...，可以全部设置，也可以根据需要设置，多个用逗号分隔。

具体配置方式

```
location /getUser{
    add_header Access-Control-Allow-Origin *;
    add_header Access-Control-Allow-Methods GET,POST,PUT,DELETE;
    default_type application/json;
    return 200 '{"id":1,"name":"TOM","age":18}';
}
```