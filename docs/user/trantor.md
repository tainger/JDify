# 与 Trantor 集成

集成平台与 Trantor 深度集成, 为 Trantor 项目提供了快速集成的能力. 与其他集成场景一样, 和 Trantor 的集成也分为 `对内` 和 `对外` 两个方向.

`对内` 就是将第三方接口包装后, 供 Trantor 调用; `对外` 则是将 Trantor 接口包装成第三方系统所需要的协议, 供第三方系统调用.

目前只做了 `对内` 的场景, 对外需求暂时较少, 暂时搁置.

## 对内集成

对内集成是指内部产品(系统)调用第三方系统的接口, 这部分接口对于内部系统来说, 则是标准业务接口.

所以, 我们要声明对内的标准接口, 再完成标准接口与第三方系统的集成.

### 依赖配置

首先, 我们为 Trantor 的项目加入 dalaran 的 SDK 依赖, 并且完成相关配置.

```XML
<dependency>
    <groupId>io.terminus</groupId>
    <artifactId>dalaran-sdk</artifactId>
    <version>${project.version}</version>
</dependency>
```

在 application.yml 中加入相关配置

```yaml
terminus.dalaran:
  runtimeUrl: http://dalaran.terminus.io:8080 # dalaran 的 runtime 地址
  basePackages: "io.terminus.dalaran" # 集成点扫描的基础路径, 是个数组, 可以配置多个
```

### 集成点声明

之后, 我们可以声明对内的标准接口, 接口没有特殊要求, 出入参只要是可序列化的类, 加上相应注解即可, 如下例.

```Java
@DalaranIntegration(key = "TEST-EXT-POINT", name = "测试用集成扩展点")
public interface TestExtPoint {

    Order getOrder(long orderId);

    Order createOrder(String orderNumber, List<OrderItem> orderItems);

    @DalaranIntegrationAction(key = "getAllOrder", name = "获取所有订单")
    List<Order> allOrders();
}
```

我们可以看到, 上面的接口声明了 3 个方法, 并且类上和方法上都有相关注解.

`@DalaranIntegration` 是声明这是一个集成点, Key 和 name 是必填的, key 需要保证唯一, name 则是该集成的名称, 还有 description 可以用来写具体描述. 

声明了该注解的接口, 其所有方法都会被认为是带集成点, 即 `集成点的 Key` 和 `方法名` 组成的标识.

如果方法需要额外的声明, 比如自定义的 method key, 具体描述等, 可以在方法上使用 `@DalaranIntegrationAction` 注解. 参数和集成点注解基本一致.

完成接口声明后, SDK 会扫描相关接口, 上报相关信息给 Trantor MetaStore, 并且生成相关代理类用于调用时访问集成平台, 这些动作都是透明的.

### 接口集成

此时已经完成接口声明与基本配置, 接下来我们通过集成平台完成相关接口集成.

新建一个集成流, 选择触发器类型为 `Trantor-Integration`, 之后点击起始节点, 选择要完成的集成点, 即上述第二步的具体接口方法.

这样, 我们就声明了该集成点方法的声明, 接下来跟配置其他集成一样, 在流程内配置集成具体动作即可, 比如第三方接口访问, 模型转换等等. 参考 [`Quick Start`](./quick-start.md);

## 对外集成

暂时搁置.