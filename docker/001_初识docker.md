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

## 容器技术管理

提起容器就不得不说 chroot，因为 chroot 是最早的容器雏形。chroot 意味着切换根目录，有了 chroot 就意味着我们可以把任何目录更改为当前进程的根目录，这与容器非常相似。

### chroot

这是 chroot 维基百科定义：

> chroot 是在 Unix 和 Linux 系统的一个操作，针对正在运作的软件行程和他的子进程，改变它外显的根目录。一个运行在这个环境下，经由 chroot 设置根目录的程序，他不能够对这个指定根目录之外的文件进行访问动作，不能读取，也不能改变它的内容。

通俗地说，chroot 就是可以改变某进程的根目录，使这个程序不能访问目录之外的其他目录，这个跟我们在一个容器中是很相似的。下面我们通过一个实例来演示下 chroot。

创建 rootfs 目录：

```bash
mkdir rootfs
```

这里为了方便演示，使用了线程的 busybox 镜像来创建一个系统。(如果还不太了解 docker，可以看成是在 rootfs 下创建了一些目录和二进制文件)

```bash
cd rootfs
docker export $(docker create busybox) -o busybox.tar
tar -xf busybox.tar
```

查看 rootfs 目录：

```bash
[root@tz7 rootfs]# ls
bin  busybox.tar  dev  etc  home  proc  root  sys  tmp  usr  var
```

接下来我们看看 rootfs 的神奇之处。使用以下命令可以启动一个 sh 进程，并且把 rootfs 作为 sh 进程的根目录。

```bash
[root@tz7 rootfs]# chroot /home/rootfs /bin/sh
/ # 
```

此时，我们的命令行窗口已经处于上述命令启动的 sh 进程中。在当前 sh 命令行窗口下，我们使用 `ls` 命令查看当前进程，看是否真的与主机上的其他目录隔离开了。

```bash
/ # /bin/ls /
bin  busybox.tar  dev  etc  home  proc  root  sys  tmp  usr  var
```

这里可以看到当前进程的根目录已经变成了主机上的 `/home/rootfs` 目录。这样就实现了当前进程与主机的隔离。

到此为止，一个目录隔离的容器就完成了。但是，此时还不能称之为一个容器，为什么呢？你可以在上一步执行以下命令，查看如下路由信息：

```bash
/etc # /bin/ip route
```

执行 `ip route` 后，你可以看到网络信息并没有隔离，实际上进程等信息此时也并未隔离。要想实现一个完整的容器，我们还需要 Linux 的其他三项技术：**Namespace**，**Cgroups** 和**联合文件系统**。

Docker 就是利用 Linux 的 Namespace、Cgroups 和联合文件系统三大机制来保证实现的。它使用 Namespace 做主机名、网络、PID 等资源的隔离，使用 Cgroups 对进程或者进程组做资源(如 CPU、内存等)的限制，联合文件系统用于镜像构建和容器运行环境。

#### Namespace

Namespace 是 Linux 内核的一项功能，该功能对内核资源进行隔离，使得容器中的进程都可以在单独的命名空间中，并且只可以访问当前容器命名空间的资源。Namespace 可以隔离进程 ID、主机名、用户名、文件名、网络访问和进程间通信等相关资源。

Docker 主要用到以下五种命名空间：

1. pid namespace : 用于隔离进程 ID。

2. net namespace : 隔离网络接口，在虚拟的 net namespace 内用户可以拥有自己独立的 IP、路由、端口等。

3. mnt namespace : 文件系统挂载点隔离。

4. ipc namespace : 信号量，消息队列和共享内容的隔离。

5. uts namespace : 主机名和域名的隔离。

#### Cgroups

Cgroups 是一种 Linux 内核功能，可以限制和隔离进程的资源使用情况(CPU、内存、磁盘 I/O、网络等)。在容器的实现，Cgroups 通常用来限制容器的 CPU 和内存等资源的使用。

#### 联合文件系统

联合文件系统，又叫 UnionFS，是一种通过创建文件层进程操作的文件系统，因此，联合文件系统非常轻快。Docker 使用联合文件系统为容器构建层，使得容器可以实现写时复制以及镜像的分层构建和存储。常用的联合文件系统有 AUFS、Overlay 和 Devicemapper 等。