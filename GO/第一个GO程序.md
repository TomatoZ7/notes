# 第一个 GO 程序

第一个程序肯定是 `hello world` 啦。

## 文件夹/文件创建

首先在 GOPATH/src 下创建 hello 目录。

在 hello 目录中创建 main.go 文件：

```go
package main     // 声明 main 包，表明当前是一个可执行文件

import "fmt"    // 导入内置 fmt 包

func main(){    // main 函数，是程序执行的入口
    fmt.Println("Hello World")  // 在终端打印 Hello World
}
```

## go build

`go build` 表示将源代码编译成可执行文件。

在 hello 目录下执行 

```go
go build
```

或者在父级目录下执行

```go
go build hello
```

还可以使用 `-o` 参数来指定编译后得到的可执行文件的名称。

```go
go build -o helloworld
```

> go build 出现：`go.mod file not found in current directory or any parent directory; see 'go help modules‘` 时，需要执行 `go mod init` 初始化项目。

## 执行

在 hello 目录下执行 hello.exe 即可在终端看到 `Hello World`。