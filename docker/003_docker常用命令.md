# Docker 常用命令

## 帮助命令

```bash
docker version      # 查看 docker 版本信息
docker info         # 显示 docker 的系统信息，包括镜像和容器数量等
docker 命令 --help  # 帮助命令
```

帮助文档地址：(https://docs.docker.com/reference/)[https://docs.docker.com/reference/]

## 镜像命令

### docker images

`docker images` 查看本地的主机上的镜像

```shell
[root@tz7 /]# docker images
REPOSITORY    TAG       IMAGE ID       CREATED        SIZE
hello-world   latest    d1165f221234   4 months ago   13.3kB
```

| 参数 | 描述 |
| :--------: | :--------: |
| REPOSITORY | 镜像的仓库源 |
| TAG | 镜像的标签 |
| IMAGE ID | 镜像的 id |
| CREATED | 镜像的创建时间 |
| SIZE | 镜像的大小 |

可选项：

| 可选项 | 描述 |
| :--------: | :--------: |
| -a，--all | 列出所有镜像 |
| -q，--quiet | 只显示镜像的 id |

### docker search

`docker search` 搜索镜像

```shell
[root@tz7 ~]# docker search mysql
NAME                              DESCRIPTION                                     STARS     OFFICIAL   AUTOMATED
mysql                             MySQL is a widely used, open-source relation…   11099     [OK]
```

| 可选项 | 描述 |
| :--------: | :--------: |
| --filter | 根据条件搜索 |

```shell
# 搜索条件：收藏数大于等于 4000
[root@tz7 ~]# docker search mysql --filter=STARS=4000
NAME      DESCRIPTION                                     STARS     OFFICIAL   AUTOMATED
mysql     MySQL is a widely used, open-source relation…   11099     [OK]       
mariadb   MariaDB Server is a high performing open sou…   4203      [OK]
```

### docker pull

`docker pull [Registry]/[Repository]/[Image][:Tag]` 下载镜像

| 可选项 | 描述 |
| :---: | :--: |
| Registry | 注册服务器，Docker 默认会从 docker.io 拉取镜像，如果你有自己的镜像仓库，可以把 Registry 替换为自己的注册服务器。 |
| Reponsitory | 镜像仓库，通常把一组相关联的镜像归为一个镜像仓库，library 为 Docker 默认的镜像仓库。 |
| Image | 镜像名称 |
| Tag | 版本号，默认为 latest |

```shell
[root@iZuf61wwjib0gi7cyckz02Z ~]# docker pull mysql
Using default tag: latest   # 如果不写 tag 版本，默认就是 latest
latest: Pulling from library/mysql
b4d181a07f80: Pull complete     # 分层下载，是 docker image 的核心，联合文件系统
a462b60610f5: Pull complete 
578fafb77ab8: Pull complete 
524046006037: Pull complete 
d0cbe54c8855: Pull complete 
aa18e05cc46d: Pull complete 
32ca814c833f: Pull complete 
9ecc8abdb7f5: Pull complete 
ad042b682e0f: Pull complete 
71d327c6bb78: Pull complete 
165d1d10a3fa: Pull complete 
2f40c47d0626: Pull complete 
Digest: sha256:52b8406e4c32b8cf0557f1b74517e14c5393aff5cf0384eff62d9e81f4985d4b # 签名
Status: Downloaded newer image for mysql:latest
docker.io/library/mysql:lates   # 真实地址
```

这里的 `docker pull mysql` 和 `docker pull docker.io/library/mysql:lates` 是等价的。

### docker rmi

`docker rmi 镜像ID [...镜像ID]` 删除镜像

```shell
[root@tz7 ~]# docker rmi 容器ID         # 删除一个或多个镜像
[root@tz7 ~]# docker rmi -f $(docker images -aq)    # 删除所有的镜像
```

### docker tag

`docker tag [SOURCE_IMAGE][:TAG] [TARGET_IMAGE][:TAG]` 重命名镜像

```shell
[root@tz7 ~]# docker tag busybox:latest mybusybox:latest         # 通过此命令我们会看到两个镜像的 IMAGE ID 完全一样，是因为它们指向了同一个镜像文件。
```

## 容器命令

说明 : 有了镜像才可以构建容器

### 新建容器并启动

`docker run [可选项] image`

| 可选项 | 描述 |
| :--------: | :--------: |
| --name | 容器名称，可以用来区分相同镜像构建不同容器 |
| -d | 后台方式运行 |
| -it | 使用交互方式运行，进入容器查看内容 |
| -p | 指定端口，有四种方式指定：<br/>`-p 主机ip:主机端口:容器端口`<br/>`-p 主机端口:容器端口`(常用)<br/>`-p 容器端口`<br/>`容器端口` |
| -P | 随机指定端口 |

#### 案例

```shell
# 启动并进入容器
[root@tz7 ~]# docker run -it centos /bin/bash
[root@f2565f205a2d /]# ls
bin  dev  etc  home  lib  lib64  lost+found  media  mnt  opt  proc  root  run  sbin  srv  sys  tmp  usr  var
[root@f2565f205a2d /]# exit
exit
[root@tz7 ~]# 
```


### 列出所有运行中的容器

`docker ps [可选项]`

| 可选项 | 描述 |
| :--------: | :--------: |
| -a | 当前运行的容器 + 历史运行过的容器 |
| -n=num | 最近创建的 num 个容器 |
| -q | 只显示容器的编号 |

### 退出容器

`exit` : 退出容器并停止运行

`Ctrl` + `P` + `Q` : 仅退出容器

### 删除容器

`docker rm 容器ID [...容器ID]` 删除容器

```shell
[root@tz7 ~]# docker rm 容器ID         # 删除一个或多个容器，在运行中的无法删除
[root@tz7 ~]# docker rm -f $(docker ps -aq)         # 删除所有容器
[root@tz7 ~]# docker ps -aq|xargs docker rm         # 删除所有容器
```

### 启动/停止容器

```shell
[root@tz7 ~]# docker start 容器ID
[root@tz7 ~]# docker restart 容器ID
[root@tz7 ~]# docker stop 容器ID
[root@tz7 ~]# docker kill 容器ID
```


## 其他命令

### 后台启动容器

`docker run -d 容器名`

> 这里有一个坑需要注意：当我们后台启动后 `docker ps` 发现容器停止了，这是因为 docker 发现容器没有提供服务，就会立刻停止。一般来说 docker 后台启动必须要有一个前台进程。

### 查看日志

`docker logs -f -t --tail 10 容器ID`

-tf : 显示日志

--tail number : 显示最后的日志行数

### 查看容器中进程信息

`docker top 容器ID`

### 查看容器元数据

`docker inspect 容器ID`

### 进入当前正在运行的容器

```bash
docker exec [OPTIONS] CONTAINER COMMAND [ARG...]
```

其中 `[OPTIONS]` 参数如下：

| 参数名 | 简写 | 后接数据类型 | 描述 |
| :---: | :-: | :--------: | :-: |
| --detach | -d | / | 分离模式：后台运行 |
| --detach-keys | / | string | 重写分离容器的键序列 |
| --env | -e | list | 设置环境变量 |
| --env-file | / | list | 通过一个文件设置环境变量 |
| --interactive | -i | 即使没有了解，也保持 STDIN 开启 |
| --privileged | / | / | 赋予命令扩展特权 |
| --tty | -t | / | 分配一个伪 TTY |
| --user | -u | string | docker 用户名或 ID |
| --workdir | -w | string | 指定容器内的工作目录 |

#### 注意

+ 执行的 COMMAND 只会在容器默认的工作目录运行，如果基础镜像中有一个使用了 `WORKDIR` 指定自定义工作目录，则会在该目录下执行 COMMAND。

+ COMMAND 必须是一个可执行的命令，使用链接和引号命令将不会被执行。举个例子：

```bash
docker exec -it my_container "echo a && echo b"   # 这个命令不会执行
docker exec -ti my_container sh -c "echo a && echo b"   # 这个命令执行
```

`docker attach 容器ID` 进入容器正在执行的终端，不会启动新的进程。

### 从容器内拷贝文件到主机上

首先要进入 docker 容器内部

```bash
docker cp 容器ID:容器内路径 主机路径
```

## docker 命令小结

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker8.png)