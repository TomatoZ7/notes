# 什么是 MQTT？

MQTT（Message Queuing Telemetry Transport）是一种轻量级、基于发布-订阅模式的消息传输协议，适用于资源受限的设备和低带宽、高延迟或不稳定的网络环境。它在物联网应用中广受欢迎，易于实施，能够实现传感器、执行器和其它设备之间的高效通信。

# MQTT 的优势

MQTT 协议已成为物联网数据传输的标准，因为它具有以下优势：

**轻量、高效**

物联网设备通常在处理能力、内存和能耗方面受到限制。MQTT 开销低、报文小的特点使其非常适合这些设备，因为它消耗更少的资源，例如最小的 MQTT 控制消息可以少至两个数据字节。即使在有限的能力下也能实现高效的通信。

**可靠**

物联网网络常常面临高延迟或连接不稳定的情况。MQTT 提供了持久会话（Persistent Session）和保活机制：

- **持久会话**：客户端与 Broker 保持有状态的会话。客户端重新连接时，可以继续使用原来的 ClientID，不需要重新订阅和从头建立状态。
- **保活**：客户端可以在建立连接时指定一个保活间隔，这会促使 Broker 定期检查连接状态。如果连接中断，Broker 会储存未传递的消息（根据 QoS 级别确定），并在客户端重新连接时尝试传递它们。

MQTT 定义了三种不同的 QoS（Quality of Service，服务质量等级），以确保 IoT 用例的可靠性：

- Qos 0：消息最多传送一次。如果当前客户端不可用，它将丢失这条消息。
- Qos 1：消息至少传送一次。
- QoS 2：消息只传送一次。

**安全**

MQTT 提供传输层安全（TLS）和安全套接层（SSL）加密功能。此外，MQTT 还通过用户名/密码凭证或客户端证书提供身份验证和授权机制，以保护网络及其资源的访问。

**可扩展**

MQTT 的发布-订阅模式为设备之间提供了无缝的双向通信方式。客户端既可以向主题发布消息，也可以订阅接收特定主题上的消息，从而实现了物联网生态系统中的高效数据交换，而无需直接将设备耦合在一起。这种模式也简化了新设备的集成，同时保证了系统易于扩展。

**语言支持**

几种语言（如 Python）对 MQTT 协议的实施提供广泛的支持。因此，开发人员可以在任何类型的应用程序中以最少的编码快速实现它。

# MQTT 的组成

- MQTT 客户端

任何运行 MQTT 客户端库的应用或设备都是 MQTT 客户端。 MQTT 客户端既可以发布消息，也可以订阅消息。例如，使用 MQTT 的即时通讯应用是客户端，使用 MQTT 上报数据的各种传感器是客户端，各种 MQTT 测试工具也是客户端。

- MQTT Broker

MQTT Broker 是负责处理客户端请求的关键组件，包括建立连接、断开连接、订阅和取消订阅等操作，同时还负责消息的转发。

# MQTT 主题

MQTT 协议根据主题来转发消息。主题通过 `/` 来区分层级，类似于 URL 路径，例如：

```
chat/room/1

sensor/10/temperature

sensor/+/temperature
```

MQTT 主题支持以下两种通配符：

- `+`：表示单层通配符，例如 `a/+` 匹配 `a/x` 或 `a/y`。
- `#`：表示多层通配符，例如 `a/#` 匹配 `a/x`、`a/b/c/d`。

注意通配符主题**只能用于订阅**，不能用于发布。

# MQTT 的工作流程

1. 客户端使用 TCP/IP 协议与 Broker 建立连接，可以选择使用 TLS/SSL 加密来实现安全通信。客户端提供认证信息，并指定会话类型（Clean Session 或 Persistent Session）。
2. 客户端既可以向特定主题发布消息，也可以订阅主题以接收消息。当客户端发布消息时，它会将消息发送给 MQTT Broker；而当客户端订阅消息时，它会接收与订阅主题相关的消息。
3. MQTT Broker 接收发布的消息，并将这些消息转发给订阅了对应主题的客户端。它根据 QoS 等级确保消息可靠传递，并根据会话类型，为断开连接的客户端存储消息。

# MQTT 实战

## demo

### 基础的服务

先启动一个简单的 broker 服务：

```go
// broker.go
package main

import (
	"log"
	"os"
	"os/signal"

	mqtt "github.com/mochi-mqtt/server/v2"
	"github.com/mochi-mqtt/server/v2/hooks/auth"
	"github.com/mochi-mqtt/server/v2/listeners"
)

func main() {
	server := mqtt.New(nil)

	tcp := listeners.NewTCP("t1", ":1883")
	err := server.AddListener(tcp, nil)
	if err != nil {
		log.Fatalf("failed to add listener: %v", err)
	}

	log.Println("MQTT Broker started on :1883 ...")

	go func() {
		if err := server.Serve(); err != nil {
			log.Fatalf("server error: %v", err)
		}
	}()

	sig := make(chan os.Signal, 1)
	signal.Notify(sig, os.Interrupt)
	<-sig

	log.Println("shutting down broker...")
	server.Close()
}
```

启动一个客户端订阅 broker 主题：

```go
// client_sub.go
package main

import (
	"log"
	"os"
	"os/signal"

	mqtt "github.com/eclipse/paho.mqtt.golang"
)

func main() {
	broker := "tcp://localhost:1883"
	topic := "demo/topic"
	clientID := "demo-sub"

	opts := mqtt.NewClientOptions().
		AddBroker(broker).
		SetClientID(clientID).
		SetOrderMatters(false)
	opts.OnConnect = func(c mqtt.Client) {
		log.Printf("已连接 broker %s", broker)
	}
	opts.OnConnectionLost = func(c mqtt.Client, err error) {
		log.Printf("连接丢失: %v", err)
	}

	// 建立连接
	client := mqtt.NewClient(opts)
	if token := client.Connect(); token.Wait() && token.Error() != nil {
		log.Fatalf("连接失败: %v", token.Error())
	}

	// 订阅主题并打印收到的消息
	if token := client.Subscribe(topic, 0, func(_ mqtt.Client, msg mqtt.Message) {
		log.Printf("收到消息 topic=%s payload=%s", msg.Topic(), string(msg.Payload()))
	}); token.Wait() && token.Error() != nil {
		log.Fatalf("订阅失败: %v", token.Error())
	}

	log.Printf("订阅中，主题=%s", topic)

	// 等待中断信号，优雅退出
	sig := make(chan os.Signal, 1)
	signal.Notify(sig, os.Interrupt)
	<-sig

	log.Println("收到中断信号，开始退出...")
	client.Unsubscribe(topic)
	client.Disconnect(250)
}
```

启动一个客户端往 broker 主题发送消息：

```go
// client_pub.go
package main

import (
	"log"

	mqtt "github.com/eclipse/paho.mqtt.golang"
)

func main() {
	broker := "tcp://localhost:1883"
	topic := "demo/topic"
	message := "hello mqtt"
	clientID := "demo-pub"

	opts := mqtt.NewClientOptions().
		AddBroker(broker).
		SetClientID(clientID).
		SetOrderMatters(false)
	opts.OnConnect = func(c mqtt.Client) {
		log.Printf("已连接 broker %s", broker)
	}
	opts.OnConnectionLost = func(c mqtt.Client, err error) {
		log.Printf("连接丢失: %v", err)
	}

	client := mqtt.NewClient(opts)
	if token := client.Connect(); token.Wait() && token.Error() != nil {
		log.Fatalf("连接失败: %v", token.Error())
	}

	log.Printf("发布消息 %s 到 %s", message, topic)
	if token := client.Publish(topic, 0, false, message); token.Wait() && token.Error() != nil {
		log.Fatalf("发布失败: %v", token.Error())
	}

	log.Println("发布完成，准备断开连接")
	client.Disconnect(250)
}
```

最终效果：

```bash
> go run ./client_sub
2025/12/10 13:40:07 已连接 broker tcp://localhost:1883
2025/12/10 13:40:07 订阅中，主题=demo/topic
2025/12/10 13:40:10 收到消息 topic=demo/topic payload=hello mqtt
```

### 用户名密码校验

```go
// broker.go
func main() {
	server := mqtt.New(nil)

	// 添加用户名密码认证和 ACL 权限控制
	err := server.AddHook(new(auth.Hook), &auth.Options{
		Ledger: &auth.Ledger{
			Auth: auth.AuthRules{
				{Username: "demo-pub", Password: "password123", Allow: true},
				{Username: "demo-sub", Password: "password456", Allow: true},
			},
		},
	})
	if err != nil {
		log.Fatalf("failed to add auth hook: %v", err)
	}

	......
}
```

客户端设置用户名和密码：

```go
// client_pub.go
func main() {
	......

	opts := mqtt.NewClientOptions().
		AddBroker(broker).
		SetClientID(clientID).
		SetUsername("demo-pub").
		SetPassword("password123").
		SetOrderMatters(false)

	......
}
```

### 消息持久化

MQTT 提供了 Retained Message（保留消息）用于记录每个 Topic **最后一条**消息，如果有新的会被**覆盖**。

如果消费者掉线，会在重新启动后获取到最新的保留消息。

```go
// broker.go
func main() {
	server := mqtt.New(nil)

	// 添加 Redis 持久化 hook
	err := server.AddHook(new(redis.Hook), &redis.Options{
		Options: &rv8.Options{
			Addr:     "127.0.0.1:6379", // Redis 地址
			Password: "",               // 无密码
			DB:       0,                // 使用默认数据库
		},
	})
	if err != nil {
		log.Fatalf("failed to add redis hook: %v", err)
	}

	......
}
```

```go
// client_pub.go
func main() {
	......

	log.Printf("发布消息 %s 到 %s", message, topic)
	if token := client.Publish(topic, 0, true, message); token.Wait() && token.Error() != nil {
		log.Fatalf("发布失败: %v", token.Error())
	}

	......
}
```






# 什么是 MQTT over WSS？

MQTT over WebSockets (WSS) 是一种 MQTT 实施，用于将数据直接接收到 Web 浏览器中。MQTT 协议定义了一个 JavaScript 客户端来为浏览器提供 WSS 支持。在这种情况下，该协议照常工作，但它向 MQTT 消息添加了额外标头以支持 WSS 协议。您可以将其视为包装在 WSS 信封中的 MQTT 消息负载。

# 参考

[什么是 MQTT？- aws](https://aws.amazon.com/cn/what-is/mqtt/)


<!-- TODO 协议格式、用户保存机制、broker/sub 掉线 -->