# GitFlow 概述

## GitFlow 的常用分支

### master

主分支，产品的功能全部实现后，最终在 master 分支对外发布。

该分支为只读唯一分支，只能从其它分支 (release/hotfix) 合并，不能在此分支上修改。

另外所有在 master 分支的推送应该打标签做记录，方便追溯。

例如 release 合并到 master，或 hotfix 合并到 master。

### develop

主开发分支，基于 master 分支克隆。

包含所有要发布到下一个 release 的代码。

该分支为只读唯一分支，只能从其他分支合并。

feature 功能分支完成，合并到 develop (不推送)。

develop 拉取 release 分支，提测。

release/hotfix 分支上线完毕，合并到 develop 并推送。

### feature

功能开发分支，基于 develop 分支克隆，主要用于新需求新功能的开发。

功能开发完毕后合到 develop 分支(未正式上线之前不推送到远程中央仓库)。

feature 分支可同时存在多个，用于团队多个功能同时开发，属于临时分支，功能完成后可选删除。

### release

测试分支，基于 feature 分支合并到 develop 之后，从 develop 分支克隆。

主要用于提交给测试人员进行功能测试，测试过程中发现的 bug 在本分支修复，修复完成上线后合并到 develop/master 分支并推送(完成功能)，打 tag。

属于临时分支，功能上线后可选删除。

### hotfix

补丁分支，基于 master 分支克隆，主要用于对线上的版本进行 bug 修复。

修复完毕后合并到 develop/master 分支并推送，打 tag。

属于临时分支，补丁修复上线后可选删除。

所有 hotfix 分支的修改会进入到下一个 release。

## 主要工作流程

1. 初始化项目为 gitflow，默认创建 master 分支，然后从 master 拉取第一个 develop 分支。

2. 从 develop 分支进行编码开发(多个开发人员拉取多个 feature 同时进行开发，互不影响)。

3. feature 分支完成后，合并到 develop (不推送，feature 功能完成还未提测，推送后会影响到其他功能分支的开发)。

合并 feature 到 develop，可以选择删除当前 feature，也可以不删除。但当前 feature 分支就不可更改了，必须从 release 分支继续编码修改。

4. 从 develop 拉取 release 分支进行提测，提测过程中在 release 分支上修改 bug。

5. release 分支上线后，合并 release 分支到 develop/master 并推送。

合并之后，可选删除当前 release 分支，若不删除，则当前 release 不可修改。线上有问题也必须从 master 拉取 hotfix 分支进行修改。

6. 上线之后若发现线上 bug，从 master 分支拉取 hotfix 进行 bug 修改。

7. hotfix 通过测试上线后，合并 hotfix 分支到 develop/master 并推送。

合并之后，可选删除当前 hotfix，若不删除，则当前 hotfix 不可修改，若补丁未修复，需要从 master 拉取新的 hotfix 继续修改。

8. 当进行一个 feature 时，若 develop 分支有变动，如其他开发人员完成功能并上线，则需要将完成的功能合并到自己分支上。

即合并 develop 到当前 feature 分支。

9. 当进行一个 release 分支时，若 develop 有变动，如其他开发人员完成功能并上线，则需要将完成的功能合并到自己分支上。

即合并 develop 到当前 release 分支(因为当前 release 分支通过测试后会发布到线上，如果不合并最新的 develop 分支，就会发生丢代码的情况)。

引用大神的 GitFlow 工作流程图：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/GitFlow.png)

[传送门 : GitFlow详解教程](https://blog.csdn.net/xingbaozhen1210/article/details/81386269)
