# Rest 风格操作 ES

基于 kibana 和 elasticsearch-header 操作

## 创建一个索引

```
PUT /索引名/类型名/文档ID
{ 请求体 }
```

![images](https://github.com/TomatoZ7/notes-of-tz/blob/master/images/ESrest1.png)

如果自己的文档子段没有指定类型，那么 ES 就会给我们默认配置字段类型。

## 创建规则(类比创建数据库)

```
PUT /test2
{
    "mappings" : {
        "porperties" : {
            "name" : {
                "type" : "text"
            },
            "age" : {
                "type" : "long"
            },
            "birthday" : {
                "type" : "date"
            }
        }
    }
}
```

> 注意：7.13 不适用

## 获取相应信息 

```
GET /索引名/类型名/文档ID
```

## 修改

```
# 曾经
PUT /test1/type1/1

# now
POST /test1/type1/1/_update
```

## 删除 

```
DELETE /test1/{type1}/{ID}
```