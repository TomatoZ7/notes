# docker 安装

## docker 架构

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker3.png)

**镜像 image**

docker 镜像就好比是一个模板，可以通过这个模板来创建容器服务。比如 `Nginx 镜像 ==> run ==> Nginx 容器`，一个镜像可以创建多个容器，最终项目/服务就是在容器中运行。

**容器 container**

Docker 利用容器技术，独立运行一个或一个组应用，通过镜像来创建。

目前可以把容器理解为一个简易的 linux 系统。

**仓库 repository**

存放镜像的地方，分为共有仓库和私有仓库。如 Docker Hub，阿里云等。

## 安装

### 1、卸载旧版本

```bash
sudo yum remove docker \
                docker-client \
                docker-client-latest \
                docker-common \
                docker-latest \
                docker-latest-logrotate \
                docker-logrotate \
                docker-engine
```

### 2、需要的安装包

```bash
sudo yum install -y yum-utils
```

### 3、安装镜像的仓库

```bash
sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo

# 使用阿里云镜像
sudo yum-config-manager \
    --add-repo \
    http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

### 4、更新 yum 软件包索引(可选)

```bash
yum makecache fast
```

### 5、安装 docker

```bash
# docker-ce : 社区     ee : 企业版
sudo yum install docker-ce docker-ce-cli containerd.io
```

### 6、启动 docker

```bash
sudo systemctl start docker

# 查看 docker 版本
docker version
```

### 7、hello world

```bash
$ docker run hello-world
Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
```

### 8、查看 pull 下来的 hello-word 镜像

```bash
docker images
```

### 9、卸载 docker

```bash
# 卸载依赖
sudo yum remove docker-ce docker-ce-cli containerd.io

# 删除资源
sudo rm -rf /var/lib/docker
sudo rm -rf /var/lib/containerd
```