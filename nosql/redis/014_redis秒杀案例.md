# redis 秒杀案例

本文使用到的技术主要是 laravel(php 框架)、redis、linux 服务器(1核2G)

## 一、laravel 接口(初步配置)

```php
namespace ...;

use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Redis;
use Illuminate\Support\Str;

...

/**
 * @params $skuid
 *
 * 模拟秒杀
 */
public function spike($skuid)
{
    $redis = Redis::connection()->client();

    // 1、拼接 redis key
    $sku_key = 'sk:' . $skuid . ':qt';

    $user_id = Str::random(5);
    $user_key = 'sk:' . $skuid . ':user';

    // 2、商品是否处于秒杀阶段
    if (!$redis->exists($sku_key)) {
        return response()->json('秒杀还未开始！');
    }

    // 3、判断用户是否重复秒杀
    if ($redis->sIsMember($user_key, $user_id)) {
        Log::info("{$user_id} 重复参与秒杀。");
        return response()->json('已经参与秒杀！');
    }

    // 4、商品库存
    if ($redis->get($sku_key) < 1) {
        Log::info("{$user_id} 秒杀已经结束。");
        return response()->json('秒杀已经结束！');
    }

    // 5、秒杀
    try {

        $redis->decr($sku_key);
        $redis->sAdd($user_key, $user_id);

        return response()->json('恭喜你成功抢到商品！');

    }catch (\Exception $e) {
        Log::error("{$user_id} " . $e->getMessage());
        return response()->json($e->getMessage());
    }
}
```

## 二、并发测试

### 1、安装 ab 测试工具

```bash
yum install httpd-tools
```

### 2、测试接口

```bash
ab -n 1000 -c 100 http://xxx.xx.xxx.xx:xxxx/api/test/redis/spike/7777
```

### 3、查看日志

一般来说会存在**商品超卖**的情况(我这里没有)。

## 