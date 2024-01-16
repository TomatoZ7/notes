# Go 并发编程 | 深入理解 WaitGroup

## 1.介绍

WaitGroup 是 sync 包用来做任务编排的一个并发原语，主要用来解决一个 goroutine 等待多个 goroutine 执行完成的场景，常见的有后端 worker 启动了多个子消费者干活、并发爬虫，并发下载等。

## 2.源码剖析

我们将基于 [GO 1.20.12](https://github.com/golang/go/blob/go1.20.12/src/sync/waitgroup.go) 来进行解读。

### 2.1 数据结构

```go
type WaitGroup struct {
	noCopy noCopy

	state atomic.Uint64 // 高 32 bit 是计数值, 低 32 bit 是 waiter 的计数。
	sema  uint32
}
```

`noCopy` 是一个辅助字段，用于辅助 vet 工具检查这个 WaitGroup 实例是否被复制。具体来说，如果在执行 `go vet` 时如果检测到 WaitGroup 实例被复制就会报错。但是执行 `go run` 是不会报错的。

`state` 是一个复合字段，高 32 位是计数值，低 32 位是 waiter 的计数。

`sema` 是信号量字段。

其实在过去的几个版本中，WaitGroup 的数据结构都有改变，例如 [go1.19.13](https://github.com/golang/go/blob/go1.19.13/src/sync/waitgroup.go#L23)：

```go
type WaitGroup struct {
	noCopy noCopy

	// 64-bit value: high 32 bits are counter, low 32 bits are waiter count.
	// 64-bit atomic operations require 64-bit alignment, but 32-bit
	// compilers only guarantee that 64-bit fields are 32-bit aligned.
	// For this reason on 32 bit architectures we need to check in state()
	// if state1 is aligned or not, and dynamically "swap" the field order if
	// needed.
	state1 uint64
	state2 uint32
}
```

[go1.17.13](https://github.com/golang/go/blob/go1.17.13/src/sync/waitgroup.go#L20)：

```go
type WaitGroup struct {
	noCopy noCopy

	// 64-bit value: high 32 bits are counter, low 32 bits are waiter count.
	// 64-bit atomic operations require 64-bit alignment, but 32-bit
	// compilers do not ensure it. So we allocate 12 bytes and then use
	// the aligned 8 bytes in them as state, and the other 4 as storage
	// for the sema.
	state1 [3]uint32
}
```

基本上都是围绕计数值、waiter 的计数、信号量和操作系统的位数来构建的，这里就不再展开。

WaitGroup 提供了 3 个方法，保持了 Go 一贯的简洁风格。

```go
func (wg *WaitGroup) Add(delta int)
func (wg *WaitGroup) Done()
func (wg *WaitGroup) Wait()
```

接下来，我们会在源代码基础上删除数据竞争检测等非主要逻辑代码，来分析这些方法。

### 2.2 Add & Done

```go
func (wg *WaitGroup) Add(delta int) {
	// 高 32 bit 是计数值 v，所以把 delta 左移 32，增加到计数上
	state := wg.state.Add(uint64(delta) << 32)
	v := int32(state >> 32) // 计数值 counter
	w := uint32(state)      // waiter count

	// counter 不允许为负数，否则 panic
	if v < 0 {
		panic("sync: negative WaitGroup counter")
	}
	// w != 0: 正常情况下 waiter 肯定大于等于 0，当 waiter != 0 时，说明已经执行了 Wait()；
	// delta > 0: 说明这是一次计数值加的操作；
	// v == int32(delta): 也就是 v + delta == delta，推导出 v = 0，说明可能是第一次执行 Add() 操作或执行 Add(-1) 把 v 减到了 0；
	// 综上，这个判断语句是检测是否并发调用 Add()、Wait() 方法，防止 WaitGroup 滥用。
	if w != 0 && delta > 0 && v == int32(delta) {
		panic("sync: WaitGroup misuse: Add called concurrently with Wait")
	}
	// counter > 0 || 没有 waiter: 直接退出
	if v > 0 || w == 0 {
		return
	}
	// 走到这里，说明当前 goroutine 将 counter 设置为 0，在还有 waiter 的情况下。
	// 可能是正常的 Done 操作，也可能是出现了并发调用 Add 和 Wait 的情况，所以还是要做好简单且健全的检测。
	if wg.state.Load() != state {
		panic("sync: WaitGroup misuse: Add called concurrently with Wait")
	}
	// 成功通过检测，说明是是正常的 Done 操作将 counter 值置为 0，这时候就可以清空 state 值，开始新一轮的计数。
	wg.state.Store(0)
	// 释放信号量，唤醒 Wait 的 goroutine
	for ; w != 0; w-- {
		runtime_Semrelease(&wg.sema, false, 0)
	}
}

// Done 方法实际就是计数值减 1
func (wg *WaitGroup) Done() {
	wg.Add(-1)
}
```

`Add` 方法主要是操作 `state` 的计数部分，将传入的 `delta` 值通过原子操作加到 `state` 的计数部分。`delta` 值也可以为负数，相当于 `state` 值的计数部分减去 `delta`，实际上，`Done` 方法就是通过 `Add(-1)` 实现的。

### 2.3 Wait

```go
func (wg *WaitGroup) Wait() {
	// 可能存在并发调用 Wait 的情况，所以使用 for 循环
	for {
		state := wg.state.Load()
		v := int32(state >> 32) // 计数值 counter
		w := uint32(state)      // waiter count
		if v == 0 {
			// Counter 为 0，不需要等待了，直接返回
			return
		}
		// 原子操作增加 waiter count
		if wg.state.CompareAndSwap(state, state+1) {
			runtime_Semacquire(&wg.sema)
			if wg.state.Load() != 0 {
				panic("sync: WaitGroup is reused before previous Wait has returned")
			}
            // 被唤醒，不再阻塞，直接返回
			return
		}
	}
}
```

`Wait` 方法不断检查 `state` 值，如果其中的计数值 counter 为 0，说明所有的任务已完成，调用者不必阻塞等待，可以继续执行后面的逻辑；如果计数值 counter 大于 0，说明此时还有任务未完成，需要将 waiter count 加 1，并等待。

## 3.WaitGroup 常见错误

使用 WaitGroup 的常规操作是预先设定好计数值，然后调用相同次数的 `Done` 方法来完成所有任务。如果操作不正确，就有可能导致 panic，接下来我们来看看 WaitGroup 的常见错误。

### 3.1 计数值为负数

虽然 `Add(delta int)` 的 `delta` 值可以为负数，但是 WaitGroup 会检查计数值，如果为负数，则会 panic。

```go
if v < 0 {
    panic("sync: negative WaitGroup counter")
}
```

使计数值为负数的常见操作有：

+ 调用 `Add` 方法时传递的 `delta` 参数为负值，如果当前的计数值加上一个负值的结果大于等于 0 的话是没问题的，但是如果小于 0，就会 panic；
+ 调用 `Done` 方法次数大于预期设定的计数值。

### 3.2 Add 时机不正确

```go
func main() {
	var wg sync.WaitGroup
	go subWork(&wg)
	go subWork(&wg)

	wg.Wait() // 由于这里的计数值为 0，所以不会阻塞，继续执行

	fmt.Println("Done")
}

func subWork(wg *sync.WaitGroup) {
	time.Sleep(time.Second)

	wg.Add(1)
	fmt.Println("subWorking...")
	wg.Done()
}
```

在这个例子中我们期望打印两次 `subWorking...`，但最终结果是只打印了 `Done`。

如果调用 `Add` 的时机在 `Wait` 之后，那么就会造成执行结果与预期结果不一致，在并发条件下可能导致 panic。通常做法应该是在一个 goroutine 预先设置计数值，在执行 `Wait` 操作，确保所有的 `Add` 方法调用之后再调用 `Wait` 方法。

### 3.3 WaitGroup 未结束就被重用

WaitGroup 是支持重用的：

> If a WaitGroup is reused to wait for several independent sets of events, new Add calls must happen after all previous Wait calls have returned.

但是必须等上一轮 `Wait` 结束后才能被重用，否则就会 panic。

```go
func main() {
	var wg sync.WaitGroup
	wg.Add(1)

	go func() {
		wg.Done() 
		wg.Add(1) 
	}()

	wg.Wait()
}
```

在这个例子中，第 8 行的 `wg.Add(1)` 和 `wg.Wait()` 如果并发执行，则会 `panic: sync: WaitGroup is reused before previous Wait has returned`。

## 4.小结

本篇文章我们介绍了 WaitGroup 的基本使用、底层设计和易错盘点，希望能对你有帮助。