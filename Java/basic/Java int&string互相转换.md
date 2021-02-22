# Java int 和 string 互相转换

## int 转 string

```java
int num = 7;
```

### 1) 拼接字符串 (不推荐)
```java
String s = "" + num;
```

### 2) 任何类型的数据转换成字符串 (通用型，推荐方法)
```java
String s = String.valueOf(num);
```

### 3) string 转 int 类似
```java
Integer i = new Integer(num);
String s = i.toString(num);
```

### 4)
```java
String s = Integer.toString(num);
```


## string 转 int

```java
String s = "123456";
```

### 1) (推荐方法)
```java
int y = Integer.parseInt(s);
```

### 2)
```java
Integer i = new Integer(s);
int x = i.intValue();
```