# Go defer 关键字的实现原理

## 1.定义

defer 能够让我们推迟执行某些函数调用，推迟到当前函数返回前才实际执行。defer 与 panic 和 recover 结合，形成了 Go 语言风格的异常与捕获机制。

## 2.使用场景

defer 语句经常被用于处理成对的操作，如文件句柄关闭、连接关闭、释放锁。

## 3.优点

方便开发者使用。

## 4.缺点

有性能损耗。

## 5.实现原理

Go1.14 中编译器会将 defer 函数直接插入到函数的尾部，无需链表和栈上参数拷贝，性能大幅提升。把 defer 函数在当前函数内展开并直接调用，这种方式被称为 open coded defer。

源代码：

```go
func A(i int) {
	defer A1(i, 2*i)
	if i > 1 {
		defer A2("Hello", "eggo")
	}
	// code to do something
	return
}
func A1(a,b int) {
	//......
}
func A2(m,n string) {
	//......
}
```

编译后（伪代码）：

```go
func A(i int) {
    // code to do something
    if i > 1 {
        A2("Hello", "eggo")
    }
    A1(i, 2*i)
    return
}
```

## 6.代码示例

### 6.1 函数退出前，按照先进后出的顺序，执行 defer 函数

```go
package main

import "fmt"

// defer：延迟函数执行，先进后出
func main() {
    defer fmt.Println("defer1")
    defer fmt.Println("defer2")
    defer fmt.Println("defer3")
    defer fmt.Println("defer4")
    fmt.Println("11111")
}

// 11111
// defer4
// defer3
// defer2
// defer1
```

### 6.2 panic后的defer函数不会被执行（遇到panic，如果没有捕获错误，函数会立刻终止）

```go
package main

import "fmt"

// panic后的defer函数不会被执行
func main() {
    defer fmt.Println("panic before")
    panic("发生panic")
    defer func() {
        fmt.Println("panic after")
    }()
}

// panic before
// panic: 发生panic
```

### 6.3 panic 没有被 recover 时，抛出的 panic 到当前 goroutine 最上层函数时，最上层程序直接异常终止

```go
package main

import "fmt"

func F() {
	defer func() {
		fmt.Println("b")
	}()
	panic("a")
}

// 子函数抛出的panic没有recover时，上层函数时，程序直接异常终止
func main() {
	defer func() {
		fmt.Println("c")
	}()
	F()
	fmt.Println("继续执行")
}

// b
// c
// panic: a
```

### 6.4 panic 有被 recover 时，当前 goroutine 最上层函数正常执行

```go
package main

import "fmt"

func F() {
	defer func() {
		if err := recover(); err != nil {
			fmt.Println("捕获异常:", err)
		}
		fmt.Println("b")
	}()
	panic("a")
}

func main() {
	defer func() {
		fmt.Println("c")
	}()
	F()
	fmt.Println("继续执行")
}

// 捕获异常: a
// b
// 继续执行
// c
```