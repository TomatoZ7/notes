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

### operate
| id | user_id | action | create_time |
| :--: | :--: | :--: | :--: | :--: |
| 1 | 1 | login | 2021-01-01 12:00:00 |
| 2 | 1 | logout | 2021-01-02 12:00:00 |git

&emsp;

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

&emsp;

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

&emsp;

## 改
### 根据条件更新(自定义更新字段)
```xml
<update id="updateUser" parameterType="com.test.domain.User">
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

&emsp;

## 查
### 定义返回 map 
```xml
<resultMap id="user" type="com.test.domain.User">
    <result column="id" property="id"/>
    <result column="name" property="name"/>
    <result column="gender" property="gender"/>
    <result column="phone" property="phone"/>
    <result column="create_time" property="create_time"/>
</resultMap>
```
需要注意：
1. `select` 标签中用 `resultMap="user"` 来确定返回map，且如果 `com.test.domain.User` 类中有其他参数也会被一并返回，只是值为 `null`；
2. `resultMap` 中的 `id` 最好保持唯一，否则会报错；
3. `column` 对应 `sql` 语句的查询字段(存在别名的情况)， `property` 对应 `com.test.domain.User` 类的属性，需要有 get/set 方法。

### 用 where + if 标签自定义查询语句
```xml
<select id="queryUser" parameterType="Map" resultMap="user">
    SELECT id FROM user 
    <where>
        <if test="name != null">
            name = #{name}
        </if>
        <if test="phone != null">
            AND phone = #{phone}
        </if>
    </where>
</select>
```

### 查询分页并返回总记录数
分页查询一般返回 **当前列表数据** + **数据总数**，这里我多定义一个 `resultMap` :
```xml
<resultMap id="userCount" type="java.lang.Integer">
    <result column="count"/>
</resultMap>
```
查询：
```xml
<select id="userList" parameterType="Map" resultMap="user, userCount">
    SELECT SQL_CALC_FOUND_ROWS * FROM user LIMIT #{limit} OFFSET #{offset};
    SELECT FOUND_ROWS() as count;
</select>
```
`dao` 层接收：
```xml
List<List<?>> userList(Map<?,?> param);
```
通过 `list.get(int index)` 即可获得相对应的数据。

### 连表查询1
新建一个 `class`，把需要的字段写进去，然后使用 `resultMap` 记录好即可。
```xml
<resultMap id="userOperation" type="com.test.domain.UserOperation">
    <result column="id" property="id"/>
    <result column="name" property="name"/>
    <result column="gender" property="gender"/>
    <!-- 操作记录 --> 
    <result column="user_id" property="user_id"/>
    <result column="action" property="action"/>
    <result column="operate_time" property="create_time"/>
</resultMap>
<select id="getUserOperation" resultMap="userOperation">
    SELECT 
        u.id, u.name, u.gender,
        o.user_id, o.action, o.create_time as operate_time
    FROM
        user AS u INNER JOIN operate AS o ON u.id = o.user_id;
</select>
```

### 连表查询1
首先在 `com.test.domain.User` 类下定义好映射关系(如果比较复杂建议重新生成一个类去定义相对应的映射关系):
```java
// ...
public class User {
    // ...

    // 一对多
    private List<Operate> operation;
    // 一对一
    // private Operate operation
}
```
接着在 `resultMap` 标签中使用 `collection`:
```xml
<resultMap id="userOperation" type="com.test.domain.User">
    <result column="id" property="id"/>
    <result column="name" property="name"/>
    <result column="gender" property="gender"/>
    <!-- 操作记录 --> 
    <collection property="operation" ofType="com.test.domain.Operate">
        <id column="oid" property="id" />      <!-- 这里最好设置一个别名，防止sql报错或者仅有一条数据 --> 
        <result column="action" property="action"/>
        <result column="operate_time" property="create_time"/>
    </collection>
</resultMap>
<select id="getUserOperation" resultMap="userOperation">
    SELECT 
        u.id, u.name, u.gender,
        o.id as oid, o.user_id, o.action, o.create_time as operate_time
    FROM
        user AS u INNER JOIN operate AS o ON u.id = o.user_id;
</select>
```

### ${} 的使用（慎用）
```xml
<select id="userList" resultMap="user">
    SELECT * FROM user ORDER BY ${column} ${order};
</select>
```
在上述语句中我们可以很方便的自定义数据的排序规则，但是 `${}` 这种方式会直接拼接 `sql` 语句，可能导致 **sql注入**。