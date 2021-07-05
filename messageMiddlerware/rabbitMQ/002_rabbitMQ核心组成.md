# rabbitMQ 核心组成部分

## rabbitMQ 核心组成部分

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/rmq_composition1.jpg)

核心概念
Server : 又称 Broker，接受客户端的连接，实现 AMQP 实体服务。
 
Connection : 连接，应用程序与 Broker 的网络连接(TCP/IP)。

Channel : 网络信道，几乎所有的操作都在 Channel 中进行，Channel 是进行消息读写的通道，客户端可以建立不同的  Channel，每个 Channel 代表一个会话任务。

Message : 消息。服务与应用程序之间传送的数据，由 Properties 和 body 组成，Properties 可以对消息进行修饰，比如消息的优先级，延迟等高级特性，Body 则就是消息体的内容。

Virtual Host : 虚拟地址，用于进行逻辑隔离最上层的消息路由，一个虚拟主机理由可以有若干个 Exchange 和 Queue，同一个虚拟主机里面不能有相同名字的 Exchange。

Exchange : 交换机，接受消息，根据路由键发送消息到绑定的队列。(不具备消息存储的能力)

Bindings : Exchange 和 Queue 之间的虚拟连接，binding 中可以保护多个 routing key.

Routing key : 是一个路由规则，虚拟机可以用它来确定如何路由一个特定消息。

Queue : 队列。也称为 Message Queue 消息队列，保存消息并将它们转发给消费者。

## rabbitMQ 的运行流程

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/rmq_composition2.png)

## rabbitMQ 支持消息的模式

[参考官网](https://www.rabbitmq.com/getstarted.html)