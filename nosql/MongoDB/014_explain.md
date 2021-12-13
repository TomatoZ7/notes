# explain

## 1 使用 explain()

`explain()` 能提供大量与查询相关的信息，对于速度比较慢的查询来说，这是最重要的诊断工具之一。通过查看一个查询的 `explain()` 输出信息，可以知道查询使用了哪个索引，以及是如何使用的。

最常见的 `explain()` 输出有 2 种类型，使用索引的查询和没有使用索引的查询。对于特殊类型的索引，生成的查询计划可能会有些许不同，但是大部分字段都是相似的。另外，分片返回的是多个 `explain()` 的聚合，因为查询会在多个服务器上执行。

如果一个查询不适用索引，是因为它使用了 `BasicCursor` (基本游标)。反过来说，大部分使用索引的查询使用的是 `BtreeCursor` (某些特殊类型的索引如地理空间索引，使用的是它们自己类型的游标)。

对于使用了复合索引的查询，最简单情况下的 `explain()` 输出如下所示：

```js
> db.users.find({"age" : 42}).explain()
{
    "cursor" : "BtreeCursor age_1_username_1",
    "isMultiKey" : false,
    "n" : 8332,
    "nscannedObjects" : 8332,
    "nscanned" : 8332,
    "nscannedObjectsAllPlans" : 8332,
    "nscannedAllPlans" : 8332,
    "scanAndOrder" : false,
    "indexOnly" : false,
    "nYields" : 0,
    "nChunkSkips" : 0,
    "millis" : 91,
    "indexBounds" : {
        "age" : [
            [
                42,
                42
            ]
        ],
        "username" : [
            [
                {
                    "$minElement" : 1
                },
                {
                    "$maxElement" : 1
                }
            ]
        ]
    },
    "server" : "ubuntu:27017"
}
```

从输出信息中可以看到它使用的索引是 `age_1_username_1`。`millis` 表明了这个查询的执行速度，时间是从服务器收到请求开始一直到发出响应为止。然而，这个数值不一定真的是你希望看到的值。如果 `MongoDB` 尝试了多个查询计划，那么 `millis` 显示的是这些查询计划话费的总时间，而不是最优查询计划所花的事件。

接下来依次介绍这些字段：

1. `"cursor" : "BtreeCursor age_1_username_1"`

`BtreeCursor` 表示本次查询使用了索引，具体来说，是使用了复合索引 `{age: 1, username: 1}`。如果查询要对结果进行逆序遍历，或者是使用了多键索引，就可以在这个字段中看到 `reverse` 和 `multi` 这样的值。

2. `"isMultiKey" : false`

用于说明本次查询是否使用了多键索引。

3. `"nscannedObjects" : 8332`

这是 `MongoDB` 按照索引指针去**磁盘**上查找实际文档的次数。如果查询包含的查询条件不是索引的一部分，或者说要求返回不在索引内的字段，`MongoDB` 就必须依次查找每个索引条目的指向文档。

4. `"nscanned" : 8332`

如果有使用索引，那么这个数字就是查找过的索引条目数量。如果本次查询是一次全表扫描，那么这个数字就表示检查过的文档数量。

5. `"scanAndOrder" : false`

`MongoDB` 是否在内存中对结果集进行了排序。

6. `"indexOnly" : false`

`MongoDB` 是否只使用索引才能完成此次查询。

在本例中，从 `nscanned` 和 `n` 相等可以看出只使用索引就找到了全部的匹配文档。但是本次查询要求返回所有字段，而索引只包含 `age` 和 `username` 两个字段。如果将查询范围缩小为索引字段，那么 `indexOnly` 就会为 `true`。

7. `"nYields" : 0`

为了让写入请求能够顺利进行，本次查询暂停的次数。如果有写入请求需要处理，查询会周期性地施放它们的锁，以便写入能够顺利进行。

8. `"millis" : 91`

数据库本次查询所耗费的时间。

9. `"indexBounds": {...}`

这个字段描述了索引的使用情况，给出了索引的遍历范围。由于查询中的第一个语句是精确匹配，因此索引只需要查找 42 这个值就可以了。本次查询没有指定第二个索引键，因此这个索引键上没有限制，数据库会在 `age` 为 42 的条目中奖用户名介于负无穷(`{"$minElement": 1}`) 和正无穷(`{"$maxElement": 1}`) 的条目都找出来。

再来看一个稍微复杂点的例子：假如有一个 `{"username": 1, "age": 1}` 上的索引和一个 `{"age": 1, "username": 1}` 上的索引。同时查询 `username` 和 `age` 时，会发生什么情况？这取决于具体的查询：

```js
> db.c.find({age : {$gt : 10}, username : "sally"}).explain()
{
    "cursor" : "BtreeCursor username_1_age_1",
    "indexBounds" : [
        [
            {
                "username" : "sally",
                "age" : 10
            },
            {
                "username" : "sally",
                "age" : 1.7976931348623157e+308
            }
        ]
    ],
    "nscanned" : 13,
    "nscannedObjects" : 13,
    "n" : 13,
    "millis" : 5
}
```

由于要在 `username` 上执行精确匹配，在 `age` 上进行范围查询，因此，数据库选择使用 `{"username": 1, "age": 1}` 索引，这与查询语句的顺序相反。

另一方面来说，如果需要对 `age` 精确匹配而对 `username` 进行范围查询，`MongoDB` 就会使用另一个索引：

```js
> db.c.find({"age" : 14, "username" : /.*/}).explain()
{
    "cursor" : "BtreeCursor age_1_username_1 multi",
    "indexBounds" : [
        [
            {
                "age" : 14,
                "username" : ""
            },
            {
                "age" : 14,
                "username" : {
                }
            }
        ],
        [
            {
                "age" : 14,
                "username" : /.*/
            },
            {
                "age" : 14,
                "username" : /.*/
            }
        ]
    ],
    "nscanned" : 2,
    "nscannedObjects" : 2,
    "n" : 2,
    "millis" : 2
}
```

## 2 hint()

可以使用 `hint()` 强制 `MongoDB` 使用特定的索引。例如，如果希望 `MongoDB` 在上个例子中使用 `{"username": 1, "age": 1}`，可以这么做：

```js
> db.c.find({"age": 14, "username": /.*/}).hint({"username": 1, "age": 1}})
```

## 3 查询优化器

`MongoDB` 的查询优化器与其他数据库稍有不同。基本来说，如果一个索引能够精确匹配一个查询(要查询 `x`，刚好在 `x` 上有一个索引)，那么查询优化器就会使用这个索引。否则，可能会有几个索引都适合你的查询。`MongoDB` 会从这些可能的索引子集中为每次查询计划选择一个，这些查询计划是**并行执行**的。最早返回 100 个结果的就是胜者，其他的查询计划就会被中止。

这个查询计划会被缓存，这个查询接下来都会使用它，直到集合数据发生了比较大的变动。如果在最初的计划评估之后集合发生了比较大的数据变动，查询优化器就会重新挑选可行的查询计划。建立索引时，或者是每执行 1000 次查询后，查询优化器都会重新评估查询计划。

`explain()` 输出信息里的 `allPlans` 字段显示了本次查询尝试过的每个查询计划。