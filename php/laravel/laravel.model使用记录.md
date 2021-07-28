# Laravel Model 的使用记录

除了 `model` 的日常开发写法，这里还记录一些"特殊"的写法。


## 增

### 1 replicate

```php
// 用 replicate 可以 clone 一个 Model
$user = User::find(1);
$clone_user = $user->replicate();
$clone_user->save();
```

### 2 创建模型

创建 `user` 模型类：

```bash
php artisan make:model Models\\User
```

创建 `user` 模型类的同时生成相关文件：

```bash
# -m : 创建一个 migration 文件
# -c : 创建一个 controller 文件
# -r : 创建一个资源控制器，测试之后发现与 -c 一致
php artisan make:model Models\\User -mcr
```





## 删

1 自定义软删除字段

```php
class User extends Model
{
    user SoftDeletes;

    const DELETED_AT = "delete_time";
    // 或者
    public function getDeletedAtColumn()
    {
        return 'delete_time';
    }
}
```



## 改

### 1 push

```php
// User 类中包含模型关联
class User extends Model
{
    public function phone()
    {
        return $this->hasOne('App\Models\Phone');
    }
}

$user = User::first();
$user->name = 'tomato';
$user->phone->mobile = '13515013510';
$user->save();                              // 仅更新
$user->push();                              // 更行 User 和 Phone Model
```

### 2 getChanges / getDirty

`getChanges` 在 `save` 之后输出结果集

`getDirty` 在 `save` 之前输出结果集

#### 2.1 getChanges

```php
// getChange 查询 model 修改的属性
$user = User::first();
$user->name;                // Peter
$user->name = 'tomato';     // tomato
$user->getChanges();        // []

$user->save();

$user->getChanges();    // ['name' => 'Peter', 'updated_at' => '...']
```

#### 2.2 getDirty / isDirty

```php
// isDirty 查询 model 是否已更改
// getDirty 查询 model 修改的属性
$user = User::first();
$user->name;                // Peter
$user->isDirty();           // false

$user->name = 'tomato';
$user->isDirty();           // true
$user->getDirty();          // ['name' => 'Peter']

$user->save();
$user->isDirty();           // false
$user->getDirty();          // []
```

### 3 getOriginal

```php
// getOriginal 查询修改前的 Model 信息
$user = User::first();
$user->name;                // Peter
$user->name = 'tomato';

$user->getOriginal('name');     // Peter
$user->getOriginal();       // original user record
```





## 查

### 1 find

#### 1.1 find

```php
// 指定属性
User::find($id, ['name', 'email']);

// 查询多条记录
User::find([1, 2, 3]);
```

#### 1.2 findOrFail

```php
// 查找一条记录原先是这样的：
$user = User::find($id);
if (is_null($user)) {
    throw new \Exception('user not found');
}

// 使用 findOrFail 可以简化代码量：
$user = User::findOrFail($id);      
$user = User::findOrFail(9999);                     // No query results for model [App\\Models\\User] 9999

// 同样的，也可以指定属性：
$user = User::findOrFail($id, ['name', 'email']);
```

### 2 is

```php
/**
 * is 可以判断两个 model 的主键、所属数据表和数据库是否一致
 * 
 * Determine if two models have the same ID and belong to the same table.
 *
 * @param  \Illuminate\Database\Eloquent\Model|null  $model
 * @return bool
 */
public function is($model)
{
    return ! is_null($model) &&
            $this->getKey() === $model->getKey() &&
            $this->getTable() === $model->getTable() &&
            $this->getConnectionName() === $model->getConnectionName();
}
```

```php
$user = User::find(1);
$same_user = User::find(1);
$diff_user = User::find(2);
$user->is($same_user);          // true
$user->is($diff_user);          // false
```

### 3 refresh / fresh

#### 3.1 refresh

```php
$user = User::find(1);

// 中间有不是针对 user 对象的更新操作，如直接修改表记录，可以使用 refresh 重新加载 Model
$user->refresh();
```

#### 3.2 fresh

```php
// 与 refresh 不同的是，fresh 不会改变原来的 Model，而是返回一个新的 Model
$user = User::find(1);
$user->name;                    // apple

$updated_user = $user->fresh();
$user->name;                    // apple
$updated_user->name;            // tomato
```

### 4 whereColumn

```php
$user = User::where('name', 'tomato')->first();

// 使用 whereColumn 可以将上述写法改写为：
$user = User::whereName('tomato')->first();
```

另外，在 `Eloquent` 里也有些和时间相关的预定义方法：

```php
// 根据日期查询
User::whereDate('created_at', date('Y-m-d'));
User::whereDay('created_at', date('d'));     
User::whereMonth('created_at', date('m'));
User::whereYear('created_at', date('Y'));
```

### 5 withDefault

`belongsTo` 关联允许定义默认模型，这适应于当关联结果返回的是 `null` 的情况。这种设计模式通常称为 [空对象模式](https://en.wikipedia.org/wiki/Null_object_pattern)，为您免去了额外的条件判断代码。在下面的例子中，`user` 关联如果没有找到文章的作者，就会返回一个空的 `App\User` 模型。

```php
/**
 * 获得此文章的作者。
 */
public function user()
{
    return $this->belongsTo('App\User')->withDefault();
}
```

您也可以通过传递数组或者使用闭包的形式，填充默认模型的属性：

```php
/**
 * 获得此文章的作者。
 */
public function user()
{
    return $this->belongsTo('App\User')->withDefault([
        'name' => '游客',
    ]);
}

/**
 * 获得此文章的作者。
 */
public function user()
{
    return $this->belongsTo('App\User')->withDefault(function ($user) {
        $user->name = '游客';
    });
}
```