# Java length、length() 和 size() 的区别

## lengh

是一个**属性**

针对的是 **数组**

返回数组的长度  

```java
char[] array = {'a', 'b', 'c'};

System.out.println(array.length);   // 3
```

## length()

是一个**方法**

针对的是 **字符串**

返回字符串的长度

```java
String s = "abcdefg";

System.out.println(s.length());   // 7
```

## size()

是一个**方法**

针对的是 **泛型集合**

返回集合元素的个数

```java
List<Object> list = new ArrayList();

list.add("james");

System.out.println(list.size());    // 1

```