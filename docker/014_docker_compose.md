# Docker Compose

## [一、简介](https://docs.docker.com/compose/)

在之前使用 docker 的时候每个镜像都需要编写 dockerfile 文件去 build，每个容器都需要手动去 run 镜像生成，而如果在生产中，容器量一大，这些操作也会变得非常繁琐。

所以就需要 Docker Compose 来定义运行多个容器并轻松高效地管理它们。

官方文档：

> Compose is a tool for defining and running multi-container Docker applications. With Compose, you use a YAML file to configure your application’s services. Then, with a single command, you create and start all the services from your configuration. To learn more about all the features of Compose, see [the list of features](https://docs.docker.com/compose/#features).
>
> Compose works in all environments: production, staging, development, testing, as well as CI workflows. You can learn more about each case in [Common Use Cases](https://docs.docker.com/compose/#common-use-cases).
>
> Using Compose is basically a three-step process:
>
>   1. Define your app’s environment with a `Dockerfile` so it can be reproduced anywhere.
>
>   2. Define the services that make up your app in `docker-compose.yml` so they can be run together in an isolated environment.
>
>   3. Run `docker compose up` and the [Docker compose command](https://docs.docker.com/compose/cli-command/) starts and runs your entire app. You can alternatively run docker-compose up using the docker-compose binary.

提炼出几点：

1、Compose 是 Docker 官方的开源项目，需要安装。

2、编写 Dockerfile 通过 `docker compose up` 让程序可以在任何地方运行。

3、docker-compose.yml 示例：

```yml
version: "3.9"  # optional since v1.27.0
services:
  web:
    build: .
    ports:
      - "5000:5000"
    volumes:
      - .:/code
      - logvolume01:/var/log
    links:
      - redis
  redis:
    image: redis
volumes:
  logvolume01: {}
```

4、service : 服务，例如一个 redis 服务。

5、app : 项目，包含了一组关联的容器。

## [二、安装](https://docs.docker.com/compose/install/)

#### 1、下载

```bash
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 使用国内镜像
sudo curl -L "https://get.daocloud.io/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" > /usr/local/bin/docker-compose
```

#### 2、授权

```bash
sudo chmod +x /usr/local/bin/docker-compose
```

## [三、开始体验](https://docs.docker.com/compose/gettingstarted/)

### 官方案例：通过 python 的 Flask 框架和 Redis 实现一个 web 计数器

#### 1、安装

1) 创建项目目录

```bash
mkdir composetest

cd composetest
```

2) 创建 `app.py` 文件

```py
import time

import redis
from flask import Flask

app = Flask(__name__)
cache = redis.Redis(host='redis', port=6379)

def get_hit_count():
    retries = 5
    while True:
        try:
            return cache.incr('hits')
        except redis.exceptions.ConnectionError as exc:
            if retries == 0:
                raise exc
            retries -= 1
            time.sleep(0.5)

@app.route('/')
def hello():
    count = get_hit_count()
    return 'Hello World! I have been seen {} times.\n'.format(count)
```

3) 创建 `requirements.txt` 文件

```txt
flask
redis
```

#### 2、创建 Dcokerfile

```bash
FROM python:3.7-alpine
WORKDIR /code
ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0
RUN apk add --no-cache gcc musl-dev linux-headers
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
EXPOSE 5000
COPY . .
CMD ["flask", "run"]
```

#### 3、通过 compose 文件定义服务

创建 `docker-compose.yml` 文件

```yml
version: "3.9"
services:
  web:
    build: .
    ports:
      - "5000:5000"
  redis:
    image: "redis:alpine"
```

#### 4、通过 compose 构建和运行项目

在当前文件夹下，通过运行 `docker-compose.yml` 开启项目。

```bash
docker-compose up
```

![image](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/docker-compose1.png)

#### 5、查看网络

发现会创建一个默认的网络：

```bash
[root@tz7 ~]# docker network ls
NETWORK ID     NAME                  DRIVER    SCOPE
d4d36e76eb2b   composetest_default   bridge    local
```