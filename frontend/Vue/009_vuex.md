# vuex

## 1 介绍

`Vuex` 是一个专为 `Vue.js` 应用程序开发的**状态管理模式**。

它采用**集中式存储管理**应用的所有组件的状态，并以相应的规则保证状态以一种可预测的方式发生变化。

`Vuex` 也集成到 `Vue` 的官方调试工具 `devtools extension`，提供了诸如零配置的 `time-travel` 调试、状态快照导入导出等高级调试功能。

### 1.1 状态管理

**状态管理模式、集中式存储管理**，可以简单地将其看成把需要多个组件共享的变量全部存储在一个对象里面。

然后将这个对象放在顶层的 `Vue` 实例中，让其他组件可以使用。

这样，多个组件就可以共享这个对象中的所有变量属性。

### 1.2 响应式

通过上面的描述我们也可以自己封装一个变量来管理，但是 `Vuex` 的封装能够使它里面的所有属性做到**响应式**，减少我们自己封装的工作量。

## 2 管理什么状态

有什么状态需要在多个组件间进行共享？如：用户的登录状态、用户名称、头像等信息，收藏的商品、购物车中的商品等。`

## 3 使用

### 3.1 安装

```shell
npm install vuex --save
```

### 3.2 开始使用

#### 3.2.1 新建文件导入 Vuex

`src/store/index.js` : 

```js
import Vue from 'vue'
import Vuex from 'vuex'

// 1. 安装插件
Vue.use(Vuex)

// 2. 创建对象
const store = new Vuex.Store({
    state: {
        counter: 100      // 保存状态
    },
    mutations: {

    },
    actions: {

    },
    getters: {

    },
    modules: {
        
    }
})

// 3. 导出
export default store
```

#### 3.2.2 main.js

```js
import Vue from 'vue'
import App from './App'
import store from './store'         // 导入

Vue.config.productionTip = false

new Vue({
  el: '#app',
  store,                // 挂载
  render: h => h(App)
})
```

#### 3.2.3 使用保存的状态

`src/components/Hello/Hello.vue` :

```html
<template>
    <div>
        <h2>{{$store.state.counter}}</h2>
    </div>
</template>

<script>
export default({
    name: 'Hello'
})
</script>
```

`App.vue` :

```html
<template>
  <div id="app">
    <h2>{{$store.state.counter}}</h2>
    <button @click="counter++">+</button>
    <button @click="counter--">-</button>
    
    <hello></hello>
  </div>
</template>

<script>
import Hello from './components/Hello/Hello'

export default {
  name: 'App',
  components: {
    Hello
  },
  data() {
    return {
      message: 'Hello World'
    }
  }
}
</script>
```

## 4 Vuex 状态管理图例

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/frontend/images/vuex1.jpg)