# docker 安装 nginx

## 一、搜索镜像

```shell
[root@tz7 ~]# docker search nginx
NAME                              DESCRIPTION                                     STARS     OFFICIAL   AUTOMATED
nginx                             Official build of Nginx.                        15141     [OK]       
jwilder/nginx-proxy               Automated Nginx reverse proxy for docker con…   2041                 [OK]
richarvey/nginx-php-fpm           Container running Nginx + PHP-FPM capable of…   816                  [OK]
```

## 二、拉取镜像

```shell
[root@tz7 ~]# docker pull nginx
```

## 三、运行测试

```shell
[root@tz7 ~]# docker images
REPOSITORY    TAG       IMAGE ID       CREATED        SIZE
nginx         latest    4cdc5dd7eaad   3 days ago     133MB

# -d : 后台运行
# --name : 给容器取名
# -p 宿主机端口:容器端口
[root@tz7 ~]# docker run -d --name nginx01 -p 3344:80 nginx
a600218ea730a1d2e2653144eef0984c0d8935a44e9ea95a243eafb75a390392

[root@tz7 ~]# docker ps
CONTAINER ID   IMAGE     COMMAND                  CREATED         STATUS         PORTS                   NAMES
a600218ea730   nginx     "/docker-entrypoint.…"   3 seconds ago   Up 2 seconds   0.0.0.0:3344->80/tcp   nginx01

[root@tz7 ~]# curl localhost:3344
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```

## 四、进入容器

```shell
[root@tz7 ~]# docker ps
CONTAINER ID   IMAGE     COMMAND                  CREATED          STATUS          PORTS                   NAMES
a600218ea730   nginx     "/docker-entrypoint.…"   24 minutes ago   Up 24 minutes   0.0.0.0:23344->80/tcp   nginx01
[root@tz7 ~]# docker exec -it a600218ea730 /bin/bash
root@a600218ea730:/# ls
bin  boot  dev	docker-entrypoint.d  docker-entrypoint.sh  etc	home  lib  lib64  media  mnt  opt  proc  root  run  sbin  srv  sys  tmp  usr  var
root@a600218ea730:/# whereis nginx
nginx: /usr/sbin/nginx /usr/lib/nginx /etc/nginx /usr/share/nginx
root@a600218ea730:/# cd /etc/nginx/
root@a600218ea730:/etc/nginx# ls
conf.d	fastcgi_params	mime.types  modules  nginx.conf  scgi_params  uwsgi_params
```