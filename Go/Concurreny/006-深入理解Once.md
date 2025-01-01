# 深入理解 Once

## 1.使用场景

在日常开发中，要实现初始化单例对象，常见的有如下 3 种方式：

1. 全局变量声明

```go
package main

import "net/http"

var client = http.DefaultClient
```

2. init 函数

```go
package main

import "net/http"

var client *http.Client

func init() {
	client = http.DefaultClient
}
```

3. 自定义初始化函数

```go
package main

import (
	"net/http"
)

var client *http.Client

func InitClient() {
	client = http.DefaultClient
}

func main() {
	InitClient()
}
```

这 3 种方法在使用上都是线程安全的，但如果在初始化的过程中某个单例对象并未被实际使用，那么就会造成内存浪费和拖慢程序启动，又或者它非程序启动所必须，我们就可以将它们延迟初始化，这时候 `sync.Once` 就派上用场了。

在多数情况下，`sync.Once` 被用于控制变量的初始化，这个变量的读写满足如下三个条件：

+ 当且仅当第一次访问某个变量时，进行初始化；
+ 变量初始化过程中，所有读都被阻塞，直到初始化完成；
+ 变量仅初始化一次，初始化完成后驻留在内存里。

## 2.使用

`sync.Once` 只暴露了一个方法 `Do`：

```go
func (o *Once) Do(f func())
```

参数 `f` 是一个无参数无返回值的函数，只有第一次调用 `Do` 方法时 `f` 才会执行，就算第一次传入的参数 `f` 和第二次传入的参数 `f` 不一样，也只会执行第一次传入的参数 `f`。

```go
package main

import (
	"net/http"
	"sync"
)

var client *http.Client
var once sync.Once

func GetClient() {
	once.Do(func() {
		client = http.DefaultClient
	})
}
```

## 3.源码解析

我们将基于 [GO 1.20.12: https://github.com/golang/go/blob/go1.20.12/src/sync/once.go](https://github.com/golang/go/blob/go1.20.12/src/sync/once.go) 来解析。

### 3.1 数据结构

```go
type Once struct {
    // done indicates whether the action has been performed.
    // It is first in the struct because it is used in the hot path.
    // The hot path is inlined at every call site.
    // Placing done first allows more compact instructions on some architectures (amd64/386),
    // and fewer instructions (to calculate offset) on other architectures.
	done uint32     // 表示操作是否已执行，0: 未执行 1: 已执行
	m    Mutex
}
```

这里解释了为什么将 `done` 置为 Once 的第一个字段：`done` 在热路径中，`done` 放在第一个字段，能够减少 CPU 指令，也就是说，这样做能够提升性能。

简单解释下这段话：

1. 热路径 hot path 是指程序中执行频率非常高的代码段，`sync.Once` 绝大部分场景都会访问 `o.done`，在热路径上是比较好理解的，如果 hot path 编译后的机器码指令更少，更直接，必然是能够提升性能的；
2. 为什么放在第一个字段就能够减少指令呢？因为结构体第一个字段的地址和结构体的指针是相同的，如果是第一个字段，直接对结构体的指针解引用即可。如果是其他的字段，除了结构体指针外，还需要计算与第一个值的偏移(calculate offset)。在机器码中，偏移量是随指令传递的附加值，CPU 需要做一次偏移值与指针的加法运算，才能获取要访问的值的地址。因为，访问第一个字段的机器代码更紧凑，速度更快。

### 3.2 Do 方法

```go
func (o *Once) Do(f func()) {
	// Note: Here is an incorrect implementation of Do:
	//
	//	if atomic.CompareAndSwapUint32(&o.done, 0, 1) {
	//		f()
	//	}
	//
	// Do guarantees that when it returns, f has finished.
	// This implementation would not implement that guarantee:
	// given two simultaneous calls, the winner of the cas would
	// call f, and the second would return immediately, without
	// waiting for the first's call to f to complete.
	// This is why the slow path falls back to a mutex, and why
	// the atomic.StoreUint32 must be delayed until after f returns.

	if atomic.LoadUint32(&o.done) == 0 {
		// Outlined slow-path to allow inlining of the fast-path.
		o.doSlow(f)
	}
}

func (o *Once) doSlow(f func()) {
	o.m.Lock()
	defer o.m.Unlock()
	if o.done == 0 {
		defer atomic.StoreUint32(&o.done, 1)
		f()
	}
}
```

这里讨论了为什么不用 `atomic.CompareAndSwapUint32(&o.done, 0, 1)` 的原因：

如果 `f` 执行的很慢，在并发场景下后面调用 `Do` 方法的 goroutine 知道 `done` 已被设置为 1（已执行）并且立即 return 了，但是由于 `f` 还没执行完，所以获取初始化资源时可能会得到空的资源。

所以，正确的实现是在初始化时，并发的 goroutine 进入 `doSlow` 方法，利用互斥锁+双检查机制，保证 `f` 只会被执行一次并且并发的 goroutine 会等待 `f` 执行完成。

## 4.易错盘点

### 4.1 f 函数重复使用 Once

```go
package main

import (
	"net/http"
	"sync"
)

var client *http.Client
var once sync.Once

func GetClient() {
	once.Do(InitClient)
}

func InitClient() {
	once.Do(func() {
		client = http.DefaultClient
	})
}

func main() {
	GetClient()
}
```

由于 Once 内部使用了互斥锁，如果在 `f` 中再次调用 `once.Do` 方法，会导致死锁。

### 4.2 Once 作为局部变量初始化多次

```go
package main

import (
	"net/http"
	"sync"
)

var client *http.Client

func GetClient() {
	var once sync.Once
	once.Do(func() {
		client = http.DefaultClient
	})
}
```

如果你希望全局变量只初始化一次，那么应该将 Once 也声明为全局变量。

## 5.拓展：初始化成功判断

如果 `f` 函数初始化失败，即使再次调用 `Do` 方法，Once 也会认为执行已经成功，不会再次执行 `f`。

我们可以拷贝一份源码，在此基础上修改：

```go
package main

import (
	"sync"
	"sync/atomic"
)

type Once struct {
	m    sync.Mutex
	done uint32
}

func (o *Once) Do(f func() error) error {
	if atomic.LoadUint32(&o.done) == 0 {
		return o.doSlow(f)
	}

	return nil
}

func (o *Once) doSlow(f func() error) error {
	o.m.Lock()
	defer o.m.Unlock()
	var err error
	if o.done == 0 {
		err = f()
		if err == nil { // 初始化成功才将标记置为已初始化
			atomic.StoreUint32(&o.done, 1)
		}
	}
	return err
}
```

## 6.小结

本文我们介绍了 `sync.Once`，常用来实现单例模式或延迟初始化等场景，希望能对你有帮助。