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

## 2、validation 验证器

其返回值可以用 failedValidation 重新定义结构，并且 `return` 是无效的，应该用 `throw`

```php
protected function failedValidation(Validator $validator)
{
    throw new HttpResponseException(
        new JsonResponse([
            'stat' => 0,
            'msg' => $validator->errors()->first(),
            'data' => []
        ])
    );
}
```

## 3、Model 的实例化对象（待确认）

我是使用 `find($id)` 查找到对象后直接用该对象的 `update()` 方法更新数据表，结果报错了。(版本：laravel 5.5)