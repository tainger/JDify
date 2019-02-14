# 集成平台(Dalaran)

## 关键词

* 可扩展
* 可视化配置
* 无代码

## 基本概念

* 事件流: 集成平台所有集成动作都以事件流为基础, 即 `A系统 -> B系统` 的过程, 实际上是 A系统 触发了一个事件流, 事件流配置的逻辑完成到 B系统 的逻辑处理, 比如协议转换, 参数转换, 流程封装等, 可以简单认为类似工作流. 每一个事件流可以认为是一个集成流程.
* 事件消息: 事件流触发后, 在执行节点间传递的内容, 即为事件消息.
* 执行节点: 即 DalaranComponent
    * 触发器: 即 DalaranListener, 提供触发集成的服务, 如 Http server, Dubbo provider, Timer executor 等. 可以认为是事件流的起始节点, 一般来讲也是终止节点.
    * 执行器: 即 DalaranEndpoint, 执行处理逻辑的部分, 如参数转换, Http 调用, Sap 调用等. 一般为事件流过程节点.



## 集成流程演示

假设我们要做一个 http -> http 的集成流程, 过程中需要做一下参数映射.

> 流程为: 接收Http请求 -> 完成参数转换 -> 发起 Http 请求访问目标系统

以下为消息流的配置示例, 使用这套配置的主要原因是将配置内容抽象化, 后续可以做可视化配置:

```json
{
  "properties": { // 配置参数, 理论上配置可以有多级, 类似 Spring boot 优先级覆盖.
    "port": 8184,
    "targetHost": "localhost",
    "targetPort": 8080
  },
  "listener": { // 触发器, 触发集成流程, 下面为一个 基于 netty 的 http 触发器
    "type": "netty-http-listener",
    "config": {
      "protocol": "http",
      "host": "0.0.0.0",
      "port": "{{port}}", // 可以获取配置参数
      "path": "/orderItems",
      "method": "POST"
    }
  },
  "endpoints": [ // 执行节点, Demo 目前只支持单流程, 复杂流程后面再搞, 理论上嵌一个 路由或者逻辑节点即可
    {
      "type": "json2object", // 将接受到的 Http json 转化为对象
      "config": {
        "outType": "io.terminus.test.OrderItem"
      }
    },
    {
      "type": "object-mapping", // 完成对象的映射转换, mapping 内即为映射关系, ${xxx} 为表达式, 可以从消息体或者上下文中获取
      "config": {
        "targetModel": "io.terminus.test.ExtOrderItem",
        "mapping": {
          "itemName": "${header.name}",
          "itemPrice": "${body.price}",
          "test": "${header.Content-Type}"
        }
      }
    },
    {
      "type": "object2json" // 将对象转化为 http json
    },
    {
      "type": "http-request", // 发起 http 请求, 完成集成过程
      "config": {
        "method": "POST",
        "host": "{{targetHost}}",
        "port": "{{targetPort}}",
        "path": "/orders"
      }
    }
    // 后面理论上可能还涉及返回对象转换, 不在复述...
  ]
}
```

上述主要配置为 listener 和 endpoints, 主要是声明一个 type 和 config, type 声明了要使用何种 listener 或 endpoints, config 提供了其所需要的配置内容.

## 可扩展设计

上述配置示例也描述了集成的主要内容就是 listener 和 endpoints, 而这两部分是完全可扩展的.

listener 只需要实现 DalaranListener 接口, 配置其所需要产生的 camel url 即可, 这部分还有再封装的空间.
endpoint 实现 DalaranEndpoint 接口, 并且通过 DalaranComponent 声明 type 和接受 config 的 class 即可.

demo 提供的 listener 和 endpoint 就是通过上述方式编写.

## 可视化配置设计

示例的结构化配置即为可视化配置的基础, 允许配置方引入 component, 选择 type, 并且根据所指定的 config class 自动绘制配置界面, 根据配置参数类型自动处理.
String, Integer, Long 等常用类型直接生成对用输入框, 实现了 DalaranConfigEnum 接口的 enum 会自动生成 Select, 后续还可以提供更多可视化配置的方式.