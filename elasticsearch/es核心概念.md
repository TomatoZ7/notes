# es 核心概念

> elasticsearch 面向文档，一切都是 JSON。

## 关系型数据库和 elasticsearch

| Relational DB | Elasticsearch |
| :-----------: | :-----------: |
| 数据库(database) | 索引(indexes) |
| 表(tables) | types (7.x 会慢慢废弃 8.x 会完全废弃) |
| 行(row) | documents |
| 字段(columns) | fields |

elasticsearch (集群)中可以包含多个索引(数据库)，每个索引中可以包含多个类型(表)，每个类型下又包含多个文档(行)，每个文档中又包含多个字段(列)。

## 物理设计

elasticsearch 在后台把每个**索引划分为多个分片**，每份分片可以在集群中的不同服务器间迁移。

## 逻辑设计

一个索引类型中，包含多个文档，比如说文档1，文档2。当我们索引一篇文章时，可以通过这样的顺序找到它：索引->类型->文档ID，通过这个组合我们就能所引导某个具体的文档。

注意：ID 不必是整数，实际上它是个字符串。

## 文档
索引和搜索数据的最小单位是文档，elasticsearch 中，文档有几个重要属性：

+ 自我包含，一篇文章同时包含字段和对应的值，也就是同时包含 key:value。

+ 可以是层次型的，一个文档中包含自文档，复杂的逻辑就是这么来的。

+ 灵活的结构，文档不依赖预先定义的模式，我们知道关系型数据库中，要提前定义字段才能使用，在 elasticsearch 中，对于字段是非常灵活的，有时候，我们可以忽略该字段，或者动态的添加一个新的字段。

尽管我们可以随意的新增或者忽略某个字段，但是，每个字段的类型非常重要，比如一个年龄字段类型，可以是字符串也可以是整形。因为 elasticsearch 会保存字段和类型之间的映射及其他的设置。这种映射具体到每个映射的每种类型，这也是为什么在 elasticsearch 中，类型有时候也称为映射类型。

## 类型

类型是文档的逻辑容器，就像关系型数据库一样，表格是行的容器。类型中对于字段的定义称为映射，比如 name 映射为字符串类型。我们说文档是无模式的，它们不需要拥有映射中所定义的所有字段，比如新增一个字段，那么 elasticsearch 是怎么做的呢? elasticsearch 会自动的将新字段加入映射，但是这个字段的不确定它是什么类型，elasticsearch 就开始猜，如果这个值是 18，那么 elasticsearch 会认为它是整形。但是 elasticsearch 也可能猜不对，所以最安全的方式就是提前定义好所需要的映射，这点跟关系型数据库殊途同归了，先定义好字段，然后再使用，别整什么幺蛾子。

## 索引

索引是映射类型的容器，elasticsearch 中的索引是一个非常大的文档集合。 索引存储了映射类型的字段和其他设置。 然后它们被存储到了各个分片上了。我们来研究下分片是如何工作的。

## 物理设计：节点和分片如何工作

一个集群至少有一个节点，而一个节点就是一个 elasticsearch 进程，节点可以有多个索引默认的，如果你创建索引，那么索引将会有 5 个分片(primary shard，又称主分片)构成的，每个主分片会有一个副本(replica shard，又称复制分片)。

![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/EScore1.png)

上图是一个有 3 个节点的集群，可以看到主分片和对应的复制分片都不会在同一个节点内，这样有利于某个节点挂掉了，数据也不至于丢失。实际上，一个分片是一个 Lucene 索引，一个包含倒排索引的文件目录，倒排索引的结构使得 elasticsearch 在不扫描全部文档的情况下，就能告诉你哪些文档包含特定的关键字。

等等，倒排索引是什么鬼?

## 倒排索引

elasticsearch 使用的是一种称为倒排索引的结构，采用 Lucene 倒排索作为底层。这种结构适用于快速的全文搜索，一个索引由文档中所有不重复的列表构成，对于每一个词，都有一个包含它的文档列表。例如，现在有两个文档，每个文档包含如下内容: 

```
study every day,good good up to forever # 文档1包含的内容
To forever, study every day,good good up # 文档2包含的内容
```

为了创建倒排索引，我们首先要将每个文档拆分成独立的词(或称为词条或者 tokens)，然后创建一个包含所有不重复的词条的排序列表，然后列出每个词条出现在哪个文档:

![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/EScore2.png)

现在，我们试图搜索 to forever

![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/EScore3.png)

两个文档都匹配，但是第一个文档比第二个文档的匹配度更高。如果没有别的条件，现在，这两个包含关键字的文档都将返回。

## ES 的索引和 Lucene 的索引对比

在 elasticsearch 中，索引(库)这个词被频繁使用，这就是术语的使用。在 elasticsearch 中，索引被分为多个分片，每份分片是一个 Lucene 的索引。所以一个 elasticsearch 索引是由多个 Lucene 索引组成的。别问为什么，谁让 elasticsearch 使用 Lucene 作为底层呢!如无特指，说起索引都是指 elasticsearch 的索引。