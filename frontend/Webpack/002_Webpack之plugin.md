# Webpack 之 plugin

## 1 plugin

`wenpack` 中的插件，就是对 `webpack` 现有功能的各种扩展，如：打包优化，文件压缩等。

## 2 loader 和 plugin 的区别

`loader` 主要用于转换某些类型的模块，它是一个转换器。

`plugin` 是插件，它是对 `webpack` 本身的扩展，是一个扩展器。

## 3 plugin 的使用

1. 通过 npm 安装需要使用的 plugins

2. 在 webpack.config.js 中的 plugins 中配置插件

## 4 示例

### 4.1 使用 BannerPlugin 为打包的文件添加版权声明

#### 4.1.1 修改 webpack.config.js 文件

```js
const path = require('path')
const webpack = require('webpack')

module.exports = {
    ...
    plugins: [
        new webpack.BannerPlugin('版权声明：xxxxxxx')
    ]
}
```

#### 4.1.2 重新打包后查看 bundle.js 文件头部

### 4.2 使用 htmlWebpackPlugin 打包 html

在之前的打包中，并没有将根目录的 `index.html` 文件打包进 `dist` 文件夹中，接下来我们使用 `htmlWebpackPlugin` 打包 `index.html`。

#### 4.2.1 htmlWebpackPlugin 作用

1. 自动生成 `index.html` 文件，并且可以指定模板来生成。

2. 将打包的 `js` 文件，自动通过 `script` 标签插入到 `body` 中。

#### 4.2.2 安装

```shell
npm install html-webpack-plugin --save-dev
```

#### 4.2.3 修改 webpack.config.js 文件

`plugin` 部分代码：

```js
plugins: [
    new webpack.BannerPlugin('版权声明：xxxxxxx')
    new htmlWebpackPlugin({
        template: 'index.html'      // 根据什么模板来生成 index.html
    })
]
```

#### 4.2.4 publicPath

如果 `webpack.config.js` 设置了 `publicPath`，则最好去掉，否则会导致 `script` 标签的引入路径出错。

### 4.3 使用 uglifyjs-webpack-plugin 对打包的 js 进行压缩

#### 4.3.1 安装

```shell
# 指定版本号 1.1.1，和 cli2 保持一致
npm install uglifyjs-webpack-plugin@1.1.1 --save-dev
```

#### 4.3.2 修改 webpack.config.js

```js
const uglifyjsPlugin = require('uglifyjs-webpack-plugin')

module.exports = [
    plugins: {
        new uglifyjsPlugin()
    }
]
```

#### 4.4.3 重新打包，查看被压缩的 bundle.js