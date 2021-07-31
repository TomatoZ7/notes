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