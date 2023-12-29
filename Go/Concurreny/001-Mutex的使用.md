# Go 并发编程 | Mutex 的使用

## 1.Mutex 解决了什么问题？

我们先来看一个经典的「计数器」案例：

```go
func main() {
	count := 0
	wg := sync.WaitGroup{}
	wg.Add(100)

	for i := 0; i < 100; i++ {
		go func() {
			for j := 0; j < 10000; j++ {
				count++
			}
			wg.Done()
		}()
	}

	// 等待 100 个 goroutine 完成
	wg.Wait()
	fmt.Println(count)
}
```

在这段代码中，我们设置了 100 个 goroutine 来对计数器 `count` 进行 1w 次自增，我们期望最终的结果是 100w，但实际情况如下：

```sh
$ go run main.go
218900
$ go run main.go
213350
$ go run main.go
173200
```

这是为什么呢？我们来分析一下 `count++` 的汇编代码：

```go
package main

func main() {
	var count int
	for i := 0; i < 10; i++ {
		count++
	}
}
```

使用 `go tool compile -S -l -N main.go` 得到汇编代码：

```
0x0028 00040 (main.go:6) MOVD    main.count-8(SP), R0
0x002c 00044 (main.go:6) ADD     $1, R0, R0
0x0030 00048 (main.go:6) MOVD    R0, main.count-8(SP)
```

从汇编代码中我们可以得知，`count++` 并不是原子操作，至少包含读取，计算，写回步骤。也就是说，在同一时刻可能存在多个 goroutine 同时读取 `count` 值，自增并写回 `count` 值，对我们来说就是少了这几次的自增操作，结果自然就不如预期了。

**这个时候就该轮到 Mutex 上场了。**Mutex 是 Go 语言互斥锁（排他锁）的实现，用于保护共享资源，防止多个线程同时访问或修改这些资源，从而避免数据竞争和不确定的行为。

Mutex 具有以下特性：

1. 互斥性：一次只允许一个线程持有锁，其他线程必须等待，直到锁被释放；
2. 阻塞和等待：如果一个线程尝试获得互斥锁，但锁已经被其他线程持有，那么它将被阻塞，直到锁被释放；
3. 释放：线程在完成对共享资源的操作后应该释放互斥锁，以便其他线程可以获得锁并继续执行，这是为了避免死锁和确保资源的合理释放。

我们在上述代码中引入 Mutex：

```go
func main() {
	var mu sync.Mutex // 声明互斥锁
	count := 0
	wg := sync.WaitGroup{}
	wg.Add(100)

	for i := 0; i < 100; i++ {
		go func() {
			for j := 0; j < 10000; j++ {
				mu.Lock() // 加锁
				count++
				mu.Unlock() // 解锁
			}
			wg.Done()
		}()
	}

	// 等待 100 个 goroutine 完成
	wg.Wait()
	fmt.Println(count)
}
```

运行后得到预期结果 100w。

## 2.Mutex 的使用方法

### 2.1 直接声明使用

Mutex 的零值是没有 goroutine 等待的未加锁的状态，所以不需要额外的初始化，直接声明变量即可。

```go
var mu sync.Mutex
```

### 2.2 作为字段嵌入 struct 使用

```go
type Counter struct {
	count int
	mu    sync.Mutex
}

func main() {
	var c Counter
	...

	c.mu.Lock() // 加锁
	c.count++
	c.mu.Unlock() // 解锁
	...
}
```

这种情况也不必初始化 Mutex 字段，不会因为没有初始化出现空指针或者是无法获取到锁的情况。

### 2.3 直接嵌入 struct 使用

```go
type Counter struct {
	sync.Mutex
	count int
}

func main() {
	var c Counter
	...

	c.Lock() // 加锁
	c.count++
	c.Unlock() // 解锁
	...
}
```

这种方式一般建议将 `sync.Mutex` 放在结构体中要控制的字段的上一行，然后将这两行与其他的字段使用空行分隔开，这样逻辑观感会更清晰。

## 3.Mutex 常见错误

### 3.1 Lock/Unlock 不是成对出现

Mutex 解锁后没有释放锁，会出现死锁；而对一个未加锁的 Mutex 执行 Unlock 操作，则会抛出 fatal error。

常见的错误场景有：

1. 代码过于冗长，导致后面忘记 Unlock；
2. 代码层级过多，如 if/else 分支过多导致某个分支忘记 Unlock；
3. 误删 Lock/Unlock 或将 Lock/Unlock 误写成 Unlock/Lock；

针对这种情况，建议一般是加锁的时候配合 `defer` 一起使用：

```go
mu.Lock()
defer mu.Unlock()
```

或者将边界情况的处理封装成一个函数，对外不暴露锁逻辑：

```go
func (c *Counter) Incr() {
	c.mu.Lock()
	c.count++
	c.mu.Unlock()
}
```

### 3.2 复制已使用的 Mutex

为什么不能复制已使用的 Mutex，这就要追溯到 Mutex 的源码了：

```go
type Mutex struct {
	state int32
	sema  uint32
}
```

从源码中我们知道 Mutex 是一个带有状态 `state` 的对象，这个状态记录了 Mutex 的加锁情况。如果我们复制了一个已经加锁的 Mutex 到一个新的变量，这个变量初始状态居然是被加锁的，那显然不符合日常开发逻辑。特别是在并发环境下，你可能以为你复制的是一个 Mutex 零值，实际上在多个 goroutine 的并发访问下，很难确定要复制的 Mutex 的状态是什么。

除了“显示”复制，还要注意“隐式”复制。我们知道 Go 语言参数传递是值传递的，那么在函数传参的时候也有可能出现 Mutex 复制：

```go
type Counter struct {
	sync.Mutex
	Count int
}

func main() {
	var c Counter
	c.Lock()
	defer c.Unlock()
	c.Count++
	foo(c) // 复制锁
}

// 这里 Counter 的参数是通过复制的方式传入的
func foo(c Counter) {
	c.Lock()
	defer c.Unlock()
	fmt.Println("in foo")
}
```

### 3.3 重入

**重入**通常指的是在一个执行流程中，允许同一个线程多次进入同一个代码块或函数而不产生问题的特性。

**可重入锁**允许同一线程多次请求锁，而不会产生死锁或其他问题。

但是，**Mutex 是不可重入的锁**。

当同一个 goroutine 对同一个 Mutex 重复加锁时，会抛出 `fatal error: all goroutines are asleep - deadlock!` 的错误：

```go
var mu sync.Mutex

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

### 3.4 死锁

**死锁**是指在多线程（或多进程）的程序中，两个或多个线程（或进程）由于争夺资源而陷入互相等待对方释放资源的状态，如果没有外部干涉，它们都将无法执行下去。

死锁的发生需要满足 4 个条件：

1. **互斥**：至少有一个资源是排他性的，即一次只能被一个线程（进程）使用，其他线程必须等待至资源被释放；
2. **持有和等待**：一个线程（进程）在持有至少一个资源的同时请求其他资源；
3. **不可剥夺**：已经分配的资源在未使用完之前不能被其他进程强制剥夺；
4. **循环等待**：存在一组线程（进程），它们之间等待资源的释放形成了一个环路，如 P1 等待 P2，P2 等待 P3，P3 等待 P1。

我们举一个 2 个 goroutine 互相等待的例子：

```go
func main() {
	var mu1, mu2 sync.Mutex
	var wg sync.WaitGroup
	wg.Add(2)

	go func() {
		defer wg.Done()

		mu1.Lock()
		defer mu1.Unlock()

		fmt.Println("mu1 Locking...")
		mu2.Lock()
		mu2.Unlock()
	}()

	go func() {
		defer wg.Done()

		mu2.Lock()
		defer mu2.Unlock()

		fmt.Println("mu2 Locking...")
		mu1.Lock()
		mu1.Unlock()
	}()

	wg.Wait()
	fmt.Println("finish!")
}
```

## 4.总结

本篇文章我们探讨了 Mutex 的基本使用和易错场景，下篇文章我们将研究不同版本 Mutex 的历史演进和底层实现。