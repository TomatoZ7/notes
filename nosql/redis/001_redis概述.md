# redis 概述

## Redis 是什么

Redis (<span style="color:red;">Re</span>mote <span style="color:red;">Di</span>ctionary <span style="color:red;">S</span>erver)，即远程字典服务！

是一个开源的使用 ANSI C语言编写、支持网络、可基于内存亦可持久化的日志型、Key Value 数据库，并提供多种语言的 API。

Redis 会周期性的把更新的数据写入磁盘或者把修改操作写入追加的记录文件，并且在此基础上实现了 master-slave (主从)同步。

## Redis 能做什么

1. 内存存储、持久化(内存中是断电即失，所以持久化很重要rdb、aof)

2. 效率高，可以用于高速缓存

3. 发布订阅系统

4. 地图信息分析

5. 计时器、计数器

......

## 特性

1. 多样的数据类型

2. 持久化

3. 集群

4. 事务

......

## 相关文档

1. 官网 [https://redis.io/](https://redis.io/)

2. 中文网 [https://www.redis.cn/](https://www.redis.cn/)