# Java 如何判断两个字符串是否相等
## 关于“相等”
“相等”分两种，一钟是比较两个变量的值，另一种是比较两个变量的引用。

## 常用的比较运算符：==
example1：
```java 
String str1 = "abc";
String str2 = "abc";
str1 == str2;   // true
```
这是因为 java 中字符串的值是不可改变的，相同的字符串在内存中只会存一份，所以 `str1` 和 `str2` 指向的是同一个对象。

example2:
```java
String str1 = new String("abc");
String str2 = new String("abc");
str1 == str2    // false 此时 str1 和 str2 指向不同的对象
```

&emsp;

## 用 equals 方法比较字符串值是否相等
example
```java
String str1 = new String("abc");
String str2 = new String("abc");
str1.equals(str2);  // true
```