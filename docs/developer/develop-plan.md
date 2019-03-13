# 开发计划

* 03-11 ~ 03-15: 设计阶段, 输出设计文档与 Demo 工程, 并评审通过
* 03-??: kickoff
* 03-18 ~ 03-29: 基本框架与部分核心组件完成开发, 手写配置可完成常见场景集成工作, 输出基础工程
* 04-01 ~ 04-19: 前端参与, 完成可视化配置, 完善基础框架并补充核心组件, 如东购有集成场景输出, 可搬套实验一下
* 04-22 ~ 04-30: 达到提测标准, 提供基础部署方式, 可对内尝试推广使用

### 核心组件

* 触发器:
    * Timer
    * Http listener
    * Dubbo provider
* 处理器:
    * Http client
    * Dubbo consumer
    * Exception
    * ReTry
    * Mapper: 提供基本映射, mapping function 下版提供

> 视东购项目需求调整优先级

### 二期组件

* 触发器:
    * RocketMQ consumer
    * Kafka consumer
* 处理器:
    * RocketMQ producer
    * Kafka producer
    * SAP HANA client
    * SOAP client
    * Branch: 分支, 可以并行
    * Aggregator: 配合 Branch 做聚合
    * Router
    * Loop
    * Mapping function
    * Database read
    * Database write
    * Script: 脚本语言还未定, 大概率 Java 或者 JS
    * Dynamic Router
    * Delayer
    * Cache
    * Async: 异步执行

### 可能不需要的组件

* 处理器:
    * Filter
    * Sequencer
    * Splitter: