# Git 基础 - tag 的使用

- [Git 基础 - tag 的使用](#git-基础---tag-的使用)
  - [1.创建 tag](#1创建-tag)
    - [1.1 附注 tag](#11-附注-tag)
    - [1.2 轻量 tag](#12-轻量-tag)
    - [1.3 在特定的提交记录打 tag](#13-在特定的提交记录打-tag)
    - [1.4 将 tag 推送到远程仓库](#14-将-tag-推送到远程仓库)
  - [2.查看 tag](#2查看-tag)
    - [2.1 列表](#21-列表)
    - [2.2 单个 tag](#22-单个-tag)
  - [3.删除 tag](#3删除-tag)
    - [3.1 本地删除](#31-本地删除)
    - [3.2 远程删除](#32-远程删除)
    - [3.3 删除 tag 所在的分支，会有什么影响？](#33-删除-tag-所在的分支会有什么影响)
  - [4.检出 tag](#4检出-tag)
  - [5.参考](#5参考)

Git 的 tag 功能是一个非常有用的工具，它可以帮助开发者在代码仓库中标记重要的里程碑和版本号。

Tag 可以看作是一个快照，用于永久性地标记提交记录。它们通常用于发布新版本或里程碑，并且可以帮助开发者追踪项目的历史记录。Tag 在 Git 中是轻量级对象，不会像分支那样占用额外的存储空间，因此创建 Tag 对存储的影响很小。

## 1.创建 tag

Git 支持两种 tag：轻量 tag（lightweight）与附注 tag（annotated）。

轻量 tag 很像一个不会改变的分支——它只是某个特定提交的引用。

而附注 tag 是存储在 Git 数据库中的一个完整对象，它们是可以被校验的，其中包含打 tag 者的名字、电子邮件地址、日期时间，此外还有一个 tag 信息，并且可以使用 GNU Privacy Guard（GPG）签名并验证。 通常会建议创建附注 tag，这样你可以拥有以上所有信息。但是如果你只是想用一个临时的 tag，或者因为某些原因不想要保存这些信息，那么也可以用轻量 tag。

### 1.1 附注 tag

要创建一个附注 tag，只需要你在执行 `tag` 命令时指定 `-a` 选项：

```sh
$ git tag -a v1.2.0 -m "version 1.2.0"
```

`-m` 选项指定了一条将会存储在 tag 中的信息。如果没有为附注 tag 指定一条信息，Git 会启动编辑器要求你输入信息。

通过使用 `git show` 命令可以看到 tag 信息和与之对应的提交信息：

```sh
$ git show v1.2.0
tag v1.2.0
Tagger: Oliver Zhou <my-email.com>
Date:   Mon May 15 16:47:50 2023 +0800

version 1.2.0

commit 1a3870e3f700e98697d8fd5cc5f9835e5c78ecfc (HEAD -> main, tag: v1.2.0, origin/main)
Author: Oliver Zhou <my-email.com>
Date:   Mon May 15 16:47:01 2023 +0800

    feat:func IsPalindrome

diff --git a/pkg/string.go b/pkg/string.go
```

输出显示了打 tag 者的信息、打 tag 的日期时间、附注信息，然后显示具体的提交信息。

### 1.2 轻量 tag

另一种给提交打 tag 的方式是使用轻量 tag。轻量 tag 本质上是将提交校验和存储到一个文件中——没有保存任何其他信息。创建轻量 tag，不需要使用 `-a`、`-s` 或 `-m` 选项，只需要提供 tag 名字：

```sh
$ git tag v1.3.0
```

这时，如果在 tag 上运行 `git show`，你不会看到额外的 tag 信息。命令只会显示出提交信息：

> 这里相比官方文档提供的例子多出了 diff 后面的一段，可能是版本更新的原因。

```sh
$ git show v1.3.0
commit 3c5155f9cd640658bfb1f8ef87d141fa5e6f8332 (HEAD -> main, tag: v1.3.0, origin/main)
Author: Oliver Zhou <my-email.com>
Date:   Mon May 15 17:15:11 2023 +0800

    feat:func FindMax

diff --git a/pkg/slice.go b/pkg/slice.go
new file mode 100644
index 0000000..f94db46
--- /dev/null
+++ b/pkg/slice.go
@@ -0,0 +1,11 @@
+package pkg
```

### 1.3 在特定的提交记录打 tag

你也可以对过去的提交打 tag。假设提交历史是这样的：

```sh
$ git log --pretty=oneline
15027957951b64cf874c3557a0f3547bd83b3ff6 Merge branch 'experiment'
a6b4c97498bd301d84096da251c98a07c7723e65 beginning write support
0d52aaab4479697da7686c15f77a3d64d9165190 one more thing
6d52a271eda8725415634dd79daabbc4d9b6008e Merge branch 'experiment'
0b7434d86859cc7b8c3d5e1dddfed66ff742fcbc added a commit function
4682c3261057305bdd616e23b64b0857d832627b added a todo file
166ae0c4d3f420721acbb115cc33848dfcc2121a started write support
9fceb02d0ae598e95dc970b74767f19372d61af8 updated rakefile
964f16d36dfccde844893cac5b347e7b3d44abbc commit the todo
8a5cbc430f1a9c3d00faaeffd07798508422908a updated readme
```

现在，假设在 v1.2 时你忘记给项目打 tag，也就是在 `updated rakefile` 提交。你可以在之后补上 tag。要在那个提交上打 tag，你需要在命令的末尾指定提交的校验和（或部分校验和）：

```sh
$ git tag -a v1.2 9fceb02
```

### 1.4 将 tag 推送到远程仓库

默认情况下，git push 命令并不会传送 tag 到远程仓库服务器上。在创建完 tag 后你必须显式地推送 tag 到共享服务器上。这个过程就像共享远程分支一样——你可以运行 `git push origin <tagname>`。

```sh
$ git push origin v1.3.0
```

如果要推送所有远端不存在的 tag，可以使用：

```sh
$ git push <remote> --tags
```

> 注意：使用 `git push <remote> --tags` 推送 tag 并不会区分轻量 tag 和附注 tag，没有简单的选项能够让你只选择推送一种 tag。

## 2.查看 tag

### 2.1 列表

全部查看：

```sh
$ git tag
```

按特定模式进行查找。例如，Git 自身的源代码仓库包含 tag 的数量超过 500 个。如果只对 1.8.5 系列感兴趣，可以运行：

```sh
$ git tag -l "v1.8.5*"
v1.8.5
v1.8.5-rc0
v1.8.5-rc1
v1.8.5-rc2
v1.8.5-rc3
v1.8.5.1
v1.8.5.2
v1.8.5.3
v1.8.5.4
v1.8.5.5
```

> 注意：
> 
> 按照通配符列出 tag 需要 `-l` 或 `--list` 选项
> 
> 如果你只想要完整的 tag 列表，那么运行 git tag 就会默认假定你想要一个列表，它会直接给你列出来， 此时的 -l 或 --list 是可选的。
> 
> 然而，如果你提供了一个匹配 tag 名的通配模式，那么 `-l` 或 `--list` 就是强制使用的。

### 2.2 单个 tag

要查看单个 tag 的信息，可以使用：

```sh
$ git show v1.3.0
```

## 3.删除 tag

### 3.1 本地删除

```sh
$ git tag -d <tagname>
```

### 3.2 远程删除

```sh
$ git push origin :refs/tags/<tagname>
```

上面这种操作的含义是，将冒号前面的空值推送到远程 tag 名，从而高效地删除它。

第二种更直观的删除远程 tag 的方式是：

```sh
$ git push origin --delete <tagname>
```

### 3.3 删除 tag 所在的分支，会有什么影响？

正如前文所说，tag 的存储和分支的存储是互相独立的，所以删除包含某个 tag 的分支，通常不会导致代码本身出现问题。

但是该 tag 会成为孤儿 tag（orphaned tag），它不再与任何分支关联。这可能会对项目管理和版本控制造成混淆，也可能会影响其他正在用该 tag 进行部署或测试的工作。

## 4.检出 tag

如果你想查看某个 tag 所指向的文件版本，可以使用 `git checkout` 命令， 虽然这会使你的仓库处于“分离头指针（detached HEAD）”的状态——这个状态有些不好的副作用：

```sh
$  git checkout v1.3.0              
注意：正在切换到 'v1.3.0'。

您正处于分离头指针状态。您可以查看、做试验性的修改及提交，并且您可以在切换
回一个分支时，丢弃在此状态下所做的提交而不对分支造成影响。

如果您想要通过创建分支来保留在此状态下所做的提交，您可以通过在 switch 命令
中添加参数 -c 来实现（现在或稍后）。例如：

  git switch -c <新分支名>

或者撤销此操作：

  git switch -

通过将配置变量 advice.detachedHead 设置为 false 来关闭此建议

HEAD 目前位于 3c5155f feat:func FindMax
```

在“分离头指针”状态下，如果你做了某些更改然后提交它们，tag 不会发生变化，但你的新提交将不属于任何分支，并且将无法访问，除非通过确切的提交哈希才能访问。因此，如果你需要进行更改，比如你要修复旧版本中的错误，那么通常需要创建一个新分支：

```sh
$ git checkout -b version2 v1.3.0
Switched to a new branch 'version2'
```

如果在这之后又进行了一次提交，`version2` 分支就会因为这个改动向前移动，此时它就会和 v1.3.0 tag 稍微有些不同，这时就要当心了。

## 5.参考

[官方文档 - 2.6 Git Basics - Tagging](https://git-scm.com/book/en/v2/Git-Basics-Tagging)