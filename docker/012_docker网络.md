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

发现这些容器的网卡，都是成对的，使用到了 veth-pair 技术。

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

## --link(不推荐使用)

```bash
docker run -d -P --name tomcat03 --link tomcat02 tomcat

docker exec -it tomcat03 ping tomcat02

docker exec -it tomcat03 cat /etc/hosts
```

通过上述 3 条指令可以得出：--link 就是在 hosts 配置中增加了 `容器ip 容器名 容器ID` 的配置。

## 自定义网络

```bash
# 查看所有 docker 网络
docker network ls
```

> docker 网络模式<br/>
> bridge : 桥接（默认，自己创建使用 bridge 桥接模式） <br/>
> none : 不配置网络<br/>
> host : 主机模式，与宿主机共享网络<br>
> container : 容器网络连通(较少使用)

### 创建自定义网络

```bash
[root@tz7 ~]# docker network create --driver bridge --subnet 192.168.0.0/16 --gateway 192.168.0.1 mynet
9aac2f6f22e6a191c26816f033b552404a562e84a4fca77f9ce51082e31e4353
[root@tz7 ~]# docker network ls
NETWORK ID     NAME      DRIVER    SCOPE
4ef38342f09f   bridge    bridge    local
160b3a2c3f9f   host      host      local
9aac2f6f22e6   mynet     bridge    local
727f7e050c4a   none      null      local
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_network5.png)

```bash
[root@tz7 ~]# docker run -d -P --name tomcat-net-1 --net mynet tomcat
60190aa497d6a96dee6e67b05c5507e4b82742aad861c81c4574b1756dd3074c
[root@tz7 ~]# docker run -d -P --name tomcat-net-2 --net mynet tomcat
f9ab662f3aff107c2b0f02f35acd1e6f00e7c11ec5ecf390e079c39f843f520e
[root@tz7 ~]# docker network inspect mynet
[
    {
        "Name": "mynet",
        "Id": "9aac2f6f22e6a191c26816f033b552404a562e84a4fca77f9ce51082e31e4353",
        "Created": "2021-07-15T21:26:01.641410113+08:00",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "192.168.0.0/16",
                    "Gateway": "192.168.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "60190aa497d6a96dee6e67b05c5507e4b82742aad861c81c4574b1756dd3074c": {
                "Name": "tomcat-net-1",
                "EndpointID": "806e9d3ae44a570b4aad48a01947cc6885d75ef8b8402a9ed72b14d96a6a56a7",
                "MacAddress": "02:42:c0:a8:00:02",
                "IPv4Address": "192.168.0.2/16",
                "IPv6Address": ""
            },
            "f9ab662f3aff107c2b0f02f35acd1e6f00e7c11ec5ecf390e079c39f843f520e": {
                "Name": "tomcat-net-2",
                "EndpointID": "3733de08aa036a898530a979f31bd60fa0f6f734b7acf1a0bbb4880b2986b659",
                "MacAddress": "02:42:c0:a8:00:03",
                "IPv4Address": "192.168.0.3/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```

执行 ping 命令均可 ping 通：

```bash
docker exec -it tomcat-net-1 ping tomcat-net-2

docker exec -it tomcat-net-1 ping 196.168.0.3
```

由此可见自定义网络 docker 都已经帮我们维护好了对应关系，推荐使用。


## 网络连通

将容器放入指定网络下，实现一个容器两个 ip。

```bash
docker network connect [OPTIONS] NETWORK CONTAINER
```