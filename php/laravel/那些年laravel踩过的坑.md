# 那些年 laravel 踩过的坑

## 1、optional 和 json_encode

数组经过 `optional()` 后成为 Optional 对象，这个时候是无法被 `json_encode()` 解析的。

```php
$arr = ['val1', 'val2'];

var_dump(json_encode($arr));    // string(15) "["val1","val2"]"

var_dump(optional($arr));
/*
    object(Illuminate\Support\Optional)#51 (1) {
      ["value":protected]=>
      array(2) {
        [0]=>
        string(4) "val1"
        [1]=>
        string(4) "val2"
      }
    }
 */

var_dump(json_encode(optional($arr)));      // string(2) "{}"

var_dump((object)$arr);
/*
    object(stdClass)#51 (2) {
      [0]=>
      string(4) "val1"
      [1]=>
      string(4) "val2"
    }
 */

var_dump(json_encode((object)$arr));    // string(23) "{"0":"val1","1":"val2"}"
```