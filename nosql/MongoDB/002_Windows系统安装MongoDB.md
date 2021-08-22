# Windows 操作系统安装 MongoDB

### 1 官方下载

在 [https://www.mongodb.com/try/download/community](https://www.mongodb.com/try/download/community) 下载二进制文件。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/nosql/MongoDB/images/mongo_download_1.jpg)

### 2 运行文件

双击运行下载的文件。一路 next，建议选择 `custom` 自定义安装路径。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/nosql/MongoDB/images/mongo_download_2.jpg)

配置好数据和日志的存放目录：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/nosql/MongoDB/images/mongo_download_3.jpg)

接着一路 next 直到 install 开始安装。

### 3 配置环境变量

配置到 `bin` 目录，如 `E:\env\MongoDB\bin`

### 4 建立目录

现在需要建立一个目录，以便 `MongoDB` 能够写入数据库文件。

`MongoDB` 默认尝试使用当前驱动器的 `\data\db` 目录作为其数据目录。例如，在 C 盘下运行 `mongod`，则会使用 `C:\data\db`。

在这里我在 E 盘根目录建立 `data` 目录：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/nosql/MongoDB/images/mongo_download_4.jpg)


也可以在文件系统中的任何位置建立这一目录或其他空目录。如不使用 `\data\db`，则需在启动 `mongod` 时指定路径：

```
$ bin\mongod.exe --dbpath E:\Documents and Settings\MyDocuments\db
```

### 5 运行

```
$ bin\mongo.exe
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/nosql/MongoDB/images/mongo_download_5.jpg)

此时会自动连接到 `test` 文档(数据库)，可以使用 `db` 命令查看。