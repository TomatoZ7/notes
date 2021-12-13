# redis.conf 阅读

## 单位

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_conf1.png)

配置文件对 unit单位 大小写不敏感

## 包含

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_conf2.png)

## 网络

```conf
################################## MODULES #####################################

bind 127.0.0.1
protected-mode yes      # 保护模式，开启时会禁止远程连接进行 CRUD 操作
port 6379
```

## 通用

```conf
################################# GENERAL #####################################

daemonize yes       # 以守护进程的方式运行，默认 no

pidfile /var/run/redis_6379.pid     # 如果以守护进程的方式运行，就需要指定一个 pid 文件

# 日志
# Specify the server verbosity level.
# This can be one of:
# debug (a lot of information, useful for development/testing)
# verbose (many rarely useful info, but not a mess like the debug level)
# notice (moderately verbose, what you want in production probably)
# warning (only very important / critical messages are logged)
loglevel notice
logfile ""      # 日志的文件位置名

databases 16    # 数据库数量，默认 16
always-show-logo no     # 是否总是显示 logo
```

## 快照

这部分主要是跟持久化有关，在规定的时间内，执行了多少次操作，则会持久化到文件(.rdb|.aof)

```conf
################################ SNAPSHOTTING  ################################

save 3600 1         # 3600s 内有一个 key 发生了变化则持久化
save 300 100
save 60 10000

stop-writes-on-bgsave-error yes     # 持久化如果出错，是否还需要继续工作

rdbcompression yes          # 是否压缩 rdb 文件，会消耗一些 cpu 资源

rdbchecksum yes         # 保存 rdb 文件时是否进行错误的校验

dir ./      # rdb 文件保存的目录
```

## 复制

主从复制相关配置。

```conf
################################# REPLICATION #################################

replicaof <masterip> <masterport>
```

## 安全

可以设置 redis 密码

```bash
127.0.0.1:6379> clear
127.0.0.1:6379> ping
PONG
127.0.0.1:6379> config get requirepass
1) "requirepass"
2) ""
127.0.0.1:6379> config set requirepass "123456"     # 设置 redis 密码
OK
127.0.0.1:6379> auth 123456         # 使用密码登录
OK
127.0.0.1:6379> config get requirepass
1) "requirepass"
2) "123456"
```

## 客户端

```conf
################################### CLIENTS ####################################

maxclients 10000        # 设置客户端能连接上 redis 的最大数量
```

## 内存管理

```conf
############################## MEMORY MANAGEMENT ################################

maxmemory <bytes>       # redis 配置最大内存容量

maxmemory-policy noeviction     # 内存达到上限后的处理策略
    # volatile-lru : 只对设置了过期时间的 key 进行 LRU（默认值） 
    # allkeys-lru : 删除 lru 算法的 key   
    # volatile-random : 随机删除即将过期 key   
    # allkeys-random : 随机删除   
    # volatile-ttl : 删除即将过期的   
    # noeviction : 永不过期，返回错误
```

## AOF

```conf
############################## APPEND ONLY MODE ###############################

appendonly no       # 默认不开启
appendfilename "appendonly.aof"     # aof 持久化文件名

# appendfsync always        # 每次修改都会 sync，消耗性能
appendfsync everysec        # 每秒执行一次 sync，可能会丢失这 1 秒的数据
# appendfsync no            # 不执行 sync，操作系统自己同步数据，速度最快
```