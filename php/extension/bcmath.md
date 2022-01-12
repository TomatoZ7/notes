# PHP bcmath 扩展

PHP 有一个 `bcmath` 高精确度的数学扩展，它可以为任意精度数学计算提供了**二进制计算器(Binary Calculator)**，它支持任意大小和精度的数字，以字符串形式描述。在需要处理数字计算时，不要在简单地使用四则运算，而要用 `bcmath` 相关的函数来处理。

## 1 安装

本类函数仅在 PHP 编译时配置了 `--enable-bcmath` 时可用。PHP 的 `Windows` 版本已内建对此扩展的支持，不需要载入额外的扩展来使用这些函数。如果需要编译安装，请参考 PHP 安装编译配置里的扩展。

## 2 bcmath 函数

官方文档：[https://www.php.net/manual/zh/ref.bc.php](https://www.php.net/manual/zh/ref.bc.php)

### 2.1 bcmod 

#### 说明

对一个任意精度数字取模。

#### 用法

```php
bcmod(string $num1, string $num2, ?int $scale = null): string
```

获取 `num1` 除以 `num2` 的余数。除非 `num2` 为零，否则结果的符号与 `num1` 相同。

#### 参数

| parameter | description |
| :-- | :-- |
| num1 | 被除数，`string` 类型 |
| num2 | 除数，`string` 类型 |
| scale | php7.2 引入，设置返回结果中小数点后的小数位数，可使用 `bcscale()` 全局设置，默认 0 |

#### 范例

```php
// 符号跟随左操作数
bcscale(0);
echo bcmod( '5',  '3');  //  2
echo bcmod( '5', '-3');  //  2
echo bcmod('-5',  '3');  // -2
echo bcmod('-5', '-3');  // -2

// 输出小数位
bcscale(1);
echo bcmod('5.5', '2.8');  // 2.7
echo bcmod('5.5', '2.8', 2);  // 2.70
```

### 2.2 bcdiv

#### 说明

两个任意精度的数字除法计算。

#### 用法

```php
bcdiv(string $num1, string $num2, ?int $scale = null): string
```

`num1` 除以 `num2`。

#### 参数

| parameter | description |
| :-- | :-- |
| num1 | 被除数，`string` 类型 |
| num2 | 除数，`string` 类型 |
| scale | 设置返回结果中小数点后的小数位数，可使用 `bcscale()` 全局设置，默认 0 |

#### 范例

```php
echo bcdiv('105', '6.55957', 3);  // 16.007
```

### 2.3 bcpow

#### 说明

任意精度数字的乘方

#### 用法

```php
bcpow(string $num, string $exponent, ?int $scale = null): string
```

`num` 的 `exponent` 次方运算。

#### 参数

| parameter | description |
| :-- | :-- |
| num | `string` 类型的底数 |
| exponent | `string` 类型的指数。如果指数非整数，会被截断 |
| scale | php7.3 引入，设置返回结果中小数点后的小数位数，可使用 `bcscale()` 全局设置，默认 0 |

#### 范例

```php
echo bcpow('4.2', '3', 2);  // 74.08
```

### 2.4 bcmul

#### 说明

两个任意精度数字乘法计算

#### 用法

```php
bcmul(string $num1, string $num2, ?int $scale = null): string
```

#### 参数

| parameter | description |
| :-- | :-- |
| num1 | `string` 类型 |
| num2 | `string` 类型 |
| scale | php7.3 引入，设置返回结果中小数点后的小数位数，可使用 `bcscale()` 全局设置，默认 0 |

#### 范例

```php
echo bcmul('1.34747474747', '35', 3); // 47.161
echo bcmul('2', '4'); // 8
```

### 2.5 bcadd

#### 说明

两个任意精度数字加法计算

#### 用法

```php
bcadd(string $num1, string $num2, ?int $scale = null): string
```

#### 参数

| parameter | description |
| :-- | :-- |
| num1 | `string` 类型 |
| num2 | `string` 类型 |
| scale | 设置返回结果中小数点后的小数位数，可使用 `bcscale()` 全局设置，默认 0 |

#### 范例

```php
$a = '1.234';
$b = '5';

echo bcadd($a, $b);     // 6
echo bcadd($a, $b, 4);  // 6.2340
```