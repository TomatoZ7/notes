# MySQL 实战 50 题

## 建表

首先建立好 4 张表，并插入数据。

### 学生表

```sql
-- 创建学生表
CREATE TABLE IF NOT EXISTS student(
    s_id VARCHAR(10) COMMENT '学生id',
    s_name VARCHAR(20) COMMENT '姓名',
    s_age DATE COMMENT '出生年月',
    s_sex VARCHAR(10) COMMENT '性别'
);

-- 往学生表中插入数据
INSERT INTO student VALUES('01' , '赵雷' , '1990-01-01' , '男');
INSERT INTO Student VALUES('02' , '钱电' , '1990-12-21' , '男');
INSERT INTO Student VALUES('03' , '孙风' , '1990-05-20' , '男');
INSERT INTO Student VALUES('04' , '李云' , '1990-08-06' , '男');
INSERT INTO Student VALUES('05' , '周梅' , '1991-12-01' , '女');
INSERT INTO Student VALUES('06' , '吴兰' , '1992-03-01' , '女');
INSERT INTO Student VALUES('07' , '郑竹' , '1989-07-01' , '女');
INSERT INTO Student VALUES('08' , '王菊' , '1990-01-20' , '女');
```

### 课程表

```sql
-- 创建课程表
CREATE TABLE IF NOT EXISTS course(
    c_id VARCHAR(10) COMMENT '课程id',
    c_name VARCHAR(20) COMMENT '课程名',
    t_id VARCHAR(10) COMMENT '教师id'
);

-- 往课程表插入数据
INSERT INTO Course VALUES('01' , '语文' , '02');
INSERT INTO Course VALUES('02' , '数学' , '01');
INSERT INTO Course VALUES('03' , '英语' , '03');
```

### 教师表

```sql
-- 创建教师表
CREATE TABLE IF NOT EXISTS teacher (
    t_id VARCHAR(10) COMMENT '教师id',
    t_name VARCHAR(20) COMMENT '姓名'
);

-- 往教师表插入数据
INSERT INTO Teacher VALUES('01' , '张三');
INSERT INTO Teacher VALUES('02' , '李四');
INSERT INTO Teacher VALUES('03' , '王五');
```

### 分数表

```sql
-- 创建成绩表
CREATE TABLE IF NOT EXISTS score (
    s_id VARCHAR(10) COMMENT '分数id',
    c_id VARCHAR(10) COMMENT '课程id',
    score VARCHAR(10) COMMENT '分数'
);

-- 往成绩表插入数据
INSERT INTO Score VALUES('01' , '01' , 80);
INSERT INTO Score VALUES('01' , '02' , 90);
INSERT INTO Score VALUES('01' , '03' , 99);
INSERT INTO Score VALUES('02' , '01' , 70);
INSERT INTO Score VALUES('02' , '02' , 60);
INSERT INTO Score VALUES('02' , '03' , 80);
INSERT INTO Score VALUES('03' , '01' , 80);
INSERT INTO Score VALUES('03' , '02' , 80);
INSERT INTO Score VALUES('03' , '03' , 80);
INSERT INTO Score VALUES('04' , '01' , 50);
INSERT INTO Score VALUES('04' , '02' , 30);
INSERT INTO Score VALUES('04' , '03' , 20);
INSERT INTO Score VALUES('05' , '01' , 76);
INSERT INTO Score VALUES('05' , '02' , 87);
INSERT INTO Score VALUES('06' , '01' , 31);
INSERT INTO Score VALUES('06' , '03' , 34);
INSERT INTO Score VALUES('07' , '02' , 89);
INSERT INTO Score VALUES('07' , '03' , 98);
```

## 练习

### 1、查询"01"课程比"02"课程成绩高的学生的学号及课程分数

```sql
SELECT 
    a.s_id, score1, score2 
FROM
    (SELECT s_id, score AS score1 FROM score WHERE c_id = '01') AS a
    INNER JOIN
    (SELECT s_id, score AS score2 FROM score WHERE c_id = '02') AS b
    ON a.s_id = b.s_id
WHERE
    score1 > score2;
```

### 2、查询"01"课程比"02"课程成绩高的学生的信息及课程分数

```sql
SELECT 
    s.*, score1, score2
FROM
    (SELECT s_id, score AS score1 FROM score WHERE c_id = '01') AS a
    INNER JOIN (SELECT s_id, score AS score2 FROM score WHERE c_id = '02') AS b ON a.s_id = b.s_id
    INNER JOIN student AS s ON s.s_id = a.s_id
WHERE
    score1 > score2;
```

### 3、查询平均成绩大于等于60分的同学的学生编号和学生姓名和平均成绩

```sql
SELECT 
    st.s_id, st.s_name, AVG(sc.score) AS avg_score
FROM
    student AS st 
    INNER JOIN score AS sc ON st.s_id = sc.s_id
GROUP BY
    st.s_id
HAVING
    avg_score >= 60;
```

### 4、查询所有同学的学生编号、学生姓名、选课总数、所有课程的总成绩(没成绩显示null)

```sql
SELECT 
    st.s_id, st.s_name, COUNT(sc.c_id) AS count, SUM(sc.score) AS sum
FROM
    student AS st
    LEFT JOIN score AS sc ON sc.s_id = st.s_id
GROUP BY
    st.s_id
```

### 5、查询姓“李”的老师的个数

```sql 
SELECT COUNT(*) FROM teacher WHERE t_name LIKE '李%';
```

### 6、查询没学过“张三”老师课的学生的学号、姓名

```sql
SELECT
	s_id, s_name 
FROM
	student 
WHERE
	s_id NOT IN (
	    SELECT s_id FROM score AS sc
		    INNER JOIN course AS c ON c.c_id = sc.c_id
		    INNER JOIN teacher AS t ON c.t_id = t.t_id AND t.t_name = '张三'
    )
```

### 7、查询学过编号为“01”的课程并且也学过编号为“02”的课程的学生的学号、姓名

```sql
SELECT
	s_id, s_name 
FROM
	student 
WHERE
	s_id IN ( SELECT s_id FROM score WHERE c_id = '01' OR c_id = '02' GROUP BY s_id HAVING COUNT( s_id ) > 1 )
```

### 8、查询课程编号为“02”的总成绩

```sql
SELECT SUM(score) FROM score WHERE c_id = '02';
```

### 9、查询没有学全所有课的学生的学号、姓名

```sql
SELECT
	st.s_id, s_name 
FROM
	student AS st
	INNER JOIN score AS sc ON sc.s_id = st.s_id 
GROUP BY
	st.s_id
HAVING
	COUNT(sc.c_id) < (SELECT count(DISTINCT c_id) FROM course);
```

### 10、查询至少有一门课与学号为“01”的学生所学课程相同的学生的学号和姓名

```sql
SELECT
	st.s_id, st.s_name 
FROM
	student AS st
	INNER JOIN score AS sc ON sc.s_id = st.s_id 
WHERE
	sc.c_id IN ( SELECT c_id FROM score WHERE s_id = '01' )
	AND st.s_id <> '01' 
GROUP BY
	st.s_id
```

### 11、查询和“01”号同学所学课程完全相同的其他同学的信息

```sql
SELECT
	st.s_id, st.s_name 
FROM
	student AS st
	INNER JOIN score AS sc ON sc.s_id = st.s_id 
WHERE
	sc.c_id IN ( SELECT c_id FROM score WHERE s_id = '01' )
	AND st.s_id <> '01' 
GROUP BY
	st.s_id
HAVING
    COUNT(st.s_id) = (SELECT COUNT(*) FROM score WHERE s_id = '01')
```

### 12、查询两门及其以上不及格课程的同学的学号，姓名及其平均成绩

```sql
SELECT
	st.s_id, st.s_name, AVG(sc.score)
FROM
	student AS st
	INNER JOIN score AS sc ON sc.s_id = st.s_id 
WHERE
	sc.score < 60
GROUP BY 
	st.s_id
HAVING	
	COUNT(st.s_id) >= 2
```

### 13、检索"01"课程分数小于60，按分数降序排列的学生信息

```sql
SELECT
	st.* 
FROM
	student AS st
	INNER JOIN score AS sc ON sc.s_id = st.s_id 
WHERE
	sc.c_id = '01' 
	AND sc.score < 60 
ORDER BY
	sc.score DESC
```

### 14、按平均成绩从高到低显示所有学生的所有课程的成绩以及平均成绩

```sql
SELECT
	s_id,
	SUM(CASE c_id WHEN '01' THEN score ELSE NULL END) AS score1,
	SUM(CASE c_id WHEN '02' THEN score ELSE NULL END) AS score2,
	SUM(CASE c_id WHEN '03' THEN score ELSE NULL END) AS score3,
	AVG(score) AS avg_score 
FROM
	score 
GROUP BY
	s_id
ORDER BY
	avg_score DESC
```

### 15、查询各科成绩最高分、最低分、平均分、及格率、中等率、优良率、优秀率

```sql
SELECT
	c.c_id,
	c.c_name,
	MAX( sc.score ),
	MIN( sc.score ),
	AVG( sc.score ),
	SUM( CASE WHEN sc.score >= 60 THEN 1 ELSE 0 END ) / COUNT( * ) AS '及格率',
	SUM( CASE WHEN sc.score BETWEEN 70 AND 80 THEN 1 ELSE 0 END ) / COUNT( * ) AS '中等率',
	SUM( CASE WHEN sc.score BETWEEN 80 AND 90 THEN 1 ELSE 0 END ) / COUNT( * ) AS '优良率',
	SUM( CASE WHEN sc.score >= 90 THEN 1 ELSE 0 END ) / COUNT( * ) AS '优秀率'
FROM
	score AS sc
	INNER JOIN course AS c ON sc.c_id = c.c_id 
GROUP BY
	c.c_id
```

### 16、按平均成绩进行排序，显示总排名和各科排名，Score 重复时保留名次空缺

```sql
SELECT
	st.s_id, st.s_name,
	rank_01,
	rank_02,
	rank_03,
	rank_total 
FROM
	student AS st
	LEFT JOIN ( SELECT s_id, RANK() OVER ( PARTITION BY c_id ORDER BY score DESC ) AS rank_01 FROM score WHERE c_id = '01' ) AS s1 ON s1.s_id = st.s_id
	LEFT JOIN ( SELECT s_id, RANK() OVER ( PARTITION BY c_id ORDER BY score DESC ) AS rank_02 FROM score WHERE c_id = '02' ) AS s2 ON s2.s_id = st.s_id
	LEFT JOIN ( SELECT s_id, RANK() OVER ( PARTITION BY c_id ORDER BY score DESC ) AS rank_03 FROM score WHERE c_id = '03' ) AS s3 ON s3.s_id = st.s_id
	LEFT JOIN ( SELECT s_id, RANK() OVER ( ORDER BY AVG( score ) DESC ) AS rank_total FROM score GROUP BY s_id ) AS s4 ON s4.s_id = st.s_id 
ORDER BY
	rank_total ASC
```

### 17、按各科成绩进行排序，并显示排名

```sql
SELECT
	s1.c_id,
	s1.s_id,
	s1.score,
	COUNT(s2.score)+1 AS rank_score 
FROM
	score AS s1
	LEFT JOIN score AS s2 ON s1.score < s2.score AND s1.c_id = s2.c_id 
GROUP BY
	s1.c_id,
	s1.s_id,
	s1.score 
ORDER BY
	s1.c_id,
	s1.score DESC
```

### 18、查询学生的总成绩并进行排名

```sql
SELECT
	st.s_id,
	st.s_name,
	SUM( sc.score ) AS total_score,
	RANK() OVER(ORDER BY SUM(sc.score) DESC) AS total_rank
FROM
	student AS st
	INNER JOIN score AS sc ON st.s_id = sc.s_id 
GROUP BY
	st.s_id,
	st.s_name 
ORDER BY
	total_score DESC
```

### 19、查询不同老师所教不同课程平均分从高到低显示

```sql
SELECT
	t.t_id,
	t.t_name,
	AVG(sc.score) AS avg_score 
FROM
	teacher AS t
	INNER JOIN course AS c ON c.t_id = t.t_id
	INNER JOIN score AS sc ON sc.c_id = c.c_id 
GROUP BY
	t.t_id
ORDER BY
	avg_score DESC
```

### 20、查询所有课程的成绩第2名到第3名的学生信息及该课程成绩

```sql

```

### 21、使用分段[100-85],[85-70],[70-60],[<60]来统计各科成绩，分别统计各分数段人数：课程ID和课程名称

```sql
SELECT
	c.c_id,
	c.c_name,
	SUM( CASE WHEN sc.score < 60 THEN 1 ELSE 0 END ) AS '分段[<60]',
	SUM( CASE WHEN sc.score BETWEEN 60 AND 70 THEN 1 ELSE 0 END ) AS '分段[70-60]',
	SUM( CASE WHEN sc.score BETWEEN 70 AND 85 THEN 1 ELSE 0 END ) AS '分段[85-70]',
	SUM( CASE WHEN sc.score BETWEEN 85 AND 100 THEN 1 ELSE 0 END ) AS '分段[100-85]'
FROM
	score AS sc
	INNER JOIN course AS c ON sc.c_id = c.c_id 
GROUP BY
	c.c_id
```

### 22、查询学生平均成绩及其名次

```sql
SELECT
	st.s_id,
	st.s_name,
	AVG( sc.score ),
	RANK() OVER( ORDER BY AVG( sc.score ) DESC ) AS avg_rank 
FROM
	score AS sc
	INNER JOIN student AS st ON sc.s_id = st.s_id 
GROUP BY
	st.s_id
```

### 23、查询各科成绩前三名的记录

```sql
--- 方法1
SELECT
	c_id, s_id, score 
FROM
	score AS sc1 
WHERE
	(SELECT COUNT( sc2.s_id ) FROM score AS sc2 
		WHERE sc1.c_id = sc2.c_id AND sc1.score < sc2.score 
	) < 3 
GROUP BY
	sc1.c_id,
	sc1.s_id
ORDER BY
	sc1.c_id,
	sc1.score DESC


--- 方法2
SELECT
	c_id, s_id, score 
FROM
	( SELECT c_id, s_id, score, DENSE_RANK() OVER( PARTITION BY c_id ORDER BY score DESC ) AS score_rank FROM score ) AS sc 
WHERE
	sc.score_rank <= 3
```

### 24、查询每门课程被选修的学生数

```sql
SELECT c_id, COUNT(s_id) AS count FROM score GROUP BY c_id
```

### 25、查询出只有两门课程的全部学生的学号和姓名

```sql
SELECT
	st.s_id, st.s_name, COUNT( c_id ) AS count 
FROM
	student AS st
	INNER JOIN score AS sc ON st.s_id = sc.s_id 
GROUP BY
	sc.s_id
HAVING
	count <= 2
```