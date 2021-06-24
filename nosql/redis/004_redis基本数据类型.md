# redis 数据类型

Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。 它支持多种类型的数据结构，如 **字符串（strings）**， **散列（hashes）**， **列表（lists）**， **集合（sets）**， **有序集合（sorted sets）** 与范围查询， **bitmaps**， **hyperloglogs** 和 **地理空间（geospatial）** 索引半径查询。 Redis 内置了 **复制（replication）**，**LUA脚本（Lua scripting）**， **LRU驱动事件（LRU eviction）**，**事务（transactions）** 和不同级别的 **磁盘持久化（persistence）**， 并通过 **Redis哨兵（Sentinel）**和自动 **分区（Cluster）**提供高可用性（high availability）。

## Redis Key 操作

```sh
keys *      # 查看所有的 key 

EXISTS key  # 判断当前的 key 是否存在

move key 1  # 移除当前的 key，一般不会这么用

expire key seconds  # 设置 key 的过期时间，单位秒

ttl key     # 查看当前 key 的剩余时间

type key    # 查看当前 key 的类型
```

## String(字符串)

```bash
set key     # 设置值

get key     # 获得值

APPEND key value    # 追加字符串，如果 key 不存在，则新建

STRLEN key  # 获取字符串的长度


incr key    # 自增 1

decr key    # 自减 1

INCRBY|DECRBY key number   # 设置自增/自减步长


GETRANGE key start end  # 截取字符串，闭区间，起始 0

SETRANGE key offset value   # 替换指定位置开始到结尾的字符串


SETEX key seconds value     # 设置 key-value 并设置过期时间(单位：秒)

SETNX key value     # 当 key 不存在时创建该 key-value


MSET key value [key value ...]  # 同时设置多个 key-value

MGET key [key ...]  # 获取所有指定 key 值

MSETNX key value [key value]    # 设置多个 key-value，仅当所有 key 不存在时才会设置成功，体现了 redis 的原子性


GETSET key value    # 设置一个 key-value，并获取设置前的值
```

String 的使用场景：value 除了是字符串还可以是数字

+ 计数器

+ 统计计数

+ 对象缓存存储

## List

```bash
LPUSH key value [value ...]     # 从队列左边入队一个或多个元素

RPUSH key value [value ...]     # 从队列的右边入队一个或多个元素

LRANGE key start stop   # 从列表中获取指定返回的元素


LPOP key    # 从队列左边出队一个元素

RPOP key    # 从队列右边出队一个元素


LINDEX key index    # 通过下标 index 获取 list 中的一个元素
# 当 index 超过范围的时候返回 nil


LLEN key    # 获取队列 list 的长度


LREM key count value    # 移除 list 中指定 count 个数的 value 值
# count > 0: 从头往尾移除值为 value 的元素。
# count < 0: 从尾往头移除值为 value 的元素。
# count = 0: 移除所有值为 value 的元素。


LTRIM key start stop    # 通过下标截取指定长度，并替换原 list 值
# start 和 end 也可以用负数来表示与表尾的偏移量，比如 -1 表示列表里的最后一个元素， -2 表示倒数第二个，等等。


RPOPLPUSH source destination    # 移除列表的最后一个元素，并将它移动到新的列表中


LSET key index value    # 将列表中指定下标的值替换为另一个值，不存在则报错


LINSERT key BEFORE|AFTER pivot value
# 把 value 插入存于 key 的列表中在基准值 pivot 的前面或后面。
# 当 key 不存在时，这个 list 会被看作是空 list，任何操作都不会发生。
# 当 key 存在，但保存的不是一个 list 的时候，会返回 error。
# 当 pivot 值找不到的时候返回 -1。
```

+ list 实际上可以看做是一个双向链表

+ 在两边插入或者改动值，效率最高，中间元素，相对来说效率会低一点

+ 适用场景：队列、栈、消息队列......

## Set(无序不重复集合)

set 中的值是不能重复的。

```bash
SADD key member [member ...]
# 添加一个或多个指定的 member 元素到集合的 key 中
# 添加多个时如果 member 重复则忽略，并且会继续添加其他 member

SMEMBERS key    # 返回 key 集合所有的元素.

SISMEMBER key member    # 返回成员 member 是否是集合 key 的成员.


SCARD key   返回集合元素的数量. 


SREM key member [member ...]    # 移除 key 集合中指定元素，如果不是集合中的元素则忽略，并继续移除其他 member


SRANDMEMBER key [count]
# 随机返回 key 集合中的 count 个元素，默认是 1 个.
# 当 count 大于集合长度，则返回集合所有元素
# 当 count 小于 0 则取绝对值


SPOP key [count]    # 随机从存储 key 集合中移除并返回一个或多个随机元素。


SMOVE source destination member
# 将 member 从 source 集合移动到 destination 集合中. 
# 如果 source 集合不存在或者不包含指定的元素，则不执行任何操作
# 如果 destination 集合不存在或者不包含指定的元素，则只发生 source 移除操作


SDIFF key [key ...]     
# 返回一个集合与给定集合的差集的元素.
# 如果是多个 key，则是取并集的差集

SINTER key [key ...]    # 返回指定所有的集合的成员的交集.

SUNION key [key ...]    # 返回给定的多个集合的并集中的所有成员.
```

使用场景：

用户粉丝和关注分别放到 set 里，可以取到两个用户之间的共同关注、共同好友等信息。

## Hash(哈希)

Hash 可以看做 Key-Map 集合

```bash
HSET key field value    # 设置 key 指定的哈希集中指定字段的值，如果存在，则覆盖

HGET key field  # 返回 key 指定的哈希集中该字段所关联的值

HMSET key field value [field value ...]     # 批量设置 key-value。如果存在则覆盖

HMGET key field [field ...]     # 批量返回指定字段，不存在则返回 nil

HGETALL key     # 返回 key 指定的哈希集中所有的字段和值。

HDEL key field [field ...]      # 删除一个或多个指定的 field，如不存在则忽略


HLEN key    # 返回 key 指定的哈希集包含的字段的数量。


HEXISTS key field   # 返回 hash 里面 field 是否存在


HKEYS key   # 返回所有 field

HVALS key   # 返回所有 value


HINCRBY key field increment     # 增加 key 指定的哈希集中指定字段的数值。如 field 不存在，则创建


HSETNX key field value
# 只在 key 指定的哈希集中不存在指定的字段时，设置字段的值。
# 如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key 关联。
# 如果字段已存在，该操作无效果。
```

## zset(有序集合)

```bash
ZADD key [NX|XX] [CH] [INCR] score member [score member ...]
# 添加一个或多个 score/member 对
# XX : 仅仅更新存在的成员，不添加新成员。
# NX : 不更新存在的成员。只添加新成员。
# CH: 修改返回值为发生变化的成员总数，原始是返回新添加成员的总数 (CH 是 changed 的意思)。更改的元素是新添加的成员，已经存在的成员更新分数。所以在命令中指定的成员有相同的分数将不被计算在内。注：在通常情况下，ZADD返回值只计算新添加成员的数量。
# INCR: 当ZADD指定这个选项时，成员的操作就等同 ZINCRBY 命令，对成员的分数进行递增操作。


ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]
# 对 key 里的 score/member 进行排序
# min/max 可以指定为 -inf/+inf，也可以使用区间写法

ZRANGE key start stop [WITHSCORES]
# 返回存储在有序集合 key 中的指定范围的元素。 返回的元素可以认为是按得分从最低到最高排列。如果得分相同，将按字典排序。

ZREVRANGE key start stop [WITHSCORES]
# 同上。排序从高到低。


ZREM key member [member ...]    # 移除指定 member


ZCARD key   # 返回key的有序集元素个数。


ZCOUNT key min max  # 返回有序集 key 中，score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max)的成员。
```

使用场景：

班级成绩表、工资表、带权重信息