# Go slice 的底层实现原理

切片是基于数组实现的，它的底层是数组，可以理解为对底层数组的抽象。

源码包中 src/runtime/slice.go 定义了 slice 的数据结构：

```go
type slice struct {
    array unsafe.Pointer
    len   int
    cap   int
}
```

slice 占用 24 个字节。

array: 指向底层数组的指针，占用 8 个字节。

len:切片的长度，占用 8 个字节。

cap:切片的容量，cap 总是大于等于 len 的，占用 8 个字节。

slice 有 4 种初始化方式：

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

通过一个简单的程序，看下 slice 初始化调用的底层函数

```go
package main

import "fmt"

func main() {
    slice := make([]int, 0)
    slice = append(slice, 1)
    fmt.Println(slice, len(slice), cap(slice))
}
```

通过 `go tool compile -S test.go | grep CALL` 得到汇编代码

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

初始化 slice 调用的是 runtime.makeslice，makeslice 函数的工作主要就是计算 slice 所需内存大小，然后调用 mallocgc 进行内存的分配。

所需内存大小 = 切片中元素大小 * 切片的容量

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