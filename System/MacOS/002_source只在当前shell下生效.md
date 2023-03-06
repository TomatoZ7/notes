# source 命令只在当前 shell 下生效

Mac 中有许多不同的 shell，而在使用 **bash** 终端的时候，Mac 加载环境变量的顺序是：

+ /etc/profile
+ /etc/paths
+ ~/.bash_profile
+ ~/.bash_login
+ ~/.profile
+ ~/.bashrc

前两个是系统级别的，开机时会自动加载，后面 3 个是当前用户级别的环境变量。按照从前往后的顺序读取，如果 `~/.bash_profile` 存在，则后面的几个文件就会被忽略不读，如果 `~/.bash_profile` 不存在，才会依次读取垢面的文件。

`~/.bashrc` 没有上述规则，它是 bash shell 打开的时候载入的。

而如果你使用的是 **zsh** 终端的话，则不会加载 `~/.bash_profile`，而是去加载 `~/.zshrc`。

你需要在 `~/.zshrc` 中添加一行：

```sh
source ~/.bash_profile
```

或者在 `~/.zshrc` 文件里重新配置。

查看已安装 shell：

```sh
$ cat /etc/shells
# List of acceptable shells for chpass(1).
# Ftpd will not allow users to connect who are not using
# one of these shells.

/bin/bash
/bin/csh
/bin/dash
/bin/ksh
/bin/sh
/bin/tcsh
/bin/zsh
```