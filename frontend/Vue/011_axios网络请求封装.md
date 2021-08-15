# axios 网络请求封装

`axios` 支持多种请求方式：

```js
axios(config)
axios.request(config)
axios.get(url[, config])
axios.delete(url[, config])
axios.head(url[, config])
axios.post(url[, data[, config]])
axios.put(url[, data[, config]])
axios.patch(url[, data[, config]])
```

## 1 axios 基本使用

```js
axios({
  url: 'http://123.207.32.32:8000/home/data',
//   method: 'get',
  params: {
    type: 'pop',
    page: 1
  }
}).then(res => {
  console.log(res)
})
```

## 2 axios 并发请求

```js
axios.all(
    [ 
      axios({
        url: 'http://123.207.32.32:8000/home/multidata'
      }),
      axios({
        url: 'http://123.207.32.32:8000/home/data',
        params: {
          type : 'sell',
          page: 5
        }
      })
    ]
  )
  // .then(res => {
  //   console.log(res)
  // })
  .then(axios.spread((res1, res2) => {
    console.log(res1)
    console.log(res2)
  }))
```

## 3 全局配置

上述示例中我们可以把请求的 `url` 前缀抽离出来，减少冗余代码。

```js
axios.defaults.baseURL = 'http://123.207.32.32:8000'
axios.defaults.timeout = 5000   // ms
```

## 4 常见的配置选项

```js
//  请求地址
url: '/user'

// 请求类型
method: 'get'

// 请求根路径
baseURL: 'https://github.com'

// 请求前的数据处理
transformRequest: [function(data) {}]

// 请求后的数据处理
transformResponse: [function(data) {}]

// 自定义请求头
headers: {'x-Requested-With':'XMLHttpRequest'}

// URL 查询对象
params: {id: 12}

// 查询对象序列化函数
paramsSerializer: function(params) {}

// request body
data: {key: 'aa'}

// 超时设置
timeout: 1000

// 跨域是否带 token
withCredentials: false

// 自定义请求处理
adapter: function(resolve, reject, config) {}

// 身份验证信息
auth: {uname: '',pwd: '12'}

// 响应的数据格式 json/blob/document/arraybuffer/text/stream
responseType: 'json'
```