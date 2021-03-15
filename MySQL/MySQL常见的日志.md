# MySQL 常见的日志

MySQL 中有七种日志文件，分别是：

+ 二进制日志 binlog
+ 重做日志 redolog
+ 回滚日志 undolog
+ 错误日志 errorlog
+ 慢查询日志 slow query log
+ 一般查询日志 general log
+ 中继日志 relay log

## 二进制日志 (binlog)

### 作用

1. 用于复制，在主从复制中，从库利用主库上的 binlog 进行重播，实现主从同步。
2. 用于数据库的基于时间点的还原。

### 内容

binlog 记录数据库执行的写入性操作（不包括查询）信息，以二进制的形式保存在磁盘中，是逻辑格式的日志，可以简单认为就是执行过的事务中的 sql 语句。
但不完全是 sql 语句这么简单，而是包括了执行的 sql 语句（增删改）的反向信息，也就意味着 delete 对应着 delete 本身和其反向的 insert；update 对应着 update 执行前后的版本信息；insert 对应着 delete 和 insert 本身的信息。

在使用 mysqlbinlog 解析 binlog 之后一切会真相大白。因此可以基于 binlog 做到类似于 oracle 的闪回功能，其实都是依赖于 binlog 中的日志记录。

### 什么时候产生

事务提交的时候，一次性将事务中的 sql 语句(一个事务可能对应多条 sql 语句)按照一定的格式记录到 binlog 中。

> 这里与 redolog 很明显的差异是 redolog 并不一定是在事务提交的时候刷新到磁盘，redolog 是在事务开启之后就开始逐步写入磁盘。

因此对于事务的提交，即便是较大的事务，提交(commit)都是很快的，但是在开启了 binlog 的情况下，对于较大事务的提交，可能会变得比较慢一点。

### 什么时候释放

binlog 的默认保持时间由参数 `expire_logs_days` 配置，也就是说对于非活动的日志文件，在生成时间超过 `expire_logs_days` 配置的天数之后，会被删除。

