# Java 遍历字符串的 3 种方式

## 1) .length + charAt()
```java
for (int i = 0; i < str.length(); i++) {
	System.out.println(str.charAt(i));
}
```

## 2) .length + substring(i, i+1)
```java
for (int i = 0; i < str.length(); i++) {
	System.out.println(str.substring(i, i+1));
}
```

## 3) .toCharArray()
```java
char[] c = str.toCharArray();
for (int i = 0; i < c.length; i++) {
	System.out.println(c[i]);
}
```