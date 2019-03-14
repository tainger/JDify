## flow

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
  "trigger": { // 触发器, 触发集成流程, 下面为一个 基于 netty 的 http 触发器
    "type": "netty-http-listener",
    "config": {
      "protocol": "http",
      "host": "0.0.0.0",
      "port": "{{port}}", // 可以获取配置参数
      "path": "/orderItems",
      "method": "POST"
    }
  },
  "processors": [ // 执行节点, Demo 目前只支持单流程, 复杂流程后面再搞, 理论上嵌一个 路由或者逻辑节点即可
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

上述主要配置为 trigger 和 processors, 主要是声明一个 type 和 config, type 声明了要使用何种 trigger 或 processors, config 提供了其所需要的配置内容.


## 另一种配置方式

考虑到触发器/处理器复用的角度, 可以将触发器/处理器配置的内容作为实例保存, 而 Flow 中配置时引用其实例 Id 即可.

举例:

```json
{
  "properties": {
    "port": 8184,
    "targetHost": "localhost",
    "targetPort": 8080
  },
  "trigger": {
    "type": "netty-http-listener",
    "config": "onOrderCreated"
  },
  "processors": [
    {
      "type": "object-mapping",
      "config": "TerminusOrderToERPOrder"
    },
    {
      "type": "http-request",
      "config": "CallCreateOrder"
    }
  ]
}
```

> 考虑使用这种方式, 组件实例可以复用, 其次也可以将 config 内容与其 component 调用分离, 这部分需要做个 poc