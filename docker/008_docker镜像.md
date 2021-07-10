# docker 镜像

## 镜像是什么

镜像是一种轻量级、可执行的独立软件包，用来打包软件运行环境和基于运行环境开发的软件，它包含运行某个软件所需的所有内容，包括代码、运行时的库、环境变量和配置文件。

所有的应用，直接打包成 docker 镜像，就可以直接跑起来。

#### 如何得到镜像：

+ 从远程仓库下载

+ 从其他地方拷贝

+ 自己煮做一个镜像 DockerFile

## docker 镜像加载原理

### UnionFS(联合文件系统)

UnionFS(联合文件系统) : Union 文件系统(UnionFS)是一种分层、轻量级并且高性能的文件系统，它支持对文件系统的修改作为一次提交来一层层的叠加，同时可以将不同目录挂载到同一个虚拟文件系统下(unite several directories into a single virtual filesystem)。Union 文件系统是 Docker 镜像的基础。镜像可以通过分层来进行继承，基于基础镜像(没有父镜像)，可以制作各种具体的应用镜像。

特性 : 一次同时加载多个文件系统，但从外面看起来，只能看到一个文件系统，联合加载会把各层文件系统叠加起来，这样最终的文件系统会包含所有底层的文件和目录。

### docker 镜像加载原理

docker 的镜像实际上由一层一层的文件系统组成，这种层级的文件系统 UnionFS。

bootfs(boot file system)主要包含 bootloader 和 kernel。bootloader 主要是引导加载 kernel，Linux 刚启动时会加载 bootfs 文件系统，在 Docker 镜像的最底层是 bootfs。这一层与我们典型的 Linux/Unix 系统是一样的，包含 boot 加载器和内核。当 boot 加载完成之后整个内核就都在内存中了，此时内存的使用权已由 bootfs 转交给内核，此时系统也会卸载 bootfs。

rootfs(root file system)在 bootfs 之上。包含的就是典型 Linux 系统中的 /dev，/proc，/bin，/etc 等标准目录和文件。rootfs 就是各种不同的操作系统发行版，比如 Ubuntu，Centos 等等。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_image1.png)

对于一个精简的 OS，rootfs 可以很小，只需要包含最基本的命令，工具和程序库即可，因为底层直接用 Host 的 kernel，自己只需要提供 rootfs 就可以了。由此可见对于不同的 linux 发行版，bootfs 基本是一致的。rootfs 会有差别，因此不同的发行版可以共用 bootfs。

## 分层理解

### 分层的镜像

我们在下载镜像的时候可以观察到下载是一层一层进行的：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_image2.png)

可以使用 `docker inspect redis:latest` 查看分层：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_image4.png)

### 理解

所有的 docker 镜像都起始于一个基础镜像层，当进行修改或增加新的内容时，就会在当前镜像层之上，创建新的镜像层。

举一个简单的例子，例如基于 Ubuntu Linux 16.04 创建一个新的镜像，这就是新镜像的第一层；如果在该镜像中添加 Python 包，就会在基础镜像层之上创建第二个镜像层；如果继续添加一个安全补丁，就会创建第三个镜像层。

该镜像当前已经包含 3 个镜像层，如下图所示（这仅仅只是一个简单的例子）：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_image3.png)

在添加额外的镜像层的同时，竞相始终保持是当前所有镜像的组合。下图举了一个简单的例子，每个镜像层包含 3 个文件，而镜像包含了来自两个镜像层的 6 个文件。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_image5.png)

上图中的镜像层跟之前图中的略有区别，主要目的是便于展示文件。

下图中展示了一个稍微复杂的三层镜像，在外部看来整个镜像只有 6 个文件，这是因为最上层中的文件 7 是文件 5 的一个更新版本。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_image6.png)

这种情况下，上层镜像层中的文件覆盖了底层镜像层中的文件。这样就使得文件的更新版本作为一个新镜像层添加到镜像当中。

Docker 通过存储引擎(新版本采用快照机制)的方式来实现镜像层堆栈，并保证多镜像层对外展示为统一的文件系统。

Linux 上可用的存储引擎有 AUFS、Overlay2、Device Mapper、Btrfs 以及 ZFS。顾名思义，每种存储引擎都基于 Linux 中对应的文件系统或者块设备技术，并且每种存储引擎都有其独有的性能特点。

Docker 在 Windows 上仅支持 windowsflter 一种存储引擎，该引擎基于 NTFS 文件系统之上实现了分层和 CoW。

下图屏示了与系统显示相同的三层镜像，所有镜像层维叠并合并，对外提供统一的视图。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_image7.png)

## 特点

docker 镜像都是只读的，当容器启动时，一个新的可写层被加载到镜像的顶部。

这一层就是我们通常说的容器层，容器之下都叫镜像层。