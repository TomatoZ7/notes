# Go 如何控制并发的goroutine数量?

## 1.为什么要控制goroutine并发的数量？

在开发过程中，如果不对 goroutine 加以控制而进行滥用的话，可能会导致服务整体崩溃。比如耗尽系统资源导致程序崩溃，或者 CPU 使用率过高导致系统忙不过来。

## 2.用什么方法控制 goroutine 并发的数量？

### 2.1 有缓冲 channel

利用缓冲满时发送阻塞的特性

```go
package main

import (
    "fmt"
    "runtime"
    "time"
)

var wg = sync.WaitGroup{}

func main() {
    // 模拟用户请求数量
    requestCount := 10
    fmt.Println("goroutine_num", runtime.NumGoroutine())
    // 管道长度即最大并发数
    ch := make(chan bool, 3)
    for i := 0; i < requestCount; i++ {
        wg.Add(1)
        ch <- true
        go Read(ch, i)
    }

     wg.Wait()
}

func Read(ch chan bool, i int) {
    fmt.Printf("goroutine_num: %d, go func: %d\n", runtime.NumGoroutine(), i)
    <-ch
    wg.Done()
}
```

输出结果：默认最多不超过 3 个 goroutine 并发执行

```sh
goroutine_num 1
goroutine_num: 4, go func: 1
goroutine_num: 4, go func: 3
goroutine_num: 4, go func: 2
goroutine_num: 4, go func: 0
goroutine_num: 4, go func: 4
goroutine_num: 4, go func: 5
goroutine_num: 4, go func: 6
goroutine_num: 4, go func: 8
goroutine_num: 4, go func: 9
goroutine_num: 4, go func: 7
```

### 2.2 无缓冲 channel

任务发送和执行分离，指定消费者并发协程数

```go
package main

import (
    "fmt"
    "runtime"
    "sync"
)

var wg = sync.WaitGroup{}

func main() {
    // 模拟用户请求数量
    requestCount := 10
    fmt.Println("goroutine_num", runtime.NumGoroutine())
    ch := make(chan bool)
    for i := 0; i < 3; i++ {
        go Read(ch, i)
    }

    for i := 0; i < requestCount; i++ {
        wg.Add(1)
        ch <- true
    }

    wg.Wait()
}

func Read(ch chan bool, i int) {
    for _ = range ch {
        fmt.Printf("goroutine_num: %d, go func: %d\n", runtime.NumGoroutine(), i)
        wg.Done()
    }
}
```