# go get 失败解决方法

`go get` 时由于防火墙的原因，会导致失败。目前可以通过修改 `GOPROXY` 的方法解决该问题。

只需要将环境变量 `GOPROXY` 设置成 `https://goproxy.cn` 即可。

方法一：

```sh
go env -w GOPROXY=https://goproxy.cn
```

方法二（MacOS 环境）：

```sh
vi ~/.bash_profile

```