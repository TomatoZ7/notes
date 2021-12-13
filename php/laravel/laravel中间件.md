# Laravel 中间件

## 1 中间件注册、创建

[https://learnku.com/docs/laravel/5.5/middleware/1294#registering-middleware](https://learnku.com/docs/laravel/5.5/middleware/1294#registering-middleware)

## 2 前置中间件和后置中间件

前置中间件：

```php
public function handle($request, Closure $next)
{
	// 业务逻辑...

	return $next($request);
}
```

后置中间件：

```php
public function handle($request, Closure $next)
{
	$response = $next($request);

	// 业务逻辑...

	return $response;
}
```