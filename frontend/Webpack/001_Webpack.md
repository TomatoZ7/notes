# Webpack 详解

## 1 什么是 Webpack

官方解释：

```
At its core,webpack is a static module bundler for modern JavaScript applications
从本质上讲，webpack 是一个现代的 JacaScript 应用的静态模块打包工具
```

`webpack` 可以让我们进行模块化开发，并帮助我们处理模块间的依赖关系。

而且不仅仅是 js 文件，css、图片、json 文件等在 `webpack` 中都可以被当做模块来使用。

**打包** 就是将 weboack 中的各种资源模块进行打包合并成一个或多个包(Bundle)。

并且在打包过程中，还可以对资源进行处理，比如压缩图片，将 scss 转成 css，将 ES6 语法转成 ES5 语法，将 ts 转成 js 等操作。

## 2 webpack 安装

安装 `webpack` 首先需要安装 Node.js，Node.js 自带包管理工具 `npm`。

#### 2.1 全局安装 webpack：

```shell
# 指定 3.6.0 版本是因为vue cli2 依赖该版本
npm install webpack@3.6.0 -g
```

#### 2.2 局部安装 webpack：

`--save-dev` 是开发时依赖，项目打包后则不再使用。

#### 2.3 全局安装和局部安装

+ 在终端直接执行 `webpack` 命令，使用的是全局安装的 `webpack`

+ 在 `package.json` 中定义了 `scripts` 时，其中包含了 `webpack` 命令，那么使用的是局部 `webpack`

## 3. webpack 基本使用示例

### 3.1 项目目录

```
- project/
-   dist/
-   src/
-       info.js
-       main.js
-       mathUtil.js
-   index.html
```

### 3.2 info.js

```js
export const name = 'JavaScript'
export const type = 'Script'
```

### 3.3 mathUtil.js

```js
function add(num1, num2) {
	return num1 + num2
}

function mul(num1, num2) {
	return num1 * num2
}

module.exports = {
	add, mul
}
```

### 3.4 main.js

```js
const {add, mul} = require('./mathUtils.js')

console.log(add(10, 20))
console.log(mul(10, 20))

import {name, type} from './info.js'

console.log(name)
console.log(type)
```

### 3.5 编写完后 webpack 打包

```shell
webpack ./src/main.js ./dist/bundle.js
```

### 3.6 index.html

```html
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title></title>
</head>
<body>

</body>
</html>

<script src="./dist/bundle.js"></script>
```

### 3.7 访问 index.html 文件

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/front/images/webpack.png)

## 4 webpack.config.js 配置

新建 `project/webpack.config.js` 文件：

```js
const path = require('path')

module.exports = {
    entry: './src/main.js',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'bundle.js'
    }
}
```

因为 `output.path` 需要绝对路径，所以需要使用 `node` 安装 `path` 模块动态获取路径:

```shell
$ npm init
```

## 5 使用 npm run build 代替 webpack

在 `project/package.json` 新增命令：

```json
{
  "name": "webpack-demo",
  "version": "1.0.0",
  "description": "",
  "main": "webpack.config.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "build" : "webpack"     // 新增
  },
  "author": "",
  "license": "ISC",
  "dependencies": {
    "path": "^0.12.7"
  }
}
```

即可使用 `npm run build` 来代替 `webpack` 打包。

## 6 在项目下安装 webpack

```shell
$ npm install webpack@3.6.0 --save-dev
```

安装之后执行 `npm run build` 会优先使用本地 `webpack` 依赖，而只要是在终端运行 `webpack` 都是全局的。

## 7 loader

`loader` 是 `webpack` 的一个核心概念，他可以对 css、图片进行加载或 ES6 转 ES5，TS 转 ES5，scss、less 转 css 等。

### 7.1 安装 css loader

`style-loader` 负责将样式添加到 `dom` 中

`css-loader` 负责加载 css 文件

```shell
$ npm install --save-dev style-loader

$ npm install --save-dev css-loader
```

### 7.2 配置 webpack.config.js

```js
module.exports = {
    ...
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']         // 使用多个 loader 时从右向左读
            }
        ]
    }
}
```

### 7.3 其他 loader

![less-loader](https://www.webpackjs.com/loaders/less-loader/)

![sass-loader](https://www.webpackjs.com/loaders/sass-loader/)

![图片处理 url-loader](https://www.webpackjs.com/loaders/url-loader/)

![ES6 转 ES5](https://www.webpackjs.com/loaders/babel-loader/)

## 8 引入 vue.js

### 8.1 安装

```shell
$ npm install vue --save
```

### 8.2 引入 vue 依赖

```js
import vue from 'vue'
```