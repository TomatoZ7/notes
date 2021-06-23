# Nginx 高可用解决方案

如果 Nginx 宕机了，那么那么整套系统都将服务对外提供服务了，这个如何解决？

## Keepalived

使用Keepalived来解决，Keepalived 软件由 C 编写的，最初是专为 LVS 负载均衡软件设计的，Keepalived 软件主要是通过 VRRP 协议实现高可用功能。

### VRRP 介绍

VRRP（Virtual Route Redundancy Protocol）协议，翻译过来为虚拟路由冗余协议。VRRP 协议将两台或多台路由器设备**虚拟成一个设备**，对外提供**虚拟路由器 IP**，而在路由器组内部，如果实际拥有这个对外 IP 的路由器如果工作正常的话就是 MASTER，MASTER 实现针对虚拟路由器 IP 的各种网络功能。其他设备不拥有该虚拟 IP，状态为 BACKUP，除了接收MASTER 的 VRRP 状态通告信息以外，不执行对外的网络功能。当主机失效时，BACKUP 将接管原先 MASTER 的网络功能。

从上面的介绍信息获取到的内容就是 VRRP 是一种协议，那这个协议是用来干什么的？

1. 选择协议

VRRP 可以把一个虚拟路由器的责任动态分配到局域网上的 VRRP 路由器中的一台。其中的虚拟路由即 Virtual 路由是由 VRRP 路由群组创建的一个不真实存在的路由，这个虚拟路由也是有对应的 IP 地址。而且 VRRP 路由 1 和 VRRP 路由 2 之间会有竞争选择，通过选择会产生一个 Master 路由和一个 Backup 路由。

2. 路由容错协议

Master 路由和 Backup 路由之间会有一个心跳检测，Master 会定时告知 Backup 自己的状态，如果在指定的时间内，Backup 没有接收到这个通知内容，Backup 就会替代 Master 成为新的 Master。Master 路由有一个特权就是虚拟路由和后端服务器都是通过 Master 进行数据传递交互的，而备份节点则会直接丢弃这些请求和数据，不做处理，只是去监听 Master 的状态。

用了 Keepalived 后，解决方案如下：

## 环境搭建

环境准备

| VIP             | IP              | 主机名      | 主/从  |
| --------------- | --------------- | ----------- | ------ |
|                 | 192.168.200.133 | keepalived1 | Master |
| 192.168.200.222 |                 |             |        |
|                 | 192.168.200.122 | keepalived2 | Backup |

### keepalived 安装

1. 从[官方网站](https://keepalived.org/)下载 keepalived

2. 将下载的资源 keepalived-2.0.20.tar.gz 上传到服务器

3. 创建 keepalived 目录，方便管理资源

```sh
mkdir keepalived
```

4. 将压缩文件进行解压缩，解压缩到指定的目录

```sh
tar -zxf keepalived-2.0.20.tar.gz -C keepalived/
```

5. 对 keepalived 进行配置，编译和安装

```sh
cd keepalived/keepalived-2.0.20

./configure --sysconf=/etc --prefix=/usr/local

make && make install
```

安装完成后，关注以下两个文件：

一个是 `/etc/keepalived/keepalived.conf`，keepalived 的系统配置文件，我们主要操作的就是该文件。

一个是 `/usr/local/sbin` 目录下的 `keepalived`，是系统配置脚本，用来启动和关闭 keepalived。

## Keepalived 配置文件介绍

这里面会分三部，第一部分是 global 全局配置、第二部分是 vrrp 相关配置、第三部分是 LVS 相关配置。

这里没有用到 LVS，所以重点关注前两部分。

### global全局部分

```conf
global_defs {
   #通知邮件，当keepalived发送切换时需要发email给具体的邮箱地址
   notification_email {
     tom@itcast.cn
     jerry@itcast.cn
   }
   #设置发件人的邮箱信息
   notification_email_from zhaomin@itcast.cn
   #指定smpt服务地址
   smtp_server 192.168.200.1
   #指定smpt服务连接超时时间
   smtp_connect_timeout 30
   #运行keepalived服务器的一个标识，可以用作发送邮件的主题信息
   router_id LVS_DEVEL
   
   #默认是不跳过检查。检查收到的VRRP通告中的所有地址可能会比较耗时，设置此命令的意思是，如果通告与接收的上一个通告来自相同的master路由器，则不执行检查(跳过检查)
   vrrp_skip_check_adv_addr
   #严格遵守VRRP协议。
   vrrp_strict
   #在一个接口发送的两个免费ARP之间的延迟。可以精确到毫秒级。默认是0
   vrrp_garp_interval 0
   #在一个网卡上每组na消息之间的延迟时间，默认为0
   vrrp_gna_interval 0
}
```

### VRRP 部分

该部分可以包含以下四个子模块：

1. vrrp_script
2. vrrp_sync_group
3. garp_group
4. vrrp_instance

我们会用到第一个和第四个：

```conf
#设置keepalived实例的相关信息，VI_1为VRRP实例名称
vrrp_instance VI_1 {
    state MASTER  		#有两个值可选MASTER主 BACKUP备
    interface ens33		#vrrp实例绑定的接口，用于发送VRRP包[当前服务器使用的网卡名称]
    virtual_router_id 51#指定VRRP实例ID，范围是0-255
    priority 100		#指定优先级，优先级高的将成为MASTER
    advert_int 1		#指定发送VRRP通告的间隔，单位是秒
    authentication {	#vrrp之间通信的认证信息
        auth_type PASS	#指定认证方式。PASS简单密码认证(推荐)
        auth_pass 1111	#指定认证使用的密码，最多8位
    }
    virtual_ipaddress { #虚拟IP地址设置虚拟IP地址，供用户访问使用，可设置多个，一行一个
        192.168.200.222
    }
}
```

### 配置实例

服务器1

```conf
global_defs {
   notification_email {
        tom@itcast.cn
        jerry@itcast.cn
   }
   notification_email_from zhaomin@itcast.cn
   smtp_server 192.168.200.1
   smtp_connect_timeout 30
   router_id keepalived1
   vrrp_skip_check_adv_addr
   vrrp_strict
   vrrp_garp_interval 0
   vrrp_gna_interval 0
}

vrrp_instance VI_1 {
    state MASTER
    interface ens33
    virtual_router_id 51
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.200.222
    }
}
```

服务器2

```conf
! Configuration File for keepalived

global_defs {
   notification_email {
        tom@itcast.cn
        jerry@itcast.cn
   }
   notification_email_from zhaomin@itcast.cn
   smtp_server 192.168.200.1
   smtp_connect_timeout 30
   router_id keepalived2
   vrrp_skip_check_adv_addr
   vrrp_strict
   vrrp_garp_interval 0
   vrrp_gna_interval 0
}

vrrp_instance VI_1 {
    state BACKUP
    interface ens33
    virtual_router_id 51
    priority 90
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.200.222
    }
}
```

### keepalived 之 vrrp_script

keepalived 只能做到对网络故障和 keepalived 本身的监控，即当出现网络故障或者 keepalived 本身出现问题时，进行切换。但是这些还不够，我们还需要监控 keepalived 所在服务器上的其他业务，比如 Nginx，如果 Nginx 出现异常了，仅仅 keepalived 保持正常，是无法完成系统的正常工作的，因此需要根据业务进程的运行状态决定是否需要进行主备切换，这个时候，我们可以通过编写脚本对业务进程进行检测监控。

#### 实现步骤

1. 在 keepalived 配置文件中添加对应的配置项

```conf
vrrp_script 脚本名称
{
    script "脚本位置"
    interval 3 #执行时间间隔
    weight -20 #动态调整vrrp_instance的优先级
}
```

2. 编写脚本

ck_nginx.sh

```sh
#!/bin/bash
num=`ps -C nginx --no-header | wc -l`
if [ $num -eq 0 ];then
 /usr/local/nginx/sbin/nginx
 sleep 2
 if [ `ps -C nginx --no-header | wc -l` -eq 0 ]; then
  killall keepalived
 fi
fi
```

`Linux ps` 命令用于显示当前进程 (process) 的状态。

`-C(command)` : 指定命令的所有进程

`--no-header` 排除标题

3. 为脚本文件设置权限

```sh
chmod 755 ck_nginx.sh
```

4. 将脚本添加到

```conf
vrrp_script ck_nginx {
   script "/etc/keepalived/ck_nginx.sh" #执行脚本的位置
   interval 2		#执行脚本的周期，秒为单位
   weight -20		#权重的计算方式
}
vrrp_instance VI_1 {
    state MASTER
    interface ens33
    virtual_router_id 10
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.200.111
    }
    track_script {
      ck_nginx
    }
}
```

5. 如果效果没有出来，可以使用 `tail -f /var/log/messages`查看日志信息，找对应的错误信息。

6. 测试

### 配置优化

通常如果 master 服务死掉后 backup 会变成 master，但是当 master 服务又好了的时候 master 此时会抢占 VIP，这样就会发生两次切换对业务繁忙的网站来说是不好的。

所以我们要在配置文件加入 nopreempt 非抢占，但是这个参数只能用于 state 为 backup，故我们在用 HA 的时候最好 master 和 backup 的 state 都设置成 backup 让其通过 priority 来竞争。