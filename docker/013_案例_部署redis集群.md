#   Redis 集群部署案例

## 一、创建网络

```bash
docker network create redis --subnet 172.38.0.0/16
```

## 二、通过脚本创建 redis 服务

### 生成集群配置文件

```bash
for port in $(seq 1 6); \
do \
mkdir -p /mydata/redis/node-${port}/conf
touch /mydata/redis/node-${port}/conf/redis.conf
cat << EOF >/mydata/redis/node-${port}/conf/redis.conf
port 6379
bind 0.0.0.0
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
cluster-announce-ip 172.38.0.1${port}
cluster-announce-port 6379
cluster-announce-bus-port 16379
appendonly yes
EOF
done
```

### 开启服务

```bash
docker run -p 6371:6379 -p 16371:16379 --name redis-1 \
-v /mydata/redis/node-1/data:/data \
-v /mydata/redis/node-1/conf/redis.conf:/etc/redis/redis.conf \
-d --net redis --ip 172.38.0.11 redis:5.0.9-alpine3.11 redis-server /etc/redis/redis.conf
```

```bash
docker run -p 6376:6379 -p 16376:16379 --name redis-6 \
-v /mydata/redis/node-6/data:/data \
-v /mydata/redis/node-6/conf/redis.conf:/etc/redis/redis.conf \
-d --net redis --ip 172.38.0.16 redis:5.0.9-alpine3.11 redis-server /etc/redis/redis.conf
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_redis_cluster1.png)

### 三、开启集群

### 进入容器

```bash
docker exec -it redis-1 /bin/sh
```

### 创建集群

```bash
redis-cli --cluster create 172.38.0.11:6379 172.38.0.12:6379 172.38.0.13:6379 172.38.0.14:6379 172.38.0.15:6379 172.38.0.16:6379 --cluster-replicas 1
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker_redis_cluster2.png)

### 四、查看集群信息

```bash
cluster info

cluster nodes
```