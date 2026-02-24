# Go 1.24 新特性：基于 Swiss Tables 的 map 实现

<!-- 
大纲：
    swiss table 的实现原理
    go map 实现 swiss table 
        底层源码实现 + 画图
        查找过程解析
        哈希冲突的解决方案
        扩容
 -->

## [Go map 实现 Swiss Table](https://github.com/golang/go/blob/go1.24.7/src/internal/runtime/maps/map.go)

```go
type Map struct {
    // 已填充的 slot 数量（即所有 table 中元素的数量）。不包括已删除的槽位。
    // 必须位于第一个（编译器已知，用于 len() 内置函数）。
	used uint64

    // 为该 map 提供一个唯一的随机哈希种子
	seed uintptr

	// 通常 dirPtr 通常指向一个 table 指针数组
	//
	// dirPtr *[dirLen]*table
	//
	// 该数组的长度 (dirLen) 为 `1 << globalDepth`. 数组可能会出现多个元素指向同一个 table 的情况.
	//
	// Small map 优化: 如果 map 长度始终小于等于 abi.SwissMapGroupSlots (8)，那么 dirPtr 会直接指向单个 group.
	//
	// dirPtr *group
	//
	// 在这种情况下，dirLen 为 0。 used 计算 group 中已使用的 slot 数量.
    // 注意 small map 永远不会有已删除的 slot (因为没有要维护的探测许略)
	dirPtr unsafe.Pointer
	dirLen int

    // 查找 table 数组时使用的位数
	globalDepth uint8

    // 查找 table 数组时使用的哈希位运算偏移量.
    // 在 64 位系统上，就是 64 - globalDepth.
	globalShift uint8

    // 一个标志位，在写入时会被设置为 1。并发写入的切换会增加检测到竞争的概率。
	writing uint8

	// Clear 调用的序列计数器. 用于在迭代过程中检测 map 是否被清除.
	clearSeq uint64
}
```