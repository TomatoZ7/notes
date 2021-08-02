# Vue CLI

如果开发大型项目，需要考虑代码目录结构、项目结构和部署、热加载、代码单元测试等，为了更高效的完成这些工作，我们需要**脚手架工具**来帮助我们。

## 1 CLI

CLI : Command-Line Interface，翻译为命令行界面，俗称**脚手架**。

Vue CLI 是一个官方发布的针对 `vuejs` 开发的项目。

使用 `vue-cli` 可以快速搭建 `Vue` 开发环境以及对应的 `webpack` 配置。

## 2 环境

### 2.1 Node 环境

`Node` 版本至少 `8.9` 以上。

### 2.2 Webpack

## 3 Vue CLI 的使用

### 3.1 安装 Vue 脚手架

```shell
npm install -g @vue/cli
```

注意：上面安装的是 Vue CLI3 的版本，是不能够按 Vue CLI2 的方式初始化项目的。

### 3.2 拉取 2.x 模板

```shell
npm install @vue/cli-init -g
```

### 初始化项目

CLI2 : `vue init webpack my-project`

CLI3 : `vue create my-project`