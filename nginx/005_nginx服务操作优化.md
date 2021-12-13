# Nginx 配置成系统服务

正常来看，如果想要启动、关闭或重新加载 nginx 配置文件，都需要先进入到 nginx 的安装目录的 sbin 目录，然后使用 nginx 的二级制可执行文件来操作，相对来说操作比较繁琐。

另外也想把 Nginx 设置成随着服务器启动就自动完成启动操作。

## Nginx 配置成系统服务

### 1、在 `/usr/lib/systemd/system` 目录下添加 nginx.service，内容如下:

```
vim /usr/lib/systemd/system/nginx.service
```

```
[Unit]
Description=nginx web service
Documentation=http://nginx.org/en/docs/
After=network.target

[Service]
Type=forking
PIDFile=/usr/local/nginx/logs/nginx.pid
ExecStartPre=/usr/local/nginx/sbin/nginx -t -c /usr/local/nginx/conf/nginx.conf
ExecStart=/usr/local/nginx/sbin/nginx
ExecReload=/usr/local/nginx/sbin/nginx -s reload
ExecStop=/usr/local/nginx/sbin/nginx -s stop
PrivateTmp=true

[Install]
WantedBy=default.target
```

### 2、添加完成后如果权限有问题需要进行权限设置

```
chmod 755 /usr/lib/systemd/system/nginx.service
```

### 3、使用系统命令来操作 Nginx 服务

```
启动: systemctl start nginx
停止: systemctl stop nginx
重启: systemctl restart nginx
重新加载配置文件: systemctl reload nginx
查看nginx状态: systemctl status nginx
开机启动: systemctl enable nginx
```

## Nginx命令配置到系统环境

将 `xxx/nginx/sbin/nginx` 二进制可执行文件加入到系统的环境变量，这样的话在任何目录都可以使用 `nginx` 对应的相关命令。

### 1、修改 `/etc/profile` 文件

```
vim /etc/profile
在最后一行添加
export PATH=$PATH:/usr/local/nginx/sbin
```

### 2、使之立即生效

```sh
source /etc/profile
```

### 3、执行 nginx 命令

```sh
nginx -V
```