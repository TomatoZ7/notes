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

## 5 axios 实例

如果有个别配置与公共配置不同，则可以自己创建一个 `axios` 实例：

```js
const instance = axios.create({
  baseURL: 'http://xxx.xxx.x.xx:8000',
  timeout: 100000
})

instance({
  url: '/home/data',
  params: {
    type: 'pop',
    page: 1
  }
})
.then(res => {
  console.log(res)
})
```

## 6 axios 封装

封装是为了更好的管理和解耦，如果每一个 `js` 文件都依赖了 `axios` 框架，如果哪一天需要对网络请求框架做出调整，那么就需要到每一个文件中去改动，这是不现实的。

创建目录和文件： `src/network/request.js`

### 6.1 封装方式一

```js
import axios from 'axios'

export function request(config, success, failure) {
  const instance = axios.create({
    baseURL: 'http://123.207.32.32:8000',
    timeout: 5000
  })

  // 发送网络请求
  instance(config)
    .then(res => {
      success(res)
    })
    .catch(err => {
      failure(err)
    })
}
```

调用：

```js
import {request} from './network/request'

request({
  url: '/home/multidata'
}, res => {
  console.log(res)
}, err => {
  console.log(err)
})
```

### 6.2 封装方式二 通过 Promise

```js
import axios from 'axios'

export function request(config) {
  return new Promise((resolve, reject) => {
    const instance = axios.create({
      baseURL: 'http://123.207.32.32:8000',
      timeout: 5000
    })

    // 发送网络请求
    instance(config)
      .then(res => {
        resolve(res)
      })
      .catch(err => {
        reject(err)
      })
  })
}
```

调用：

```js
import {request} from './network/request'

request({
  url: '/home/multidata'
}).then(res => {
  console.log(res)
}.catch(err => {
  console.log(err)
}))
```

### 6.3 封装方式三

```js
import axios from 'axios'

export function request(config) {
  const instance = axios.create({
    baseURL: 'http://123.207.32.32:8000',
    timeout: 5000
  })

  return instance(config) // 本身返回的就是 Promise，无须再封装
}
```