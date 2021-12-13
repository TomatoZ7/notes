# Portainer 安装

Portainer 是 docker 的图形化界面管理工具，提供一个后台面板供我们处理。

```bash
docker run -d -p 8088:9000 \
--restart=always -v /var/run/docker.sock:/var/run/docker.sock --privileged=true portainer/portainer
```

测试访问：ip:8088

进去之后会先创建一个 user，然后选择 local：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_portainer1.png)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_portainer2.png)