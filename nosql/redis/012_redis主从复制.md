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


## 环境配置实例

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

> 真实的主从配置应该是在配置文件中配置的，这样的话是永久的。我们这里使用命令的方式是暂时的。

### 层层链路

还有一种配置类似于**链表**的配置，一台服务器既是从机，也是主机。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_replication1.png)

这个时候也能完成主从复制。

## 主写从读

主机可写可读：

```bash
127.0.0.1:6379> keys *
(empty array)
127.0.0.1:6379> set k1 v1
OK
127.0.0.1:6379> get k1
"v1"
```

从机只能读不能写：

```bash
127.0.0.1:6380> get k1
"v1"
127.0.0.1:6380> set k2 v2
(error) READONLY You can't write against a read only replica.
```

## 测试宕机

主机宕机，从机依旧连接到主机，但是没有写操作，如果主机恢复，从机依旧可以获取到主机新写入的数据。

从机宕机，如果使用命令行 `SLAVEOF` 配置，如果从机恢复，它就会变回主机。

主机宕机，从机可以使用 `SLAVEOF no one` 命令**手动**将自己升级为主机。

## 哨兵模式(自动选举主机的模式)

### 概述

主从切换技术是当主服务器宕机后，需要手动把一台从服务器切换为主服务器，这就需要人工干预，费时费力，还会造成一段时间内服务不可用。这不是一种推荐的方式，更多时候，我们优先考虑**哨兵模式**。Redis 从 2.8 开始正式提供了 Sentinel (哨兵模式)架构来解决这个问题。

哨兵能够后台监控主机是否故障，如果故障了根据投票数**自动将从库转换为主库**。

哨兵模式是一种特殊的模式，首先 Redis 提供了哨兵的命令，哨兵是一个独立的进程，作为进程，它会独立运行。其原理是**哨兵通过发送命令，等待 Redis 服务器响应，从而监控运行的多个 Redis 实例**。

### 单哨兵

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_replication2.png)

这里的哨兵有两个作用

+ 通过发送命令，让 Redis 服务器返回监控其运行状态，包括主服务器和从服务器。

+ 当哨兵检测到 master 宕机，会自动将 slave 切换成 master，然后通过**发布订阅模式**通知其他的从服务器，修改配置文件，让他们切换主机。

然后一个哨兵进程也可能存在突发问题，为此，我们可以使用多个哨兵进行监控。各个哨兵之间还会进行监控，这样就形成了多哨兵模式。

### 多哨兵

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_replication3.png)

假设主服务器宕机，哨兵1先检测到这个结果，系统并不会马上进行 failover 过程，仅仅是哨兵1主观认为主服务器不可用，这个现象称为**主观下线**。

当后面的哨兵也检测到主服务器不可用，并且数量达到一定值时，那么哨兵之间就会进行一次投票，投票的结果由一个哨兵发起，进行 failover (故障转移)操作。切换成功后，就会通过发布订阅模式，让各个哨兵把自己监控的从服务器切换主机，这个过程称为**客观下线**。

### 测试

#### 1、配置哨兵配置文件 sentinel.conf

```bash
# sentinel monitor 被监控的名称 host port 1
# 最后的数字 1，代表主机挂了，slave 投票看让谁接替成为主机，票数最多的，就会成为主机
sentinel monitor myredis 127.0.0.1 6379 1
```

#### 2、启动哨兵

```bash
[root@izuf61wwjib0gi7cyckz02z bin]# redis-sentinel myredisconf/sentinel.conf 
19865:X 28 Jun 2021 10:01:08.696 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
19865:X 28 Jun 2021 10:01:08.696 # Redis version=6.2.4, bits=64, commit=00000000, modified=0, pid=19865, just started
19865:X 28 Jun 2021 10:01:08.696 # Configuration loaded
19865:X 28 Jun 2021 10:01:08.696 * monotonic clock: POSIX clock_gettime
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 6.2.4 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                  
 (    '      ,       .-`  | `,    )     Running in sentinel mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 26379
 |    `-._   `._    /     _.-'    |     PID: 19865
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           https://redis.io       
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

19865:X 28 Jun 2021 10:01:08.697 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
19865:X 28 Jun 2021 10:01:08.702 # Sentinel ID is 46502cbf6ed39a7246c91fa0b9931b112004b26b
19865:X 28 Jun 2021 10:01:08.702 # +monitor master myredis 127.0.0.1 6379 quorum 1
19865:X 28 Jun 2021 10:01:08.702 * +slave slave 127.0.0.1:6380 127.0.0.1 6380 @ myredis 127.0.0.1 6379
19865:X 28 Jun 2021 10:01:08.706 * +slave slave 127.0.0.1:6381 127.0.0.1 6381 @ myredis 127.0.0.1 6379
```

### 3、测试

我们把主机 `SHUTDOWN` 掉后，可以在 sentinel 进程里看到如下处理信息，会在从机中随机选择一个服务器(这里有一个投票算法)。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_replication3.png)

如果主机(6379)此时回来了，只能归并到新的主机(6381)下，当从机。

### 哨兵模式的优缺点

优点 : 

1、哨兵集群，基于主从复制模式，所有的主从配置优点，它都有；

2、主从可以切换，故障可以转移，系统的可用性就会更好；

3、哨兵模式就是主从模式的升级，手动到自动，更加智能。

缺点 :

1、redis 不好在线扩容，集群容量一旦达到上限，在线扩容就十分麻烦；

2、实现哨兵模式的配置比较复杂，有很多配置项。

### 哨兵模式配置文件

```conf
# Example sentinel.conf
 
# 哨兵sentinel实例运行的端口 默认26379
port 26379
 

# 哨兵sentinel的工作目录
dir /tmp
 

# 哨兵sentinel监控的redis主节点的 ip port 
# master-name  可以自己命名的主节点名字 只能由字母A-z、数字0-9 、这三个字符".-_"组成。
# quorum 当这些quorum个数sentinel哨兵认为master主节点失联 那么这时 客观上认为主节点失联了
# sentinel monitor <master-name> <ip> <redis-port> <quorum>
sentinel monitor mymaster 127.0.0.1 6379 2
 

# 当在Redis实例中开启了requirepass foobared 授权密码 这样所有连接Redis实例的客户端都要提供密码
# 设置哨兵sentinel 连接主从的密码 注意必须为主从设置一样的验证密码
# sentinel auth-pass <master-name> <password>
sentinel auth-pass mymaster MySUPER--secret-0123passw0rd
 
 
# 指定多少毫秒之后 主节点没有应答哨兵sentinel 此时 哨兵主观上认为主节点下线 默认30秒
# sentinel down-after-milliseconds <master-name> <milliseconds>
sentinel down-after-milliseconds mymaster 30000
 

# 这个配置项指定了在发生failover主备切换时最多可以有多少个slave同时对新的master进行 同步，
这个数字越小，完成failover所需的时间就越长，
但是如果这个数字越大，就意味着越 多的slave因为replication而不可用。
可以通过将这个值设为 1 来保证每次只有一个slave 处于不能处理命令请求的状态。
# sentinel parallel-syncs <master-name> <numslaves>
sentinel parallel-syncs mymaster 1


# 故障转移的超时时间 failover-timeout 可以用在以下这些方面： 
#1. 同一个sentinel对同一个master两次failover之间的间隔时间。
#2. 当一个slave从一个错误的master那里同步数据开始计算时间。直到slave被纠正为向正确的master那里同步数据时。
#3.当想要取消一个正在进行的failover所需要的时间。  
#4.当进行failover时，配置所有slaves指向新的master所需的最大时间。不过，即使过了这个超时，slaves依然会被正确配置为指向master，但是就不按parallel-syncs所配置的规则来了
# 默认三分钟
# sentinel failover-timeout <master-name> <milliseconds>
sentinel failover-timeout mymaster 180000
 
# SCRIPTS EXECUTION
 
#配置当某一事件发生时所需要执行的脚本，可以通过脚本来通知管理员，例如当系统运行不正常时发邮件通知相关人员。
#对于脚本的运行结果有以下规则：
#若脚本执行后返回1，那么该脚本稍后将会被再次执行，重复次数目前默认为10
#若脚本执行后返回2，或者比2更高的一个返回值，脚本将不会重复执行。
#如果脚本在执行过程中由于收到系统中断信号被终止了，则同返回值为1时的行为相同。
#一个脚本的最大执行时间为60s，如果超过这个时间，脚本将会被一个SIGKILL信号终止，之后重新执行。
 
#通知型脚本:当sentinel有任何警告级别的事件发生时（比如说redis实例的主观失效和客观失效等等），将会去调用这个脚本，
这时这个脚本应该通过邮件，SMS等方式去通知系统管理员关于系统不正常运行的信息。调用该脚本时，将传给脚本两个参数，
一个是事件的类型，
一个是事件的描述。
如果sentinel.conf配置文件中配置了这个脚本路径，那么必须保证这个脚本存在于这个路径，并且是可执行的，否则sentinel无法正常启动成功。
#通知脚本
# sentinel notification-script <master-name> <script-path>
sentinel notification-script mymaster /var/redis/notify.sh


# 客户端重新配置主节点参数脚本
# 当一个master由于failover而发生改变时，这个脚本将会被调用，通知相关的客户端关于master地址已经发生改变的信息。
# 以下参数将会在调用脚本时传给脚本:
# <master-name> <role> <state> <from-ip> <from-port> <to-ip> <to-port>
# 目前<state>总是“failover”,
# <role>是“leader”或者“observer”中的一个。 
# 参数 from-ip, from-port, to-ip, to-port是用来和旧的master和新的master(即旧的slave)通信的
# 这个脚本应该是通用的，能被多次调用，不是针对性的。
# sentinel client-reconfig-script <master-name> <script-path>
sentinel client-reconfig-script mymaster /var/redis/reconfig.sh
```

## 复制原理

slave 启动成功连接到 master 后会发送一个 sync 同步命令。

master 接到命令，启动后台的存盘进程，同时收集所有接收到的用于修改数据集命令，在后台进程执行完毕之后，master 将传送**整个数据文件**到 slave，并完成一次完全同步。

**全量复制** : slave 服务在接受到数据库文件数据后，将其存盘并加载到内存中。

**增量复制** : master 继续将新的所有收集到的修改命令一次传给 slave，完成同步。

只要是重新连接 master，一次完全同步(全量复制)将被自动执行。