# Go hand off 机制

## 1.概念

也称为 P 分离机制，当本线程 M 因为 G 进行的系统调用阻塞时，线程释放绑定的 P，把 P 转移给其他空闲的 M 执行，也提高了线程利用率（避免站着茅坑不拉shi）。

## 2.分离流程

当前线程 M 阻塞时，释放 P，给其它空闲的 M 处理。

![hand-off](../Images/hand-off.png)