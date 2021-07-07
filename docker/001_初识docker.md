# 初识 docker

官网：[https://www.docker.com/](https://www.docker.com/)

官方文档：[https://docs.docker.com/](https://docs.docker.com/)

仓库：[https://hub.docker.com/](https://hub.docker.com/)

## What can docker do ?

### 虚拟机技术

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker1.png)

虚拟机技术的缺点：

1. 占用资源多

2. 冗余步骤多

3. 启动缓慢

### 容器化技术

**容器化技术不是模拟一个完整的操作系统。**

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker2.png)

### 比较 docker 和传统虚拟机的不同：

+ 传统虚拟机是虚拟出一套硬件，运行一个完整的操作系统，然后在这个系统上安装和运行软件。

+ 容器内的应用是直接运行在宿主机的内核之上，容器内没有自己的内核，也没有虚拟硬件，所以轻便。

+ 容器之间互相隔离，有自己的文件系统，互不干扰。

### DevOps(开发运维)

**应用更快速的交付和部署**

传统交付：技术文档 + 安装程序

Docker：打包镜像发布测试 + 一键运行

**更便捷的升级和扩缩容**

**更简单的系统运维**

在容器化之后，我们的开发，测试环境都是高度一致的。

**更高效的计算资源利用**

Docker 是内核级别的虚拟化，可以在一个物理机上运行很多的容器实例！服务器的性能被压榨到极致。