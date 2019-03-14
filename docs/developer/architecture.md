## 架构设计

集成平台整体设计是基于 [Apache Camel](http://camel.apache.org) 的, Camel 的详细说明本文不再赘述.

> Apache Camel is a powerful open source integration framework based on known Enterprise Integration Patterns with powerful bean integration.

Camel 提供了一系列集成向的 Component, 另外还有一套成熟的路由调度, 基于 Camel 做集成平台会减少很多底层工作量.

其作为集成平台底层的路由调度框架, 上述 `触发器/处理器` 等逻辑组件, 也是基于 Camel 的 component 标准进行扩展和配置封装.

上述的 `集成流程` 本质上也是 Camel DSL 的封装.

> Apache Camel 基于 Apache License 2.0 授权协议, 商业友好.

### 核心概念

这里要描述的详细一些

* Trigger (触发器): 提供触发集成的服务, 一切集成流程的起点, 如监听 Http 请求, 定时触发, 消费消息等, 详见 触发器章节.
* Processor (处理器): 具体执行集成的逻辑动作, 如参数转换, Rest 调用, 读取数据等等, 详见 处理器章节.
* Flow (集成流程): 指从集成事件触发, 到所有集成动作完成的整体过程编排, 一般描述了整体的集成逻辑.
* Message (集成消息): 集成流程节点间传递的内容, 我们称之为 Message, 一般带有固定格式, 以 Model 为结构描述.
* Model (模型): 即 MessageModel, 描述了消息的结构化模型, 数据映射转换也是基于模型处理.

### 可扩展设计

本产品最核心的可扩展点在于 `触发器` 和 `处理器`, 因为我们设计上都是基于 Camel 进行封装开发, 所以本质上 `触发器/处理器` 都是 Camel 中的 DSL 配置过程.

> 具体开发细节可以参考 [如何编写处理器](./writing-processor.md) 和 [如何编写触发器](./writing-trigger.md).

如果现有的 Camel component 不能满足需求, 可以自行定制 camel component, 在新的 trigger/processor 内加入其依赖即可, 如我们自行编写的 `Dubbo Component`:

> camel component 的编写标准详见[Writing Camel components](http://camel.apache.org/writing-components.html)

### XMind

![](../images/xmind.jpg)

### 核心类图

![](../images/core-class.jpg)

### 核心逻辑

```sequence
Dalaran -> DalaranComponentLoader: 加载所有触发器和处理器
Dalaran -> DalaranFlowLoader: 加载所有集成流程
DalaranFlowLoader -> CamelRouteBuilder: 转化为 Camel Java DSL
CamelRouteBuilder -> DalaranComponentLoader: 过程中拉取所需触发器和处理器
CamelRouteBuilder -> DalaranFlowLoader: 注册并生效
```

![](../images/startup-flow.jpg)

