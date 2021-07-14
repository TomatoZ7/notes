# Docker 网络

## 理解 docker0

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_network1.jpg)

可以看到有三个网络。

> 问题：docker 是如何处理网络访问的？

```bash
[root@tz7 ~]# docker run -P -d --name tomcat01 tomcat

# 查看容器的内部网络地址 ip addr，发现容器启动的时候会得到一个 eth0@if63 ip 地址，这是由 docker 分配的
[root@tz7 ~]# docker exec -it e856e40707f ip addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
62: eth0@if63: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default 
    link/ether 02:42:ac:12:00:02 brd ff:ff:ff:ff:ff:ff link-netnsid 0
    inet 172.18.0.2/16 brd 172.18.255.255 scope global eth0
       valid_lft forever preferred_lft forever

# linux 能否 ping 通容器内部？
[root@iZuf61wwjib0gi7cyckz02Z ~]# ping 172.18.0.2
PING 172.18.0.2 (172.18.0.2) 56(84) bytes of data.
64 bytes from 172.18.0.2: icmp_seq=1 ttl=64 time=0.098 ms
64 bytes from 172.18.0.2: icmp_seq=2 ttl=64 time=0.075 ms
```

我们每启动一个 docker 容器，docker 就会给 docker 容器分配一个 ip，我们只要安装了 docker，就会有一个网卡 docker0。

再次执行 `ip addr`：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_network1.png)

在启动一个容器测试，发现又多了一对网卡：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_network2.png)

发现这些容器的网卡，都是成对的，就是使用到了 veth-pair 技术。

veth-pair 就是一对虚拟设备接口，它们都是成对出现的，一端连着协议，一端彼此相连。

正因为有这个特性，veth-pair 充当一个桥梁，连接各种虚拟网络设备。

测试容器与容器之间是否可以 ping 通：

```bash
docker exec -it tomcat02 ping 172.18.0.2
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_network3.png)

结论：tomcat01 和 tomcat02 是共用一个路由器：docker0。

所有的容器不指定网络的情况下，都是 docker0 路由的，docker 会给我们的容器分配一个默认的可用 ip。

## 小结

docker 使用的是 linux 的桥接，宿主机中是一个 Docker 容器的网桥 docker0。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_network4.png)

docker 中所有的网络接口都是虚拟的，虚拟的转发效率高。

如果容器删除，对应的网桥对就没了。