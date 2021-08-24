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