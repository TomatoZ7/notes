# Go 语言之 slice 进阶

- [Go 语言之 slice 进阶](#go-语言之-slice-进阶)
  - [1.底层](#1底层)
    - [1.1 底层实现](#11-底层实现)
    - [1.2 初始化时的底层实现](#12-初始化时的底层实现)
  - [2.slice 是线程安全的吗？](#2slice-是线程安全的吗)
  - [3.slice 的扩容机制](#3slice-的扩容机制)
    - [3.1 Go 1.17 及更早版本](#31-go-117-及更早版本)
    - [3.2 Go 1.18 至 Go 1.20 版本](#32-go-118-至-go-120-版本)
  - [4.array 和 slice 的区别](#4array-和-slice-的区别)
  - [5.slice 的深拷贝和浅拷贝](#5slice-的深拷贝和浅拷贝)

## 1.底层

### 1.1 底层实现

slice 是基于 array 实现的，它的底层是 array，可以理解为对底层 array 的抽象。

源码包中 src/runtime/slice.go 定义了 slice 的数据结构：

```go
type slice struct {
    array unsafe.Pointer
    len   int
    cap   int
}
```

可以看到 `slice` 占用 24 个字节，其中：

+ `array`: 指向底层数组的指针，占用 8 个字节；
+ `len`: 切片的长度，占用 8 个字节；
+ `cap`: 切片的容量，`cap` 总是大于等于 `len` 的，占用 8 个字节。

![image](Images/slice_1.png)

### 1.2 初始化时的底层实现

slice 共有 4 种初始化方式：

```go
// 1 直接声明
var slice1 []int

// 2 使用字面量
slice2 := []int{1,2,3,4}

// 3 使用 make 创建 slice
slice3 := make([]int, 3, 5)

// 4 从切片或数组截取
slice4 := arr[1:3]
```

现在有以下程序：

```go
package main

import "fmt"

func main() {
    s := make([]int, 0)
    s = append(s, 1)
    fmt.Println(s, len(s), cap(s))
}
```

我们可以通过 `go tool compile -S test.go | grep CALL` 得到汇编代码：

```sh
$ go tool compile -S main.go| grep CALL
0x002c 00044 (main.go:6)        CALL    runtime.makeslice(SB)
0x0050 00080 (main.go:7)        CALL    runtime.growslice(SB)
0x0080 00128 (main.go:8)        CALL    runtime.convTslice(SB)
0x0094 00148 (main.go:8)        CALL    runtime.convT64(SB)
0x00a8 00168 (main.go:8)        CALL    runtime.convT64(SB)
0x011c 00284 ($GOROOT/src/fmt/print.go:274)     CALL    fmt.Fprintln(SB)
0x0130 00304 (main.go:5)        CALL    runtime.morestack_noctxt(SB)
```

从汇编代码中我们可以知道初始化 slice 底层调用的是 `runtime.makeslice`，`makeslice` 函数的工作主要就是计算 slice 所需内存大小，然后调用 `mallocgc` 进行内存的分配。

所需的内存大小 = 切片中元素大小 * 切片的容量。

以下是 `makeslice` 源码：

```go
func makeslice(et *_type, len, cap int) unsafe.Pointer {
    mem, overflow := math.MulUintptr(et.size, uintptr(cap))
    if overflow || mem > maxAlloc || len < 0 || len > cap {
        // NOTE: Produce a 'len out of range' error instead of a
        // 'cap out of range' error when someone does make([]T, bignumber).
        // 'cap out of range' is true too, but since the cap is only being
        // supplied implicitly, saying len is clearer.
        // See golang.org/issue/4085.
        mem, overflow := math.MulUintptr(et.size, uintptr(len))
        if overflow || mem > maxAlloc || len < 0 {
            panicmakeslicelen()
        }
        panicmakeslicecap()
    }

    return mallocgc(mem, et, true)
}
```

`makeslice` 输入参数包括元素类型指针 `et`、长度 `len`、容量 `cap`。这个函数的返回值是一个指向新 slice 内存空间的指针。

在函数内部，首先使用 `math.MulUintptr` 函数计算出要分配的内存大小（即 `et.size * cap`）：

```go
// MulUintptr returns a * b and whether the multiplication overflowed.
// On supported platforms this is an intrinsic lowered by the compiler.
func MulUintptr(a, b uintptr) (uintptr, bool) {
	if a|b < 1<<(4*goarch.PtrSize) || a == 0 {
		return a * b, false
	}
	overflow := b > MaxUintptr/a
	return a * b, overflo
```

其中 `a|b < 1<<(4*goarch.PtrSize)` 这段代码是一个 Go 语言中的条件语句，用于比较 `a` 和 `b` 的值是否都小于 `1<<(4*goarch.PtrSize)`，如果都小于，那么返回 `true`。其中 `goarch.PtrSize` 是一个常量，表示指针类型在当前系统架构下的字节大小。

具体来说，右侧表达式 `1<<(4*goarch.PtrSize)` 表示将二进制数 1 左移 `4*goarch.PtrSize` 位，相当于将其乘以 `2^(4*goarch.PtrSize)`。这样就得到了一个大于等于 1 的整数，在与 a 和 b 进行位运算后，可以得到它们的一部分二进制位，进而比较它们的大小关系。

回到 `makeslice`，通过 `et.size * cap` 与 `maxAlloc` 常量的比较和其他条件的检查，确保分配的内存不会超出系统限制或者非法操作。如果检查失败，则会抛出一个异常，否则调用 `mallocgc` 函数分配内存，并将分配到的内存指针返回给调用方。

## 2.slice 是线程安全的吗？

不是。

我们可以简单写个函数验证一下：

```go
func main() {
	slice := make([]int, 0)

	for i := 0; i < 10; i++ {
		go func() {
			slice = append(slice, 1)
		}()
	}

	for i := 0; i < 10; i++ {
		go func() {
			if len(slice) > 0 {
				slice = slice[:len(slice)-1]
			}
		}()
	}

	time.Sleep(time.Second * 2)

	fmt.Println("length of slice:", len(slice))
}
```

可以发现运行后每次结果都不同，说明 slice 并不支持并发读写。

从 slice 的底层结构也可以看出，因为它没有使用锁等方式。

slice 在并发执行中不会报错，但是数据会丢失。

## 3.slice 的扩容机制

### 3.1 Go 1.17 及更早版本

1. 如果期望容量大于当前容量的 2 倍，那么扩容后的容量大小为期望容量；
2. 否则：
   1. 如果原始容量小于 1024，那么扩容后的容量为原始容量的 2 倍；
   2. 如果原始容量大于等于 1024，那么会进入一个循环，每次循环扩容 1.25 倍，直到扩容后的容量大于期望容量，如果循环过程中发生整数溢出，则将扩容后的容量置为期望容量。

相关[源码](https://github.com/golang/go/blob/release-branch.go1.17/src/runtime/slice.go#L180)：

```go
// $GOROOT/src/runtime/slice.go:181
newcap := old.cap
doublecap := newcap + newcap
if cap > doublecap {
    newcap = cap
} else {
    if old.cap < 1024 {
        newcap = doublecap
    } else {
        // Check 0 < newcap to detect overflow
        // and prevent an infinite loop.
        for 0 < newcap && newcap < cap {
            newcap += newcap / 4
        }
        // Set newcap to the requested cap when
        // the newcap calculation overflowed.
        if newcap <= 0 {
            newcap = cap
        }
    }
}
```

### 3.2 Go 1.18 至 Go 1.20 版本

1. 如果期望容量大于当前容量的 2 倍，那么扩容后的容量大小为期望容量；
2. 否则：
   1. 如果原始容量小于 256，那么扩容后的容量为原始容量的 2 倍；
   2. 如果原始容量大于等于 256，那么会进入一个循环，每次循环都会增加 `(当前容量 + 3 ✖️ 256) / 4` 的容量，这条公式意味着从 2 倍扩容到 1.25 倍扩容的平滑过渡，如果循环过程中发生整数溢出，则将扩容后的容量置为期望容量。

相关[源码](https://github.com/golang/go/blob/release-branch.go1.19/src/runtime/slice.go#L200)

```go
newcap := old.cap
doublecap := newcap + newcap
if cap > doublecap {
    newcap = cap
} else {
    const threshold = 256
    if old.cap < threshold {
        newcap = doublecap
    } else {
        // Check 0 < newcap to detect overflow
        // and prevent an infinite loop.
        for 0 < newcap && newcap < cap {
            // Transition from growing 2x for small slices
            // to growing 1.25x for large slices. This formula
            // gives a smooth-ish transition between the two.
            newcap += (newcap + 3*threshold) / 4
        }
        // Set newcap to the requested cap when
        // the newcap calculation overflowed.
        if newcap <= 0 {
            newcap = cap
        }
    }
}
```

## 4.array 和 slice 的区别

1. array 是值类型，slice 是引用类型。所以在作为函数参数传递时，array 会拷贝整个数组，slice 传递的是指针；
1. array 是定长的，slice 是变长的，并且总是指向底层的 array。

## 5.slice 的深拷贝和浅拷贝

> 浅拷贝是指在复制对象时，只复制对象本身和其中包含的基本类型数据，而不会复制对象所引用的其他对象。也就是说，在浅拷贝中，复制出的新对象与原对象共享同一些引用对象。因此，如果修改了其中一个对象中的引用对象，另一个对象也会随之改变。
>
> 深拷贝则意味着在复制对象时，除了复制对象本身和其中包含的基本类型数据外，还会递归地复制对象所引用的其他对象。也就是说，在深拷贝中，复制出的新对象和原对象是完全独立的，它们之间没有任何引用关系。因此，即使修改其中一个对象中的引用对象，另一个对象也不会受到影响。

实现深拷贝的方式：

1. [`copy`](https://golang.org/pkg/builtin/#copy) 函数；
2. 遍历 slice 再赋值。

实现浅拷贝的方式：

1. 默认的赋值操作。