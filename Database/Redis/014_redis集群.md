# redis 集群

## 问题背景

当 redis 容量不够了，怎么扩容？

并发写操作，单台 redis 压力过大，如何分摊？

另外，主从模式、层层链路模式，如果主机宕机导致 ip 地址发生变化，应用程序配置需要修改对应的主机地址、端口信息等。

之前可以通过代理来解决，但是 redis3.0 中提供了解决方案，就是**无中心化集群**配置。

## 集群

redis 集群实现了对 redis 的水平扩容，即启动 N 个 redis 节点，将数据库分布存储在这 N 个节点，每个节点存储总数据的 1/N。

redis 集群通过分区(partition)来提供一定程度的可用性(availability) : 即使集群中有一部分节点失效或者无法进行通讯，集群也可以继续处理命令请求。

## 集群搭建实例

### 1、删除持久化数据

删除 rdb、aof 文件。

### 2、配置 6 个 redis 实例，以端口号区分 (6379-6381, 6389-6391)

redis63xx.conf : 

```conf
include ./redis.conf;
pidfile /var/run/redis_63xx.pid
port 63xx
dbfilename "dump63xx.rdb"

############### redis cluster ###############

# 打开集群模式
cluster-enabled yes

# 设置节点配置文件名
cluster-config-file nodes-63xx.conf

# 设置节点失联时间，超过该时间(毫秒)，集群自动进行主从切换
cluster-node-timeout 15000
```

### 3、启动配置好的 6 个 redis 服务

```bash
redis-server redis63xx.conf
```

### 4、将 6 个节点合成一个集群

组合之前，确保所有 redis 实例成功启动，nodes-63xx.conf 文件都生成正常。

1. 进入 redis 安装目录下的 src 目录

```bash
cd your_redis_install_dir/src
```

2. 执行命令

```bash
# ip 不能写 127.0.0.1
redis-cli --cluster create --cluster-replicas 1 ip:6379 ip:6380 ip:6381 ip:6389 ip:6390 ip:6391
```

注意几点 : 

+ protected-mode 要改为 no

+ requirepass 这里我设为空，否则会报权限验证不通过

3. 执行结果

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_cluster1.jpg)

### 5、 -c 采用集群策略连接，设置数据会自动切换到相应的写主机

```bash
redis-cli -c -p 6379
```

查看集群节点信息 `CLUSTER NODES`

```bash
127.0.0.1:6379> CLUSTER NODES
96cc3e49f633018704b726b8a4eeacaacc868e6f your_ip:6391@16391 slave d6e882968e0ea5df7706cd2fcd6c70bf33e0d572 0 1625020767561 3 connected
d6e882968e0ea5df7706cd2fcd6c70bf33e0d572 your_ip:6381@16381 master - 0 1625020767000 3 connected 10923-16383
80144b253eaa32d2ae4c3df3510e12566d03d4c2 your_ip:6389@16389 slave b306d5c6ad06dd2148b0c99138e0ae49d31a731d 0 1625020766558 1 connected
b6264c0f1d4132e5b564897d78c6cc290fe0a63d your_ip:6390@16390 slave be17484c58fedb491a0767bb3b5f429c8285f9b1 0 1625020768000 2 connected
b306d5c6ad06dd2148b0c99138e0ae49d31a731d your_ip:6379@16379 myself,master - 0 1625020765000 1 connected 0-5460
be17484c58fedb491a0767bb3b5f429c8285f9b1 your_ip:6380@16380 master - 0 1625020768571 2 connected 5461-10922
```

## redis cluster 如何分配这 6 个节点

一个集群至少要有 3 个主节点。

选项 `--replicas 1` 表示我们希望为集群的每个主节点创建一个从节点。

分配原则尽量保证每个主数据库运行在不同的 ip 地址，每个从库和主库不在一个 ip 地址上。

## 什么是 slots

一个 redis 集群包含 16384 个插槽(hash slot)，数据库中的每个键都属于这 16384 个插槽的其中一个。

集群使用公式 `CRC16(key)%` 来计算 key 属于哪个槽，其中 `CRC16(key)%` 用于计算 key 的 CRC16 校验和。

集群中的每个节点负责处理一部分插槽，这点可以从上面的 `CLUSTER NODES` 返回结果中可以看到：

+ master6379 负责 0-5460
+ master6380 负责 5461-10922
+ master6381 负责 10923-16383

## 在集群中录入值

```bash
127.0.0.1:6379> set k1 v1
->Redirected to slot [12706] located at 196.168.44.168:6379
OK
```

注意 : 不在同一个 slot 同时 `mset` 多个 key 是行不通的，可以通过组来实现。

```bash
mset name{user} Nancy age{user} 20
```

## 查询集群中的值

```bash
# 返回 slot 槽中的 key 个数
CLUSTER COUNTKEYSINSLOT <slot>

# 返回 count 个 slot 槽中的键
CLUSTER GETKEYSINSLOT <slot> <count>
```

## 故障恢复

如果主节点下线，那么从节点升为主节点。

如果主节点恢复，那么成为从节点。

如果某一段插槽的主从都挂掉，看 [cluster-require-full-coverage](https://blog.csdn.net/weixin_35738304/article/details/112518907) 配置。