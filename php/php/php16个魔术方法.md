# PHP 16 个魔术方法

## 前言

PHP 中把两个下划线 __ 开头的方法称为**魔术方法**(Magic Methods), 魔术方法包括：

1. __construct(), 类的构造函数
2. __destruct(), 类的析构函数
3. __call(), 在对象调用一个不可访问的方法时调用
4. __callStatic(), 用静态方式调用一个不可访问的方法时调用
5. __get(), 获得一个类的成员变量时调用 
6. __set(), 设置一个类的成员变量时调用 
7. __isset(), 当对不可访问属性调用 isset() 或 empty() 时调用
8. __unset(), 当对不可访问属性调用 unset() 时调用
9. __sleep(), 执行 serialize() 时会先调用这个函数
10. __wakeup(), 执行 unserialize() 时会先调用这个函数
11. __toString(), 类被当成字符串时的回应方法
12. __invoke(), 调用函数的方式调用一个对象时的回应方法
13. __set_state(), 调用 var_export() 导出类时，此静态方法会被调用
14. __clone(), 当对象复制完成时调用
15. __autoload(), 尝试加载未定义的类
16. __debuginfo(), 打印所调试的信息


[思否](https://segmentfault.com/a/1190000007250604)