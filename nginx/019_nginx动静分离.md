# nginx 动静分离

对于一些静态资源直接放在 nginx 服务器上，动态资源则请求被代理服务器，降低被代理服务器压力。

## 配置实例

```conf
upstream webservice{
   server 192.168.200.146:8080;
}
server {
        listen       80;
        server_name  localhost;

        #动态资源
        location /demo {
            proxy_pass http://webservice;
        }
        #静态资源
        location ~/.*\.(png|jpg|gif|js){
            root html/web;
            gzip on;
        }

        location / {
            root   html/web;
            index  index.html index.htm;
        }
}
```