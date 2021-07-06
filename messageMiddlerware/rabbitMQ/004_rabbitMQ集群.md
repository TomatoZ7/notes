# rabbitMQ 集群

## 一、rabbitMQ 集群搭建

配置的前提是你的 rabbitmq 可以运行起来，比如 `ps aux | grep rabbitmq` 你能看到相关进程，又比如运行 `rabbitctl status` 你可以看到相应的 `running` 标识。

> 注意 : 确保 rabbitMQ 可以运行的，确保完成之后，把单机版的 rabbitMQ 服务停止。

## 二、单机多实例搭建

场景 : 假设有两个 rabbitMQ 节点，分别为 rabbit-1，rabbit-2，rabbit-1 作为主节点，rabbit-2 作为从节点。

启动命令 : RABBITMQ_NODE_PORT = 5672 RABBITMQ_NODENAME = rabbit-1 rabbitmq-server -detached

结束命令 : rabbitmqctl -n rabbit-1 stop

### 1、启动第一个节点 rabbit-1

```bash
> sudo RABBITMQ_NODE_PORT=5672 RABBITMQ_NODENAME=rabbit-1 rabbit-server start &

********************省略********************
Start broker...
completed with 0 plugins
```

至此节点 rabbit-1 启动完成。

### 2、启动第二个节点 rabbit-2

> 注意 : web 管理插件端口占用，所以还要指定其 web 插件占用的端口号 <br/>
> RABBITMQ_SERVER_START_ARGS=" -rabbitmq_managerment listener [{port, 15673}]"

```bash
> sudo RABBITMQ_NODE_PORT=5373 RABBITMQ_SERVER_START_ARGS="-rabbitmq_management listener [{port, 15673}]" RABBITMQ_NODENAME=rabbit-2 rabbit-server start &

********************省略********************
Start broker...
completed with 0 plugins
```

至此节点 rabbit-2 启动完成。

### 3、验证启动 "ps aux|grep rabbitmq"

### 4、rabbit-1 操作作为主节点

```bash
# 停止应用
# -n : 节点名字
> sudo rabbitmqctl -n rabbit-1 stop_app

# 目的是清除节点上的历史数据(如果不清除，无法将节点加入到集群)
> sudo rabbitmqctl -n rabbit-1 reset

# 启动应用
> sudo rabbitmqctl -n rabbit-1 start_app
```

### 5、rabbit-2 操作作为主节点

```bash
# 停止应用
> sudo rabbitmqctl -n rabbit-2 stop_app

# 目的是清除节点上的历史数据(如果不清除，无法将节点加入到集群)
> sudo rabbitmqctl -n rabbit-2 reset

# 将 rabbit2 节点加入到 rabbit1 (主节点)集群当中
# Server-node : 服务器主机名
> sudo rabbitmqctl -n rabbit-2 join_cluster rabbit-1@'Server-node'

# 启动应用
> sudo rabbitmqctl -n rabbit-2 start_app
```

### 6、验证集群状态

```bash
> sudo rabbitmqctl cluster_status -n rabbit-1

# 集群有两个节点 : rabbit-1@Server-node、rabbit-2@Server-node
[{nodes, [{disc,['rabbit-1@Server-node', 'rabbit-2@Server-node']}]},
 {running_nodes, ['rabbit-2@Server-node', 'rabbit-1@Server-node']},
 {cluster_name,<<"rabbit-1@Server-node.localdomain">>},
 {partitions, []},
 {alarms,[{'rabbit-2@Server-node', []}, {'rabbit-1@Server-node', []}]}]
```

### 7、Web 监控

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/rmq_cluster1.png)

> 注意 : 访问的时候需要给不同的主机都设置账号密码

### 8、多机部署

如果采用多机部署方式，需读取其中一个节点的 cookie，并复制到其他节点（节点之间通过 cookie 确定相互是否可通信）。cookie 存放在 /var/lib/rabbitmq/.erlang.cookie。

例如：主机名分别为 rabbit-1、rabbit-2

1、逐个启动各节点

2、配置各节点的 hosts 文件(`vim /etc/hosts`)

ip1 : rabbit-1

ip2 : rabbit-2

其他步骤与单机部署方式雷同。