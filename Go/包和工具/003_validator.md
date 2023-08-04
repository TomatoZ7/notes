# Go 验证器 validator 详解

- [Go 验证器 validator 详解](#go-验证器-validator-详解)
	- [1.安装](#1安装)
	- [2.使用](#2使用)
		- [2.1 基本使用](#21-基本使用)
		- [2.2 跨字段验证](#22-跨字段验证)
		- [2.3 跨结构体验证](#23-跨结构体验证)
		- [2.4 验证 slice、map](#24-验证-slicemap)
		- [2.5 自定义类型验证器](#25-自定义类型验证器)
		- [2.6 国际化/翻译](#26-国际化翻译)
	- [3.错误处理](#3错误处理)
	- [4.常用验证规则](#4常用验证规则)
		- [4.1 比较](#41-比较)
		- [4.2 跨字段校验](#42-跨字段校验)
		- [4.3 跨结构体跨字段校验](#43-跨结构体跨字段校验)
		- [4.4 字符串相关验证](#44-字符串相关验证)
		- [4.5 格式化验证](#45-格式化验证)
		- [4.6 其他验证](#46-其他验证)

在开发过程中，我们往往需要对用户提交的数据进行验证，以保证数据的合法性和完整性。今天我们就来介绍 Go 语言社区推出的一个验证包 —— [validator](https://github.com/go-playground/validator)。

validator 包根据 tags 对结构体和单个字段的值进行验证。它具备以下优秀的功能：

1. 提供了一系列验证规则用于验证，并且支持自定义验证规则；
2. 支持跨字段、跨结构体进行验证；
3. 支持多维字段如 array、slice、map 等；
4. 在验证接口类型前会先确定它的底层数据类型；
5. 支持自定义字段类型比如 sql 驱动程序 [Valuer](https://go.dev/src/database/sql/driver/types.go?s=1210:1293#L29)；
6. 可以自定义并支持国际化（i18n）的错误信息；
7. 是 gin 框架的默认验证器。

## 1.安装

使用 `go get`：

```sh
go get github.com/go-playground/validator/v10
```

然后在你的源码中引入 validator 包：

```go
import "github.com/go-playground/validator/v10"
```

## 2.使用

### 2.1 基本使用

```go
package main

import (
	"fmt"

	"github.com/go-playground/validator/v10"
)

type User struct {
	FirstName string `validate:"required"`
	LastName  string `validate:"required"`
	Age       uint8  `validate:"gte=0,lte=130"`
}

var validate *validator.Validate

func main() {
	validate = validator.New()
	validateStruct()
}

func validateStruct() {
	user := &User{
		FirstName: "Badger",
		LastName:  "Smith",
		Age:       135,
	}

	err := validate.Struct(user)
	if err != nil {
		for _, err := range err.(validator.ValidationErrors) {
			fmt.Println(err.Namespace())       // User.Age
			fmt.Println(err.Field())           // Age
			fmt.Println(err.StructNamespace()) // User.Age
			fmt.Println(err.StructField())     // Age
			fmt.Println(err.Tag())             // lte
			fmt.Println(err.ActualTag())       // lte
			fmt.Println(err.Kind())            // uint8
			fmt.Println(err.Type())            // uint8
			fmt.Println(err.Value())           // 135
			fmt.Println(err.Param())           // 130
			fmt.Println(err.Error())           // Key: 'User.Age' Error:Field validation for 'Age' failed on the 'lte' tag
			fmt.Println()
		}
	}
}
```

### 2.2 跨字段验证

跨字段的验证规则有很多，具体可以查看文末[规则表](TODO)。下面的示例使用了 `eqfield` 规则，它表示当前字段必须等于指定的字段。

```go
package main

import (
	"fmt"

	"github.com/go-playground/validator/v10"
)

type User struct {
	Password        string `validate:"required"`
	ConfirmPassword string `validate:"required,eqfield=Password"`
}

var validate *validator.Validate

func main() {
	validate = validator.New()
	validateStruct()
}

func validateStruct() {
	user := User{
		Password:        "password",
		ConfirmPassword: "pass",
	}

	err := validate.Struct(user)
	if err != nil {
		for _, err := range err.(validator.ValidationErrors) {
			fmt.Println(err.Error()) // Key: 'User.ConfirmPassword' Error:Field validation for 'ConfirmPassword' failed on the 'eqfield' tag
		}
	}
}
```

### 2.3 跨结构体验证

除了跨字段验证，validator 还支持跨结构体验证，在[规则表](TODO)中你会发现，跨结构体的验证规则通常是在跨字段的验证规则的 `field` 前加上 `cs`，可以理解为 `cross-struct`。所以下面示例的 `eacsfield` 就是用来验证当前字段是否等于制定结构体的字段：

```go
package main

import (
	"fmt"

	"github.com/go-playground/validator/v10"
)

type User struct {
	Uid     string `validate:"required,eqcsfield=Account.PayUid"`
	Account Account
}

type Account struct {
	PayUid string `validate:"required"`
}

var validate *validator.Validate

func main() {
	validate = validator.New()
	validateStruct()
}

func validateStruct() {
	account := Account{
		PayUid: "uid-1025",
	}

	user := User{
		Uid:     "uid-1024",
		Account: account,
	}

	err := validate.Struct(user)
	if err != nil {
		for _, err := range err.(validator.ValidationErrors) {
			fmt.Println(err.Error()) // Key: 'User.Uid' Error:Field validation for 'Uid' failed on the 'eqcsfield' tag
		}
	}
}
```

### 2.4 验证 slice、map

验证 slice 和 map 等复合数据类型时，通常需要加上 `dive` 表示递归验证，否则将不会深入验证数据类型内部元素。

```go
package main

import (
	"fmt"

	"github.com/go-playground/validator/v10"
)

type User struct {
	// 第一个 required 规则用于确保切片本身不为零值（即不为 nil）。
	// dive 规则指定了对切片元素进行递归验证。
	// 第二个 required 规则用于验证切片中的每个元素是否不为空。
	Accounts []string `validate:"required,dive,required"`

	// 第一个 required 规则用于确保 map 本身不为零值（即不为 nil）。
	// dive 规则指定了对 map 值进行递归验证。
	// gt=0 规则用于验证 map 中的每个值是否大于 0。
	// 第二个 required 规则用于验证 map 中的每个值是否不为空。
	Balance map[string]int `validate:"required,dive,gt=0,required"`
}

var validate *validator.Validate

func main() {
	validate = validator.New()
	validateStruct()
}

func validateStruct() {
	user := User{
		Accounts: []string{"account-1", "", "account-3"},
		Balance: map[string]int{
			"key1": 1,
			"key2": -1,
		},
	}

	err := validate.Struct(user)
	if err != nil {
		for _, err := range err.(validator.ValidationErrors) {
			// Key: 'User.Accounts[1]' Error:Field validation for 'Accounts[1]' failed on the 'required' tag
			// Key: 'User.Balance[key2]' Error:Field validation for 'Balance[key2]' failed on the 'gt' tag
			fmt.Println(err.Error())
		}
	}
}
```

### 2.5 自定义类型验证器

```go
package main

import (
	"database/sql"
	"database/sql/driver"
	"fmt"
	"reflect"

	"github.com/go-playground/validator/v10"
)

type DbBackedUser struct {
	Name sql.NullString `validate:"required"`
	Age  sql.NullInt64  `validate:"required"`
}

var validate *validator.Validate

func main() {

	validate = validator.New()

	// 注册所有的 sql.Null* 类型，使用 ValidateValuer 自定义类型函数进行验证
	validate.RegisterCustomTypeFunc(ValidateValuer, sql.NullString{}, sql.NullInt64{}, sql.NullBool{}, sql.NullFloat64{})

	x := DbBackedUser{
		Name: sql.NullString{String: "", Valid: true},
		Age:  sql.NullInt64{Int64: 0, Valid: false},
	}

	err := validate.Struct(x)

	if err != nil {
		fmt.Printf("Err(s):\n%+v\n", err)
	}
}

// ValidateValuer 实现了 validator.CustomTypeFunc 接口
func ValidateValuer(field reflect.Value) interface{} {
	// 如果字段的类型实现了 driver.Valuer 接口（即支持向数据库写入值），则进行验证
	if valuer, ok := field.Interface().(driver.Valuer); ok {

		val, err := valuer.Value() // 获取字段的值
		if err == nil {
			return val
		}
		// 处理错误
	}

	return nil
}
```

最终输出：

```sh
Err(s):
Key: 'DbBackedUser.Name' Error:Field validation for 'Name' failed on the 'required' tag
Key: 'DbBackedUser.Age' Error:Field validation for 'Age' failed on the 'required' tag
```

### 2.6 国际化/翻译

在前面的示例中，我们可以看到返回的信息都是英文的，如果需要翻译成中文信息，首先需要安装翻译包：

```sh
go get -u github.com/go-playground/locales
go get -u github.com/go-playground/universal-translator
```

接着我们修改 2.1 的代码，实现翻译：

```go
package main

import (
	"fmt"

	"github.com/go-playground/locales/zh"
	ut "github.com/go-playground/universal-translator"
	"github.com/go-playground/validator/v10"
	zhTrans "github.com/go-playground/validator/v10/translations/zh"
)

type User struct {
	FirstName string `validate:"required"`
	LastName  string `validate:"required"`
	Age       uint8  `validate:"gte=0,lte=130"`
}

var validate *validator.Validate
var trans ut.Translator

func main() {
	validate = validator.New()

	// 中文翻译器
	uniTrans := ut.New(zh.New())
	trans, _ = uniTrans.GetTranslator("zh")
	// 注册翻译器到验证器
	err := zhTrans.RegisterDefaultTranslations(validate, trans)
	if err != nil {
		panic(fmt.Sprintf("registerDefaultTranslations fail: %s\n", err.Error()))
	}

	validateStruct()
}

func validateStruct() {
	user := &User{
		FirstName: "Badger",
		LastName:  "Smith",
		Age:       135,
	}

	err := validate.Struct(user)
	if err != nil {
		for _, err := range err.(validator.ValidationErrors) {
			// 翻译
			fmt.Println(err.Translate(trans)) // Age必须小于或等于130
		}
	}
}
```

## 3.错误处理

通过源码，我们可以发现 `validator` 返回的错误类型有两种，分别是参数错误 `InvalidValidationError` 和验证错误 `ValidationErrors`：

```go
// $GOPATH/pkg/mod/github.com/go-playground/validator/v10/errors.go

// InvalidValidationError describes an invalid argument passed to
// `Struct`, `StructExcept`, StructPartial` or `Field`
type InvalidValidationError struct {
	Type reflect.Type
}

// Error returns InvalidValidationError message
func (e *InvalidValidationError) Error() string {

	if e.Type == nil {
		return "validator: (nil)"
	}

	return "validator: (nil " + e.Type.String() + ")"
}

// ValidationErrors is an array of FieldError's
// for use in custom error messages post validation.
type ValidationErrors []FieldError

// Error is intended for use in development + debugging and not intended to be a production error message.
// It allows ValidationErrors to subscribe to the Error interface.
// All information to create an error message specific to your application is contained within
// the FieldError found within the ValidationErrors array
func (ve ValidationErrors) Error() string {

	buff := bytes.NewBufferString("")

	for i := 0; i < len(ve); i++ {

		buff.WriteString(ve[i].Error())
		buff.WriteString("\n")
	}

	return strings.TrimSpace(buff.String())
}
```

通常情况下一般会忽略对 `InvalidValidationError` 的判断/处理。

## 4.常用验证规则

完整的验证规则表可以参阅[这里](https://pkg.go.dev/github.com/go-playground/validator/v10#readme-baked-in-validations)。

详细的验证规则介绍可以参阅[这里](https://pkg.go.dev/github.com/go-playground/validator/v10#pkg-overview)

### 4.1 比较

| 规则 | 描述 |
| - | - |
| eq | 等于 |
| eq_ignore_case | 等于，忽略大小写 |
| ne | 不等于 |
| ne_ignore_case | 不等于，忽略大小写 |
| gt | 大于 |
| gte | 大于等于 |
| lt | 小于 |
| lte | 小于等于 |

### 4.2 跨字段校验

| Tag | Description |
| - | - |
| eqfield | 当前字段必须等于指定字段 |
| nefield | 当前字段必须不等于指定字段 |
| gtfield | 当前字段必须大于指定字段 |
| gtefield | 当前字段必须大于等于指定字段 |
| ltfield | 当前字段必须小于指定字段 |
| ltefield | 当前字段必须小于等于指定字段 |
| fieldcontains | 当前字段必须包含指定字段，只能用于字符串类型 |
| fieldexcludes | 当前字段必须不包含指定字段，只能用于字符串类型 |

### 4.3 跨结构体跨字段校验

「跨结构体跨字段校验」规则与「跨字段校验」规则类似，一般是在「跨字段校验」规则的 `field` 前加上 `cs` 字符。

| Tag | Description |
| - | - |
| eqcsfield | 当前字段必须等于指定结构体的字段 |
| necsfield | 当前字段必须不等于指定结构体的字段 |
| gtcsfield | 当前字段必须大于指定结构体的字段 |
| gtecsfield | 当前字段必须大于等于指定结构体的字段 |
| ltcsfield | 当前字段必须小于指定结构体的字段 |
| ltecsfield | 当前字段必须小于等于指定结构体的字段 |

### 4.4 字符串相关验证

| Tag | Description |
| - | - |
| alpha | 仅限字母 |
| alphanum | 仅限字母、数字 |
| alphanumunicode | 仅限字母、数字和 Unicode |
| alphaunicode | 字母和 Unicode |
| ascii | ASCII |
| boolean | 当前字段必须是能够被 `strconv.ParseBool` 解析为字符串的值 |
| contains | 当前字段必须包含指定字符串 |
| startswith | 当前字段必须以指定字符串开头 |
| startsnotwith | 当前字段必须不是以指定字符串开头 |
| endswith | 当前字段必须以指定字符串结尾 |
| endsnotwith | 当前字段必须不是以指定字符串结尾 |
| uppercase | 当前字段的字母必须是大写，可包含数字，不能为空 |
| lowercase | 当前字段的字母必须是小写，可包含数字，不能为空 |

### 4.5 格式化验证

| Tag | Description |
| - | - |
| base64 | 当前字段必须是有效的 Base64 值，不能为空 |
| base64url |  当前字段必须是包含根据 RFC4648 规范的有效 base64 URL 安全值。 |
| json | 正确的 JSON 串 |
| rgb | 正确的 RGB 字符串 |
| rgba | 正确的 RGBA 字符串 |

### 4.6 其他验证

| Tag | Description |
| - | - |
| dir | 指定字段的值必须是已存在的目录 |
| dirpath | 指定字段的值必须是合法的目录路径 |
| file | 指定字段的值必须是已存在的文件 |
| filepath | 指定字段的值必须是合法的文件路径 |
| len | 当前字段的长度必须指定值，可用于 string、slice 等 |
| max | 当前字段的最大值必须是指定值 |
| min | 当前字段的最小值必须是指定值 |
| required | 当前字段为必填项，且不能为零值 |
| required_if | 当指定字段等于给定值时，当前字段使用 required 验证。如 `required_if=Field1 foo Field2 bar` |
| required_unless | 当指定字段不等于给定值时，当前字段使用 required 验证。如 `required_unless=Field1 foo Field2 bar` |
| required_with | 当任一指定字段不为零值时，当前字段使用 required 验证。如 `required_with=Field1 Field2` |
| required_with_all | 当所有指定字段不为零值时，当前字段使用 required 验证。 |