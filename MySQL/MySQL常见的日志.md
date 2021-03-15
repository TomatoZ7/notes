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

1. 复制：MySQL 主从复制在 Master 端开启 binlog，Master 把它的二进制日志传递给 slaves 并回放来达到 master-slave 数据一致的目的
2. 数据恢复：通过 mysqlbinlog 工具恢复数据

### 内容

binlog 记录数据库执行的写入性操作（不包括查询）信息，以二进制的形式保存在磁盘中，是逻辑格式的日志，可以简单认为就是执行过的事务中的 sql 语句。
但不完全是 sql 语句这么简单，而是包括了执行的 sql 语句（增删改）的反向信息，也就意味着 delete 对应着 delete 本身和其反向的 insert；update 对应着 update 执行前后的版本信息；insert 对应着 delete 和 insert 本身的信息。

在使用 mysqlbinlog 解析 binlog 之后一切会真相大白。因此可以基于 binlog 做到类似于 oracle 的闪回功能，其实都是依赖于 binlog 中的日志记录。

### binlog 日志格式

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/mysql_log2.png)

### 什么时候产生

事务提交的时候，一次性将事务中的 sql 语句(一个事务可能对应多条 sql 语句)按照一定的格式记录到 binlog 中。

> 这里与 redolog 很明显的差异是 redolog 并不一定是在事务提交的时候刷新到磁盘，redolog 是在事务开启之后就开始逐步写入磁盘。

因此对于事务的提交，即便是较大的事务，提交(commit)都是很快的，但是在开启了 binlog 的情况下，对于较大事务的提交，可能会变得比较慢一点。

### 什么时候释放

binlog 的默认保持时间由参数 `expire_logs_days` 配置，也就是说对于非活动的日志文件，在生成时间超过 `expire_logs_days` 配置的天数之后，会被删除。

### 对应的物理文件

默认情况下，对应的物理文件位于数据库的 data 目录下的 ib_logfile1 & ib_logfile2。
innodb_log_group_home_dir 指定日志文件组所在的路径，默认 ./，表示在数据库的数据目录下。
innodb_log_files_in_group 指定 binlog 文件组中文件的数量，默认2。

关于文件的大小和数量，由以下两个参数配置：<br/>
`innodb_log_file_size` binlog 的大小<br/>
`innodb_mirrored_log_groups` 指定了日志镜像文件组的数量，默认1

### 其他

很重要的一点，redo log 是什么时候开始写盘的？前面说了是在事物开始之后逐步写盘的。

之所以说 binlog 是在事务开始之后逐步写入 binlog 文件，而不一定是事务提交才写入 binlog 缓存，原因就是 binlog 有一个缓存区 innodb_log_buffer。innodb_log_buffer 的默认大小为 8M，innodb 存储引擎会先将 binlog 写入 innodb_log_buffer 中，然后会通过以下三种方式将 innodb 日志缓冲区的日志刷新到磁盘。

1. Master Thread 每秒一次执行刷新 innodb_log_buffer 到 binlog 文件。
2. 每个事务提交时会将 binlog 刷新到 binlog 文件。
3. 当 binlog 缓存可用空间少于一半时，binlog 缓存被刷新到 binlog 文件。

由此可以看出，binlog 通过不止一种方式写入到磁盘，尤其对于第 1 种方式，innodb_log_buffer 到 binlog 文件是 Master Thread 线程的定时任务。

因此 binlog 的写盘，并不一定随着事务的提交才写入 binlog 文件，而是随着事务的开始，逐步开始的。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/mysql_log1.png)

引用 《MySQL技术内幕 Innodb 存储引擎》(page37) 上的原话：

> 即使某个事务还没有提交，Innodb存储引擎仍然每秒会将重做日志缓存刷新到重做日志文件。这一点是必须要知道的，因为这可以很好地解释再大的事务的提交（commit）的时间也是很短暂的。