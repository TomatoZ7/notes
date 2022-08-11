# mysql 连接超时报错解决

## 背景
2021-01-28 最近使用 ssm 框架进行开发时长时间无操作服务端会500：
>   com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: The last packet successfully received from the server was 54,337,996 milliseconds ago.  The last packet sent successfully to the server was 54,337,997 milliseconds ago. is longer than the server configured value of 'wait_timeout'. You should consider either expiring and/or testing connection validity before use in your application, increasing the server configured values for client timeouts, or using the Connector/J connection property 'autoReconnect=true' to avoid this problem

&emsp;

## 报错原因
MySQL 服务器默认的 `wait_timeout` 是 28800 秒即 8 小时，意味着如果一个连接的空闲时间超过 8 小时，MySQL 将自动断开连接，而连接池却认为该连接还是有效的（因为并未校验连接的有效性），当应用申请使用该连接时，就会报上述错误。

&emsp;

## 解决
### mysql5 以下的版本
在 jdbc 的 url 中加入 `autoReconnect=true` 属性，如：
```properties
jdbc:mysql://localhost:3306/database_name?autoReconnect=true
```

### mysql5.0 以上版本
修改 `my.ini` 里面的 `wait_timeout` 为最大时间，如：
```ini
[mysqld]
wait_timeout=31536000
interactive_timeout=31536000
```

### 最后重启 mysql 服务，重启 tomcat，让配置文件生效

&emsp;

## 其他解决方案（未验证）
上述方案可能会存在系统久置不用再次访问仍然会报错的情况，这里放上其他同学分享的两个永久解决方案：
### 1、减少连接池内连接的生存周期
```xml
<bean id="dataSource"  class="com.mchange.v2.c3p0.ComboPooledDataSource">       
    <property name="maxIdleTime" value="1800"/>    
    <!--other properties -->    
</bean> 
```

### 2、定期使用连接池中的连接，使其不失效
```xml
<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">    
    <property name="preferredTestQuery" value="SELECT 1"/>    
    <property name="idleConnectionTestPeriod" value="18000"/>    
    <property name="testConnectionOnCheckout" value="true"/>    
</bean>  
```