# Vuex 核心概念

+ **State**

+ **Getters**

+ **Mutation**

+ **Action**

+ **Module**


## 1 State 单一状态树(Simple Source of Truth)

单一状态树，也叫单一状态源。

什么是单一状态树？举个例子，在国内我们有很多信息需要记录：个人档案、公积金档案、社保档案、婚姻信息、房产信息等被分别存放在不同的地方进行管理。当我们有一天需要办理某个业务时候，会发现需要到各个对应的工作地点去提取档案信息，最后到办理业务处提交证明等信息。

这种保存信息的方案，不仅仅低效，而且不方便管理，以及日后的维护也不容易。

类比于 `Vuex` 开发，如果你的状态信息是保存到多个 `State` 对象中的，那么之后的管理和维护等都会变困难。所以 `Vuex` 使用了单一状态树来管理应用层级的全部状态。单一状态数能够让我们直接找到某个状态的片段，而且在之后的维护和调试中，也可以非常方便的管理和维护。

## 2 Getters 基本使用

有时候我们需要经常获取 `state` 数据经过加工后的结果，这时候就可以使用 `getters`。

### 2.1 getters 使用

`src/store/index.js` :

```js
const store = new Vuex.Store({
    state: {
        counter: 100
    },
    mutations: {...},
    actions: {},
    getters: {
        powerCounter(state) {
            return state.counter * state.counter
        }
    },
    modules: {}
})
```

`src/App.vue` :

```html
<template>
  <div id="app">
    ...
    <h2>{{$store.getters.powerCounter}}</h2>
    ...
  </div>
</template>
```

### 2.2 getters 复用

如果我们既想取平方又想取立方的话该怎么做？可以在多传入一个 `getters` 参数：

`src/store/index.js` :

```js
const store = new Vuex.Store({
    state: {
        counter: 100
    },
    mutations: {...},
    actions: {},
    getters: {
        powerCounter(state) {
            return state.counter * state.counter
        },
        cubicCounter(state, getters) {
            return getters.powerCounter * state.counter
        }
    },
    modules: {}
})
```

`src/App.vue` :

```html
<template>
  <div id="app">
    ...
    <h2>{{$store.getters.powerCounter}}</h2>
    <h2>{{$store.getters.cubicCounter}}</h2>
    ...
  </div>
</template>
```

### 2.3 动态传参

`src/store/index.js` :

```js
const store = new Vuex.Store({
    state: {
        counter: 100
    },
    mutations: {...},
    actions: {},
    getters: {
        powerCounter(state) {
            return state.counter * state.counter
        },
        cubicCounter(state, getters) {
            return getters.powerCounter * state.counter
        },
        multiCounter(state) {
            return function (n) {
                return n * state.counter
            }
        }
    },
    modules: {}
})
```

`src/App.vue` :

```html
<template>
  <div id="app">
    ...
    <h2>{{$store.getters.powerCounter}}</h2>
    <h2>{{$store.getters.cubicCounter}}</h2>
    <h2>{{$store.getters.multiCounter(5)}}</h2>
    ...
  </div>
</template>
```

