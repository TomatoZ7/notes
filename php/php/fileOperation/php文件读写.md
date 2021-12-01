# php 文件读写

## 1 fopen 打开文件

```php
fopen(filename,mode,include_path,context)
```

| 参数 | 描述 |
| :-- | :-- |
| filename | **必需**。规定要打开的文件或 `URL` |
| mode | **必需**。规定对文件/流的访问类型：<br/>`r` : 只读方式，文件指针指向文件头<br/>`r+` : 读写方式，文件指针指向文件头<br/>`w` : 写入方式，清除文件内容，如果文件不存在则尝试创建之<br/>`w+` : 读写方式，清除文件内容，如果文件不存在则尝试创建之<br/>`a` : 写入方式，文件指针指向文件末尾，如果文件不存在则尝试创建之<br/>`a+` : 读写方式，文件指针指向文件末尾<br/>`x` : 创建一个新的文件并以写入方式打开，如果文件已存在则返回 `FALSE` 和一个错误<br/>`x+` : 创建一个新的文件并以读写方式打开，如果文件已存在则返回 `FALSE` 和一个错误 |
」
| include_path | 可选。如果也需要在 `include_path` 中检索文件的话，可以将该参数设为 `1` 或 `TRUE` |
| context | 可选。规定文件句柄的环境。`context` 是一套可以修改流的行为的选项 |

其中，`r+`、`w+`、`a+` 都是读写模式，读取方式一样，写入方式区别如下：

| 描述 | r+ | w+ | a+ |
| :-- | :-- | :-- | :-- |
| 写入方式 | 从文件头部开始覆盖，其他内容不变 | 清空文件并写入 | 从文件尾部追加，其他内容保留 |
| 文件不存在 | 报错 | 创建 | 创建 |

> 注意：Windows 下文件路径是 `\`，应该使用 `/`。

## 2 fwrite 写入文件

```php
fwrite(file,string,length)
```

| 参数 | 描述 |
| :-- | :-- |
| file | 必需。规定要打开的文件 |
| string | 必需。规定要写入文件的字符串 |
| length | 可选。规定要写入的最大字节数 |

## 3 读取文件

### 3.1 fgetc : 一次读取一个字节

```php
$f = fopen('t.txt');
$content = fgetc($f);
fclose($f);
```

### 3.2 fread : 一次读取多个字节

```php
fread(resource $handle, int $length)
```

```php
$f = fopen('t.txt');
echo fread($file, 3);
fclose($f);
```

### 3.3 fgets : 一次读取一行

```php
fgets(file, length)
```

| 参数 | 描述 |
| :-- | :-- |
| file | 必需。规定要读取的文件 |
| length | 可选。规定要读取的字节数，默认是 1024 字节 |

### 3.4 fpassthru : 一次性读出所有数据并返回

```php
$f = fopen('t.txt');
fpassthru($f, 'r');
fclose($f);
```

> 注意：此方法会直接输出，并不需要获取返回内容再输出。

### 3.5 file : 把整个文件读入一个数组中，数组中的每个元素都是文件中相应的一行，包括换行符在内

```php
file(path, include_path, context)
```

| 参数 | 描述 |
| :-- | :-- |
| path | 必需。规定要读取的文件 |
| include_path | 可选。如果也想在 `include_path` 中搜寻文件的话，可以将该参数设为 `1`。 |
| context | 可选。规定文件句柄的环境。 `context` 是一套可以修改流的行为的选项。若使用 `null`，则忽略。 |

> 注意：这里并不需要 `fopen` 和 `fclose`。

## 4 关闭文件

`fclose` 返回一个 `bool` 值，一般来说都是 `true`。

是否打开文件后一定要关闭？

即使不手写 `fclose`，在 `php` 脚本执行结束后，也会**自动关闭文件**。但在一个长时间执行的脚本中，如果不写 `fclose`，在文件加锁的情况下会造成阻塞，所以，写 `fclose` 是一个好习惯。

## 5 其他

### 5.1 file_exists : 检查文件或目录是否存在

```php
file_exists(path): bool
```

### 5.2 filesize : 返回指定文件的大小

```php
filesize(filename): int|bool
```

### 5.3 unlink : 删除一个文件

```php
unlink(filename): bool
```

### 5.4 feof : 判断文件指针已到达文件末尾

```php
feof(filename): bool
```

### 5.5 \r & \n

`\r` 和 `\n` 各占一个字节。

### 5.6 file_get_contents & file_put_contents

`file_get_contents()` 把整个文件读入一个字符串中。

```php
file_get_contents(path, include_path, context, start, max_length)
```

| 参数 | 描述 |
| :-- | :-- |
| path | 必需。规定要读取的文件 |
| include_path | 可选。如果您还想在 `include_path` 中搜索文件的话，设为 `1` |
| context | 可选。规定文件句柄环境，设为 `null` 则忽略 |
| start | 可选。规定文件开始读取位置 |
| max_length | 可选。规定读取的字节数 |

`file_put_contents()` 把一个字符串写入文件中，如不存在，则创建之。

```php
file_put_contents(string $filename, mixed $data, int $flags = 0, resource $context)
```

| 参数 | 描述 |
| :-- | :-- |
| file | 必需。规定要写入的文件 |
| data | 必需。规定要写入文件的数据，可以是字符串、数组或数据流 |
| mode | 可选。规定如何打开文件：<br/> FILE_USE_INCLUDE_PATH <br/> FILE_APPEND <br/> LOCK_EX |
| context | 可选。规定文件句柄的环境 |

[资料来源](https://www.runoob.com/php/php-ref-filesystem.html)