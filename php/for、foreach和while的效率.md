# for、foreach和while的运行效率

## for 和 foreach

在数组长度未知的情况下： 

`count()` 在循环体中 < `count()` 在循环体外 < `foreach`

当循环体中比较的值固定时：

`for` > `foreach`

主要是因为 `for` 循环时每次循环都要判断 `$i` 是否小于数组长度，这占用了很大一部分时间。

而 `foreach` 依赖 IEnumerable。
第一次 `var a in GetList()` 时，调用 `GetEnumerator` 返回第一个对象并赋给 a，以后每次再执行 `var a in GetList()` 时，调用 `MoveNext`，直到循环结束。期间 `GetList()` 只执行一次。 

> IEnumerable接口是非常的简单，只包含一个抽象的方法GetEnumerator()，它返回一个可用于循环访问集合的IEnumerator对象。<br/> IEnumerator对象有什么呢？它是一个真正的集合访问器，没有它，就不能使用foreach语句遍历集合或数组，因为只有IEnumerator对象才能访问集合中的项，假如连集合中的项都访问不了，那么进行集合的循环遍历是不可能的事情了。

## for 和 while

while是通用的循环结构，直接移动内部指标。一般情况比 for 略慢。