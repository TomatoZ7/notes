# Python 数据类型转换

## 1 str()

```py
>>> str(True)
'True'
>>> str(False)
'False'
>>> str(1)
'1'
>>> str(99.999)
'99.999'
```

## 2 int()

```py
>>> int(True)
1
>>> int(False)
0
>>> int('128')
128
>>> int(99.9)
99
>>> int('128.88')
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
ValueError: invalid literal for int() with base 10: '128.88'
>>> int('hello py')
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
ValueError: invalid literal for int() with base 10: 'hello py'
```

> 字符串中只有整型字符串可以转为 `int` 类型。`float` 类型会被直接截取。

## 3 float()

```py
>>> float(True)
1.0
>>> float(False)
0.0
>>> float('128')
128.0
>>> float('128.88')
128.88
>>> float('hello py')
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
ValueError: could not convert string to float: 'hello py'
>>> float(99)
99.0
```

> 字符串中只有整型字符串可以转为 `float` 类型。转成 `float` 之后会跟上 `.0`。