# 远程连接 MySQL 问题记录

## 1、确认账号密码

## 2、确认端口是否打开(阿里云安全组、linux 防火墙端口)

## 3、根据返回状态码

### 权限问题

> 1045 - Access denied for user 'root'@'::1' (using password: YES)

```sql
use mysql;

select host,user from user;

# 格式 : grant 权限 on 数据库名.表名 to 用户@登录主机 identified by "用户密码";
# @ 后面是访问 MySQL 的客户端 IP 地址（或主机名） % 代表任意的客户端，如果填写 localhost 为本地访问（那此用户就不能远程访问该mysql数据库了）。
GRANT ALL PRIVILEGES ON *.* TO root@"%" IDENTIFIED BY "abc123456"; 
```