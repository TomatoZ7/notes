# 插槽

插槽可以让组件更具扩展性

## 插槽的基本使用

```html
<div id="app">
    <cpn><i>插槽iii</i></cpn>
    <cpn></cpn>
    <cpn>
        <p>slot-p1</p>
        <p>slot-p2</p>
    </cpn>
</div>

<template id="cpn">
    <div>
        <h2>我是组件</h2>
        <p>我是组件ppp</p>
        <slot><button>按钮</button></slot>
    </div>
</template>
```

## 具名插槽

当组件中包含多个 `slot` 插槽时，我们无法准确替换其中某一个，这时候就使用具名插槽。

```html
<div id="app">
    <cpn><span slot="center">具名插槽</span></cpn>
</div>

<template id="cpn">
    <div>
        <slot name="left"><span>左边</span></slot>
        <slot name="center"><span>中间</span></slot>
        <slot name="right"><span>右边</span></slot>
    </div>
</template>
```