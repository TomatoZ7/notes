# nginx 防盗链

## 什么是资源盗链

资源盗链指的是此内容不在自己服务器上，而是通过技术手段，绕过别人的限制将别人的内容放到自己页面上最终展示给用户。以此来盗取大网站的空间和流量。简而言之就是用别人的东西成就自己的网站。

## Nginx 防盗链的实现原理

了解防盗链的原理之前，我们得先学习一个 HTTP 的头信息 `referer`，当浏览器向 web 服务器发送请求的时候，一般都会带上 `referer`，来告诉浏览器该网页是从哪个页面链接过来的。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_anti_leech1.png)

后台服务器可以根据获取到的这个 `referer` 信息来判断是否为自己信任的网站地址，如果是则放行继续访问，如果不是则可以返回 403 (服务端拒绝访问)的状态信息或者返回一张默认的图片。

## Nginx 防盗链的具体实现

valid_referers : nginx 会通过查看 `referer` 自动和 `valid_referers` 后面的内容进行匹配，如果匹配到了就将 `$invalid_referer` 变量置为 0，如果没有匹配到，则将 `$invalid_referer` 变量置为 1，匹配的过程中不区分大小写。

| 语法   | valid_referers none\|blocked\|server_names\|string... |
| ------ | ----------------------------------------------------- |
| 默认值 | —                                                     |
| 位置   | server、location                                      |

none : 如果 Header 中的 `referer` 为空，允许访问。

blocked : 在 Header 中的 `referer` 不为空，但是该值被防火墙或代理进行伪装过，如不带"http://" 、"https://"等协议头的资源允许访问。

string : 可以支持正则表达式和 * 的字符串。如果是正则表达式，需要以 `~` 开头表示。

例如:

```
location ~*\.(png|jpg|gif){
    valid_referers none blocked www.baidu.com 192.168.200.222 *.example.com example.*  www.example.org  ~\.google\.;
    if ($invalid_referer){
        return 403;
        # rewrite ^/ http://www.web.com/images/forbidden.png;
    }
    root /usr/local/nginx/html;
}
```

## 针对目录进行防盗链

配置如下：

```
location /images {
    valid_referers none blocked www.baidu.com 192.168.200.222 *.example.com example.*  www.example.org  ~\.google\.;
    if ($invalid_referer){
        return 403;
        # rewrite ^/ http://www.web.com/images/forbidden.png;
    }
    root /usr/local/nginx/html;
}
```