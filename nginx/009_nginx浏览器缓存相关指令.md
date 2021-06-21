# nginx 浏览器缓存相关指令

## expires : 控制页面缓存

可以通过该指令控制 HTTP 响应头的 `Expires` 和 `Cache-Control`

| 语法   | expires   [modified] time<br/>expires epoch\|max\|off; |
| ------ | ------------------------------------------------------ |
| 默认值 | expires off;                                           |
| 位置   | http、server、location                                 |

time : 可以整数也可以是负数，指定过期时间。如果是负数，`Cache-Control` 则为 `no-cache`，如果为整数或 0，则 `Cache-Control` 的值为 `max-age=time`。

epoch : 指定 `Expires` 的值为 '1 January,1970,00:00:01 GMT'(1970-01-01 00:00:00)，则 `Cache-Control` 的值为 `no-cache`。

max : 指定 `Expires` 的值为 '31 December2037 23:59:59GMT'(2037-12-31 23:59:59)，则 `Cache-Control` 的值为 10 年。

off : 默认不缓存。

## add_header : 添加指定的响应头和响应值

| 语法   | add_header name value [always]; |
| ------ | ------------------------------- |
| 默认值 | —                               |
| 位置   | http、server、location...       |

`Cache-Control` 作为响应头，可以设置如下值：

| 指令             | 说明                                           |
| ---------------- | ---------------------------------------------- |
| must-revalidate  | 可缓存但必须再向源服务器进行确认               |
| no-cache         | 缓存前必须确认其有效性                         |
| no-store         | 不缓存请求或响应的任何内容                     |
| no-transform     | 代理不可更改媒体类型                           |
| public           | 可向任意方提供响应的缓存                       |
| private          | 仅向特定用户返回响应                           |
| proxy-revalidate | 要求中间缓存服务器对缓存的响应有效性再进行确认 |
| max-age=<秒>     | 响应最大Age值                                  |
| s-maxage=<秒>    | 公共缓存服务器响应的最大Age值                  |