# Linux 安装 MongoDB

本文以 `centos` 为例。

## 1 创建目录

必须创建一个目录以便数据库写入文件。数据库会默认使用 `/data/db` 目录，也可指定其他目录。如建立默认目录，则应确保拥有正确的写权限。可通过如下命令，创建目录并设置权限：

```shell
$ mkdir -p /data/db
$ chown -R $USER:$USER /data/db
```

如有必要，可使用 `mkdir -p` 命令，建立指定目录及其所有父目录(针对父目录不存在的情况)。

使用 `chown` 命令可改变 `/data/db` 的所有权，以便实现用户对其的写入。

当然，也可在 `home` 文件夹中建立一个目录，并在启动数据库时指定其作为 `MongoDB` 的数据目录，从而避开权限问题。

## 2 复制链接

到 (官网 https://www.mongodb.com/try/download/community)[https://www.mongodb.com/try/download/community] 拷贝下载链接：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/nosql/MongoDB/images/mongo_linux_install_1.jpg)

## 3 安装

```shell
$ wget https://repo.mongodb.org/yum/redhat/7/mongodb-org/5.0/x86_64/RPMS/mongodb-org-server-5.0.3-1.el7.x86_64.rpm

$ ls
mongodb-org-server-5.0.3-1.el7.x86_64.rpm

# 安装
$ rpm -ivh mongodb-org-server-5.0.3-1.el7.x86_64.rpm
```

## 4 查看配置文件

```shell
$ cat /etc/mongod.conf 
# mongod.conf

# for documentation of all options, see:
#   http://docs.mongodb.org/manual/reference/configuration-options/

# where to write logging data.
systemLog:
  destination: file
  logAppend: true
  path: /var/log/mongodb/mongod.log     ####### 日志文件位置

# Where and how to store data.
storage:
  dbPath: /var/lib/mongo    ####### 数据文件存放位置
  journal:
    enabled: true       ####### 设置为 true，启用操作日志，以确保写入持久性和数据的一致性，会在 dbPath 目录下创建 journal 目录
#  engine:
#  wiredTiger:

# how the process runs
processManagement:
  fork: true  # fork and run in background      ####### 是否后台运行，设置为 true 启动进程在后台运行的守护进程模式
  pidFilePath: /var/run/mongodb/mongod.pid  # location of pidfile
  timeZoneInfo: /usr/share/zoneinfo

# network interfaces
net:
  port: 27017
  bindIp: 127.0.0.1  # Enter 0.0.0.0,:: to bind to all IPv4 and IPv6 addresses or, alternatively, use the net.bindIpAll setting.    
  ####### 允许远程访问，127.0.0.1 限制本地访问，0.0.0.0 允许任意客户端远程访问，也可以用逗号分隔 ip 绑定多个地址


#security:

#operationProfiling:

#replication:

#sharding:

## Enterprise-Only Options

#auditLog:

#snmp:
```

## 5 启动

```shell
$ systemctl restart mongod

$ netstat -anpt | grep 27017
tcp        0      0 127.0.0.1:27017         0.0.0.0:*               LISTEN      21564/mongod
```

## 6 进入 MongoDB



<!-- https://docs.mongodb.com/v5.0/tutorial/install-mongodb-on-red-hat/ -->