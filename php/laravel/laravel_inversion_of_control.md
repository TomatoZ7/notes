# (转) Laravel Inversion of Control (控制反转) 概念简介

## 概述
loC(控制反转)，是面向对象编程中的一种设计原则，可以用来减低计算机代码之间的耦合度。

&emsp;

## 实现控制反转，有两种方式：
+ Dependency Injection (DI) - 依赖注入
+ Dependency Lookup - 依赖查找  

两者的区别在于：  
前者是被动的接收对象，在类实例创建过程中即创建了依赖的对象，通过类型或名称来判断将不同的对象注入到不同的属性中；  
而后者是主动索取相应类型的对象，获得依赖对象的时间也可以在代码中自由控制。

&emsp;

## 哪些方面的 [控制] 被 [反转] 了？
依赖对象的 \[获得\] 被反转了。

&emsp;

## 技能描述
class A 中用到了 class B 的对象b，一般情况下，需要在 A 的内部显示 new 一个 B 的对象。
采用**依赖注入**之后，class A 内只需定义一个私有的 B 对象，不需要直接使用 new 来获取这个对象，而是通过 **容器控制程序** 来将 B 对象在外部 new 出来并注入到 A 类里的引用中。
而具体的获取方法、对象被获取时的状态由 **容器** 来指定。  

### 假设我有一部 iPhone，我的 iPhone 依赖充电器才能充电。
```
class iPhone
{
    // 电量
    private $power;

    // 充电
    public function charge()
    {

    }
}
```
### 我还有一个苹果充电器：
```
class AppleCharger
{
    public function charge()
    {
        return 100;
    }
}
```
### 在此前，iPhone 内部 [控制] 着只能用哪一款充电器：
```
class iPhone
{
    // 电量
    private $power;

    // 充电，只能用原装充电器
    public function charge()
    {
        $charger = new AppleCharger;
        $this->power = $charger->charge();
    }
}
```
```
// 充电
$iphone = new iPhone;
$iphone->charge();
```
### 使用**依赖注入**以后，我来决定给 iPhone 用哪一款充电器。
```
class iPhone
{
    private $power;
    private $charger;

    // 依赖注入充电器，无关充电器类型
    public function __construct(Charger $charger)
    {
        $this->charger = $charger;
    }

    // 充电
    public function charge()
    {
        $this->power = $this->charger->charge();
    }
}
```
```
interface Charger
{
    public function charge();
}
```
```
// Laravel 容器
use Illuminate\Container\Container;
$container = Container::getInstance();

// 给它一个原装的充电器
$container->bind(Charger::class, AppleCharger::class);

// 或者使用其他的充电器
$container->bind(Charger::class, OtherCharger::class);

// 充电
$iphone = $container->make(iPhone::class);
$iphone->charger();
```
可见，使用**依赖注入**后，控制权 \[反转\] 了，由外部来决定给它什么类型的充电器(依赖对象)。  
Laravel 管这个 **容器控制程序** 叫 **Service Container (服务容器)**，它来控制着各种依赖的获取方法。