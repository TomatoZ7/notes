# redis 主从复制

## 概念

主从复制，是指将一台 Redis 服务器的数据，复制到其他的 Redis 服务器。前者称为**主节点**(master/leader)，后者称为**从节点**(slave/follower)；**数据的复制是单向的，只能由主节点到从节点。**Master 以写为主，Slave 以读为主。

默认情况下，每台 Redis 服务器都是主节点；且一个主节点可以有多个从节点(或没有从节点)，但一个从节点只能有一个主节点。

### 作用

1、数据冗余 : 主从复制实现了数据的热备份，是持久化之外的一种数据冗余方式。

2、故障恢复 : 当主节点出现问题时，可以由从节点提供服务，实现快速的故障恢复；实际上是一种服务的冗余。

3、负载均衡 : 在主从复制的基础上，配合读写分离，可以由主节点提供写服务，由从节点提供读服务(即写 Redis 数据时应用连接主节点，读 Redis 数据时应用连接从节点)，分担服务器负载；尤其是在写少读多的场景下，通过多个从节点分担读负载，可以大大提高 Redis 服务器的并发量。

4、高可用基石 : 除了上述作用以外，主从复制还是哨兵和集群能够实施的基础，因此说主从复制是 Redis 高可用的基础。


一般来说，要将 Redis 运用于工程项目中，只使用一台 Redis 是万万不能的(宕机)，原因如下:

1、从结构上，单个 Redis 服务器会发生单点故障，并月一台服务器需要处理所有的请求负载，压力较大；

2、从容量上，单个 Redis 服务器内存容量有限，就算一台 Redis 服务器内存容量为 256G，也不能将所有内存用作 Redis 存储内存一般来说，单台 Redis 最大使用内存不应该超过 20G。

电商网站上的商品，一般都是一次上传，无数次浏览的，也就是"读多写少"。
对于这种场景，我们可以使如下这种架构:

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_master1.png)


## 环境配置

只配置从库，不用配置主库！

```bash
127.0.0.1:6379> info replication        # 查看当前库的信息
# Replication
role:master             # 角色 master
connected_slaves:0      # 没有从机
master_failover_state:no-failover
master_replid:76901aaafab8c6bee029538b1336c5d21bdda828
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:0
second_repl_offset:-1
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0
```

复制 3 个配置文件，然后修改对应的信息

1. 端口

2. pid file 名字

3. log 文件名字

4. dump.rdb 名字

修改完毕之后启动 redis 服务：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_master2.png)

### 一主二从配置实例

默认情况下，每台 redis 服务器都是主节点；我们一般情况下只用**配置从机**就好了。

```bash
127.0.0.1:6380> SLAVEOF 127.0.0.1 6379
OK
127.0.0.1:6380> info replication
# Replication
role:slave              # 当前角色(从机)
master_host:127.0.0.1   # 主机信息
master_port:6379
master_link_status:up
master_last_io_seconds_ago:0
master_sync_in_progress:0
slave_repl_offset:12950
slave_priority:100
slave_read_only:1
replica_announced:1
connected_slaves:0
master_failover_state:no-failover
master_replid:14ad45990496843199d59a14ec75aceb0afc430c
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:12950
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:12950
```

主机查看：

```bash
127.0.0.1:6379> info replication
# Replication
role:master
connected_slaves:1      # 从机的配置信息
slave0:ip=127.0.0.1,port=6380,state=online,offset=56,lag=0
master_failover_state:no-failover
master_replid:14ad45990496843199d59a14ec75aceb0afc430c
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:56
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:56
```