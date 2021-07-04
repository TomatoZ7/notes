# 安装 RabbitMQ

RabbitMQ : 开源的遵循 AMQP 协议实现的基于 Erlang 语言编写，支持多种客户端(语言)。用于在分布式系统中存储消息，转发消息，具有高可用、高可扩性、易用性等特征。

## 一、下载 RabbitMQ

[官网下载](https://www.rabbitmq.com/download.html)

根据服务器，选择对应的下载版本

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/rmq_inst1.png)

## 二、erlang 下载安装

[RabbitMQ-erlang 版本兼容矩阵](https://www.rabbitmq.com/download.html)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/rmq_inst2.png)

下载安装步骤：

```bash
# 下载 erlang 的rpm仓库
wget http://packages.erlang-solutions.com/erlang-solutions-2.0-1.noarch.rpm

# yum 安装 epel-release
yum -y install epel-release

# 安装erlang的rpm仓库
rpm -Uvh erlang-solutions-2.0-1.noarch.rpm

# 正式安装
yum -y install erlang

# 安装成功，查看版本
erl -v
```

## 三、安装 RabbitMQ 依赖组件 socat

```bash
# 安装 socat 插件
yum install -y socat
```

## 四、安装 RabbitMQ

```bash
rpm -Uvh rabbitmq-server-3.8.18-1.el7.noarch.rpm

yum install -y rabbitmq-server


# 常用服务操作命令
systemctl start rabbitmq-server # 启动
systemctl status rabbitmq-server # 状态
systemctl stop rabbitmq-server # 停止
systemctl restart rabbitmq-server # 重启
chkconfig rabbitmq-server on # 设置开机自启动
```

## 五、RabbitMQ 管理界面及授权操作

1. 默认情况下，RabbitMQ 是没有安装 web 端的客户端插件，需要安装才可以生效

```bash
rabbitmq-plugins enable rabbitmq_management
```

默认访问地址 : yourip:15672

> rabbitmq 有一个默认的账号密码 `guest`，默认情况下只能在本机访问，所以需要添加一个远程登录的用户。

2. 授权账号和密码

1) 新增用户

```bash
rabbitmqctl add_user admin admin
```

2) 设置用户分配操作权限

```bash
rabbitmqctl set_user_tags admin administrator
```

用户级别 : 

+ adminstrator 可以登录控制台、查看所有信息、可以对 rabbitmq 进行管理

+ monitoring 监控者 登录控制台、查看所有信息

+ policymaker 策略制定者 登录控制台、制定策略

+ managment 普通管理员 登录控制台、

3) 为用户添加资源权限

```bash
rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
```

### 操作集合

```bash
rabbitmqctl add_user 账号 密码

rabbitmqctl set_user_tags 账号 身份

# 修改密码
rabbitmqctl change_password 账号 新密码

# 删除用户
rabbitmqctl delete_user 账号

# 用户清单
rabbitmqctl list_users

# 设置权限
rabbitmqctl set_permissions -p / 账号 ".*" ".*" ".*"
```