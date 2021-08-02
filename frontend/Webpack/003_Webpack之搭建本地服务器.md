# Webpack 搭建本地服务器

`webpack` 提供了一个可选的本地开发服务器，这个本地服务器基于 `node.js` 搭建，内部使用 `express` 框架，可以实现浏览器自动刷新修改后的结果。

## 1 安装

它是一个单独的模块，在 `webpack` 中使用之前需要先安装它：

```shell
npm install --save-dev webpack-dev-server@2.9.1
```

## 2 配置

### 2.1 配置项

`devserver` 作为 `webpack` 中的一个选项，选项本身可以设置如下属性：

+ `contentBase` : 为哪一个文件夹提供本地服务，默认是根文件夹，这里写 `./dist`

+ `port` : 端口号

+ `inline` : 页面实时刷新

+ `historyApiFallback` : 在 `SPA` 页面中，依赖 `HTML5` 的 `history` 模式

### 2.2 配置文件 webpack.config.js

```js
devServer: {
    contentBase: './dist',
    inline: true
}
```

### 2.3 配置指令

由于是 `--save-dev` 安装的而非全局安装，所以需要在 `package.json` 配置一个新的命令：

```json
"scripts": {
    ...
    "dev": "webpack-dev-server [--open]"        // --open : 自动打开浏览器
}
```