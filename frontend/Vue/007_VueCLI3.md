# Vue CLI3

## 1 认识

### 1.1 与 CLI2 的区别

+ vue-cli 3 是基于 webpack 4 打造，vue-cli 2 是 webpack 3

+ vue-cli 3 的设计原则是 "0配置"，移除的配置文件根目录下的 `build` 和 `config` 等目录

+ vue-cli 3 提供了 vue ui 命令，提供了可视化配置，更加人性化

+ 移除了 `static` 文件夹，新增了 `public` 文件夹，并且 `index.html` 移动到 `public` 中

## 2 使用 CLI3 构建项目

### 2.1 命令

```shell
$ vue create vueCLI3Test
```

### 2.2 选择配置模式

```shell
? Please pick a preset: (Use arrow keys)
> Default ([Vue 2] babel, eslint)
  Default (Vue 3) ([Vue 3] babel, eslint)
  Manually select features
```

可以选择默认配置，也可以选择自定义配置，为了看清所有的配置项，这里选择了自定义配置(最后一个)。

### 2.3 选择配置项

```shell
? Check the features needed for your project:
>(*) Choose Vue version
 (*) Babel
 ( ) TypeScript
 ( ) Progressive Web App (PWA) Support
 ( ) Router
 ( ) Vuex
 ( ) CSS Pre-processors
 (*) Linter / Formatter
 ( ) Unit Testing
 ( ) E2E Testing 
```

按键盘空格即可进行勾选。

### 2.4 选择 vue.js 版本

```shell
? Choose a version of Vue.js that you want to start the project with
  2.x
> 3.x
```

### 2.5 额外配置存放位置

```shell
? Where do you prefer placing config for Babel, ESLint, etc.?
> In dedicated config files
  In package.json
```
 
`Babel`、`ESLint` 等配置是重新建一个文件(In dedicated config files)还是在 `package.json` 里。

### 2.6 保存此次配置

```shell
? Save this as a preset for future projects? (y/N) Yes
? Save preset as: tomato
```

保存了以后就可以直接选择自己的配置命名，无需重复选择。

### 2.7 删除配置

在 `用户目录/.vuerc` 文件里的 `presets` 里可以删除。

```json
{
  "useTaobaoRegistry": true,
  "presets": {
    "tomato": {
      "useConfigFiles": true,
      "plugins": {
        "@vue/cli-plugin-babel": {}
      },
      "vueVersion": "2"
    }
  }
}
```

## 3 项目目录

`public` : 一些静态文件、图片可以放在这目录下，打包的时候会原封不动的放进 `dist` 文件夹

`src` : 源代码 

`.browserslistrc` : 关于浏览器的相关配置

`package.json` : 配置的命令、安装的依赖等信息

## 4 图形化管理

```js
vue ui
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/frontend/images/vueCLI3(1).png)

## 5 配置文件查看和修改

配置文件路径 : `node_modules/@vue/cli-service/webpack.config.js`

### 5.1 修改配置

需要在当前项目根目录创建 `vue.config.js` 文件(文件名不能变)：

```js
module.exports = {
    // 配置
}
```

会把 `vue.config.js` 里的配置和默认配置进行合并。