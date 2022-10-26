# Redis 集群倾斜问题

- [Redis 集群倾斜问题](#redis-集群倾斜问题)
  - [1.Redis 集群出现倾斜的影响](#1redis-集群出现倾斜的影响)
  - [2.导致 Redis 集群倾斜的常见原因](#2导致-redis-集群倾斜的常见原因)
  - [3.Redis 集群倾斜问题的排查方式](#3redis-集群倾斜问题的排查方式)
    - [3.1 排查节点热点 key，确定 top commands](#31-排查节点热点-key确定-top-commands)
    - [3.2 系统是否使用较大的集合键](#32-系统是否使用较大的集合键)
    - [3.3 检查集群每个分片的数据槽分配是否均匀](#33-检查集群每个分片的数据槽分配是否均匀)
    - [3.4 系统是否大量使用 keys Hash Tags](#34-系统是否大量使用-keys-hash-tags)
    - [3.5 是否因为 client output buffer 异常，导致内存容量倾斜](#35-是否因为-client-output-buffer-异常导致内存容量倾斜)
  - [4.如何有效避免Redis集群倾斜问题](#4如何有效避免redis集群倾斜问题)
  - [5.参考](#5参考)

对于分布式系统而言，整个集群处理请求的效率和存储容量，往往取决于集群中响应最慢或存储增长最快的节点。所以在系统设计和容量规划时，我们尽量保障集群中各节点的”数据和请求分布均衡“。但在实际生产系统中，出现数据容量和请求倾斜（类似 Data Skew）问题是比较常见的。

示例：2019 年春节抽奖服务，业务评估峰值 QPS 是 2w，转化到 Redis 集群为 10w QPS 和 5GB 内存存储，部署 5 个分片每个分片 1GB+2W QPS 的 Redis 集群（包含预留容量）。结果活动开始时，才发现服务存在”热点 key"，请求严重倾斜, 峰值时的 6w QPS 都集中到其中一个分片，导致这分片过载，整个抽奖服务雪崩。

Redis分布式集群倾斜问题，主要分为两类：

1. 数据存储容量倾斜，数据存储总是落到集群中少数节点；
2. QPS 请求倾斜，QPS 总是落到少数节点。

## 1.Redis 集群出现倾斜的影响

1. QPS 集中到少数 Redis 节点，引起少数节点过载，会拖垮整个服务，同时集群处理 QPS 能力不具备可扩展性；
2. 数据容量倾斜，导致少数节点内存爆增，出现 OOM Killer 和集群存储容量不具备可扩展性；
3. 运维管理变复杂，类似监控告警内存使用量、QPS、连接数、Redis cpu busy 等值不便统一；
4. 因集群内其他节点资源不能被充分利用，导致 Redis 服务器/容器资源利率低；
5. 增大自动化配置管理难度；单集群节点尽量统一参数配置；

## 2.导致 Redis 集群倾斜的常见原因

一般是系统设计时，键空间（keyspace）设计不合理：

+ 系统设计时，Redis 键空间（keyspace）设计不合理，出现**热点 key**，导致这类 key 所在节点 QPS 过载，集群出现 QPS 倾斜；
+ 系统存在大的集合 key（hash，set，list 等），导致大 key 所在节点的容量和 QPS 过载，集群出现 QPS 和容量倾斜；
+ DBA 在规划集群或扩容不当，导致数据槽（slot）数分配不均匀，导致容量和请求 QPS 倾斜；
+ 系统大量使用 Keys Hash Tags, 可能导致某些数据槽位的 key 数量多，集群集群出现 QPS 和容量倾斜；
+ 工程师执行 monitor 这类命令，导致当前节点 client 输出缓冲区增大；used_memory_rss 被撑大；导致节点内存容量增大，出现容量倾斜；

## 3.Redis 集群倾斜问题的排查方式

### 3.1 排查节点热点 key，确定 top commands

当集群因热点 key 导致集群 QPS 倾斜，需快速定位热点 key 和 top commands。可使用开源工具 redis-faina，或有实时 Redis 分析平台更好。

以下是使用 redis-faina 工具分析，可见两个前缀 key 的 QPS 占比基本各为 50%, 明显热点 key；也能看到 auth 命令的异常（top commands）。

```c
Overall Stats
========================================
Lines Processed         100000
Commands/Sec            7276.82

Top Prefixes
========================================
ar_xxx         49849   (49.85%)

Top Keys
========================================
c8a87fxxxxx        49943   (49.94%)
a_r:xxxx           49849   (49.85%)

Top Commands
========================================
GET             49964   (49.96%)
AUTH            49943   (49.94%)
SELECT          88      (0.09%)
```

### 3.2 系统是否使用较大的集合键

系统使用大 key 导致集群节点容量或 QPS 倾斜，比如一个 5kw 字段的 hash key, 内存占用在近 10GB，这个 key 所在 slot 的节点的内存容量或 QPS 都很有可能倾斜。

这类集合 key 每次操作几个字段，很难从 proxy 或 sdk 发现 key 的大小。

可使用 redis-cli --bigkeys 分析节点存在的大键。如果需全量分析，可使用 [redis-rdb-tools](https://github.com/sripathikrishnan/redis-rdb-tools) 对节点的 RDB 文件全量分析，通过结果 size_in_bytes 列得到大 key 的占用内存字节数。

示例使用 redis-cli 进行抽样分析：

```sh
redis-cli  --bigkeys -p 7000                                 

# Scanning the entire keyspace to find biggest keys as well as
# average sizes per key type.  You can use -i 0.1 to sleep 0.1 sec
# per 100 SCAN commands (not usually needed).
[00.00%] Biggest string found so far 'key:000000019996' with 1024 bytes
[48.57%] Biggest list   found so far 'mylist' with 534196 items
-------- summary -------
Sampled 8265 keys in the keyspace!
Total key length in bytes is 132234 (avg len 16.00)

Biggest string found 'key:000000019996' has 1024 bytes
Biggest   list found 'mylist' has 534196 items

8264 strings with 8460296 bytes (99.99% of keys, avg size 1023.75)
1 lists with 534196 items (00.01% of keys, avg size 534196.00)
```

### 3.3 检查集群每个分片的数据槽分配是否均匀

下面以 Redis Cluster 集群为例确认集群中，每个节点负责的数据槽位（slots）和 key 个数。下面 demo 的部分实例存在不轻度“倾斜”但不严重，可考虑进行 reblance.

```c
redis-trib.rb info redis_ip:port
nodeip:port (5e59101a...) -> 44357924 keys | 617 slots | 1 slaves.
nodeip:port (72f686aa...) -> 52257829 keys | 726 slots | 1 slaves.
nodeip:port (d1e4ac02...) -> 45137046 keys | 627 slots | 1 slaves.
---------------------省略------------------------
nodeip:port (f87076c1...) -> 44433892 keys | 617 slots | 1 slaves.
nodeip:port (a7801b06...) -> 44418216 keys | 619 slots | 1 slaves.
nodeip:port (400bbd47...) -> 45318509 keys | 614 slots | 1 slaves.
nodeip:port (c90a36c9...) -> 44417794 keys | 617 slots | 1 slaves.
[OK] 1186817927 keys in 25 masters.
72437.62 keys per slot on average.
```

### 3.4 系统是否大量使用 keys Hash Tags

在 Redis 集群中，有些业务为达到多键的操作，会使用 Hash Tags 把某类 key 分配同一个分片，可能导致数据、qps 都不均匀的问题。可使用 `scan` 扫描 keyspace 是否有使用 Hash Tags 的，或使用 monitor，vc-redis-sniffer 工具分析倾斜节点，是否大理包含有 Hash Tags 的 key。

### 3.5 是否因为 client output buffer 异常，导致内存容量倾斜

确认是否有 client 出现 output buffer 使用量异常，引起内存过大的问题；比如执行 monitor、keys 命令或 slave 同步 full sync 时出现客户端输入缓冲区占用过大。

这类情况基本 Redis 实例内存会快速增长，很快会出现回落。通过监测 client 输出缓冲区使用情况；分析见下面示例：

```sh
# 通过监控client_longest_output_list输出列表的长度，是否有client使用大量的输出缓冲区.
redis-cli  -p 7000 info clients
# Clients
connected_clients:52
client_longest_output_list:9179
client_biggest_input_buf:0
blocked_clients:0

# 查看输出缓冲区列表长度不为0的client。 可见monitor占用输出缓冲区370MB
redis-cli  -p 7000 client list | grep -v "oll=0"
id=1840 addr=xx64598  age=75 idle=0 flags=O obl=0 oll=15234 omem=374930608 cmd=monitor
```

## 4.如何有效避免Redis集群倾斜问题

+ 系统设计 Redis 集群键空间和 query pattern 时，应避免出现热点 key, 如果有热点 key 逻辑，尽量打散分布不同的节点或添加程序本地缓存；
+ 系统设计 Redis 集群键空间时，应避免使用大 key，把 key 设计拆分打散；大 key 除了倾斜问题，对集群稳定性有严重影响；
+ Redis 集群部署和扩缩容处理，保证数据槽位分配平均；
+ 系统设计角度应避免使用 keys Hash Tag；
+ 日常运维和系统中应避免直接使用 keys，monitor 等命令，导致输出缓冲区堆积；这类命令建议作 rename 处理；
+ 合量配置 normal 的 client output buffer, 建议设置 10mb，slave 限制为 1GB 按需要临时调整（警示:和业务确认调整再修改，避免业务出错）。

在实际生产业务场景中，大规模集群很难做到集群的完全均衡，只是尽量保证不出现严重倾斜问题。

## 5.参考

[浅析 Redis 分布式集群倾斜问题 - 腾讯云](https://cloud.tencent.com/developer/article/1983840)