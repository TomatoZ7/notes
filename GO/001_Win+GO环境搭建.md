# 搭建 GO 开发环境(windows)

## 安装 GO 开发包

下载地址：[https://golang.org/dl/](https://golang.org/dl/)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/go_install1.jpg)

下载好后双击运行，一路 next 直到 install 即可。(可根据个人情况修改安装路径，建议安装到比较好找的路径)

## 安装成功验证

安装完成后点击 finish。

`win + r` 输入 `cmd` 打开小黑窗，输入 `go version`，出现响应的版本信息即安装成功。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/go_install2.png)

## 配置GOPATH

### 配置 GOPATH 环境变量

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/go_install1.png)

### GOPATH 下新建三个目录

在 GOPATH 下新建三个目录分别是：bin、pkg、src。

并将 bin 目录添加到 path 末尾

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/go_install3.png)
