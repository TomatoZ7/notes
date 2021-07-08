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

`docker pull 镜像名[:tag]` 下载镜像

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

`docker pull 镜像ID [...镜像ID]` 删除镜像

```shell
[root@iZuf61wwjib0gi7cyckz02Z ~]# docker rmi 容器ID         # 删除一个或多个镜像
[root@iZuf61wwjib0gi7cyckz02Z ~]# docker rmi -f $(docker images -aq)    # 删除所有的镜像
```