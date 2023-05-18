# 静态代码检查利器：golangci-lint

- [静态代码检查利器：golangci-lint](#静态代码检查利器golangci-lint)
  - [1.静态代码检查](#1静态代码检查)
  - [2.为什么选择 golangci-lint？](#2为什么选择-golangci-lint)
  - [3 下载](#3-下载)
  - [4 命令和选项](#4-命令和选项)
    - [4.1 run](#41-run)
    - [4.2 cache](#42-cache)
    - [4.3 completion](#43-completion)
    - [4.4 config 命令](#44-config-命令)
    - [4.5 linters](#45-linters)
  - [5 配置](#5-配置)
    - [5.1 命令行选项](#51-命令行选项)
    - [5.2 配置文件](#52-配置文件)
  - [6.运行](#6运行)
    - [6.1 nolint](#61-nolint)
  - [7 使用技巧](#7-使用技巧)
    - [7.1 第一次使用](#71-第一次使用)
    - [7.2 按文件修改](#72-按文件修改)
    - [7.3 调大 linters-setting.lll.line-length](#73-调大-linters-settinglllline-length)
    - [7.4 尽可能使用 golangci-lint 提供的 linter](#74-尽可能使用-golangci-lint-提供的-linter)
    - [7.5 每次修改完代码后，都使用 golangci-lint 检查一遍](#75-每次修改完代码后都使用-golangci-lint-检查一遍)
    - [7.6 在根目录存放一份通用的 golangci-lint 配置](#76-在根目录存放一份通用的-golangci-lint-配置)
  - [8.实践](#8实践)
    - [8.1 项目目录](#81-项目目录)
    - [8.2 源码](#82-源码)
    - [8.3 检查](#83-检查)
  - [9.参考](#9参考)

## 1.静态代码检查

静态代码检查是一个老生常谈的问题，它通过对源代码进行分析，找出其中的潜在问题和错误，以提高代码质量和可维护性。

Go 语言的静态代码检查工具较多，常见的有：

1. go vet：go 自带的一个静态代码检查工具，可以检测代码中常见的错误和潜在问题。
2. golangci-lint：一个基于 go vet 和 golint 等的集成工具，支持对项目进行全面的静态代码检查。
3. golint：golang 官方提供的代码风格检查工具，可以检查代码是否符合官方规范。
4. staticcheck：一个快速、精确的静态代码分析工具，可以检测更加复杂的错误和潜在问题。
5. revive：另一个轻量级的静态代码检查工具，可以检测代码中的错误和不良习惯，并提供修复建议。
6. ...

以上谈到的工具，我们可以称之为 `linter`。在维基百科是如下定义 lint 的：

> 在计算机科学中，lint 是一种工具程序的名称，它用来标记源代码中，某些可疑的、不具结构性（可能造成 bug）的段落。它是一种静态程序分析工具，最早适用于 C 语言，在 UNIX 平台上开发出来。后来它成为通用术语，可用于描述在任何一种计算机程序语言中，用来标记源代码中有疑义段落的工具。

其中 golangci-lint 是比较受欢迎的，使用人数也比较多的静态代码检查工具。golangci-lint 集成了非常多的 linter，包括上文提到的 govet，revive 等。

接下来，我们就来聊聊 golangci-lint。

## 2.为什么选择 golangci-lint？

golangci-lint 相比其他的静态代码检查工具，我觉得显著的优点有：

+ **速度快：**golangci-lint 是基于 gometalinter 开发的，但是平均速度要比 gometalinter 快 5 倍。速度快的原因有三个：
  + 可以并行执行 linter 检查代码；
  + 可以复用 go build 缓存；
  + 会缓存分析结果。
+ **可配置：**可以基于 yaml 编写配置文件，更灵活可控。
+ **可集成：**能够集成主流的 IDE，如 VS Code, Sublime Text, GoLand, GNU Emacs, Vim 等。
+ **linter 集大成者：**以 v1.52.2 的官方文档来看，已经聚合了 100+ 个 linter，并且不需要去额外安装他们。
+ **最小的误报数：**得益于 golangci-lint 调整了所聚合的 linter 的默认值。
+ **输出美观：**可携带颜色、源码行号、linter 标识，方便定位。

除此之外，golangci-lint 还在保持更新迭代，还在不断地更新 linter。有这么全的 linter 为你的代码保驾护航，你在提交代码时肯定会多一分从容与自信。

目前，有很多公司 / 项目使用了 golangci-lint 工具作为静态代码检查工具，例如 Google、Facebook、Istio、Red Hat OpenShift 等。

## 3 下载

[官方文档](https://golangci-lint.run/usage/install/)提供了多种下载方式，我们这里使用官方提供 `curl` 命令来进行下载：

```sh
$ curl -sSfL https://raw.githubusercontent.com/golangci/golangci-lint/master/install.sh | sh -s -- -b $(go env GOPATH)/bin 
golangci/golangci-lint info checking GitHub for latest tag
golangci/golangci-lint info found version: 1.52.2 for v1.52.2/darwin/arm64
golangci/golangci-lint info installed ${YOUR_GOPATH}/bin/golangci-lint
```

如果条件允许的话，可以定期更新 golangci-lint 的版本，毕竟它仍在积极地更新。

之后，我们可以通过 `golangci-lint version` 来检查是否安装成功：

```sh
$ golangci-lint version
golangci-lint has version 1.52.2 built with go1.20.2 from da04413a on 2023-03-25T18:11:28Z
```

> 似乎新版本使用 `go get` 命令不能将 `golangci-lint` 可执行程序安装到你的 `GOPATH`。

我们还可以查看默认生效或不生效的 linter 以及 linter 的分类：

```sh
$ golangci-lint help linters
```

## 4 命令和选项

我们可以通过执行 `golangci-lint -h` 查看其用法，golangci-lint 支持的**子命令**如下：

| 子命令 | 功能 |
| :--: | :--: |
| cache | 缓存控制并打印缓存的信息 |
| completion | 生成 bash/fish/powershell/zsh 等自动补全脚本 |
| config | 打印 golangci-lint 当前使用的配置文件路径 |
| help | 打印 golangci-lint 的帮助信息 |
| linters | 打印 golangci-lint 支持的 linter，并按启用/禁用分类 |
| run | 执行 golangci-lint 对代码进行检查 |
| version | 查看 golangci-lint 版本号 |

此外，golangci-lint 还支持一些**全局选项**。全局选项是指适用于所有子命令的选项，golangci-lint 支持的全局选项如下：

| 选项 | 功能 |
| :--: | :--: |
| --color | 是否带颜色打印，有 3 个值：`always`、`auto`（默认值）、`never` | 
| -j, --concurrency | 控制并发数（默认 NumCPU/10） |
| --cpu-profile-path | 记录 CPU 性能数据到指定文件 |
| -h, --help | 输出 golangci-lint 的帮助信息 |
| --mem-profile-path | 记录内存性能数据到指定文件 |
| --trace-path | 跟踪文件路径 |
| -v, --verbose | 生成更多信息 |
| --version | 版本号 |

接下来，我们介绍一下 golangci-lint 的核心子命令：run、cache、completion、config、linters。

### 4.1 run

`run` 命令执行 golangci-lint，对代码进行检查，是 golangci-lint 最为核心的一个命令。`run` 没有子命令，

### 4.2 cache

`cache` 命令用于缓存控制并打印缓存的信息，它有两个子命令：

+ `clean`：清除 cache，常用于缓存出错或缓存内容过大时；
+ `status`：打印 cache 相关的一些信息，如缓存目录和大小。

```sh
$ golangci-lint cache status
Dir: /Users/xxx/Library/Caches/golangci-lint
Size: 64B
```

### 4.3 completion

completion 命令包含 4 个子命令 bash、fish、powershell 和 zsh，分别用来输出 bash、fish、powershell 和 zsh 的自动补全脚本。

下面是一个配置在 macOS 的 bash 自动补全的示例：

```sh
echo 'source <(golangci-lint completion bash)' >>~/.bashrc
source ~/.bashrc
```

执行完上面的命令，键入如下命令，即可自动补全子命令：

```sh
$ golangci-lint comp<TAB>
```

### 4.4 config 命令

`config` 只有一个子命令 `path`，用于打印当前使用的配置文件路径：

```sh
$ golangci-lint config path
```

### 4.5 linters

`linter` 打印当前 golangci-lint 所支持的所有 linter，并分为启用、禁用两大类。

```sh
$ golangci-lint linters
Enabled by your configuration linters:
...

Disabled by your configuration linters:
...
```

## 5 配置

golangci-lint 支持两种配置方式，分别是命令行选项和配置文件。如果在命令行和配置文件中使用了相同的 bool/string/int 类型的选项，那么会优先使用命令行选项，而如果是 slice 类型的选项，命令行和配置文件中的配置会合并。

### 5.1 命令行选项

golangci-lint run 支持很多[**命令行选项**](https://golangci-lint.run/usage/configuration/#command-line-options)，可以通过 `golangci-lint run -h` 查看，下面是我们列举的一些常用的选项：

| 选项 | 功能 |
| :--: | :--: |
| --print-issued-lines | 打印检查失败代码所在行号，默认显示 |
| --print-linter-name | 打印是由哪个 linter 检查失败的，默认显示 |
| --timeout duration | 设置检查超时时间，默认 1 分钟 |
| --tests | 是否检查 `*_test.go` 文件，默认检查 |
| -c, --config PATH | 指定配置文件路径，支持 `.golangci.yml`、`.golangci.yaml`、`.golangci.toml`、`.golangci.json` 文件，并且会向上级目录查找直到根目录。 |
| --no-config | 不读取配置文件 |
| --skip-dirs strings | 设置需要忽略的文件夹，支持正则表达式。多个目录/正则表达式用逗号隔开。 |
| --skip-dirs-use-default | 是否使用预设规则排除目录，默认 true |
| --skip-files strings | 置需要忽略的文件，支持正则表达式。多个文件/正则表达式用逗号隔开。 |
| -E, --enable strings | 启用指定的 linter |
| -D, --disable strings | 禁用指定的 linter |
| --disable-all | 禁用所有 linter |
| --fast | 只运行所有启用的 linter 中支持快速检查的 linter。由于第一次运行需要缓存类型信息，所以第一次运行不会快。 |
| -e, --exclude strings | 设置需要排除的检查错误，支持正则。 |
| --exclude-use-default | 忽略预设的检查错误，默认为 true。 |
| --exclude-case-sensitive | 设置 exclude 规则时是否大小写敏感 |
| --max-issues-per-linter int | 设置每个 linter 报告的最大错误数 |
| --fix | 如果 linter 支持修复功能，则修复错误。 |

### 5.2 配置文件

我们还可以通过**配置文件**进行配置。配置文件支持以下几种文件名：

+ .golangci.yml
+ .golangci.yaml
+ .golangci.toml
+ .golangci.json

也可以通过 `-c` 或 `--config` 指定配置文件。一般来说，配置文件主要用来实现以下功能：

1. golangci-lint 检查选项，如超时时间、并发数、是否检查 `*_test.go` 文件等；
2. 输出设置，如是否带颜色输出；
3. 配置需要忽略的文件或文件夹；
4. 启用/禁用指定 linter；
5. 根据特定的 linter 进行单独配置；
6. 设置错误级别。

更详细的配置，可以参阅[官网](https://golangci-lint.run/usage/configuration/#config-file)。

## 6.运行

```sh
# 1. 对当前目录以及子目录下所有 Go 文件进行静态代码检查
$ golangci-lint run
# 该命令等效于
$ golangci-lint run ./...

# 2. 对指定的 Go 文件或目录进行检查
# 该命令不会检查 dir1 下的子目录
$ golangci-lint run dir1 dir2/... dir3/file1.go

# 3. 指定配置文件
$ golangci-lint run -c .golangci.yaml
# 不指定配置文件进行读取
$ golangci-lint run --no-config

# 4. 指定一个或多个 linter 进行检查：使用 --no-config 是为了防止意外读取到配置文件
$ golangci-lint run --no-config --disable-all -E errcheck ./...

# 5. 禁用一个或多个 linter
$ golangci-lint run --no-config -D errcheck gosimple
```

除了上面常用的 5 条指令，在实际应用过程中，我们可能会发现存在误报的情况，而这些误报往往是我们希望 golangci-lint 能够容忍或排除的 issue，那么如何尽可能地减少误报呢？可以从 3 个方面入手：

+ 设定正则匹配检查错误：使用命令行选项 `-e`/`--exclude`，或者使用配置文件配置项 `exclude`/`exclude-rules` 来设置要排除的检查错误。具体例子可以参阅官方提供的 [issues configuration](https://golangci-lint.run/usage/configuration/#issues-configuration) 示例。
+ 忽略检查的文件夹或文件：使用配置文件配置项 `run.skip-dirs`/`run.skip-files`/`issues.exclude-rules` 来忽略文件夹或文件。
+ 通过在 Go 源码中添加 `//nolint` 注释，来忽略指定的代码行。

### 6.1 nolint

在源码中注释 `nolint` 可以告诉 golangci-lint 不要对该行或该文件执行检查。当然，滥用 `nolint` 可能会掩盖真正的问题并导致代码质量下降，应该谨慎使用。下面我们来看看 `nolint` 的使用。

1. 忽略当前行所有 linter 检查

```go
var bad_name int //nolint:all
```

2. 指定一个或多个 linter 不检查当前行

```go
var bad_name int //nolint:golint,unused
```

3. 忽略代码块的检查

```go
//nolint:all
func allIssuesInThisFunctionAreExcluded() *string {
  // ...
}

//nolint:govet
var (
  a int
  b int
)
```

4. 忽略文件的检查

```go
//nolint:unparam
package pkg
```

此外，使用 `nolint` 时还有几个注意事项：

+ 推荐使用 `nolint` 时在同一行内添加使用 `nolint` 的原因：

```go
//nolint:gocyclo // This legacy function is complex but the team too busy to simplify it
func someLegacyFunction() *string {
  // ...
}
```

+ 注意是 `//nolint` 而不是 `// nolint`，因为根据 Go 的规范，机器可读的注释后面应该没有空格。

## 7 使用技巧

### 7.1 第一次使用

如果你第一次在项目中使用 golangci-lint，难免会有很多错误，为了减轻你的心智负担，你可以先按目录来进行修改，或者按[官方文档](https://golangci-lint.run/usage/faq/#how-to-integrate-golangci-lint-into-large-project-with-thousands-of-issues)推荐的，使用命令行选项 `--new-from-rev=HEAD~1` 只检查新的代码。

```sh
$ golangci-lint run --new-from-rev=HEAD~1
```

### 7.2 按文件修改

如果有很多错误，涉及很多文件，建议使用 `grep` 来逐一修改，减去来回切换文件的操作：

```sh
$ golangci-lint run ./... | grep pkg/log/log.go
```

### 7.3 调大 linters-setting.lll.line-length

在实际开发中，为了减少维护人员阅读代码的心智负担，我们往往会将 变量/常量/函数 名尽量取得有意义，这就难免会导致它们的长度偏长，很容易超过 lll linter 设置的默认最大长度 80。这里建议将 `linters-setting.lll.line-length` 设置为 120/240。

### 7.4 尽可能使用 golangci-lint 提供的 linter

我们可以使用 `golangci-lint linters` 查看支持的 linter，其中每个 linter 后面都会跟上一个属性表 `[fast: bool, auto-fix: bool]`：

+ `fast`：如果为 true，说明该 linter 可以缓存类型信息，支持快速检查。
+ `auto-fix`：如果为 true，说明该 linter 支持自动修复发现的错误。

如果使用了配置文件，那么可以使用 `golangci-lint linters -c .golangci-lint.yaml` 查看。

当然也支持自定义 linter，具体可查看[官方文档](https://golangci-lint.run/contributing/new-linters/)。

推荐我们在使用 golangci-lint 时，尽可能多的使用 linter，这样的话检查更严格，代码质量也更高。

### 7.5 每次修改完代码后，都使用 golangci-lint 检查一遍

一方面可以几时修改代码中的不规范或错误，另一方面也可以防止错误堆积，加重开发/维护人员的心智负担。

### 7.6 在根目录存放一份通用的 golangci-lint 配置

鉴于 golangci-lint 使用 `-c`/`--config` 运行时会从当前目录搜索配置文件直到根目录，这样我们可以考虑在根目录存放一份通用的配置。这样你可以不用每个项目都单独维护一份 golangci-lint 配置文件，而当你需要单独维护时只需要在项目目录下增加一个项目级别的 golangci-lint 配置文件即可。

## 8.实践

说了这么多，最后我们再实践一下。

### 8.1 项目目录

```sh
$ tree .                 
.
├── check
│   └── check.go
├── go.mod
└── main.go

2 directories, 3 files
```

### 8.2 源码

`main.go`：

```go
package main

import "fmt"

func main() {
	str := "a string"
	i := 1
	if i <= 1 {
		fmt.Printf("inappropriate formate %s\n", &str)
	}

	arr := []int{1, 2, 3}
	for _, i := range arr {
		go func() {
			fmt.Println(i)
		}()
	}
}
```

`check.go`：

```go
package main

import "fmt"

func main() {
	str := "a string"
	i := 1
	if i <= 1 {
		fmt.Printf("inappropriate formate %s\n", &str)
	}

	arr := []int{1, 2, 3}
	for _, i := range arr {
		go func() {
			fmt.Println(i)
		}()
	}
}
```

### 8.3 检查

```sh
$ golangci-lint run
check/check.go:11:9: S1028: should use fmt.Errorf(...) instead of errors.New(fmt.Sprintf(...)) (gosimple)
        err := errors.New(fmt.Sprintf("%s", val))
               ^
main.go:15:16: loopclosure: loop variable i captured by func literal (govet)
                        fmt.Println(i)
                                    ^
main.go:9:3: printf: fmt.Printf format %s has arg &str of wrong type *string (govet)
                fmt.Printf("inappropriate formate %s\n", &str)
                ^
```

## 9.参考

+ [官方文档](https://golangci-lint.run/)

> 我也尝试快速翻译了官方文档的 Usage 章节，如果您觉得阅读英文文档不太方便，也可以看看我翻译的部分：[传送门](https://github.com/TomatoZ7/golangci-lint-docs-zh)