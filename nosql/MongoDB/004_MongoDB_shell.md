# MongoDB shell 简介

`MongoDB` 自带 `JavaScript shell`，可在 `shell` 中使用命令行与 `MongoDB` 实例交互。

## 1 运行 shell

运行 `mongo` 启动 `shell`:

```shell
$ mongo
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/nosql/MongoDB/images/mongo_shell_1.jpg)

启动时，`shell` 将自动连接 `MongoDB` 服务器，须确保 `mongod` 已启动。

`shell` 是一个 `JavaScript` 解释器，可运行任意 `JavaScript` 程序。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/nosql/MongoDB/images/mongo_shell_2.jpg)

使用多行命令时，`shell` 会检测输入的 `JavaScript` 语句是否完整，如没写完可在下一行接着写。在某行连续 3 次按下回车键可取消未输入完成的命令。并退回到 `>` 提示符。

## 2 MongoDB 客户端

`shell` 是一个独立的 `MongoDB` 客户端。启动时，`shell` 会连到 `MongoDB` 服务器的 `test` 数据库，并将数据库连接赋值给全局变量 `db`。这个变量是通过 `shell` 访问 `MongoDB` 的主要入口点。

```shell
> db
test
```

为了方便习惯 `SQL shell` 的用户，`shell` 还包含一些非 `JavaScript` 语法的扩展，这些扩展不提供额外的功能，而是一些非常棒的语法糖。例如，最重要的操作之一为选择数据库：

```shell
> use foobar
switched to db foobar
> db
foobar
```

通过 `db` 变量可以访问其中的集合。例如，通过 `db.baz` 可返回当前数据库的 `baz` 集合。几乎所有的数据库操作都可以通过 `shell` 完成。

## 3 shell 中的基本操作

### 3.1 创建

`insert` 函数可将一个文档添加到集合中。

举一个存储博客的例子，首先创建一个名为 post 的局部变量，这是一个 JavaScript 对象，用于表示我们的文档。它有几个键 `title`、`content`、`date`(发布日期)。

```js
> post = {
... "title": "Here's my blog post.",
... "content": "This is edit on Mongo's shell",
... "date": new Date()
... }
{
        "title" : "Here's my blog post.",
        "content" : "This is edit on Mongo's shell",
        "date" : ISODate("2021-08-25T12:31:49.584Z")
}
```

这个对象是个有效的 `MongoDB` 文档，所以可以用 `insert` 方法将其保存到 `blog` 集合中：

```js
> db.blog.insert(post)
WriteResult({ "nInserted" : 1 })
```

### 2.2 读取

查看 `blog` 集合，可以调用 `find` 方法：

```js
> db.blog.find()
{ 
    "_id" : ObjectId("612638e4d0df37ed5cd3e984"), 
    "title" : "Here's my blog post.", 
    "content" : "This is edit on Mongo's shell", 
    "date" : ISODate("2021-08-25T12:31:49.584Z") 
}
```

使用 `find` 时，shell 默认显示最多 20 个匹配的文档。若只想查看一个文档，可用 `findOne`：

```js
> db.blog.find()
{ 
    "_id" : ObjectId("612638e4d0df37ed5cd3e984"), 
    "title" : "Here's my blog post.", 
    "content" : "This is edit on Mongo's shell", 
    "date" : ISODate("2021-08-25T12:31:49.584Z") 
}
```

### 2.3 更新

使用 `update` 修改文档。`update` 至少接受 2 个参数：第一个是限定条件，用于匹配待更新的文档，第二个是新的文档。

假设我们要对上述博客增加评论功能，就需要增加一个新的键，用于保存评论数组。

首先，变量 `post` 增加 `comments` 键：

```js
> post.comments = []
[ ]
```

执行 `update` 操作，用新的文档替换标题为 `Here's my blog post.` 的文章：

```js
> db.blog.update({"title": "Here's my blog post."}, post)
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.blog.find()
{ 
    "_id" : ObjectId("612638e4d0df37ed5cd3e984"), 
    "title" : "Here's my blog post.", 
    "content" : "This is edit on Mongo's shell", 
    "date" : ISODate("2021-08-25T12:31:49.584Z"), 
    "comments" : [ ]
}
```

### 2.4 删除

使用 `remove` 方法可将文档从数据库中永久删除。如果没有使用任何参数，它会将集合内的所有文档全部删除。它可以接受一个作为限定条件的文档作为参数。

```js
> db.blog.remove({"title": "Here's my blog post."})
WriteResult({ "nRemoved" : 1 })
```