# linux 系统速度慢的原因以及排查方法

当 linux 系统运行很慢，我们在搞清楚如何加速 linux 计算机之前，需要知道哪些方法可以帮助我们找到引导时启动的服务、以更高或更低的优先级运行的进程、CPU 运行状况、内存是否塞满了过多数据，还要检查交换内存区是否已满。最后，还要检查硬盘是否运行正常。

下面就一起来看看怎么解决 linux 系统运行太慢的问题。

## 一、检查 CPU 信息

若想加快一台慢腾腾的 linux 计算机，采取的第一步是检查 CPU 信息。你的计算机运行很耗费时间的主要原因可能是，CPU 的速度远不足以运行重量级应用程序。

打开终端，运行以下命令之一：

### cat /proc/cpuinfo

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/linux_optimize1.png)

### lscpu

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/linux_optimize2.png)

上述命令显示了有关 CPU 的详细信息，比如 vender_id、型号名称、CPU MHZ、缓存大小、微代码和 bogomips。

不妨详细介绍关于 CPU 信息的几个细节：

* bogomips : bogo 是 bogus(伪) 的意思，MIPS 是指每秒百万条指令。它是显示系统性能的独立程序。

* model_name : 表示 CPU 的制造商、型号和速度。在本文中，我们拥有速度为 2.50 GHz 的英特尔赛扬(R) CPU。

* CPU MHZ(兆赫) : 用于测量通道、总线和计算机内部时钟的传输速度。在本文中，传输速度是 2494.222 GHz。

### 解决方案

如果计算机的 CPU 传输速度显然很低，唯一的办法就是换个新的。

## 二、检查引导时启动的服务

有不同的方法来检查引导时启动的服务。你可以使用下列命令的任何一个。

### service--status-all

与 CentOS 不兼容，所以就没截图。

### chkconfig --list

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/linux_optimize3.png)

### initctl list

initctl 是守护进程控制工具，它让系统管理员得以与 Upstart 守护进程进行通信和交互。

### 解决方案

如果你的系统使用 systemd，可以使用下列命令来找到引导时运行的服务：

```sh
sudo systemctl list-unit-files --state = enabled
```

对于使用 systemd 的 Linux 发行版而言，可以使用 systemctl 命令来管理服务，以便服务不会再引导时运行。

## 三、检查 CPU 负载

除了检查引导时启动的服务外，还可以检查处理器/CPU 是否因进程而过载。你可以使用命令 `top` 来检查 CPU 负载。

`top` 命令将资源使用最高的进程排在上面。正如下面截图所见，你可以看清楚哪个进程/应用程序在滥用 CPU，必要时使用 `kill` 命令终止它。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/linux_optimize4.png)

### 解决方案

如果你运行过多的应用程序(无论是前台还是后台)，CPU 又达不到标准，最好关闭未使用的应用程序。另外，禁止你未使用的任何应用程序在后台运行。

此外，可以使用 `preload` 来加载常用的应用程序。preload 是在后台运行的守护进程，它分析经常运行的应用程序。

打开终端，运行下列命令：

```sh
sudo apt-get install preload
```

对于 Fedora 和 CentOS 用户而言，可以使用下列命令：

```sh
sudo yum install preload
```

preload 在后台运行。因此，没必要调整它。preload 将常用应用程序的一部分记载到内存中，确保更快地加载这些应用程序。

## 检查闲置内存空间

内存通常是用来存储常用应用程序的地方。你可以使用 `free` 命令来检查内存信息，比如内存可用的闲置空间。内存空间较少也会影响计算机的性能。

### 解决方案

要么升级内存，要么把耗费大量内存的应用程序换成轻量级应用程序。

## 检查硬盘是否使用过度

硬盘指示灯一直在不停地闪烁，但你不知道它在干什么？神秘的输入/输出很可能是个问题，所有有一个类似 top 的工具: iotop，它专门用来帮助诊断这类问题。

打开终端，输入命令：

```sh
sudo apt install iotop
```

对于 Fedora 和 CentOS 用户而言，可以使用下列命令：

```sh
sudo yum install iotop
```

正常的闲置系统其值基本上是零，只是在数据写入时有几个小的尖峰，如下截图所示：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/linux_optimize5.png)

然后，如果你运行 `find` 之类的磁盘密集型实用程序，可以看到 iotop 清楚列出的名称和吞吐量。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/linux_optimize6.png)

现在你可以轻松了解哪个程序在使用 I/O、谁运行它、数据读取速度等更多信息。

## 结论

虽然有很多因素可能导致系统缓慢，但 CPU、内存和磁盘 I/O 是导致绝大多数性能问题的原因。使用本文介绍的方法将帮助你查明性能问题的原因以及如何解决这些问题。

## 参考

[linux 系统速度慢,Linux运维人员你知道Linux系统运行速度太慢的原因吗?](https://blog.csdn.net/weixin_30290131/article/details/116702311)