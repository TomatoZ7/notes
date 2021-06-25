# redis 3 种特殊的数据类型

## Geospital 地理位置

Redis 的 Geo 在 3.2 版本就推出了，这个功能可以推算地理位置的信息，两地之间的距离，方圆几里的人。

### GEOADD key longitude latitude member [longitude latitude member ...]

longitude : 经度，取值从 -180 度到 180 度

latitude : 纬度，取值从 -85.05112878 到 85.05112878 度

当取值超出时，会返回一个错误。

```bash
GEOADD china:city 116.40 39.90 beijing
```

### GEOPOS key member [member ...]

从key里返回所有给定位置元素的位置（经度和纬度）。

```bash
GEOPOS china:city beijing
```

### GEODIST key member1 member2 [unit]

返回两个给定位置之间的距离。

如果两个位置之间的其中一个不存在， 那么命令返回空值。

指定单位的参数 unit 必须是以下单位的其中一个：

+ m 表示单位为米。(默认)
+ km 表示单位为千米。
+ mi 表示单位为英里。
+ ft 表示单位为英尺。

### GEORADIUS key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]

以给定的经纬度为中心， 返回键包含的位置元素当中， 与中心的距离不超过给定最大距离的所有位置元素。

在给定以下可选项时， 命令会返回额外的信息：

+ WITHDIST : 直线距离

+ WITHCOORD : 经纬度

+ WITHHASH : 以 52 位有符号整数的形式，返回位置元素经过原始 geohash 编码的有序集合分值。这个选项主要用于底层应用或者调试，实际中的作用并不大。

+ COUNT : 获取前 N 个匹配元素

```bash
GEORADIUS china:city 110 30 500 km
```

### GEORADIUSBYMEMBER key member radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]

找出位于指定范围内的元素，`GEORADIUSBYMEMBER` 的中心点是由给定的位置元素决定的， 而不是像 `GEORADIUS` 那样， 使用输入的经度和纬度来决定中心点。

```bash
GEORADIUSBYMEMBER china:city beijing 500 km
```

### GEOHASH key member [member ...]

返回一个或多个位置元素的 Geohash 表示。就是将二维的经纬度转换成一维的 hash 字符串。

### GEO 底层的实现原理就是 zset，也可以使用 zset 命令操作 GEO

```bash
127.0.0.1:6379> ZRANGE china:city 0 -1
1) "beijing"
127.0.0.1:6379> ZREM china:city beijing
(integer) 1
```

## Hyperloglog

Redis 2.8.9 版本就更新了 Hyperloglog 数据结构。

Redis Hyperloglog 是基数统计的算法。

场景分析：

统计 UV，传统来说可以使用 set 统计，将用户 id 放到 set 里面，保证不重复。但是如果是用户 id 是 uuid 等字符串形式，会占据比较大的内存空间。这时候就可以考虑使用 Hyperloglog 了。

优点：占用的内存是固定的，可以存储 2^64 个不同的元素，只需要占 12kb 的内存。从内存优化的角度来比较，Hyperloglog 首选。

官方表示有 0.81% 的错误率。

### PFADD key element [element ...]

### PFCOUNT key [key ...]

### PFMERGE destkey sourcekey [sourcekey ...]

将多个 HyperLogLog 合并（merge）为一个 HyperLogLog ， 合并后的 HyperLogLog 的基数接近于所有输入 HyperLogLog 的可见集合（observed set）的并集.

合并得出的 HyperLogLog 会被储存在目标变量（第一个参数）里面， 如果该键并不存在， 那么命令在执行之前， 会先为该键创建一个空的.

## Bitmaps

位存储

场景：记录用户打卡，由于只可以存 0 和 1，所以有局限性。

### SETBIT key offset value

设置或者清空 key 的 value (字符串)在 offset 处的 bit 值。

### GETBIT key offset

返回 key 对应的 string 在 offset 处的 bit 值。

BITCOUNT key [start end]

统计字符串被设置为 1 的 bit 数.