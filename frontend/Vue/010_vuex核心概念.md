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

## 3 Mutations

### 3.1 状态更新

`Vuex` 的 `store` 状态的更新唯一方式：提交 `Mutations`。

`Mutations` 主要包括两部分：

+ 字符串的**事件类型(type)**

+ 一个回调函数(handler)，该回调函数的第一个参数就是 `state`

#### 3.1.1 往 Mutations 里传递参数

`src/store/index.js` :

```js
mutations: {
    ...
    addNum(state, num) {
        state.counter += num
    }
},
```

`src/App.vue` :

```html
<button @click="increNum(5)">+5</button>
<button @click="increNum(10)">+10</button>

<script>
methods: {
    ...
    increNum(num) {
      this.$store.commit('addNum', num)
    }
  }
```
</script>

参数被称为 `mutations` 的载荷(Payload)，当参数很多的时候，`payload` 也可以是一个对象。

#### 3.1.2 特殊的提交风格

```js
this.$store.commit({
    type: 'addNum',
    num
})
```

此时 `addNum(state, num) {...}` 里的 `num` 将会接收传过去的对象，此时应该叫 `payload` 更加合适。

```js
mutations: {
    ...
    addNum(state, payload) {
        state.counter += payload.num
    }
},
```

### 3.2 响应规则

`Vuex` 的 `store` 中的 `state` 是响应式的，当 `state` 中的数据发生改变时，`Vue` 组件会自动更新。

这就要求我们必须遵守一些 `Vuex` 对应的规则：

+ 提前在 `store` 中初始化好所需的属性

当对一个对象新增未被初始化的属性时，如 `obj[addr] = 'github'`，响应式系统是不会显示出新增属性值的。

+ 当给 `state` 中的对象添加新属性时，使用下面的方式：

    1. 使用 `Vue.set(obj, 'addr', 'github')`

    2. 用新对象给旧对象重新赋值


### 3.3 常量类型

新建一个定义常量的文件 `src/store/mutations-types.js` :

```js
export const ADDNUM = 'addNum'
```

在文件里使用常量：

`src/App.vue` : 

```js
import {ADDNUM} from './store/mutations-types'

...

methods: {
    ...
    addNum(num) {
      this.$store.commit(ADDNUM, num)
    }
}
```

`src/store/index.js` : 

```js
import { ADDNUM } from './mutations-types.js'

...

mutations: {
    ...
    [ADDNUM](state, num) {
        state.counter += num
    }
},
```

### 3.4 同步函数

通常情况下，`Vuex` 要求我们 `Mutations` 中的方法必须是同步方法。主要的原因是当我们使用 `devtools` 时，可以帮助我们捕捉 `mutations` 的快照。但是如果是异步操作，那么 `devtools` 将不能很好的追踪这个操作什么时候会被完成。

如果真的需要异步操作，这个时候就需要 `Action` 的登场了。

## 4 Action

### 4.1 异步操作

`src/store/index.js` :

```js
const store = new Vuex.Store({
    state: {
        ...
        author: {
            name: 'tz7',
            age: 18,
            gender: 'man'
        }
    },
    mutations: {
        ...
        updateAuthor(state) {
            state.author.age = 81
        }
    },
    actions: {
        // 可以暂时认为 context 就是 store 对象
        updateAuthorAction(context) {
            setTimeout(() => {
                context.commit('updateAuthor')
            }, 2000)
        }
    },
    getters: {...},
    modules: {...}
})

export default store
```

`src/App.vue` :

```html
<template>
  <div id="app">
    <h2>{{$store.state.author}}</h2>
    <button @click="updateInfo">修改信息</button>
  </div>
</template>

<script>
export default {
  name: 'App',
  components: {...},
  data() {...},
  methods: {
      ...
    updateInfo() {
      // this.$store.commit('updateAuthor')
      this.$store.dispatch('updateAuthorAction')    // 使用 dispatch
    }
  }
}
</script>
```

### 4.2 参数传递

与 `mutations` 参数传递一致。

## 5 Module

### 5.1 认识 Module

`Vue` 使用单一状态树，意味着很多状态都会交给 `Vuex` 来管理。当应用变得非常复杂时，`store` 对象就有可能变得相当臃肿。

为了解决这一问题，`Vuex` 允许我们将 `store` 分割成模块(Module)，而每个模块拥有自己的 `state`、`mutations`、`action`、`getters` 等。

### 5.2 基本使用

`src/store/index.js` :

```js
const moduleA = {
    state: {
        'name': 'tz777-moduleA'
    },
    mutations: {
        updateName(state, payload) {
            state.name = payload
        }
    },
    actions: {
        // 此时的 context 仅表示 moduleA，commit 时只会 commit 到自己的 mutations 里
        updateNameAction(context) {
            setTimeout(() => {
                context.commit('updateName', 'Tomato')
            }, 1500)
        }
    },
    getters: {
        getRootAuthor(state, payload, rootState) {
            return rootState.author
        }
    }
}

const store = new Vuex.Store({
    state: {...},
    mutations: {...},
    actions: {...},
    getters: {...},
    modules: {
        a: moduleA
    }
})
```

`src/App.vue` : 

```html
<template>
  <div id="app">
    <h2>{{$store.state.a.name}}</h2>
    <!-- getters -->
    <h2>{{$store.getters.getRootAuthor}}</h2>
    <button @click="updateModuleName">修改名字</button>
    <button @click="asyncUpdateModuleName">异步修改名字</button>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {...},
  methods: {
    updateModuleName() {
      // mutations
      this.$store.commit('updateName', 'Tomato')
    },
    asyncUpdateModuleName() {
      // actions
      this.$store.dispatch('updateNameAction')
    }
  }
}
</script>
```