# Golang 包管理工具 go mod

在 Golang 1.11 版本之前如果我们要自定义包的话必须把项目放在 GOPATH 目录。Go 1.11 版本后无需手动配置环境变量，使用 `go mod` 管理项目，也不需要非得把项目放到 GOPATH 指定目录下，你可以在你磁盘的任何位置新建一个项目，Go 1.13 以后可以彻底不要 GOPATH 了。

## 1.go mod init 初始化项目

实际项目开发中我们首先要在我们项目目录中用 go mod 命令生成一个 go.mod 文件，管理我们项目的依赖。

```sh
go mod init [项目名]
```

## 2.Golang 中自定义包

包（package）是多个 Go 源码的集合，一个包可以简单理解为一个存放多个 `.go` 文件的文件夹。在该文件夹下所有 go 文件都要在代码头部添加如下代码，声明该文件归属的包：

```go
package [包名]
```

注意：

+ 一个文件架下面直接包含的文件只能归属一个 package，同样一个 package 的文件不能在多个文件夹下。
+ 包名可以不和文件夹的名字一样，包名不能包含 - 符号
+ 包名为 main 的包为应用程序的入口包，这种包编译后会得到一个可执行文件，而编译不包含 main 包的源代码则不会得到可执行文件。

## 3.Golang 中第三方包

第三方包的管理需要 `go mod` 命令，参数如下：

| 参数 | 描述 |
| :-: | :-: |
| download | download modules to local cache（下载依赖的 module 到本地 cache） |
| edit | edit go.mod from tools or scripts（编辑 go.mod 文件） |
| graph | print module requirement graph（打印模块依赖图） |
| init | initialize new module in current directory（在当前文件夹下初始化一个新的 module，创建 go.mod 文件） |
| tidy | add missing and remove unused modules（增加丢失的 module，去掉未用的 module） |
| vendor | make vendored copy of dependencies（将依赖复制到 vendor 下） |
| verify | verify dependencies have expected content（校验依赖，检查下载的第三方库有没有本地修改，如果有修改，则会返回非 0，否则验证成功） |
| why  | explain why packages or modules are needed（解释为什么需要依赖） |

### 3.1 安装方法

1. `go get [module name]`
2. `go mod download` 

依赖包会自动下载到 $GOPATH/pkg/mod，多个项目可以共享缓存的 mod，注意使用 go mod download 的时候首先需要在你的项目里引入第三方包。

3. `go mod vendor`：将依赖复制到当前项目的 vendor 下。