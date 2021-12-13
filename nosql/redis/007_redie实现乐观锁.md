# redis 实现乐观锁

使用 `WATCH` 进行监控，并且事务提交的时候获取版本，比较版本。

## redis 监视测试

窗口1：

```bash
127.0.0.1:6379> set money 1000
OK
127.0.0.1:6379> set out 0
OK
127.0.0.1:6379> WATCH money
OK
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> DECRBY money 500
QUEUED
127.0.0.1:6379(TX)> INCRBY out 500      # 此时事务还未执行
QUEUED
```

窗口2：

```bash
127.0.0.1:6379> DECRBY money 200
(integer) 800
127.0.0.1:6379> INCRBY out 200
(integer) 200
```

窗口1：

```bash
127.0.0.1:6379(TX)> exec
(nil)
127.0.0.1:6379> get money
"800"
127.0.0.1:6379> get out
"200"
127.0.0.1:6379> MULTI   # 不管事务成不成功执行，WATCH 都会在 exec 命令后释放
OK
127.0.0.1:6379(TX)> DECRBY money 500
QUEUED
127.0.0.1:6379(TX)> INCRBY out 500
QUEUED
127.0.0.1:6379(TX)> exec
1) (integer) 300
2) (integer) 700
127.0.0.1:6379> get money
"300"
127.0.0.1:6379> get out
"700"
```