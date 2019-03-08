# 集成平台(Dalaran)

> Dalaran(达拉然)是魔兽世界中的一个中立城市, 后来成为部落和联盟的抗魔联军纽带. 以此为名, 希望该项目能够连接一切, 集成一切, 即便是两个水火不容的集团.

## 背景

在公司产品实施过程中, 难免会存在与客户公司已存在的系统对接, 因为不同企业可能使用不同的软件提供商, 即使同一提供商可能也不尽相同.
目前主要实现方式是对接逻辑硬编码于项目之中, 这就导致项目一定程度被腐化, 而且存在部分相对的重复工作量.
集成平台希望以配置的形式满足系统间的集成, 将集成逻辑和产品项目剥离开, 已达到实施上的防腐层存在, 同时可以承担产品对外开放的职责.

## 关键词

* 无代码
* 可视化配置
* 可扩展
* 可观察

## 用户手册

* [概述](./docs/user/overview.md)
* [快速开始](./docs/user/quick-start.md)
* [触发器](./docs/user/trigger/index.md)
* [执行器](./docs/user/executor/index.md)
* [流程设计器](./docs/user/flow-design.md)

## 开发者手册

* [架构介绍](./docs/developer/architecture.md)
* [集成流程](./docs/developer/flow.md)
* [流程设计器配置](./docs/developer/flow-design.md)
* [触发器开发](./docs/developer/writing-trigger.md)
* [执行器开发](./docs/developer/writing-executor.md)
* [其他](./docs/developer/other.md)