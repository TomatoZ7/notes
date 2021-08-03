# runtime-compiler 和 runtime-only 的区别

## 1 两者主要是 main.js 文件的区别

`runtime-commpiler` :

```js
import Vue from 'vue'
import App from './App'

Vue.config.productionTip = false

new Vue({
  el: '#app',
  components: { App },
  template: '<App/>'
})
```

`runtime-only` :

```js
import Vue from 'vue'
import App from './App'

Vue.config.productionTip = false

new Vue({
  el: '#app',
  render: h => h(App)
})
```

## 2 vue 程序运行流程

```
template -> ast(abstract syntax tree) -> render 函数 -> virtual dom -> dom(UI)
```

## 3 小结

通过对运行流程的解读，我们知道 `runtime-only` 是从 `render` 函数起开始运行的，所以：

1. 性能更高

2. 代码量更少(没有 `compiler` 去处理前面两个流程)

## 4 render 函数

### 4.1 runtime-compiler 使用 render

其实 `runtime-compiler` 也可以使用 `render` 函数：

```js
import Vue from 'vue'
import App from './App'

Vue.config.productionTip = false

new Vue({
  el: '#app',
  render: function(createElement) {     // 实际上传递的参数是 createElement 函数
    // createElement('标签', { 标签的属性 }, [ 标签的内容 ])
    return createElement('h2', {class: 'box'}, ['hello world'])
  }
})
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/frontend/images/runtime-only1.jpg)
