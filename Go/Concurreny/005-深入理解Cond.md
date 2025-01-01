# Go 并发编程 | 深入理解 Cond

说起 Go 语言的 sync.Cond 你可能会比较陌生，毕竟相较于 sync 包的 Mutex、WaitGroup 等，Cond 的实际使用可能少之又少。那么今天，我们就来介绍这个鲜有使用的 Cond。

## 1.基本使用

### 1.1. 定义

sync.Cond 是 Go 语言标准库中的一个条件变量，条件变量通常用于等待某个条件为真的情况下才执行后续的操作。

### 1.2 方法/函数

Cond 相关的方法/函数：

```go
NewCond(l Locker) *Cond
func (c *Cond) Wait()
func (c *Cond) Signal()
func (c *Cond) Broadcast()
```

`NewCond(l Locker) *Cond` 要求传入一个 `Locker` 接口实例，一般使用 Mutex 或 RMMutex。

`Wait` 会释放锁并使当前 goroutine 进入等待状态，直到其他 goroutine 调用 `Signal` 或 `Broadcast` 方法唤醒它，**所以，`Wait` 方法调用时必须持有锁。**

`Signal` 唤醒一个等待的 goroutine，如果没有 goroutine 等待，会立即返回。

`Broadcast` 唤醒所有等待的 goroutine，如果没有 goroutine 等待，会立即返回。

### 1.3 使用方法

在使用 sync.Cond 时，通常需要以下几个步骤：

+ 定义一个 Locker 实例，如 Mutex、RWMutex；
+ 创建 sync.Cond 对象，并传入上一步定义的 Locker 实例；
+ 在需要阻塞等待的 goroutine 中，使用这个 Locker 实例加锁，并使用 Wait 方法阻塞；
+ 在需要通知 waiter 的 goroutine 中，使用 Signal 或 Boardcast 方法通知 waiter；
+ 最后释放锁。

下面是一个简单的示例：

```go
func main() {
	var mu sync.Mutex
	c := sync.NewCond(&mu)
	var count int

	for i := 0; i < 10; i++ {
		go func(i int) {
			// 加锁更改等待条件
			c.L.Lock()
			count++
			c.L.Unlock()

			// 唤醒所有的 waiter
			c.Broadcast()
		}(i)
	}

	c.L.Lock()
    // Wait 唤醒后还需要检查条件，所以用 for 循环
	for count != 10 {
		fmt.Println("主 goroutine 等待")
		c.Wait()
		fmt.Println("主 goroutine 被唤醒")
	}
	c.L.Unlock()

	fmt.Println("count: ", count)
}
```

## 2.源码剖析

我们将基于 [GO 1.20.12](https://github.com/golang/go/blob/go1.20.12/src/sync/cond.go) 来进行解读。

### 2.1 数据结构

```go
type Cond struct {
	noCopy noCopy

	// L is held while observing or changing the condition
	L Locker

	notify  notifyList  // goroutine 等待队列，先入先出
	checker copyChecker // 用于检查 Cond 实例是否被复制使用
}

// src/sync/runtime2.go
type notifyList struct {
	wait   uint32   // 等待唤醒的 goroutine 数量
	notify uint32   // 已被唤醒的 goroutine 数量
	lock   uintptr  // key field of the mutex
	head   unsafe.Pointer	// 队列头节点
	tail   unsafe.Pointer	// 队列尾节点
}
```

### 2.2 Wait / Signal

```go
func (c *Cond) Wait() {
	c.checker.check()
	// 增加到等待队列中
	t := runtime_notifyListAdd(&c.notify)
	c.L.Unlock()
	// 阻塞休眠直到被唤醒
	runtime_notifyListWait(&c.notify, t)
	c.L.Lock()
}

func (c *Cond) Signal() {
	c.checker.check()
	runtime_notifyListNotifyOne(&c.notify)
}

func (c *Cond) Broadcast() {
	c.checker.check()
	runtime_notifyListNotifyAll(&c.notify)
}
```

这部分源码比较简单，函数命名也清晰明了，关于 `runtime_xxx` 的源码，可以参见 [runtime/sema.go](https://github.com/golang/go/blob/go1.20.12/src/runtime/sema.go)。

## 3.使用 Cond 可能犯的错误

### 3.1 调用 Wait 时未加锁

我们重新拿回上面的例子，如果我们在调用 `Wait` 时未加锁：

```go
func main() {
	var mu sync.Mutex
	c := sync.NewCond(&mu)
	var count int

	for i := 0; i < 10; i++ {
		go func(i int) {
			// 加锁更改等待条件
			c.L.Lock()
			count++
			c.L.Unlock()

			// 唤醒所有的 waiter
			c.Broadcast()
		}(i)
	}

	// c.L.Lock()
    // Wait 唤醒后还需要检查条件，所以用 for 循环
	for count != 10 {
		fmt.Println("主 goroutine 等待")
		c.Wait()
		fmt.Println("主 goroutine 被唤醒")
	}
	// c.L.Unlock()

	fmt.Println("count: ", count)
}
```

就会抛出 `fatal error: sync: unlock of unlocked mutex`，通过对 `Wait` 方法源码阅读我们知道它会将当前 goroutine 加入等待队列，再释放锁然后进入阻塞；从逻辑上来讲这样如果某一 goroutine 一直持有锁，也无法让其他 `Wait` 的调用者加入到 notify 队列中。

### 3.2 没有检查条件是否满足

```go
func main() {
	var mu sync.Mutex
	c := sync.NewCond(&mu)
	var count int

	for i := 0; i < 10; i++ {
		go func(i int) {
			// 加锁更改等待条件
			c.L.Lock()
			count++
			c.L.Unlock()

			// 唤醒所有的 waiter
			c.Broadcast()
		}(i)
	}

	c.L.Lock()
    // Wait 唤醒后还需要检查条件，所以用 for 循环
	// for count != 10 {
		fmt.Println("主 goroutine 等待")
		c.Wait()
		fmt.Println("主 goroutine 被唤醒")
	// }
	c.L.Unlock()

	fmt.Println("count: ", count)
}
```

在这个例子中你会发现计数值 `count` 没有达到预期值 10 之后就返回了，这是因为没有进行条件检查。

## 4.小结

本篇文章我们介绍了 Cond 的基本使用、底层设计和易错盘点，希望能对你有帮助。