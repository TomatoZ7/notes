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