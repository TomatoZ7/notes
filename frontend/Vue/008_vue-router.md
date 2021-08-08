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

### 4 路由的默认配置

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

### 5 HTML5 的 history 模式

默认的 `router` 配置是哈希模式，可以改为 `history` 模式：

```js
const router = new Router({
    routes,
    mode: 'history'
})
```