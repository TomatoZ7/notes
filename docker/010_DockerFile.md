# DockerFile

## 介绍

DockerFile 是用来构建镜像的文件，由命令参数脚本组成。

构建步骤：

1. 编写一个 dockerfile 文件

2. docker build 构建成为一个镜像

3. docker run 运行镜像

4. docker push 发布镜像(Docker Hub、阿里云镜像仓库)

查看 [centos](https://hub.docker.com/_/centos) 官方 dockerfile：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/dockerfile2.png)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/dockerfile3.png)

## DockerFile 构建规范

1、每个保留关键字(命令)都大写

2、执行顺序从上到下

3、`#` 代表注释

4、每一个指令都会创建提交一个镜像层

## DockerFile 常用指令

```dockerfile
FROM            # 基础镜像，一切从这里开始构建
MAINTAINER      # 作者，姓名+邮箱
RUN             # 镜像构建的时候需要运行的命令
ADD             # 添加内容
WORKDIR         # 镜像的工作目录
VOLUME          # 挂载的目录
EXPOSE          # 暴露端口配置
CMD             # 指定容器启动的时候要运行的命令(只有最后一个会生效，可被替代)
ENTRYPOINT      # 指定容器启动的时候要运行的命令(可以追加命令)
ONBUILD         # 当构建一个被继承 DockerFile 时就回运行此指令
COPY            # 类似ADD，将我们文件拷贝到镜像中
ENV             # 构建的时候设置环境变量
```

## 案例

### 自己构建一个 centos

1、编写 dockerfile：

```dockerfile
FROM centos

MAINTAINER tz7<tz@qq.com>

ENV MYPATH /usr/local
WORKDIR $MYPATH

RUN yum -y install vim
RUN yum -y install net-tools

EXPOSE 80

CMD echo $MYPATH
CMD echo "----end----"
CMD /bin/bash
```

2、通过 dockerfile 构建镜像

```bash
[root@tz7 dockerfile]# docker build -f mycentos_dockerfile -t mycentos .
...
Successfully built 96995718343f
Successfully tagged mycentos:latest
```

3、运行测试

```bash
[root@tz7 dockerfile]# docker images
REPOSITORY   TAG       IMAGE ID       CREATED              SIZE
mycentos     latest    96995718343f   About a minute ago   287MB
[root@tz7 dockerfile]# docker run -it mycentos
[root@405eacab4432 local]# pwd
/usr/local
```

相比于原来的镜像，我们添加了工作目录和一些指令如 `pwd`、`ifconfig` 等。

4、查看构建操作记录

`docker history imageName|imageID`

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/dockerfile4.png)


## CMD 和 ENTRYPOINT 区别

### 测试 CMD

```bash
# 1、编写镜像文件
[root@tz7 dockerfile]# cat df-cmd-test 
FROM centos
CMD ["ls","-a"]

# 2、构建镜像
[root@tz7 dockerfile]# docker build -f df-cmd-test -t cmd-test .

# 3、运行容器，发现 ls -a 命令生效
[root@tz7 dockerfile]# docker run 2a41a90c7ea5
.
..
.dockerenv
bin
dev
etc
home
lib
lib64
lost+found
media
mnt
opt
proc
root
run
sbin
srv
sys
tmp
usr
var

# 4、尝试追加命令 -l 使其变为 ls -al
[root@tz7 dockerfile]# docker run 2a41a90c7ea5 -l
docker: Error response from daemon: OCI runtime create failed: container_linux.go:380: starting container process caused: exec: "-l": executable file not found in $PATH: unknown.

# CMD 的情况下，-l 替换了 CMD ["ls","-a"]，而 -l 不是命令所以报错
# 正确写法：
docker run 2a41a90c7ea5 ls -la
```

### 测试 ENTRYPOINT 

```bash
# 1、编写镜像文件
[root@tz7 dockerfile]# cat df-entrypoint-test 
FROM centos
ENTRYPOINT ["ls","-a"]

# 2、构建镜像
[root@tz7 dockerfile]# docker build -f df-entrypoint-test  -t entrypoint-test .

# 3、运行容器，发现 ls -a 命令生效
[root@tz7 dockerfile]# docker run 22e17d3cf2bc
.
..
.dockerenv
bin
dev
etc
home
lib
lib64
lost+found
media
mnt
opt
proc
root
run
sbin
srv
sys
tmp
usr
var

# 4、尝试追加命令 -l 使其变为 ls -al
[root@tz7 dockerfile]# docker run 2a41a90c7ea5 -l
total 56
drwxr-xr-x   1 root root 4096 Jul 11 13:44 .
drwxr-xr-x   1 root root 4096 Jul 11 13:44 ..
-rwxr-xr-x   1 root root    0 Jul 11 13:44 .dockerenv
lrwxrwxrwx   1 root root    7 Nov  3  2020 bin -> usr/bin
drwxr-xr-x   5 root root  340 Jul 11 13:44 dev
drwxr-xr-x   1 root root 4096 Jul 11 13:44 etc
drwxr-xr-x   2 root root 4096 Nov  3  2020 home
lrwxrwxrwx   1 root root    7 Nov  3  2020 lib -> usr/lib
lrwxrwxrwx   1 root root    9 Nov  3  2020 lib64 -> usr/lib64
drwx------   2 root root 4096 Dec  4  2020 lost+found
drwxr-xr-x   2 root root 4096 Nov  3  2020 media
drwxr-xr-x   2 root root 4096 Nov  3  2020 mnt
drwxr-xr-x   2 root root 4096 Nov  3  2020 opt
dr-xr-xr-x 108 root root    0 Jul 11 13:44 proc
dr-xr-x---   2 root root 4096 Dec  4  2020 root
drwxr-xr-x  11 root root 4096 Dec  4  2020 run
lrwxrwxrwx   1 root root    8 Nov  3  2020 sbin -> usr/sbin
drwxr-xr-x   2 root root 4096 Nov  3  2020 srv
dr-xr-xr-x  13 root root    0 Jul 11 13:44 sys
drwxrwxrwt   7 root root 4096 Dec  4  2020 tmp
drwxr-xr-x  12 root root 4096 Dec  4  2020 usr
drwxr-xr-x  20 root root 4096 Dec  4  2020 var
```