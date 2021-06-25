# redis 持久化之 AOF

## AOF(Append Only File)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_aof1.png)

以日志的形式来记录每个写操作，将 Redis 执行过的所有指令记录下来(读操作不记录)，只许追加文件但不可以改写文件，redis 启动之初会读取该文件重新构建数据，换言之，redis 重启的话就根据日志文件的内容讲写指令从前到后执行过一次以完成数据的恢复工作。

AOF 保存的是 appendonly.aof 文件

### 配置文件

```conf
############################## APPEND ONLY MODE ###############################

appendonly no       # 默认不开启
appendfilename "appendonly.aof"     # aof 持久化文件名

# appendfsync always        # 每次修改都会 sync，消耗性能
appendfsync everysec        # 每秒执行一次 sync，可能会丢失这 1 秒的数据
# appendfsync no            # 不执行 sync，操作系统自己同步数据，速度最快

auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb      # aof 默认是文件追加，如果 aof 文件超过 64m，fork 一个新进程来将文件进行重写
```

修改并重启： 

```conf
appendonly yes
```

### AOF 文件校验

如果 `appendonly.aof` 文件遭到破坏，这个时候是无法启动 redis 的，可以修复这个文件。

redis 给我们提供了一个工具 redis-check-aof，也位于 `/usr/local/bin` 目录下。

执行：

```bash
redis-check-aof --fix appendonly.aof
```

即可修复文件

## AOF 的优缺点

优点：

1. 可根据配置文件配置每一次修改都同步，文件的完整性更好

缺点：

1. 相对于数据文件来说，远远大于 RDB，且修复速度也比 RDB 慢

2. AOF 的运行效率也要比 RDB 慢

## 扩展

1、只做缓存，如果你只希望你的数据在服务器运行的时候存在，可以不使用持久化。

2、同时开启两种持久化方式

+ 在这种情况下，当 redis 重启的时候会优先载入 AOF 文件来恢复原始的数据，因为在通常情况下 AOF 文件保存的数据集要比 RDB 文件保存的数据集要完整。

+ RDB 的数据不实时，同时使用两者时服务器重启也只会找 AOF 文件，那要不要只使用 AOF 呢？建议不要。因为 RDB 更适合用于备份数据库（AOF 在不断变化不好备份），快速重启，而且不会有 AOF 可能潜在的 bug，以备不时之需。

3、性能建议

+ 因为 RDB 文件只用作后备用途，建议只在 Slave 上持久化 RDB 文件，而且只要 15 分钟备份一次就够了，只保留 `save 900 1` 这条规则。

+ 如果开启 AOF，好处是在最恶劣情况下也只会丢失不超过两秒数据，启动脚本较简单只 load 自己的 AOF 文件就可以了，代价一是带来了持续的 IO，二是 AOF rewrite 的最后将 rewrite 过程中产生的新数据写到新文件造成的阻塞几乎是不可避免的。只要硬盘许可，应该尽量减少 AOF rewrite 的频率，AOF 重写的基础大小默认值 64M 太小了，可以设到 5G 以上，默认超过原大小 100% 大小重写可以改到适当的数值。

+ 如果关闭 AOF，仅靠 Master-Slave Repllcation 实现高可用性也可以，能省掉一大笔 IO，也减少了 rewrite 时带来的系统波动。代价是如果 Master/Slave 同时挂掉，会丢失十几分钟的数据，启动脚本也要比较两个 Master/Slave 中的 RDB 文件，载入较新的那个，微博就是这种架构。