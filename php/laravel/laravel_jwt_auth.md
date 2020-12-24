# Laravel [tymon/jwt-auth] 用户认证

# 1 安装配置
## 1.1 版本选择
jwt 版本已更新到 [1.0.1 Release](https://github.com/tymondesigns/jwt-auth/releases)，具体的版本号选择可以根据项目的版本来决定。
一般来说推荐 1.0.0-rc.1 之后的版本，会减少多用户 token 认证的安全问题。  
下面均已 1.0.0 为准。

&emsp;

## 1.2 版本科普 (补充)
### α（Alpha）版
这个版本表示该 Package 仅仅是一个初步完成品，通常只在开发者内部交流，也有很少一部分发布给专业测试人员。一般而言，该版本软件的 Bug 较多，普通用户最好不要安装。

### β（Beta）版
该版本相对于 α（Alpha）版已有了很大的改进，修复了严重的错误，但还是存在着一些缺陷，需要经过大规模的发布测试来进一步消除。通过一些专业爱好者的测试，将结果反馈给开发者，开发者们再进行有针对性的修改。该版本也不适合一般用户安装。

### RC/ Preview 版
RC 即 Release Candidate 的缩写，作为一个固定术语，意味着最终版本准备就绪。一般来说 RC 版本已经完成全部功能并清除大部分的 BUG。一般到了这个阶段 Package 的作者只会修复 Bug，不会对软件做任何大的更改。

### 普通发行版本
一般在经历了上面三个版本后，作者会推出此版本。此版本修复了绝大部分的 Bug，并且会维护一定的时间。（时间根据作者的意愿而决定，例如 Laravel 的一般发行版本会提供为期一年的维护支持。）

### LTS（Long Term Support） 版
该版本是一个特殊的版本，和普通版本旨在支持比正常时间更长的时间。（例如 Laravel 的 LTS 版本会提供为期三年的 维护支持。）

&emsp;

## 1.3 使用 composer 安装 [tymon/jwt-auth] 扩展
```shell
composer require tymon/jwt-auth
```

&emsp;

## 1.4 配置 config/app.php
```php
'providers' => [

    ...

    Tymon\JWTAuth\Providers\LaravelServiceProvider::class,
]
```

&emsp;

## 1.5 发布配置
在你的 shell 运行以下命令发布程序包配置文件：
```shell
php artisan vendor:publish --provider="Tymon\JWTAuth\Providers\LaravelServiceProvider"
```
此命令会在 config 目录下生成 jwt.php 文件，你可以在此进行自定义配置。

&emsp;

### 配置项详解
config/jwt.php
```php
<?php

return [

    /*
    |--------------------------------------------------------------------------
    | JWT Authentication Secret
    |--------------------------------------------------------------------------
    |
    | 用于加密生成 token 的 secret
    |
    */

    'secret' => env('JWT_SECRET'),

    /*
    |--------------------------------------------------------------------------
    | JWT Authentication Keys
    |--------------------------------------------------------------------------
    |
    | 如果你在 .env 文件中定义了 JWT_SECRET 的随机字符串
    | 那么 jwt 将会使用 对称算法 来生成 token
    | 如果你没有定有，那么jwt 将会使用如下配置的公钥和私钥来生成 token
    |
    */

    'keys' => [

        /*
        |--------------------------------------------------------------------------
        | Public Key
        |--------------------------------------------------------------------------
        |
        | 公钥
        |
        */

        'public' => env('JWT_PUBLIC_KEY'),

        /*
        |--------------------------------------------------------------------------
        | Private Key
        |--------------------------------------------------------------------------
        |
        | 私钥
        |
        */

        'private' => env('JWT_PRIVATE_KEY'),

        /*
        |--------------------------------------------------------------------------
        | Passphrase
        |--------------------------------------------------------------------------
        |
        | 私钥的密码。 如果没有设置，可以为 null。
        |
        */

        'passphrase' => env('JWT_PASSPHRASE'),

    ],

    /*
    |--------------------------------------------------------------------------
    | JWT time to live
    |--------------------------------------------------------------------------
    |
    | 指定 access_token 有效的时间长度（以分钟为单位），默认为1小时，您也可以将其设置为空，以产生永不过期的标记
    |
    */

    'ttl' => env('JWT_TTL', 60),

    /*
    |--------------------------------------------------------------------------
    | Refresh time to live
    |--------------------------------------------------------------------------
    |
    | 指定 access_token 可刷新的时间长度（以分钟为单位）。默认的时间为 2 周。
    | 大概意思就是如果用户有一个 access_token，那么他可以带着他的 access_token 
    | 过来领取新的 access_token，直到 2 周的时间后，他便无法继续刷新了，需要重新登录。
    |
    */

    'refresh_ttl' => env('JWT_REFRESH_TTL', 20160),

    /*
    |--------------------------------------------------------------------------
    | JWT hashing algorithm
    |--------------------------------------------------------------------------
    |
    | 指定将用于对令牌进行签名的散列算法。
    |
    */

    'algo' => env('JWT_ALGO', 'HS256'),

    /*
    |--------------------------------------------------------------------------
    | Required Claims
    |--------------------------------------------------------------------------
    |
    | 指定必须存在于任何令牌中的声明。
    | 
    |
    */

    'required_claims' => [
        'iss',
        'iat',
        'exp',
        'nbf',
        'sub',
        'jti',
    ],

    /*
    |--------------------------------------------------------------------------
    | Persistent Claims
    |--------------------------------------------------------------------------
    |
    | 指定在刷新令牌时要保留的声明密钥。
    |
    */

    'persistent_claims' => [
        // 'foo',
        // 'bar',
    ],

    /*
    |--------------------------------------------------------------------------
    | Blacklist Enabled
    |--------------------------------------------------------------------------
    |
    | 为了使令牌无效，您必须启用黑名单。
    | 如果您不想或不需要此功能，请将其设置为 false。
    |
    */

    'blacklist_enabled' => env('JWT_BLACKLIST_ENABLED', true),

    /*
    | -------------------------------------------------------------------------
    | Blacklist Grace Period
    | -------------------------------------------------------------------------
    |
    | 当多个并发请求使用相同的JWT进行时，
    | 由于 access_token 的刷新 ，其中一些可能会失败
    | 以秒为单位设置请求时间以防止并发的请求失败。
    |
    */

    'blacklist_grace_period' => env('JWT_BLACKLIST_GRACE_PERIOD', 0),

    /*
    |--------------------------------------------------------------------------
    | Providers
    |--------------------------------------------------------------------------
    |
    | 指定整个包中使用的各种提供程序。
    |
    */

    'providers' => [

        /*
        |--------------------------------------------------------------------------
        | JWT Provider
        |--------------------------------------------------------------------------
        |
        | 指定用于创建和解码令牌的提供程序。
        |
        */

        'jwt' => Tymon\JWTAuth\Providers\JWT\Namshi::class,

        /*
        |--------------------------------------------------------------------------
        | Authentication Provider
        |--------------------------------------------------------------------------
        |
        | 指定用于对用户进行身份验证的提供程序。
        |
        */

        'auth' => Tymon\JWTAuth\Providers\Auth\Illuminate::class,

        /*
        |--------------------------------------------------------------------------
        | Storage Provider
        |--------------------------------------------------------------------------
        |
        | 指定用于在黑名单中存储标记的提供程序。
        |
        */

        'storage' => Tymon\JWTAuth\Providers\Storage\Illuminate::class,

    ],

];
```

&emsp;

## 1.6 生成密钥
jwt-auth 已经预先设定好了 artisan 命令，你只需运行如下命令即可生成密钥：
```shell
php artisan jwt:secret
```
此命令会在 .env 文件中新增一行 `JWT_SECRET=foobar`

&emsp;

# 2 开始使用
## 2.1 配置 Auth guard (只有在使用 Laravel 5.2 及以上版本的情况下才能使用)
config/auth.php
```php
'defaults' => [
    'guard' => 'api',
    'passwords' => 'users',
],

...

'guards' => [
    'api' => [
        'driver' => 'jwt',
        'provider' => 'users',
    ],
],
```
这里我们告诉 api guard 使用 jwt driver，并且我们将 api guard 设置为默认驱动程序。  
现在我们就可以通过 Laravel 内置的 Auth 系统，并使用 jwt-auth 作为支撑。

&emsp;

## 2.2 更改模型
使用 jwt-auth 作为用户认证，需要对你的用户模型稍作改动，主要是引入 Tymon\JWTAuth\Contracts\JWTSubject 接口并在模型类中实现2个方法，以 User 为例：
```php
<?php

namespace App;

use Tymon\JWTAuth\Contracts\JWTSubject;
use Illuminate\Notifications\Notifiable;
use Illuminate\Foundation\Auth\User as Authenticatable;

class User extends Authenticatable implements JWTSubject
{
    use Notifiable;

    // Rest omitted for brevity

    /**
     * Get the identifier that will be stored in the subject claim of the JWT.
     *
     * @return mixed
     */
    public function getJWTIdentifier()
    {
        return $this->getKey();
    }

    /**
     * Return a key value array, containing any custom claims to be added to the JWT.
     *
     * @return array
     */
    public function getJWTCustomClaims()
    {
        return [];
    }
}
```

&emsp;

## 2.3 添加权限路由
routes/api.php
```php
Route::group([

    'middleware' => 'api',
    'prefix' => 'auth'

], function ($router) {

    Route::post('login', 'AuthController@login');
    Route::post('logout', 'AuthController@logout');
    Route::post('refresh', 'AuthController@refresh');
    Route::post('me', 'AuthController@me');

});
```

&emsp;

## 2.4 添加相应的控制器方法
app/Http/Controllers/AuthController
```php
<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Auth;
use App\Http\Controllers\Controller;

class AuthController extends Controller
{
    /**
     * Create a new AuthController instance.
     *
     * @return void
     */
    public function __construct()
    {
        $this->middleware('auth:api', ['except' => ['login']]);
    }

    /**
     * Get a JWT via given credentials.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    public function login()
    {
        $credentials = request(['email', 'password']);

        if (! $token = auth()->attempt($credentials)) {
            return response()->json(['error' => 'Unauthorized'], 401);
        }

        return $this->respondWithToken($token);
    }

    /**
     * Get the authenticated User.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    public function me()
    {
        return response()->json(auth()->user());
    }

    /**
     * Log the user out (Invalidate the token).
     *
     * @return \Illuminate\Http\JsonResponse
     */
    public function logout()
    {
        auth()->logout();

        return response()->json(['message' => 'Successfully logged out']);
    }

    /**
     * Refresh a token.
     *
     * @return \Illuminate\Http\JsonResponse
     */
    public function refresh()
    {
        return $this->respondWithToken(auth()->refresh());
    }

    /**
     * Get the token array structure.
     *
     * @param  string $token
     *
     * @return \Illuminate\Http\JsonResponse
     */
    protected function respondWithToken($token)
    {
        return response()->json([
            'access_token' => $token,
            'token_type' => 'bearer',
            'expires_in' => auth()->factory()->getTTL() * 60
        ]);
    }
}
```
login 接口响应示例如下：
```json
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ",
    "token_type": "bearer",
    "expires_in": 3600
}
```
拿到 token 后，就可以携带 token 通过其他需要身份认证的接口。

&emsp;

## 2.5 token 携带方式
### 2.5.1 放在 http 请求头
```
Authorization: Bearer eyJhbGciOiJIUzI1NiI...
```
### 2.5.2 get 请求参数形式
```
http://example.dev/me?token=eyJhbGciOiJIUzI1NiI...
```
### 2.5.3 post 请求参数形式
### 2.5.4 Cookies
### 2.5.5 Laravel 路由参数
```
http://example.dev/me/token/eyJhbGciOiJIUzI1NiI...
```

&emsp;

## 2.6 自定义认证中间件
中间件需要继承 jwt 的 BaseMiddleware。  
下面我从网上借鉴的无痛刷新 token 中间件：
```php
<?php
namespace App\Http\Middleware;
use Auth;
use Closure;
use Tymon\JWTAuth\Exceptions\JWTException;
use Tymon\JWTAuth\Http\Middleware\BaseMiddleware;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Symfony\Component\HttpKernel\Exception\UnauthorizedHttpException;
// 注意，我们要继承的是 jwt 的 BaseMiddleware
class RefreshToken extends BaseMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @ param  \Illuminate\Http\Request $request
     * @ param  \Closure $next
     *
     * @ throws \Symfony\Component\HttpKernel\Exception\UnauthorizedHttpException
     *
     * @ return mixed
     */
    public function handle($request, Closure $next)
    {
        // 检查此次请求中是否带有 token，如果没有则抛出异常。
        $this->checkForToken($request);
    
        // 使用 try 包裹，以捕捉 token 过期所抛出的 TokenExpiredException  异常
        try {
            // 检测用户的登录状态，如果正常则通过
            if ($this->auth->parseToken()->authenticate()) {
                return $next($request);
            }
            throw new UnauthorizedHttpException('jwt-auth', '未登录');
        } catch (TokenExpiredException $exception) {
            // 此处捕获到了 token 过期所抛出的 TokenExpiredException 异常，我们在这里需要做的是刷新该用户的 token 并将它添加到响应头中
            try {
                // 刷新用户的 token
                $token = $this->auth->refresh();
                // 使用一次性登录以保证此次请求的成功
                Auth::guard('api')->onceUsingId($this->auth->manager()->getPayloadFactory()->buildClaimsCollection()->toPlainArray()['sub']);
            } catch (JWTException $exception) {
                // 如果捕获到此异常，即代表 refresh 也过期了，用户无法刷新令牌，需要重新登录。
                throw new UnauthorizedHttpException('jwt-auth', $exception->getMessage());
            }
        }
        // 在响应头中返回新的 token
        return $this->setAuthenticationHeader($next($request), $token);
    }
}
```
这里主要需要说的就是在token进行刷新后，不但需要将token放在返回头中，最好也将请求头中的token进行置换，因为刷新过后，请求头中的token就已经失效了，如果接口内的业务逻辑使用到了请求头中的token，那么就会产生问题。

这里使用
```php
$request->headers->set('Authorization','Bearer '.$token);
```
将token在请求头中刷新。

&emsp;

# 3 方法介绍
## 3.1 attempt()
```php
$token = auth('api')->attempt($credentials);
```
auth() 传参取决于你的 config/auth.php 配置，需传入 driver 为 jwt 的 guards。默认为 defaults 定义。  
attempt() 方法将会返回 token 值或 null。

&emsp;

## 3.2 login()
登录用户并为其返回 token 值。
```php
// Get some user from somewhere
$user = User::first();

// Get the token
$token = auth()->login($user);
```

&emsp;

## 3.3 user()
获取当前通过验证的用户。
```php
$user = auth()->user();
```
如果用户未通过验证，则返回 null。

&emsp;

## 3.4 userOrFail()
同上，只是失败时会抛出一个错误。

&emsp;

## 3.5 logout()
用户登出，会使用户 token 失效并用户处于未验证状态。
```php
auth()->logout();

// 传递 true 将令牌永远拉黑
auth()->logout(true);
```

&emsp;

## 3.6 refresh()
刷新令牌并使当前令牌失效
```php
$newToken = auth()->refresh();

// Pass true as the first param to force the token to be blacklisted "forever".
// The second parameter will reset the claims for the new token
$newToken = auth()->refresh(true, true);
```

&emsp;

## 3.7 invalidate（）
使令牌无效（将其添加到黑名单）
```php
auth()->invalidate();

// Pass true as the first param to force the token to be blacklisted "forever".
auth()->invalidate(true);
```

&emsp;

## 3.8 tokenById（）
根据给定用户的ID获取令牌。
```php
$token = auth()->tokenById(123);
```

&emsp;

## 3.9 其他比较少见的方法
### 3.9.1 添加自定义声明
```php
$token = auth()->claims(['foo' => 'bar'])->attempt($credentials);
```
### 3.9.2 显式设置令牌
```php
$user = auth()->setToken('eyJhb...')->user();
```
### 3.9.3 显式设置请求实例
```php
$user = auth()->setRequest($request)->user();
```
### 3.9.4 覆盖令牌ttl
```php
$token = auth()->setTTL(7200)->attempt($credentials);
```