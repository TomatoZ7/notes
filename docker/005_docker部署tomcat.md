# docker 安装 tomcat

## 一、搜索镜像

```shell
[root@tz7 ~]# docker search tomcat --filter=STARS=500
NAME      DESCRIPTION                                     STARS     OFFICIAL   AUTOMATED
tomcat    Apache Tomcat is an open source implementati…   3068      [OK]
```

## 二、拉取镜像

```shell
[root@tz7 ~]# docker pull tomcat
```

## 三、运行测试

```shell
[root@tz7 ~]# docker images
REPOSITORY    TAG       IMAGE ID       CREATED        SIZE
tomcat        latest    36ef696ea43d   7 days ago     667MB

[root@tz7 ~]# docker run -d --name tomcat01 -p 23355:8080 tomcat
63d532a0a3c7ad4355522872f1406c0702c078610a7aae868720f266b65e1465
```

这时候在阿里云安全组和 linux 防火墙端口都开放的情况下我们访问 `ip:3355` 却提示 `404`：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_tomcat1.png)

这是因为阿里云镜像默认是最小的镜像，其他所有不必要的都剔除(保证最小可运行的环境)：

```shell
# 可以看到部分命令是没有的，印证我们的猜想
[root@tz7 ~]# docker ps
CONTAINER ID   IMAGE     COMMAND                  CREATED          STATUS          PORTS                     NAMES
63d532a0a3c7   tomcat    "catalina.sh run"        4 minutes ago    Up 4 minutes    0.0.0.0:23355->8080/tcp   tomcat01
[root@tz7 ~]# docker exec -it 63d532a0a3c7 /bin/bash
root@63d532a0a3c7:/usr/local/tomcat# ll
bash: ll: command not found

# 可以看到 webapps 下目录是空的
root@63d532a0a3c7:/usr/local/tomcat# ls
BUILDING.txt  CONTRIBUTING.md  LICENSE	NOTICE	README.md  RELEASE-NOTES  RUNNING.txt  bin  conf  lib  logs  native-jni-lib  temp  webapps  webapps.dist  work
root@63d532a0a3c7:/usr/local/tomcat# ls webapps
root@63d532a0a3c7:/usr/local/tomcat# ls webapps.dist
ROOT  docs  examples  host-manager  manager

# 将 webapps.dist 下的所有内容都复制到 webapps 下，再刷新访问就可以了
root@63d532a0a3c7:/usr/local/tomcat# cp -rf webapps.dist/* webapps/
```