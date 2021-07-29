# v-model

表单控件在实际开发中是非常常见的。特别是对于用户信息的提交，需要大量的表单。

Vue 中使用 `v-model` 指令实现表单元素和数据的双向绑定。

### 使用

#### 示例

```html
<div id="app">
    <input type="text" v-model="message">
    <h2>{{message}}</h2>
</div>

<script>
    let app = new Vue({
        el : "#app",
        data : {
            message: ''
        }
    })
</script>
```

#### 解析

+ 当我们在输入框输入内容时

+ 因为 `input` 中的 `v-model` 绑定了 `message`，所以会实时将输入的内容传递给 `message`，`message` 发生改变。

+ 当 `message` 发生改变时，因为上面我们使用了 `Mustache` 语法，将 `message` 的值插入到 DOM 中，所以 DOM 会发生响应的改变。

+ 所以，通过 `v-model` 实现了双向的绑定。

当然，也可以将 `v-model` 用于 `textarea` 元素。

#### 小结

`v-model` 其实是一个语法糖，他的背后本质上是包含两个操作的：

1. `v-bind` 绑定一个 value 属性

2. `v-on` 指令给当前元素绑定 input 事件

也就是说

```html
<input type="text" v-model="message">
等同于
<input type="text" v-bind:value="message" v-on:input="message = $event.target.value">
```


### v-model

#### lazy

默认情况下，`v-model` 默认是在 `input` 事件中同步输入框的数据。也就是说，一旦有数据发生改变对应的 `v-model` 中的数据就会自动发生改变。

`v-model.lazy` 可以让数据在失去焦点或者回车时才会更新。

#### number

默认情况下，`input` 输入框会把输入内容当成 string 类型处理，如果只希望处理的是 number 类型，可以用 `v-model.number` 将输入框中输入的内容自动转为 number 类型。

#### trim

`v-model.trim` 可以将输入内容的首尾空格去掉。