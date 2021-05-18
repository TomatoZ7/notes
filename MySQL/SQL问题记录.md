# MySQL 之 You can't specify target table for update in FROM clause 解决办法

MySQL 中 `You can't specify target table for update in FROM clause` 错误的意思是说，不能先 select 出同一表中的某些值，再 update 这个表(在同一语句中)。

例如下面这个sql：

```sql
DELETE FROM tbl WHERE id IN 
(
    SELECT max(id) FROM tbl AS a WHERE EXISTS
    (
        SELECT 1 FROM tbl AS b WHERE a.tac=b.tac GROUP BY tac HAVING count(1)>1
    )
    GROUP BY tac
)
```

需改成：

```sql
DELETE FROM tbl WHERE id IN 
(
    SELECT a.id FROM 
    (
        SELECT max(id) AS id FROM tbl AS a WHERE EXISTS
        (
            SELECT 1 FROM tbl AS b WHERE a.tac=b.tac GROUP BY tac HAVING count(1)>1
        )
        GROUP BY tac
    ) AS a
)
```

也就是说将 select 出的结果再通过中间表 select 一遍，这样就规避了错误。注意，这个问题只出现于mysql，mssql 和 Oracle 不会出现此问题。