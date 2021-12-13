# rabbitMQ 内存监控

## 一、rabbitMQ 的内存监控

可以在 rabbitmq_management 管理界面 overview 选项卡下查看内存状况：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/rmq_memory1.jpg)

当内存使用超过配置的阈值或者磁盘剩余空间不足时，rabbitMQ 会暂时阻塞客户端的连接，并且停收接受从客户端发来的消息，以此避免服务器的崩溃，客户端与服务端的心态检测机制也会失效。

## 二、rabbitMQ 的内存警告

[官方配置文档](https://www.rabbitmq.com/configure.html)

当出现警告时，可以通过配置去修改和调整。

### 1、命令的方式

```bash
# 以下配置二选一即可，不必全部配置
rabbitmqctl set_vm_memory_high_watermark <fraction>
rabbitmqctl set_vm_memory_high_watermark absolute 50MB
```

`fraction/value` 为内存阈值。默认情况是：0.4/2GB，代表的含义是：当 RabbitMQ 的内存超过 40% 时，就会产生警告并且阻塞所有生产者的连接。通过此命令修改阈值在 Broker 重启以后将会失效，通过修改配置文件方式设置的阈值则不会随着重启而消失，但修改了配置文件一样要重启 Broker 才会生效。

### 2、配置文件方式 rabbimq.conf

```conf
# 默认
# vm_memory_high_watermark.relative = 0.4
# 使用 fraction 相对值进行设置，建议取值在 0.4~0.7 之间，不超过0.7
vm_memory_high_watermark.relative = 0.6
# 使用 absolute 的绝对值的方式，但是 KB，MB，GB 对应的命令如下
vm_memory_high_watermark.absolute = 2GB
```

## 三、rabbitMQ 的内存幻夜

在某个 Broker 节点及内存阻塞生产者之前，它会尝试将队列中的消息换页到磁盘以释放内存空间，持久化和非持久化的消息都会写入磁盘中，其中持久化的消息本身就在磁盘中有一个副本，所以在转移的过程中持久化的消息会先从内存中清除掉。

默认情况下，内存到达的阈值是 50% 时就会换页处理。

也就是说，在默认情况下该内存的阈值是 0.4 的情况下，当内存超过 0.4*0.5=02 时，会进行换页动作。

比如有 1000MB 内存，当内存的使用率达到了 400MB 已经达到了极限，但是因为配置的换页内存 0.5，这个时候会在达到极限 400MB 之前，会把内存中的 200MB 进行转移到磁盘中。从而达到稳健的运行。

可以通过设置 `vm_memory_high_watermark_paging_ratio` 来进行调整

```conf
vm_memory_high_watermark.relative = 0.4
vm_memory_high_watermark_paging_ratio = 0.7(设置小于1的值)
```

为什么设置小于 1，因为如果设置为 1 的阈值。内存都已经达到了极限了。再去换页意义不是很大了。


## 四、rabbitMQ 的磁盘预警

当磁盘的剩余空间低于确定的阈值时，rabbitMQ 同样会阻塞生产者，这样可以避免因非持久化的消息持续换页而耗尽磁盘空间导致服务器崩溃。

默认情况下磁盘预警为 50MB 的时候会进行预整。表示当前磁盘空间第 50MB 的时候会阻塞生产者并目停止内存消息换页到盘的过程。

这个阈值可以减小，但是不能完全的消除因磁盘耗尽而导致崩溃的可能性。比如在两次磁盘空间的检查空障内，第一次检查是 60MB，第二检查可能就是 1MB 就会出现警告。

### 命令修改

```bash
rabbitmqctl set_disk_free_limit <disk_limit>
rabbitmqctl set_disk_free_limit memory_limit <fraction>

disk_limit : 固定单位 KB MB GB
fraction : 是相对阈值，建议范围在 1.0~2.0 之间。(相对于内存)
```

### 配置文件修改

```conf
disk_free_limit.relative = 3.0
disk_free_limit.absolute = 50MB
```