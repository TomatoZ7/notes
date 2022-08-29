# redis 发布订阅

redis 发布订阅是一种**消息通信模式**，发送者(pub)发送消息，订阅者(sub)接收消息。(微信、微博、关注系统)

redis 客户端可以订阅任意数量的频道。

订阅/发布消息图

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_psub1.jpg)

下图展示了频道 channel1，以及订阅这个频道的三个客户端 ———— client2、client5 和 client1 之间的关系：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_psub1.png)

当有新消息通过 PUBLISH 命令发送给频道 channel1 时，这个消息就会被发送给订阅它的三个客户端：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_psub2.png)


## 命令

这些命令被广泛用于构建即时通信应用，比如网络聊天室(chatroom)和实时广播、实时提醒等。

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_psub3.png)

## 实例

订阅端(订阅一个频道)：

```bash
127.0.0.1:6379> SUBSCRIBE tz
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "tz"
3) (integer) 1
```

发送端(发送信息)：

```bash
127.0.0.1:6379> PUBLISH tz "hello,tz"
(integer) 1
127.0.0.1:6379> PUBLISH tz "how are you"
(integer) 1
```

订阅端(接收信息)：

```bash
127.0.0.1:6379> SUBSCRIBE tz
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "tz"
3) (integer) 1
1) "message"
2) "tz"
3) "hello,tz"
1) "message"
2) "tz"
3) "how are you"
```

## 原理

Redis 是使用 C 实现的，通过分析 Redis 源码里的 `pubsub.c` 文件，了解发布和订阅机制的底层实现，籍此加深对 Redis 的理解。

Redis 通过 `PUBLISH`、`SUBSCRIBE` 和 `PSUBSCRIBE` 等命令实现发布和订阅功能。

通过 `SUBSCRIBE` 命令订阅某频道后，redis-server 里维护了一个字典，字典的键就是一个个**频道**，而字典的值则是一个**链表**，链表中保存了所有订阅这个频道的**客户端**。`SUBSCRIBE` 命令的关键，就是将客户端添加到给定频道的订阅链表中。

通过 `PUBLISH` 命令向订阅者发送消息，redis-server 会使用给定的频道作为键，在它所维护的频道**字典**中查找记录了订阅这个频道的所有客户端的**链表**，遍历这个链表，将消息发布给所有订阅者。

Pub/Sub 从字面上理解就是 发布(Publish) 与 订阅(Subscribe)，在 Redis 中，你可以设定对某一个 key 值进行消息发布及消息订阅，当一个 key 值上进行了消息发布后，所有订阅它的客户端都会收到相应的消息。这一功能最明显的用法就是用作实时消息系统，比如普通的即时聊天，群聊等功能。

使用场景：

1、实时消息系统

2、实时聊天(把频道当做聊天室，将信息回显给所有人)

3、订阅、关注系统

复杂的场景会使用消息中间件如 rabbitMQ 等。