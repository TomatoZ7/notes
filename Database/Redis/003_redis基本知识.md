# redis 基本知识

## 数据库

redis 默认有 16 个数据库：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_basic1.jpg)

默认使用第 0 个数据库。

### 切换数据库

可以使用 `select` 切换数据库。

```bash
127.0.0.1:6379> select 3
OK
127.0.0.1:6379[3]> DBSIZE
(integer) 0
```

### 查看当前数据库所有 key

```bash
keys *
```

### 清空当前数据库

```bash
flushdb
```

### 清空全部数据库

```bash
FLUSHALL
```

## 默认端口 6379

[Redis 为什么要用 6379 作为默认端口？](https://www.zhihu.com/question/20084750)

## 单线程

官方表示：Redis 是基于内存操作，CPU 不是 Redis 性能瓶颈，Redis 的瓶颈是根据机器的内存和网络带宽，既然可以使用单线程来实现，就用单线程了。(redis6 之前)

Redis 是 C 语言写的，官方提供的数据为 10W+ QPS。

### Redis 单线程为什么还这么快？

先避免两个误区：

误区1 : 高性能的服务器一定是多线程的。

误区2 : 多线程一定比单线程效率高。

核心 : redis 是将所有的数据全部放在内存中的，多线程由于 CPU 上下文切换是比较耗时的操作，而对于内存系统来说，如果没有上下文切换，效率就是最高的。多次读写都在一个 CPU 上，在内存情况下，就是最佳的方案。