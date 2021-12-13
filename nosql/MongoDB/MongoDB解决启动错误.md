# MongoDB 启动错误解决

首先是 `MongoDB` 启动失败：

```shell
$ service mongod start
Redirecting to /bin/systemctl start  mongod.service
Job for mongod.service failed because the control process exited with error code. See "systemctl status mongod.service" and "journalctl -xe" for detail
```

查看错误：

```shell
$ systemctl status mongod.service
● mongod.service - MongoDB Database Server
   Loaded: loaded (/usr/lib/systemd/system/mongod.service; enabled; vendor preset: disabled)
   Active: failed (Result: exit-code) since Sun 2021-10-03 10:44:23 CST; 3min 12s ago
     Docs: https://docs.mongodb.org/manual
  Process: 26488 ExecStart=/usr/bin/mongod $OPTIONS (code=exited, status=14)
  Process: 26486 ExecStartPre=/usr/bin/chmod 0755 /var/run/mongodb (code=exited, status=0/SUCCESS)
  Process: 26483 ExecStartPre=/usr/bin/chown mongod:mongod /var/run/mongodb (code=exited, status=0/SUCCESS)
  Process: 26482 ExecStartPre=/usr/bin/mkdir -p /var/run/mongodb (code=exited, status=0/SUCCESS)
 Main PID: 17383 (code=exited, status=0/SUCCESS)

Oct 03 10:44:23 iZuf61wwjib0gi7cyckz02Z systemd[1]: Starting MongoDB Database Server...
Oct 03 10:44:23 iZuf61wwjib0gi7cyckz02Z mongod[26488]: about to fork child process, waiting until server is ready for connections.
Oct 03 10:44:23 iZuf61wwjib0gi7cyckz02Z mongod[26488]: forked process: 26491
Oct 03 10:44:23 iZuf61wwjib0gi7cyckz02Z systemd[1]: mongod.service: control process exited, code=exited status=14
Oct 03 10:44:23 iZuf61wwjib0gi7cyckz02Z systemd[1]: Failed to start MongoDB Database Server.
Oct 03 10:44:23 iZuf61wwjib0gi7cyckz02Z systemd[1]: Unit mongod.service entered failed state.
Oct 03 10:44:23 iZuf61wwjib0gi7cyckz02Z systemd[1]: mongod.service failed.
```

这一行报错导致 `mongod` 服务无法启动：

```shell
Process: 26488 ExecStart=/usr/bin/mongod $OPTIONS (code=exited, status=14)
```

上网查询结果应该是权限问题，解决：

```shell
$ cd /tmp
$ ls -l *.sock
srwx------ 1 root  root  0 Oct  3 10:30 mongodb-27017.sock
$ chown mongod:mongod mongodb-27017.sock
```

再重启就可以了。