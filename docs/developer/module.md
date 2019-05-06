## 模块划分

* camel-components: 扩展 camel 的组件, 如 Dubbo 等 camel 官方没有提供的
* components: 所有 Dalaran 官方组件, 目前是一个 module 合并在了一起, 因为目前的方式, 没有自主扩展的场景
* core: dalaran 核心库, 只负责将数据转化为可运行的 Camel route, 集成组件的加载, 输入加载等统统不关心
* runtime: Dalaran 的运行容器, 负责加载数据, 通过 core 完成集成配置的初始化和运行
* console: Dalaran 控制台, 提供可视化配置集成数据的相关接口
* common: 运行容器与控制台公用的部分, 主要是核心模型和 Repository 方法