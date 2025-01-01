# SingleFlight

singleflight 是 Go 官方扩展库 x 中提供的扩展并发原语，它可以确保在并发环境下，对同一函数的多次调用只执行一次，而其他的调用则等待该执行的结果。这对于减少对数据库、缓存或外部服务的重复请求非常有用。

简而言之，SingleFlight 提供了**请求合并**的功能。

我们将基于 [GO 1.21 https://github.com/golang/sync/blob/internal-branch.go1.21-vendor/singleflight/singleflight.go](https://github.com/golang/sync/blob/internal-branch.go1.21-vendor/singleflight/singleflight.go) 来解析。

## 1.使用

SingleFlight 的数据结构是 `Group`，它提供了三个方法：

```go
func (g *Group) Do(key string, fn func() (interface{}, error)) (v interface{}, err error, shared bool)
func (g *Group) DoChan(key string, fn func() (interface{}, error)) <-chan Result
func (g *Group) Forget(key string)
```

`Do` 方法执行参数 `fn` 函数，并返回函数执行的结果，`shard` 表示结果 `v` 和 `err` 是否返回给多个 goroutine。对于同一个 `key`，同一时间只有一个 goroutine 在执行，执行完成后将结果返回给这一时间内所有等待的 goroutine。

`DoChan` 方法跟 `Do` 类似，不过是返回一个 channel，可以从这个 channel 接收 `fn` 函数执行完后的结果。`Result` 的结构如下：

```go
type Result struct {
	Val    interface{}
	Err    error
	Shared bool
}
```

`Forget` 告诉 singleflight 忘记这个 key，这样后续这个 key 的请求会执行 `fn`，而不会等待。

下面是使用 singleflight `Do` 方法的示例：

```go
package main

import (
	"fmt"
	"sync"
	"time"

	"golang.org/x/sync/singleflight"
)

func main() {
	key := "foo"
    // 创建 singleflight.Group 实例
	g := singleflight.Group{}
	wg := sync.WaitGroup{}

    // 启动了 10 个 goroutine 来模拟并发执行任务
	for i := 0; i < 10; i++ {
		wg.Add(1)

		go func() {
			defer wg.Done()

			val, err, shared := g.Do(key, func() (interface{}, error) {
				fmt.Println("exec...")
				time.Sleep(time.Second * 10)
				return "bar", nil
			})
			if err != nil {
				fmt.Println("exec error: ", err)
				return
			}

			fmt.Printf("val: %v, shared: %v\n", val, shared)
		}()
	}

	wg.Wait()
}
```

## 2.源码解析

我们将基于 [https://github.com/golang/sync/blob/internal-branch.go1.21-vendor/singleflight/singleflight.go](https://github.com/golang/sync/blob/internal-branch.go1.21-vendor/singleflight/singleflight.go) 来解析。

### 2.1 数据结构

```go
// call 表示一个正在处理或已经处理完成的调用
type call struct {
	wg sync.WaitGroup

    // val 和 err 在 WaitGroup 完成前只会写一次，在 WaitGroup 完成后会被读取但不会被写入
    // 表示处理完成的结果
	val interface{}
	err error

    // dups 和 chans 在 WaitGroup 完成之前使用 singleflight 的互斥锁进行读写
    // 在 WaitGroup 完成之后，这些字段会被读取但不会被写入
	dups  int
	chans []chan<- Result
}

// 代表一个 singleflight 对象
type Group struct {
	mu sync.Mutex       // 互斥锁用来保护 m 的并发读写
	m  map[string]*call // 惰性初始化，当 m == nil 时才会初始化的字段
}
```

### 2.2 Do 方法

```go
// Do 执行并返回给定函数的结果，确保每次只有一个给定 key 在执行。
// 如果出现重复调用，重复调用者会等待原始调用完成并接受相同的结果。
// 返回值 shared 表示是否将 v 提供给了多个调用者
func (g *Group) Do(key string, fn func() (interface{}, error)) (v interface{}, err error, shared bool) {
	g.mu.Lock()
	if g.m == nil {
		g.m = make(map[string]*call)
	}

    // 如果 key 已存在，则解锁并等待
    // 最后返回执行结果和 shared=true
	if c, ok := g.m[key]; ok {
		c.dups++
		g.mu.Unlock()
		c.wg.Wait()

		if e, ok := c.err.(*panicError); ok {
			panic(e)
		} else if c.err == errGoexit {
			runtime.Goexit()
		}
		return c.val, c.err, true
	}

    // key 不存在，说明是第一次调用，新建 call 对象并将 {key:call} 写入 Group 的 map 中
	c := new(call)
	c.wg.Add(1)
	g.m[key] = c
	g.mu.Unlock()

    // 调用方法
	g.doCall(c, key, fn)
	return c.val, c.err, c.dups > 0
}
```

`doCall` 方法会实际调用函数 `fn`，它包含很多区分 panic 和 runtime.Goexit 的代码，这不在我们的讨论范围之内，所以以下是 `doCall` 的简化过后的代码：

> doCall 完整源码： [https://github.com/golang/sync/blob/internal-branch.go1.21-vendor/singleflight/singleflight.go#L135](https://github.com/golang/sync/blob/internal-branch.go1.21-vendor/singleflight/singleflight.go#L135)

```go
func (g *Group) doCall(c *call, key string, fn func() (interface{}, error)) {
    c.val, c.err = fn()

    g.mu.Lock()
    defer g.mu.Unlock()

    c.wg.Done()
    if g.m[key] == c {
        // 从 map 中删除 key，这意味着后续再有相同 key 的调用又会重新开始新一次的 fn 函数调用
        delete(g.m, key)
    }

    // 主要提供给 DoChan 方法，发送结果给等待的 channel
    for _, ch := range c.chans {
        ch <- Result{c.val, c.err, c.dups > 0}
    }
}
```

### 2.3 DoChan 方法

`DoChan` 方法和 `Do` 方法类似，会返回一个 channel，该 channel 会在执行完成后接收到结果，并且不会被关闭。

```go
func (g *Group) DoChan(key string, fn func() (interface{}, error)) <-chan Result {
	ch := make(chan Result, 1)
	g.mu.Lock()
	if g.m == nil {
		g.m = make(map[string]*call)
	}

	if c, ok := g.m[key]; ok {
		c.dups++
		c.chans = append(c.chans, ch)   // 这里是将需要等待的 channel 加入 c.chans，后续在 doCall 方法统一接收结果
		g.mu.Unlock()
		return ch
	}

	c := &call{chans: []chan<- Result{ch}}
	c.wg.Add(1)
	g.m[key] = c
	g.mu.Unlock()

	go g.doCall(c, key, fn)

	return ch
}
```

### 2.4 Forget 方法

`Forget` 告诉 singleflight 忘记指定 key，之后对这个 key 的第一个调用会执行 `fn` 函数，而不会等待。

```go
func (g *Group) Forget(key string) {
	g.mu.Lock()
	delete(g.m, key)
	g.mu.Unlock()
}
```

## 3.适用场景

+ 缓存击穿
+ 远程服务调用
+ 去重操作

## 4.小结

本文我们介绍了 singleflight，它可以通过合并请求的方式降低服务的并发压力，提高系统性能，常用于缓存系统中，希望对你有帮助。