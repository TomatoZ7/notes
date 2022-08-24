# Go slice 的底层实现原理

切片是基于数组实现的，它的底层是数组，可以理解为对 底层数组的抽象。

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

```