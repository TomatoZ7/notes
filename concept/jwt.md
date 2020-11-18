# JWT 原理

在前后端分离开发时为什么需要用户认证呢？原因是由于 HTTP 协定是不储存状态的(stateless)，这意味着当我们透过帐号密码验证一个使用者时，当下一个 request 请求时它就把刚刚的资料忘了。于是我们的程序就不知道谁是谁，就要再验证一次。所以为了保证系统安全，我们就需要验证用户否处于登录状态。

## 传统方式
前后端分离通过 Restful API 进行数据交互时，如何验证用户的登录信息及权限。在原来的项目中，使用的是最传统也是最简单的方式，前端登录，后端根据用户信息生成一个token，并保存这个 token 和对应的用户 id 到数据库或 Session 中，接着把 token 传给用户，存入浏览器 cookie，之后浏览器请求带上这个 cookie，后端根据这个 cookie 值来查询用户，验证是否过期。

但这样做问题就很多，如果我们的页面出现了 XSS 漏洞，由于 cookie 可以被 JavaScript 读取，XSS 漏洞会导致用户 token 泄露，而作为后端识别用户的标识，cookie 的泄露意味着用户信息不再安全。尽管我们通过转义输出内容，使用 CDN 等可以尽量避免 XSS 注入，但谁也不能保证在大型的项目中不会出现这个问题。

在设置 cookie 的时候，其实你还可以设置 httpOnly 以及 secure 项。设置 httpOnly 后 cookie 将不能被 JS 读取，浏览器会自动的把它加在请求的 header 当中，设置 secure 的话，cookie 就只允许通过 HTTPS 传输。secure 选项可以过滤掉一些使用 HTTP 协议的 XSS 注入，但并不能完全阻止。

httpOnly 选项使得 JS 不能读取到 cookie，那么 XSS 注入的问题也基本不用担心了。但设置 httpOnly 就带来了另一个问题，就是很容易的被 XSRF，即跨站请求伪造。当你浏览器开着这个页面的时候，另一个页面可以很容易的跨站请求这个页面的内容。因为 cookie 默认被发了出去。

另外，如果将验证信息保存在数据库中，后端每次都需要根据token查出用户id，这就增加了数据库的查询和存储开销。若把验证信息保存在 session 中，有加大了服务器端的存储压力。那我们可不可以不要服务器去查询呢？如果我们生成token遵循一定的规律，比如我们使用对称加密算法来加密用户id形成token，那么服务端以后其实只要解密该token就可以知道用户的id是什么了。不过呢，我只是举个例子而已，要是真这么做，只要你的对称加密算法泄露了，其他人可以通过这种加密方式进行伪造token，那么所有用户信息都不再安全了。恩，那用非对称加密算法来做呢，其实现在有个规范就是这样做的，就是我们接下来要介绍的 JWT。

&emsp;

## Json Web Token（JWT）
JWT 是一个开放标准(RFC 7519)，它定义了一种用于简洁，自包含的用于通信双方之间以 JSON 对象的形式安全传递信息的方法。JWT 可以使用 HMAC 算法或者是 RSA 的公钥密钥对进行签名。

### JWT 特点
+ 可以通过 URL, POST 参数或者在 HTTP header 发送，因此数据量小，传输速度快。
+ 严格的结构化。它自身（在 payload 中）就包含了所有与用户相关的验证消息，如用户可访问路由、访问有效期等信息，服务器无需再去连接数据库验证信息的有效性，并且 payload 支持为你的应用而定制化。
+ 支持跨域验证，可以应用于单点登录。

&emsp;

### JWT 组成
![text](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/concept_jwt_compose.jpg)
+ Header 头部  

头部包含了两部分：
1. 声明类型，这里是jwt
2. 声明加密的算法 通常直接使用 HMAC SHA256 
```
{
  "alg": "HS256",
  "typ": "JWT"
}
```
它会使用 Base64 编码组成 JWT 结构的第一部分,也就是说，它是可以被翻译回原来的样子来的。它并不是一种加密过程。如下图所示：  
![text](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/concept_jwt_header_decode.jpg)

&emsp;

+ Payload 有效载荷  

这部分就是我们存放信息的地方了，你可以把有效信息放在这里。这些有效信息分为三部分：  
1. 标准中注册的声明
2. 公共的声明
3. 私有的声明

&emsp;

**标准中注册的声明** (建议但不强制使用)：
+ iss: 该JWT的签发者，一般是服务器，是否使用是可选的；
+ iat(issued at): 在什么时候签发的(UNIX时间)，是否使用是可选的；
+ exp(expires): 什么时候过期，这里是一个Unix时间戳，是否使用是可选的；
+ aud: 接收该JWT的一方，是否使用是可选的；
+ sub: 该JWT所面向的用户，userid，是否使用是可选的；  
其他还有：
+ nbf (Not Before)：如果当前时间在nbf里的时间之前，则Token不被接受；一般都会留一些余地，比如几分钟；，是否使用是可选的；
+ jti: jwt的唯一身份标识，主要用来作为一次性token，从而回避重放攻击。

&emsp;

**公共的声明**：  
公共的声明可以添加任何的信息，一般添加用户的相关信息或其他业务需要的必要信息.但不建议添加敏感信息，因为该部分在客户端可解密。

&emsp;

**私有的声明**：  
私有声明是提供者和消费者所共同定义的声明，一般不建议存放敏感信息，因为base64是对称解密的，意味着该部分信息可以归类为明文信息。

```
// 包括需要传递的用户信息；
{ "iss": "Online JWT Builder", 
  "iat": 1416797419, 
  "exp": 1448333419, 
  "aud": "www.gusibi.com", 
  "sub": "uid", 
  "nickname": "goodspeed", 
  "username": "goodspeed", 
  "scopes": [ "admin", "user" ] 
}
```
同样的，它会使用 Base64 编码组成 JWT 结构的第二部分

&emsp;

+ Signature 签名  

前面两部分都是使用 Base64 进行编码的，即前端可以解开知道里面的信息。Signature 需要使用编码后的 header 和 payload 以及我们提供的一个密钥，然后使用 header 中指定的签名算法（HS256）进行签名。签名的作用是保证 JWT 没有被篡改过。

三个部分通过.连接在一起就是我们的 JWT 了，它可能长这个样子，长度貌似和你的加密算法和私钥有关系。

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU3ZmVmMTY0ZTU0YWY2NGZmYzUzZGJkNSIsInhzcmYiOiI0ZWE1YzUwOGE2NTY2ZTc2MjQwNTQzZjhmZWIwNmZkNDU3Nzc3YmUzOTU0OWM0MDE2NDM2YWZkYTY1ZDIzMzBlIiwiaWF0IjoxNDc2NDI3OTMzfQ.PA3QjeyZSUh7H0GfE0vJaKW4LjKJuC3dVLQiY4hii8s

其实到这一步可能就有人会想了，HTTP 请求总会带上 token，这样这个 token 传来传去占用不必要的带宽啊。如果你这么想了，那你可以去了解下 HTTP2，HTTP2 对头部进行了压缩，相信也解决了这个问题。

&emsp;

+ 签名的目的  

最后一步签名的过程，实际上是对头部以及有效载荷内容进行签名，防止内容被窜改。如果有人对头部以及有效载荷的内容解码之后进行修改，再进行编码，最后加上之前的签名组合形成新的 JWT 的话，那么服务器端会判断出新的头部和有效载荷形成的签名和 JWT 附带上的签名是不一样的。如果要对新的头部和有效载荷进行签名，在不知道服务器加密时用的密钥的话，得出来的签名也是不一样的。

&emsp;

+ 信息暴露

在这里大家一定会问一个问题：Base64 是一种编码，是可逆的，那么我的信息不就被暴露了吗？

是的。所以，在 JWT 中，不应该在有效载荷里面加入任何敏感的数据。在上面的例子中，我们传输的是用户的 User ID。这个值实际上不是什么敏感内容，一般情况下被知道也是安全的。但是像密码这样的内容就不能被放在 JWT 中了。如果将用户的密码放在了 JWT 中，那么怀有恶意的第三方通过 Base64 解码就能很快地知道你的密码了。

因此 JWT 适合用于向 Web 应用传递一些非敏感信息。JWT 还经常用于设计用户认证和授权系统，甚至实现 Web 应用的单点登录。

&emsp;

### JWT 使用
![text](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/concept_jwt_use.jpg)  
1. 首先，前端通过 Web 表单将自己的用户名和密码发送到后端的接口。这一过程一般是一个 HTTP POST 请求。建议的方式是通过 SSL 加密的传输（https 协议），从而避免敏感信息被嗅探。
2. 后端核对用户名和密码成功后，将用户的 id 等其他信息作为 JWT Payload（有效载荷），将其与头部分别进行 Base64 编码拼接后签名，形成一个 JWT。形成的 JWT 就是一个形同 lll.zzz.xxx 的字符串。
3. 后端将 JWT 字符串作为登录成功的返回结果返回给前端。前端可以将返回的结果保存在 localStorage 或 sessionStorage 上，退出登录时前端删除保存的 JWT 即可。
4. 前端在每次请求时将 JWT 放入 HTTP Header 中的 Authorization 位。(解决 XSS 和 XSRF 问题)
5. 后端检查是否存在，如存在验证 JWT 的有效性。例如，检查签名是否正确；检查 Token 是否过期；检查 Token 的接收方是否是自己（可选）。
6. 验证通过后后端使用 JWT 中包含的用户信息进行其他逻辑操作，返回相应结果。

&emsp;

## 和 Session 方式存储 id 的差异
Session 方式存储用户 id 的最大弊病在于 Session 是存储在服务器端的，所以需要占用大量服务器内存，对于较大型应用而言可能还要保存许多的状态。一般而言，大型应用还需要借助一些 KV 数据库和一系列缓存机制来实现 Session 的存储。

而 JWT 方式将用户状态分散到了客户端中，可以明显减轻服务端的内存压力。除了用户 id 之外，还可以存储其他的和用户相关的信息，例如该用户是否是管理员、用户所在的分组等。虽说 JWT 方式让服务器有一些计算压力（例如加密、编码和解码），但是这些压力相比磁盘存储而言可能就不算什么了。具体是否采用，需要在不同场景下用数据说话。

+ 单点登录

Session 方式来存储用户 id，一开始用户的 Session 只会存储在一台服务器上。对于有多个子域名的站点，每个子域名至少会对应一台不同的服务器，例如：www.taobao.com，nv.taobao.com，nz.taobao.com，login.taobao.com。所以如果要实现在login.taobao.com登录后，在其他的子域名下依然可以取到 Session，这要求我们在多台服务器上同步 Session。使用 JWT 的方式则没有这个问题的存在，因为用户的状态已经被传送到了客户端。

&emsp;

参考：  
#### [前后端分离之JWT用户认证](http://lion1ou.win/2017/01/18/)