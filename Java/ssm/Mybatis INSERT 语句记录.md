# 总结一下开发中用到的 Mybatis CRUD 操作

## 背景
2021-01-26 最近在学习 SSM 框架，刚开发完一个项目，总结一下其中用到的 SQL 语句。

> 声明： 该版本为读取 XML 文件的 SQL 语句。

## 表结构
### user 
| id | name | gender | phone | create_time |
| :----: | :----: | :----: | :----: | :----: |
| 1 | Tonny | 1 | 13501010202 | 2021-01-01 12:00:00 |
| 2 | James | 2 | 13502020303 | 2021-01-02 12:00:00 |

## 增
### 普通的插入语句
```xml
<insert id="insertUser" parameterType="com.test.domain.User">
    INSERT INTO
        user(name,gender,phone,create_time)
        VALUES(#{name},#{gender},#{phone},#{create_time});
</insert>
```
参数接收类型为 User ,也可以用 Map 类型，只需修改将 ` parameterType="com.test.domain.User" ` 修改为 ` parameterType="Map" ` 即可。

DAO 接口我一般用 int 类型接收，会返回插入的记录数。

### 查询不存在才进行插入操作
```xml
<insert id="insertUser" parameterType="com.test.domain.User">
    INSERT INTO
        user(name,gender,phone,create_time)
        SELECT #{name},#{gender},#{phone},#{create_time}
        FROM DUAL
        WHERE NOT EXISTS
            (SELECT id FROM user WHERE name = #{name});
</insert>
```
其中 `DUAL` 是临时表名称，固定写法。

### 插入后获取最新的 ID 值
```xml
<insert id="insertUser" parameterType="com.test.domain.User">
    <selectKey keyProperty="id" keyColumn="id" resultType="int" order="AFTER">
        SELECT last_insert_id();
    </selectKey>
    INSERT INTO
        user(name,gender,phone,create_time)
        VALUES(#{name},#{gender},#{phone},#{create_time});
</insert>
```
执行完成后会将新插入的 ID set 到参数 user 中，可用 `user.getId()` 获取。

## 删
### 根据主键 ID 删除单条记录
```xml
<delete id="delUserById" parameterType="java.lang.Integer">
    DELETE FROM user WHERE id = #{id};
</delete>
```
### 使用 IN 语句实现批量删除
```xml
<delete id="deleteMulti" parameterType="Map">
    DELETE FROM user WHERE id IN 
        <foreach collection="ids" item="name" index="index" open="(" close=")" separator=",">#{name}</foreach>
</delete>
```
> 注意 ids 为数组类型，查询、更新操作也可使用 foreach 语法，后面不在赘述。

## 改
### 根据条件更新(自定义更新字段)
```xml
<update id="updateUser" parameterType="com.cbh.domain.User">
    UPDATE user
    <set>
        <if test="gender != null">
            gender = #{gender}
        </if>
        <if test="phone != null">
            phone = #{phone}
        </if>
        <if test="name != null">
            name = #{name}
        </if>
    </set>
    WHERE id = #{id};
</update>
```

## 查
### 定义返回 map 
```xml
<resultMap id="user" type="com.cbh.domain.User">
    <result column="id" property="id"/>
    <result column="name" property="name"/>
    <result column="gender" property="gender"/>
    <result column="phone" property="phone"/>
    <result column="create_time" property="create_time"/>
</resultMap>
```


### 带条件查询
```xml

```

### 查询分页并返回总记录数

### 关联查询
首先需要在 `com.domain.test.User` 类下定义好映射关系(如果比较复杂建议重新生成一个类去定义相对应的映射关系)
