# vue-router

## 1 前端路由

随着 `ajax` 的出现，前后端分离的开发模式逐渐成为主流。而前端路由最主要的特点就是在此基础上由前端维护了一套路由规则。

其核心是改变 `URL`，页面不进行整体刷新。

## 2 vue-router

`vue-router` 是 `Vue.js` 官方的路由插件，它和 `vue.js` 是深度集成的，适合用于构建单页面应用。

官网：[https://router.vuejs.org/zh/](https://router.vuejs.org/zh/)

`vue-router` 是基于路由和组件的，路由用于设定访问路径，将路径和组件映射起来。

在 `vue-router` 的单页面应用中，页面的路径改变就是组件的切换。

## 3 安装和使用 vue-router

### 3.1 安装

```shell
$ npm install vue-router --save
```

### 3.2 使用

#### 3.2.1 导入路由对象，调用 Vue.use(VueRouter)

```js
// src/router/index.js
import Router from 'vue-router'

Vue.use(Router)
```

#### 3.2.2 创建路由实例，并传入路由映射配置

```js
// src/router/index.js
import Home from '../component/Home'
import About from '../component/About'

const routes = [
    {
        path: '/home',
        component: Home
    },
    {
        path: '/about',
        component: About
    }
]
const router = new Router({
    // 配置路径和组件之间的映射关系
    routes 
})

export default router
```

#### 3.2.3 在 Vue 实例中挂载创建的路由实例

```js
// src/main.js
import router from './router'

new Vue({
    el: '#app',
    router,
    render: h => h(App)
})
```

```vue
<!-- src/App.vue -->
<template>
    <div id="app">
        <router-link to="/home">首页</router-link>
        <router-link to="/about">关于</router-link>
        <router-view></router-view>
    </div>
</template>

<script>
export default {
    name: 'App'
}
</script>
```

## 4 路由的默认配置

默认情况下，我们希望一进来便显示 `Home` 页面，但是上述示例中并没有配置默认首页，这时可以通过 `redirect` 来配置：

```js
// src/router/index.js
const router = [
    {
        path: '',
        redirect: '/home'
    }
    ...
]
```

## 5 HTML5 的 history 模式

默认的 `router` 配置是哈希模式，可以改为 `history` 模式：

```js
const router = new Router({
    routes,
    mode: 'history'
})
```

## 6 router-link 属性

### 6.1 tag

指定 `<router-link>` 渲染成什么组件，默认是 `a` 标签。

```js
<router-link to='/home' tag='li'>
```

### 6.2 replace

`replace` 不会留下 `history` 记录，所以指定 `replace` 的情况下，后退键返回不能返回到上一个页面中。

```js
<router-link to='/home' tag='li' replace>
```

### 6.3 active-class

当 `<router-link>` 对应的路由匹配成功时，会自动给当前元素设置一个 `router-link-active` 的 `class`，设置 `active-class` 可以修改默认的名称。

#### 6.3.1 统一修改

在每个标签都添加 `active=class` 会比较繁琐，可以在 `src/router/index.js` 进行统一配置：

```js
const router = {
    router,
    linkActiveClass: 'active'
}
```

## 7 通过代码控制跳转

```html
<button @click="handleClick">按钮</button>

...

export default {
    handleClick() {
        this.$router.push('/home')
    }
}
```

## 8 动态路由

在某些情况下，一个页面的 `path` 可能会拼接上用户 `id` 等信息，我们可以在路由定义时指定动态参数。

```js
{
    path: '/user/:id',
    component: User
}
```

```html
<router-link to="/user/1">我的</router-link>
```

```html
<div>
    <h2>{{$route.params.id}}</h2>
</div>
```

## 9 路由的懒加载

当打包构建应用时，`JS` 包会变得非常大，影响页面加载。

如果我们能把不同的路由对应的组件分割成不同的代码块，然后当路由被访问的时候才加载对应组件，这样就更加高效了。

```js
// src/router/index.js
const Home = () => import('../components/Home')
const Home = () => import('../components/About')
const Home = () => import('../components/User')
```

## 10 路由嵌套

比如在 `/home` 中，我们希望有 `/home/news` 和 `/home/message` 访问一些内容，这两个路径各对应两个子组件。

### 10.1 创建子组件

新建两个组件 `HomeNews` 和 `HomeMessage`。

### 10.2 配置

```js
// src/router/index.js
{
    path: '/home',
    component: Home,
    children: [
        {
            path: 'news',        // 注意开头不用加 '/'
            component: HomeNews
        },
        {
            path: 'message',
            component: HomeMessage
        }
    ]
}
```

### 10.3 嵌套默认路由

```js
// src/router/index.js
{
    path: '/home',
    component: Home,
    children: [
        {
            path: '',
            redirect: 'news'
        },
        {
            path: 'news',
            component: HomeNews
        },
        {
            path: 'message',
            component: HomeMessage
        }
    ]
}
```

## 11 参数传递

### 11.1 params

配置格式：`/router/:id`

传递方式：在 `path` 后跟上对应的值

示例：`/router/123`、`/router/abc`

### 11.2 query

配置格式：即普通配置

传递方式：对象中使用 `query` 的 `key` 作为传递方式

示例：`/router?id=123`、`/router?id=abc`

```html
<router-link :to="{path: '/profile', query: {name: 'tz', age:18}}">档案</router-link>
```