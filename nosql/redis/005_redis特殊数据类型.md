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