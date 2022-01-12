# php 交换两个变量的值，在不使用第三个变量的前提下

## 两数先做加法再做减法(局限于数字) 

```php
$a = 25;
$b = 20;

$a = $a + $b = 45;
$b = $a - $b = 25;
$a = $a - $b = 20;
```

## 运用 list() 函数

```php
$a = 25;
$b = 20;

list($a, $b) = [$b, $a];
```

> list() 函数表示把数组中的值赋给一组变量
> list ( mixed $var1 [, mixed $... ] ) : array
> 像 array() 一样，这不是真正的函数，而是语言结构。
> list() 可以在单次操作内就为一组变量赋值。

## explode() 函数

```php
$a = 25;
$b = 20;

$b = explode('|', $a.','.$b)

$a = $b[1];
$b = $b[0];
```

## substr() + strlen() (局限于字符串)

```php
$a = 'abc';
$b = 'ok';

$a .= $b	// $a = 'abcok'
$b = substr($a, 0, (strlen($a) - strlen($b)));	// $b = 'abc'
$a = substr($a, strlen($b));
```

## str_replace() (局限于字符串)

```php
$a = 'abc';
$b = 'ok';

$a .= $b	// $a = 'abcok'
$b = substr($a, 0, (strlen($a) - strlen($b))); 
$a = substr($a, strlen($b));
```