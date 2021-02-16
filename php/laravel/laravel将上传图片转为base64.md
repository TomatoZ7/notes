# Laravel 将上传图片转为 base64

废话少说，直接上代码：

```php
$file = request()->file("file");
$file_base64 = chunk_split(base64_encode($file));

$file = 'data:image/jpg/png/gif;base64,' . $file_base64;
// 前端输出
$image = '<img src="data:image/jpg/png/gif;base64,' . $file_base64 .'" >';
```

##  chunk_split()
在每个字符后分割一次字符串，并在每个分割后添加 "."：
```php
$str = "Shanghai";
echo chunk_split($str, 1, ".");   // S.h.a.n.g.h.a.i.

$str = "Hello world!";
echo chunk_split($str,6,"...");     // Hello ...world!...
```

chunk_split(string, length, end)

| 参数 | 描述 |
| :----: | :----: |
| string | 必需。规定要分割的字符串。 |
| length | 可选。数字值，定义字符串块的长度。默认是 76。 |
| end | 可选。字符串值，定义在每个字符串块末端放置的内容。默认是 \r\n。 |