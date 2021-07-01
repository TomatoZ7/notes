# linux 编译安装 git

## 1、下载源码 tar 包

[官网](https://github.com/git/git/releases) 下载 tar 包然后丢到服务器上。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/linux_ins_git1.png)

## 2、解压

```bash
sudo tar -zxvf git-2.32.0.tar.gz
```

## 3、进入解压后的文件夹

```bash
(sudo) cd git-2.32.0
```

## 4、拿到解压后的源码以后我们需要编译源码了，不过在此之前需要安装编译所需要的依赖

```bash
(sudo) yum install curl-devel expat-devel gettext-devel openssl-devel zlib-devel gcc perl-ExtUtils-MakeMaker
```

## 5、编译 git 源码

```bash
(sudo) make prefix=/usr/local/git all
```

## 6、安装 git

```bash
(sudo) make prefix=/usr/local/git install
```

## 7、配置环境变量

```bash
vi /etc/profile
```

在底部加上

```
export PATH=$PATH:/usr/local/git/bin
```

## 8、刷新环境变量

```bash
source /etc/profile
```

## 9、查看 git 是否安装完成

```bash
git --version
```

## 10、配置 git

```bash
git config --global user.name "xxxxx@gmail.com"
git config --global user.email "xxxxx"

ssh-keygen -t rsa -C "xxxxx@gmail.com"
cat ~/.ssh/id_rsa.pub

# 登录 github，把密钥写入即可

ssh -T git@github.com
```