# Nginx 静态资源优化配置语法

## sendfile : 用来开启高效的文件传输模式

| 语法   | sendﬁle on \|oﬀ;          |
| ------ | ------------------------- |
| 默认值 | sendﬁle oﬀ;               |
| 位置   | http、server、location... |

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_static_opt1.png)

## tcp_nopush : 主要是用来提升网络包的传输'效率'

该指令必须在 sendfile 打开的状态下才会生效。

| 语法   | tcp_nopush on\|off;    |
| ------ | ---------------------- |
| 默认值 | tcp_nopush off;         |
| 位置   | http、server、location |

## tcp_nodelay : 提高网络包传输的'实时性'

该指令必须在 keep-alive 连接开启的情况下才生效。

| 语法   | tcp_nodelay on\|off;   |
| ------ | ---------------------- |
| 默认值 | tcp_nodelay on;        |
| 位置   | http、server、location |

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/nginx_static_opt2.png)

## 小结

`tcp_nopush` 和 `tcp_nodelay` 看起来是"互斥"的，那么为什么要将这两个值都打开？

在 linux2.5.9 之后两者是可以兼容的，三个指令都开启的好处是：

`sendfile` 可以开启高效的文件传输模式；

`tcp_nopush` 可以确保在发送到客户端之前数据包已经充分"填满"，并加快文件发送的速度；

当到达最后一个可能没有"填满"而暂停发送的数据包时，Nginx 会忽略 `tcp_nopush` 参数，`tcp_nodelay` 强制套接字发送数据。

由此可知，`tcp_nopush` 和 `tcp_nodelay` 可以同时开启，它比单独配置 `tcp_nodelay` 具有更强的性能。

所以我们可以使用如下配置来优化静态资源的处理：

```
sendfile on;
tcp_nopush on;
tcp_nodelay on;
```