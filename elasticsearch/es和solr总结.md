# Elasticsearch vs Solr 

## Elasticsearch 和 Solr 比较

!(images)[https://github.com/TomatoZ7/notes-of-tz/blob/master/images/es%26solr1.png]

!(images)[https://github.com/TomatoZ7/notes-of-tz/blob/master/images/es%26solr2.png]

!(images)[https://github.com/TomatoZ7/notes-of-tz/blob/master/images/es%26solr3.png]

## Elasticsearch vs Solr

1. es 基本是开箱即用(解压就可以用)，非常简单，Solr 安装略微复杂；

2. Solr 利用 Zookeeper 进行分布式管理，而 Elasticsearch 自身带有分布式协调管理；

3. Solr 支持更多格式的数据，比如 JSON、XML、CSV，而 Elasticsearch 仅支持 json 文件格式；

4. Solr 官方提供的功能更多，而 Elasticsearch 本身更注重于核心功能，高级功能多有第三方插件提供，例如图形化界面需要 kibana 友好支撑；

5. Solr 查询快，但更新索引时慢(即插入删除慢)，用于电商等查询多的应用；
+ ES 建立索引快(即查询慢)，即实时性查询快，用于 facebook、新浪等搜索。
+ Solr 是传统搜索应用的有力解决方案，但 ES 更适用于新兴的实时搜索应用。

6. Solr 比较成熟，有一个更大、更成熟的用户、开发和贡献者社区，而 ES 相对开发维护者较少，更新太快，学习成本较高。