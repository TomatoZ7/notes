# Protobuf 认识与使用

- [Protobuf 认识与使用](#protobuf-认识与使用)
  - [1.Protobuf 简介](#1protobuf-简介)
  - [2.优劣势](#2优劣势)
  - [3.安装](#3安装)
    - [3.1 MacOS](#31-macos)
      - [3.1.1 brew 安装](#311-brew-安装)
      - [3.1.2 下载安装](#312-下载安装)

## 1.Protobuf 简介

Protobuf 是 Protobuf Buffers 的简称，它是 Google 公司开发的一种数据描述语言，是一种轻便高效的结构化数据存储格式，可以用于结构化数据，或者说序列化。它很适合做数据存储或 RPC 数据交换。可用于通讯、数据存储等领域的语言无关、平台无关、可扩展的序列化结构数据格式。他是一种灵活、高效、自动化的机制，用于序列化结构化数据，对比 XML 和 JSON，他更小、更快、更简单。总之他是微服务中需要使用的东西。

Protobuf 刚开源时的定位类似于 XML、JSON 等数据描述语言，通过附带工具生成代码并实现结构化数据序列化的功能。这里我们更关注的是作为 Protobuf 作为接口规范的描述语言，可以作为设计安全的跨语言 RPC 接口的基础。

需要了解两点：

1. protobuf 是类似与 json 一样的数据描述语言。
2. protobuf 非常适合于 RPC 数据交换格式。

## 2.优劣势

**优势：**

1. 序列化后体积相比 json 和 xml 小，适合网络传输；
2. 支持跨平台多语言；
3. 消息格式升级和兼容性很好；
4. 序列化反序列化速度很快，快于 json 的处理速度。

**劣势：**

1. 相比 xml 和 json，应用不够广；
2. 二进制格式导致可读性差；
3. 缺乏自描述；

## 3.安装

### 3.1 MacOS

#### 3.1.1 brew 安装

```sh
brew install protobuf
```

#### 3.1.2 下载安装

1. 下载地址：[https://github.com/protocolbuffers/protobuf/releases](https://github.com/protocolbuffers/protobuf/releases)；
2. 选择 `protobuf-all-xxx.tar.gz` 包，`xxx` 代表最新的版本号，点击下载；
3. 把下载下来的文件夹移动到你的管理目录；
4. 双击解压；
5. 打开终端并 `vim ~/.bash_profile` 后面追加环境变量：

```sh
export PROTOBUF=你的管理目录/protobuf
export PATH=$PROTOBUF/bin:$PATH
```

6. 终端 `source ~/.bash_profile` 生效；（到这一步其实就可以了）
7. 终端 `cd 管理目录/protobuf`；
8. 终端 `sudo ./configure`，若执行报错自动安装某文件，点击安装，安装完成后重新执行此命令。；
9. 终端 `sudo make check`；
10. 终端 `sudo make install`；
11. 终端 `protoc --version` 检查是否安装完成;或者 `protoc --version`。