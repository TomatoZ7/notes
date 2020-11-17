# Laravel [mews/captcha] 图片验证码

# 1 安装配置
## 1.1 使用 composer 安装 [mews/captcha] 扩展
```
composer require mews/captcha
```

项目根目录 composer.json -> require 会新增一行(我的 laravel 版本是 5.6)
```
"mews/captcha": "^3.0"
```

## 1.2 config/app.php 添加相应代码
```
'providers'=>[
    // ...

    Mews\Captcha\CaptchaServiceProvider::class
]

'aliases'=>[
    // ...

    'Captcha' => Mews\Captcha\Facades\Captcha::class
]
```

## 1.3 发布配置文件(不发布即使用默认配置)
```
php artisan vendor:publish  // 生成 config/captcha.php 文件
```

## 1.4 (可选)配置自己的验证码
```
return [

    // 生成的验证码字符集
    'characters' => ['2', '3', '4', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'm', 'n', 'p', 'q', 'r', 't', 'u', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'M', 'N', 'P', 'Q', 'R', 'T', 'U', 'X', 'Y', 'Z'],

    'default' => [
        'length' => 4,      // 验证码字符长度
        'width' => 160,     // 图片宽度
        'height' => 46,     // 图片高度
        'quality' => 90,    // 图片质量
        'math' => false,    // 数学规则
        'expire' => 60,     // 过期时间
    ],

    // ...
]
```

&emsp;

# 2 生成验证码
## 2.1 Return Image
```
return captcha();

// or

Captcha::create();
```

### 返回如下:

![效果如下](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/php_laravel_captcha_ri.jpg "效果")

### 看一下源码，这里我加了整体的逻辑注释，详细的内部方法实现可以访问项目内的 /vendor/mews/captcha/src/Captcha.php
```
/**
 * Create captcha image
 *
 * @param string $config
 * @param bool $api
 * @return array|mixed
 * @throws Exception
 */
public function create(string $config = 'default', bool $api = false)
{
    $this->backgrounds = $this->files->files(__DIR__ . '/../assets/backgrounds');   // 背景图片文件
    $this->fonts = $this->files->files($this->fontsDirectory);  // 字体文件

    // 根据不同的 laravel 版本配置字体文件路径
    if (version_compare(app()->version(), '5.5.0', '>=')) {
        $this->fonts = array_map(function ($file) {
            /* @var File $file */
            return $file->getPathName();
        }, $this->fonts);
    }

    $this->fonts = array_values($this->fonts); //reset fonts array index

    // 加载 config/captcha.php 的配置项
    $this->configure($config);

    $generator = $this->generate();     // 生成验证码文本，并session存储
    $this->text = $generator['value'];

    // 根据配置，创建验证码图片
    $this->canvas = $this->imageManager->canvas(
        $this->width,
        $this->height,
        $this->bgColor
    );

    if ($this->bgImage) {
        $this->image = $this->imageManager->make($this->background())->resize(
            $this->width,
            $this->height
        );
        $this->canvas->insert($this->image);
    } else {
        $this->image = $this->canvas;
    }

    if ($this->contrast != 0) {
        $this->image->contrast($this->contrast);
    }

    $this->text();  // 将文本写入图片

    $this->lines(); // 添加干扰线

    if ($this->sharpen) {
        $this->image->sharpen($this->sharpen);  // 图片锐化
    }
    if ($this->invert) {
        $this->image->invert();     // 应该是对文本进行颠倒处理
    }
    if ($this->blur) {
        $this->image->blur($this->blur);    // 模糊
    }

    if ($api) {
        Cache::put('captcha_record_' . $generator['key'], $generator['value'], $this->expire);  // 缓存
    }

    // 如果 $api 是 true，则返回二进制编码格式；如果是 false，则直接输出一张图片
    return $api ? [
        'sensitive' => $generator['sensitive'],
        'key' => $generator['key'],
        'img' => $this->image->encode('data-url')->encoded
    ] : $this->image->response('png', $this->quality);
}
```


## 2.2 Return URL
### 返回 url 地址
```
captcha_src();

// or

Captcha::src('default');
```

### 源码如下：
```
/**
 * Generate captcha image source
 *
 * @param string $config
 * @return string
 */
public function src(string $config = 'default'): string
{
    return url('captcha/' . $config) . '?' . $this->str->random(8);
}
```

## 2.3 Return HTML
### 返回一个携带 src 的 img 标签
```
captcha_img();

// or

Captcha::img();
```

### 源码如下：
```
/**
 * Generate captcha image html tag
 *
 * @param string $config
 * @param array $attrs
 * $attrs -> HTML attributes supplied to the image tag where key is the attribute and the value is the attribute value
 * @return string
 */
public function img(string $config = 'default', array $attrs = []): string
{
    $attrs_str = '';
    foreach ($attrs as $attr => $value) {
        if ($attr == 'src') {
            //Neglect src attribute
            continue;
        }

        $attrs_str .= $attr . '="' . $value . '" ';
    }
    return new HtmlString('<img src="' . $this->src($config) . '" ' . trim($attrs_str) . '>');
}
```

&emsp;

# 3 验证 : 使用 laravel validatesRequest 
## 3.1 session 模式(代码来自官方文档)
```
// [your site path]/Http/routes.php
Route::any('captcha-test', function() {
    if (request()->getMethod() == 'POST') {
        $rules = ['captcha' => 'required|captcha'];
        $validator = validator()->make(request()->all(), $rules);
        if ($validator->fails()) {
            echo '<p style="color: #ff0000;">Incorrect!</p>';
        } else {
            echo '<p style="color: #00ff30;">Matched :)</p>';
        }
    }

    $form = '<form method="post" action="captcha-test">';
    $form .= '<input type="hidden" name="_token" value="' . csrf_token() . '">';
    $form .= '<p>' . captcha_img() . '</p>';
    $form .= '<p><input type="text" name="captcha"></p>';
    $form .= '<p><button type="submit" name="check">Check</button></p>';
    $form .= '</form>';
    return $form;
});
```

## 3.2 无状态模式 : 当你从请求网址拿到 key 和 img,可以使用下面方法来验证(代码来自官方文档)
```
//key is the one that you got from json response
// fix validator
// $rules = ['captcha' => 'required|captcha_api:'. request('key')];
$rules = ['captcha' => 'required|captcha_api:'. request('key') . ',default'];
$validator = validator()->make(request()->all(), $rules);
if ($validator->fails()) {
    return response()->json([
        'message' => 'invalid captcha',
    ]);

} else {
    //do the job
}
```

### 这里我们追一下 check_api() 方法,还是在 captcha.php 里:
```
/**
 * Captcha check
 *
 * @param string $value
 * @param string $key
 * @param string $config
 * @return bool
 */
public function check_api($value, $key, $config = 'default'): bool
{
    // pull : 从缓存中获取并删除
    if (!Cache::pull('captcha_record_' . $key)) {
        return false;
    }

    // 加载 config/captcha.php 的配置项
    $this->configure($config);

    if($this->encrypt) $key = Crypt::decrypt($key);   // 如果加密就解密
    return $this->hasher->check($value, $key);  // 返回 hash 验证结果，使用 password_verify() 函数,如果未输入，则直接返回 false
}
```

### [传送门:官方 packagist 文档](https://packagist.org/packages/mews/captcha) 