# Go 操作 Json

## 1.序列化/反序列化 Json

## 2 操作技巧

### 2.1 替换 Json 类型

```go
type Inner struct {
    Name  string `json:"name"`
    Price int    `json:"price"`
}

type Outer struct {
    Inner
    Price string `json:"price"`
}

func main() {
    m := new(Outer)
    m.Name = "tz"
    m.Price = "100"

    r, _ := json.Marshal(m)
    fmt.Println(string(r))
}
```

此时，`Inner` 结构体中的 `price` 属性的 int 数据类型就会被替换为 string 数据类型。

### 2.2 不序列化键值对

如果你不希望某些键值对被序列化，可以有 2 种选择：

```go
type s struct {
    // 1. 属性名称首字母使用小写
    score string `json:"score"`
    // 2. 使用 json:"-"
    Password string `json:"-"`
}
```

### 2.3 使用 omitempty 过滤内容为空的属性

```go
type s struct {
    Code int `json:"code"`
    Err string `json:"err,omitempty"`
}
```

### 2.4 简单的类型转换

```go
type S struct {
	Code int    `json:"code,string"`    // 数据转换
	Msg  string `json:"msg"`
}

func main() {
	s := new(S)
	s.Code = 200
	s.Msg = "success"

	r, _ := json.Marshal(s)
	fmt.Println(string(r))
}
```

### 2.5 未知的数据类型声明

如果你不知道结构体完整的数据类型，可以将它暂且声明为 `Json.RawMessage`：

```go
type S struct {
	Code int             `json:"code"`
	Msg  string          `json:"msg"`
	Data json.RawMessage `json:"data"`
}

func main() {
	jsonStr := `{
		"code": 200, 
		"msg": "ok", 
		"data": {"name": "tz", "age": 18}}`

	var s S
	_ = json.Unmarshal([]byte(jsonStr), &s)
	fmt.Printf("%T", s.Data)
}
```