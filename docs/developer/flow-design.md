## 可视化配置设计

示例的结构化配置即为可视化配置的基础, 允许配置方引入 component, 选择 type, 并且根据所指定的 config class 自动绘制配置界面, 根据配置参数类型自动处理.
String, Integer, Long 等常用类型直接生成对用输入框, 实现了 DalaranConfigEnum 接口的 enum 会自动生成 Select, 后续还可以提供更多可视化配置的方式.

## 特殊组件

其中有一些特殊组件需要自定义前端配置模式, 如 Router, SubFlow 等.

## 界面示意

![](./docs/images/flow-design.jpg)

![](./docs/images/flow-config.jpg)

## 录入模式

根据类型自动生成的表单, 会有两种录入模式, 一种是根据具体的类型而启用的方式, 如 String, Integer, Select 之类的, 另外一种是表达式, 从环境变量或者是上下文中获取参数, 例如: `${database.url}`

## 前端实现

可参考 [BPMN](https://github.com/bpmn-io/bpmn-js), 因为其使用的 [License](https://bpmn.io/license/) 有一些要求, 所以可能还是要自己实现, 或者其他选型.