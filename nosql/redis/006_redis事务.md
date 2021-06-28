# redis 事务

Redis 事务本质：一组命令的集合。一个事务中的所有命令都会被序列化，在事务执行过程中，会顺序执行。

事务特性：一次性、顺序性、排他性(执行过程中不允许其他指令执行)。(没有隔离级别的概念)

所有的命令在事务中，并没有直接被执行。只有**发起执行命令的时候才会执行**。

**Redis 单条命令是保证原子性的，但是事务不保证原子性。**

## redis 事务流程

1. 开启事务(MULTI)

2. 命令入队(...)

3. 执行事务(EXEC)

## 执行事务实例

```bash
127.0.0.1:6379> MULTI           # 开启事务
OK
127.0.0.1:6379(TX)> set k1 v1   # 命令入队
QUEUED
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> get k2
QUEUED
127.0.0.1:6379(TX)> set k3 v3
QUEUED
127.0.0.1:6379(TX)> exec        # 执行事务
1) OK
2) OK
3) "v2"
4) OK
```

## 放弃事务实例

```bash
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> set k1 v1
QUEUED
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> set k4 v4
QUEUED
127.0.0.1:6379(TX)> DISCARD     # 取消事务
OK
127.0.0.1:6379> get k4
(nil)
```

## 编译型异常 (代码/命令错误)，事务中所有的命令都不会执行

```bash
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> set k1 v1
QUEUED
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> getset k1   # 错误的命令
(error) ERR wrong number of arguments for 'getset' command
127.0.0.1:6379(TX)> set k3 v3
QUEUED
127.0.0.1:6379(TX)> EXEC        # 执行事务报错
(error) EXECABORT Transaction discarded because of previous errors.
127.0.0.1:6379> get k1          # 所有的命令都不执行
(nil)
127.0.0.1:6379> get k2
(nil)
```

## 运行时异常 (1/0)，其他命令正常执行，错误命令抛出异常

```bash
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> set k1 v1
QUEUED
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> getset k1
(error) ERR wrong number of arguments for 'getset' command
127.0.0.1:6379(TX)> set k3 v3
QUEUED
127.0.0.1:6379(TX)> EXEC        # 有命令报错，依旧正常执行其他命令
(error) EXECABORT Transaction discarded because of previous errors.
127.0.0.1:6379> get k1
(nil)
127.0.0.1:6379> get k2
(nil)
```