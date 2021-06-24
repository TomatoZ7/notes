# redis 安装

1、[下载安装包](https://download.redis.io/releases/redis-6.2.4.tar.gz?_ga=2.186097135.77163520.1624508559-1358672819.1624508559)

2、把安装到丢到服务器上。

3、`tar -zvxf redis-6.2.4.tar.gz` (文件名具体看你下载的版本)。

4、进入解压后的文件夹，可以看到 redis 的配置文件：

```shell
cd redis-6.2.4
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_inst1.jpg)

5、基本的环境安装

```sh
yum install gcc-c++

make

make install
```

6、redis 默认安装路径 `/usr/local/bin`

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_inst2.jpg)

7、将配置文件拷贝到默认安装路径 

```sh
mkdir myredisconf

cp /www/server/redis-6.2.4/redis.conf myredisconf/
```

8、redis 默认不是后台启动方式，修改 redis.conf 将其改为后台启动

```conf
daemonize yes
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_inst3.jpg)

9、启动 redis 服务

通过制定的配置文件启动服务

```sh
redis-server myredisconf/redis.conf
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_inst4.jpg)

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/redis_inst5.jpg)

10、进入 cli 模式

```sh
redis-cli
```

11、退出

```sh
shutdown

exit
```