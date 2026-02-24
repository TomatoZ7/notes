# Cursor 1.0 ChangeLog

官方 changelog: [https://www.cursor.com/cn/changelog/1-0](https://www.cursor.com/cn/changelog/1-0)

## 1.新功能/特性

### BugBot 自动化 code review

BugBot 能够自动化审查你在 GitHub 上的 Pull Reuqest(PR)，捕捉潜在的错误和问题。

每当 PR 更新时，BugBot 会自动重新运行并留下潜在问题的评论，也可以在 PR 的评论中输入 `bugbot run` 来手动运行 BugBot。点击 `Fix in Cursor` 会直接跳转到 Cursor 编辑器，并预加载完整上下文。

<video src = "./resource/cursor1-0-bug-bot.mp4" controls>

BugBot 从安装时起提供 7 天免费期，之后的收费和 Cursor Max 模式相同。

更多的可以查阅文档：[https://docs.cursor.com/bugbot](https://docs.cursor.com/bugbot)

### Background Agent 面向所有用户开放

Background Agent 是 Cursor 的远程编程代理，它能够在远程后台处理多项任务，让开发者专注于核心开发工作。

你需要先关闭隐私模式，然后就可以通过点击聊天界面中的云图标或按下 `Cmd/Ctrl+E` 快捷键立即开始使用。对于启用了隐私模式的用户，Cursor 很快也会提供启用方式。

当你在对话框输入任务后，Background Agent 就会在后台执行，并且可以点击查看它的运行情况。

![](./resource/cursor1-0-bg-agent.avif)

### Agent 支持 Jupyter Notebooks

> Jupyter Notebook是基于网页的用于交互计算的应用程序。其可被应用于全过程计算：开发、文档编写、运行代码和展示结果。  -- [Jupyter 官方文档](https://jupyter-notebook.readthedocs.io/en/stable/notebook.html)

简单来说，Jupyter Notebook 是以网页的形式打开，可以在网页页面中直接编写代码和运行代码，代码的运行结果也会直接在代码块下显示的程序。如在编程过程中需要编写说明文档，可在同一个页面中直接编写，便于作及时的说明和解释。它支持多种编程语言，最常用的是 Python。

<video src = "./resource/cursor1-0-jupyter-notebooks-web.mp4" controls>

## Memoryies 

Memoryies 以项目为单位在个人层面进行存储，目前还在 Beta 阶段。可以在 Settings -> Rules 进行管理。这一功能特别适合需要长期维护项目的用户，能够避免重复错误，提供个性化的编程体验。

Memoryies 一般是自动化存储的，cursor 会自己判断是否需要记忆。也可以在对话过程中主动要求它记下来。

<video src = "./resource/cursor1-0-memories-web.mp4" controls>

### 一键安装 MCP 服务器

一键安装 MCP 服务器，通过 OAuth 认证简化了开发环境的配置流程。

可以直接从[官方 MCP 列表](https://docs.cursor.com/tools)中添加，MCP 开发者也可以给自己服务加上「Add to Cursor」按钮（文档说明见 [docs.cursor.com/deeplinks]([docs.cursor.com/deeplinks](https://docs.cursor.com/deeplinks))）

## 其他改进

### 基础功能

+ 丰富对话可视化效果，比如输出 Mermaid 图表和 Markdown 表格
+ 设置面板优化，可以查看个人或团队的使用情况分析、更新显示名称以及查看按工具或模型细分的详细统计数据
+ @Link 和 web search 现在可以解析PDF并包含在上下文中
+ 设置中增加了网络诊断功能
+ 并行工具调用让响应更快
+ Chat 中的工具调用可以折叠

### 企业级功能

+ 企业用户只能访问稳定版本（无预发布版本）
+ 团队管理员可以禁用隐私模式
+ 提供管理 API 获取使用指标和消费数据

### 模型

+ Gemini 2.5 Flash 现在支持 Max 模式

