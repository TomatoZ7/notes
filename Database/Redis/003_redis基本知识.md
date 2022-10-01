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