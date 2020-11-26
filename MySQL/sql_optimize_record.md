# 记录一次 MySQL 百万级连表优化​

## 假设某游戏中存在下述两张表：

### 角色创建表：role_create

| id(自增id) | role_id(角色id) | date_time(创建时间) |
| :-------: | :-------------: | :----------------: |
| 1 | 10000 | 2019-01-01 12:00:00 |
| 2 | 10001 | 2019-01-02 12:00:00 |

### 角色升级表：role_level

| id(自增id) | role_id(角色id) | level(等级) | date_time(创建时间) |
| :-------: | :-------------: | :--------: | :---: |
| 1 | 10000 | 1 | 2019-01-01 12:00:00 |
| 2 | 10000 | 2 | 2019-01-01 12:30:00 |
| 3 | 10001 | 1 | 2019-01-02 13:00:00 |
| 4 | 10001 | 2 | 2019-01-02 13:30:00 |
| 5 | 10001 | 3 | 2019-01-02 14:30:00 |

&emsp;

## 问题是这样的：
### 查找在 2019-01-01 号到 2019-01-02 号期间创建角色，并且角色的最高等级 ≥ 3级的玩家。每个角色一条记录，取玩家最高等级值。查询结果显示格式：

| 角色id | 角色创建时间 | 角色最高等级
| :---: | :---: | :---: |
| 10001 | 2019-01-02 12:00:00 | 3

### 在数据量不大的情况下直接运行如下 sql 即可：
```
SELECT 
  c.role_id AS '角色id',
  c.date_time AS '角色创建时间',
  MAX(l.LEVEL) AS '角色最高等级'
FROM
  role_create AS c
  LEFT JOIN role_level AS l ON c.role_id = l.role_id
WHERE
  l.LEVEL >= 3
  AND DATE(c.date_time) BETWEEN '2019-01-01' AND '2019-01-02'
GROUP BY
  -- c.date_time,
  l.role_id
```

### 那么当 role_create 数据量达到 100W，role_level 数据量达到 1000W 的时候，对于上述的查询需求，有什么办法可以加快查询速度？

#### 首先生成测试数据，这里插播一下基于 php+sql 生成测试数据。
#### insert_data.php
```
<?php

set_time_limit(1000);

$t = time();

$file = "./insert.sql";
$fhandler = fopen($file, 'wb');

if ($fhandler) {
	$i = 1;
	while ($i <= 1000000) {
		$time = rand_time();
		$roleid = $i+100;

		$sql = "{$roleid}\t{$time}";
    	fwrite($fhandler, $sql."\n");

		$i++;
	}
	echo "写入成功,耗时：" . (time()-$t);
}

function rand_time(){
	$start = strtotime("2019-01-01 00:00:00");
	$end = strtotime("2019-01-05 00:00:00");

	return date('Y-m-d H:i:s', mt_rand($start, $end));
}
```
#### 运行 sql 命令
```
LOAD DATA local INFILE 'e:/insert.sql' INTO TABLE role_create(`role_id`, `date_time`);
```
#### 如果出现 `The used command is not allowed with this MySQL version` , 运行下面命令：
```
set global local_infile = 'ON';
```
#### 结果如下:
![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/sql_optimize_count.jpg)

&emsp;

## 开始调优
### step.1 直接运行 sql 语句,很难出结果。

### step.2 在 role_level 表上为 role_id 添加索引
我们一般会将数据量小的表作为驱动表，一般情况下Mysql 也会默认将小表作为驱动表。

此次查询结果如下，查询时间为 40 秒左右，查询到接近 50 万条记录：
![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/sql_optimize_step_one.jpg)

#### EXPLAIN 结果如下：
![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/sql_optimize_step_one_explain.jpg)


2.3 优化 sql1

sql1 全表扫描，尝试以下几个步骤：

2.3.1 去除 where 子句的 DATE 函数；

2.3.2 在 date_time 列添加索引；


此时做完上述两步，发现还是全表扫描