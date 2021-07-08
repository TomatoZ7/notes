# Docker 常用命令

## 帮助命令

```bash
docker version      # 查看 docker 版本信息
docker info         # 显示 docker 的系统信息，包括镜像和容器数量等
docker 命令 --help  # 帮助命令
```

帮助文档地址：(https://docs.docker.com/reference/)[https://docs.docker.com/reference/]

## 镜像命令

> `docker images` 查看本地的主机上的镜像

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