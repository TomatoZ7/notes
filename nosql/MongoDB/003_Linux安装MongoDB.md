# Linux 安装 MongoDB

本文以 `centos`、`MongoDB 5.0` 为例。

## 1 配置包管理系统(yum)

创建 `/etc/yum.repos.d/mongodb-org-5.0.repo`:

```repo
[mongodb-org-5.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/5.0/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-5.0.asc
```

## 2 安装 MongoDB 包

执行命令安装 MongoDB 稳定版本：

```shell
$ sudo yum install -y mongodb-org
```

要安装特定版本的 `MongoDB`，需单独指定每个组件包并将版本号附加到包名称后面：

```shell
$ sudo yum install -y mongodb-org-5.0.2 mongodb-org-database-5.0.2 mongodb-org-server-5.0.2 mongodb-org-shell-5.0.2 mongodb-org-mongos-5.0.2 mongodb-org-tools-5.0.2
```

当有新版本可用时，`yum` 会升级软件包。为了防止意外升级，可以固定包，将以下排除指令添加到 `/etc/yum.conf` 文件中：

```conf
exclude=mongodb-org,mongodb-org-database,mongodb-org-server,mongodb-org-shell,mongodb-org-mongos,mongodb-org-tools
```

## 3 配置文件

查看配置文件 `cat /etc/mongod.conf`：

```conf
# mongod.conf

# for documentation of all options, see:
#   http://docs.mongodb.org/manual/reference/configuration-options/

# where to write logging data.
systemLog:
  destination: file
  logAppend: true
  path: /var/log/mongodb/mongod.log     ; 日志目录

# Where and how to store data.
storage:
  dbPath: /var/lib/mongo        ; 数据目录
  journal:
    enabled: true
#  engine:
#  wiredTiger:

# how the process runs
processManagement:
  fork: true  # fork and run in background
  pidFilePath: /var/run/mongodb/mongod.pid  # location of pidfile
  timeZoneInfo: /usr/share/zoneinfo

# network interfaces
net:
  port: 27017
  bindIp: 127.0.0.1  # Enter 0.0.0.0,:: to bind to all IPv4 and IPv6 addresses or, alternatively, use the net.bindIpAll setting.


#security:

#operationProfiling:

#replication:

#sharding:

## Enterprise-Only Options

#auditLog:

#snmp:
```

## 4 启动 MongoDB

```shell
$ sudo systemctl start mongod
```

确认 `MongoDB` 是否启动成功：

```shell
$ sudo systemctl status mongod
```

查看版本：

```shell
$ mongo --version
```

进入 `mongo shell`：

```shell
$ mongosh
```

## 5 停止/重启

```shell
$ sudo systemctl stop mongod
```

```shell
$ sudo systemctl restart mongod
```

## 6 资料来源

[https://docs.mongodb.com/v5.0/tutorial/install-mongodb-on-red-hat/](https://docs.mongodb.com/v5.0/tutorial/install-mongodb-on-red-hat/)