# docker 部署 es

elasticsearch 文档：[https://hub.docker.com/_/elasticsearch](https://hub.docker.com/_/elasticsearch)

## 一步拉取运行

Run Elasticsearch:

```bash
[root@tz7 ~]# docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.13.3
```

> 注意 : es 十分耗内存，可能会让整个服务器处于十分卡顿的状态。如果卡顿，可以先 stop 其他容器。

查看 es 是否部署成功：

```bash
[root@tz7 ~]# docker ps
CONTAINER ID   IMAGE                  COMMAND                  CREATED             STATUS             PORTS                                            NAMES
ccd6274c2598   elasticsearch:7.13.3   "/bin/tini -- /usr/l…"   9 minutes ago       Up 9 minutes       0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp   elasticsearch
[root@tz7 ~]# curl localhost:9200
{
  "name" : "ccd6274c2598",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "lrUNsWbNQBKgXj2DYLQsSA",
  "version" : {
    "number" : "7.13.3",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "5d21bea28db1e89ecc1f66311ebdec9dc3aa7d64",
    "build_date" : "2021-07-02T12:06:10.804015202Z",
    "build_snapshot" : false,
    "lucene_version" : "8.8.2",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

`docker stats` 查看内存：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_es1.png)

## 增加 es 内存限制

### 1、先停止 es 容器运行

```bash
[root@tz7 ~]# docker ps
CONTAINER ID   IMAGE                  COMMAND                  CREATED          STATUS          PORTS                                            NAMES
ccd6274c2598   elasticsearch:7.13.3   "/bin/tini -- /usr/l…"   39 minutes ago   Up 39 minutes   0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp   elasticsearch
[root@tz7 ~]# docker stop ccd6274c2598
ccd6274c2598
```

### 2、增加 es 内存限制

通过 `-e` 修改环境配置：

```bash
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms64m -Xmx512m" elasticsearch:7.13.3
```

```bash
[root@tz7 ~]# docker ps
CONTAINER ID   IMAGE                  COMMAND                  CREATED         STATUS         PORTS                                            NAMES
908036a6731f   elasticsearch:7.13.3   "/bin/tini -- /usr/l…"   2 minutes ago   Up 2 minutes   0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp   elasticsearch02
[root@tz7 ~]# docker stats 908036a6731f
```

可以看到内存占用明显减少：

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_es2.png)

