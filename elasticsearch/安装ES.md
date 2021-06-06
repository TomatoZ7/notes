# Elasticsearch 的安装

官网：https://www.elastic.co/cn/

windows 下直接下载 ES zip 包即可。

下载完成后解压到指定目录即可。

## 目录

```
bin     启动文件
config  配置文件
    log4j2.properties   日志配置文件
    jvm.options         java 虚拟机相关的配置
    elasticsearch.yml   ES 的配置文件 (默认 9200 端口)
lib     相关 jar 包
logs    日志
modules 功能模块
plugins 插件
```

## 启动

双击 bin/elasticsearch.bat

可以看到默认启用端口 9200

![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/ESinstall1.png)

## 访问测试

浏览器输入 `http://127.0.0.1:9200/`:

![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/ESinstall2.png)

## 安装可视化界面(需要 Node 环境)

下载地址：https://github.com/mobz/elasticsearch-head

解压后 `cd elasticsearch-head`，`npm install`，`npm run start`：

![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/ESinstall3.png)

这个时候连接会提示跨域，需要在 ES 的 config/elasticsearch.yml 添加上：

```
# 开启跨域
http.cors.enabled: true
# 允许所有
http.cors.allow-origin: "*"
```

重启 ES 并重新连接即可。

## 创建索引

新建索引(可以当做是 MySQL 里的表)

![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/ESinstall4.png)