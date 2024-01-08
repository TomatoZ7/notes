# Go 并发编程 | 深入理解 RWMutex

## 1.背景与使用

在读多写少的场景使用 Mutex，如果短时间内有大量的读操作，没有写操作，那么大量的读操作在 Mutex 的机制下也只能是串行读。

针对这种情况，如果使用 RWMutex，就能够将原本的串行读优化为并行读。

RWMutex 俗称读写锁，特点是读写互斥、写写互斥，读读不互斥。在任一时刻，只能有一个 writer 或者若干个 reader 持有 RWMutex。

RWMutex 对外暴露的方法有 7 个：

+ Lock/Unlock：写操作时调用的方法，加写锁/释放写锁；
+ RLock/RUnlock：读操作时调用的方法，加读锁/释放读锁；
+ TryLock/TryRLock：尝试对 RWMutex 加写锁/读锁，并立即返回是否加锁成功；
+ RLocker：返回一个 `Locker` 接口对象，它的 `Lock` 方法是调用 RWMutex 的 `RLock` 方法，它的 `Unlock` 方法是调用 RWMutex 的 `RUnlock` 方法。

RWMutex 的使用与 Mutex 基本一致，无论是直接声明变量，还是嵌入到 struct 中，都不必显示地初始化。具体可以参考 [Mutex 的使用](./001-Mutex的使用.md#2mutex-的使用方法)

## 2.底层实现

我们将基于 [GO 1.20.12](https://github.com/golang/go/blob/go1.20.12/src/sync/rwmutex.go) 来进行解读。

### 2.1 数据结构

```go
type RWMutex struct {
	w           Mutex        // 用于解决多个 writer 的竞争
	writerSem   uint32       // writer 的信号量，当活跃的 reader 全部完成后，通过 writerSem 唤醒等待的 writer
	readerSem   uint32       // reader 的信号量，当 writer 完成写操作后，通过 readerSem 唤醒所有等待的 reader
	readerCount atomic.Int32 // 记录当前 reader 的数量
	readerWait  atomic.Int32 // 记录 writer 请求锁时需要等待结束读取操作的 reader 数量
}

const rwmutexMaxReaders = 1 << 30 // reader 最大数量
```

从数据结构我们可以看到，Go 语言的 RWMutex 是基于 Mutex 实现的。

那么接下来，我们会在源代码基础上删除数据竞争检测等非主要逻辑代码，来分析加解锁。

### 2.2 RLock: 获取读锁

```go
func (rw *RWMutex) RLock() {
    if atomic.AddInt32(&rw.readerCount, 1) < 0 {
		// 说明此时有 writer 持有锁或等待锁，当前 reader 阻塞休眠
        runtime_SemacquireMutex(&rw.readerSem, false, 0)
    }
}
```

从这里可以看出 RWMutex 是**写优先**的设计，当 writer 请求锁时，如果有一个或多个 reader 持有锁，它会等这些 reader 释放完锁，才有可能获取锁；而在 writer 等待期间，后来的 reader 会阻塞休眠，直到该 writer 完成写操作并释放锁。避免了 writer 饥饿问题。

### 2.3 RUnLock: 释放读锁

```go
func (rw *RWMutex) RUnlock() {
	// reader 计数减 1
	if r := rw.readerCount.Add(-1); r < 0 {
		// 说明此时有 writer 等待请求锁，执行 slow path 解锁
		rw.rUnlockSlow(r)
	}
}

func (rw *RWMutex) rUnlockSlow(r int32) {
	// RWMutex 状态检查
	// r+1 == 0: 说明原本 readerCount 的值为 0，也就是原本没有加读锁；
	// r+1 == -rwmutexMaxReaders: 说明原本 readerCount 的值为 -rwmutexMaxReaders，同样是没有加读锁；
	// 如果对没有 RLock 的 RWMutex 执行 RUnlock 操作，则抛出 fatal error
	if r+1 == 0 || r+1 == -rwmutexMaxReaders {
		fatal("sync: RUnlock of unlocked RWMutex")
	}

	if rw.readerWait.Add(-1) == 0 {
		// 如果是 writer 等待期间最后一个结束读取操作的 reader，则唤醒 writer
		runtime_Semrelease(&rw.writerSem, false, 1)
	}
}
```

### 2.4 Lock: 获取写锁

```go
func (rw *RWMutex) Lock() {
	// 首先，使用 Mutex 解决与其他 writer 的竞争
	rw.w.Lock()
	// 这里有两步：
	// 1. 将 RWMutex 的 readerCount 修改为负数，说明此时有活跃的 writer；
	// 2. 将转为负数的 readerCount 再加上 rwmutexMaxReaders 得到 r，r 表示当前持有读锁的 reader 数量。
	r := rw.readerCount.Add(-rwmutexMaxReaders) + rwmutexMaxReaders
	// r != 0: 说明当前有 reader 持有读锁
	// rw.readerWait.Add(r): 将当前持有读锁的 reader 数量赋值给 readerWait 字段
	if r != 0 && rw.readerWait.Add(r) != 0 {
		// 由于有 reader 持有读锁，该 writer 需要等待
		runtime_SemacquireRWMutex(&rw.writerSem, false, 0)
	}
}
```

前面我们说 RWMutex 是基于 Mutex 的，而 Mutex 主要是用于处理多 writer 竞争。一旦一个 writer 竞争到了内部的 Mutex，它就会将 `readerCount` 修改为负数，表示当前有 writer 在请求锁，从这里我们可以分析出 `readerCount` 字段有两层含义，一是保存 reader 数量，二是记录当前是否有 writer。

### 2.5 Unlock: 释放写锁

```go
func (rw *RWMutex) Unlock() {
	// 将 readerCount 修改为正数，告诉 reader 没有活跃的 writer 了
	r := rw.readerCount.Add(rwmutexMaxReaders)
	// RWMutex 状态检查
	// 如果 RWMutex 本身未加写锁，那么 readerCount 的值为正数，
	// 再加上 rwmutexMaxReaders 必然会 >= rwmutexMaxReaders，
	// 由此可以判断这是对未加锁的 RWMutex 进行了 Unlock 操作。
	if r >= rwmutexMaxReaders {
		fatal("sync: Unlock of unlocked RWMutex")
	}
	// 唤醒阻塞的 reader
	for i := 0; i < int(r); i++ {
		runtime_Semrelease(&rw.readerSem, false, 0)
	}
	// 释放内部 Mutex
	rw.w.Unlock()
}
```

## 3.RWMutex 常见错误

RWMutex 的常见错误与 Mutex 一致，更详细的说明可以观看我的这篇[关于 Mutex 常见错误](todo:link)的介绍。

### 3.1 加解锁操作不是成对出现

与 Mutex 一样，RWMutex 的 Lock/Unlock、RLock/RUnlock 的调用也必须成对出现。推荐方法也和 Mutex 一样：

+ 使用 `defer`；
+ 封装函数。

### 3.2 复制 RWMutex

前面说过，RWMutex 的基于 Mutex 实现的，而 Mutex 本身就是不能复制的，再加上 RWMutex 还有其他状态字段，所以 RWMutex 就更加不能被复制了。

### 3.3 重入

RWMutex 的重入场景大致可以分为三种。

1. 重复 Lock

RWMutex 借助内存的 Mutex 来解决不同 writer 之间的竞争问题，如果重复调用 Lock，相当于 Mutex 的重入，会抛出 `fatal error: all goroutines are asleep - deadlock!` 的错误。

```go
var mu sync.RWMutex

func main() {
	mu.Lock()
	fmt.Println("main")
	foo()
	mu.Unlock()
}

func foo() {
	mu.Lock()
	fmt.Println("foo")
	mu.Unlock()
}
```

2. RLock 期间 Lock

```go
var mu sync.RWMutex

func main() {
	mu.RLock()
	fmt.Println("rlock...")

	lock()

	mu.RUnlock()
}

func lock() {
	mu.Lock()
	fmt.Println("lock...")
	mu.Unlock()
}
```

我们知道当有活跃的 reader 时，writer 请求锁需要等待。在这个例子中，我们在活跃的 reader 执行读操作时调用 writer 的写操作，就会造成 reader 需要等待 writer 完成写操作才能释放读锁，而 writer 需要等待 reader 释放读锁才能执行写操作的互相等待的死锁状态。导致程序最后抛出 `fatal error: all goroutines are asleep - deadlock!` 的错误。

3. 活跃的 reader 依赖新 reader 的执行结果，而此时已有 writer 等待请求锁

```go
var mu sync.RWMutex

func main() {
	go func() {
		reader()
	}()

	go func() {
		// 等待，保证 reader 先请求锁
		time.Sleep(time.Second * 2)
		writer()
	}()

	select {}
}

func reader() {
	mu.RLock()
	fmt.Println("reader work...")
	// 等待，保证 writer 请求锁
	time.Sleep(time.Second * 4)
	newReaderAfterWriter()
	mu.RUnlock()
}

func writer() {
	mu.Lock()
	fmt.Println("writer work...")
	mu.Unlock()
}

func newReaderAfterWriter() {
	mu.RLock()
	fmt.Println("reader after writer work...")
	mu.RUnlock()
}

```

我们来解释一下这段代码的执行顺序：

+ reader 请求锁，成功获取到读锁；
+ writer 请求锁，此时有活跃的 reader，writer 阻塞等待；
+ reader 执行到 `newReaderAfterWriter` 函数，会有新来的 reader 请求锁，有 writer 等待请求锁，新来的 reader 阻塞等待。

这就造成了 writer、reader、新来的 reader 三者之间的循环等待，最终抛出 `fatal error: all goroutines are asleep - deadlock!` 的错误。

## 4.总结

本篇文章我们介绍了 RWMutex 的基本使用、底层设计和易错盘点，希望能对你有帮助。