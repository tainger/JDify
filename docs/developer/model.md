# 模型

集成平台中, 模型是用于描述信息结构和格式的.

## 模型类型

目前已经支持的有 `Json`, `Object`, `XML` 三种.

如果集成过程中, 让用户关心格式的话, 就会出现一些额外的操作. 我们以一个 Json 接口调用外部 XML 接口为例:

> 基本流程:
接口 A -> 
Mapper(A 入参 转 B 入参) -> 
接口 B -> 
Mapper(B 出参 转 A 出参) -> 
返回

> 如果要关心模型的类型:
接口 A -> 
`Json 转 Object` -> 
Mapper(A 入参 转 B 入参) -> 
`Object 转 XML` -> 
接口 B -> 
`XML 转 Object` -> 
Mapper(B 出参 转 A 出参) -> 
`Object 转 Json` -> 
返回

我们可以看到, 这个过程中, 用户就需要增加 4 个步骤来完成格式的转换处理, 这样用户的配置量就会增加很多. 所以, 集成平台提供了对用户透明的格式转换, 通过节点声明的序列化要求, 来进行自匹配的序列化/反序列化处理.

## 自动转换

触发器和处理器的注解上, 都有 `inputSerializeType` 和 `outputSerializeType` 的配置, 值是一个枚举.

```java
public enum BodySerializeType {
    Object, Serialized, All
}
```

通过枚举值也比较好理解, 就是组件会声明, 自己是接受 Object 还是 序列化的类型, 亦或是都可以. 
序列化的类型目前泛指除了 Object 之外的其他类型, 比如 `Json`, `XML` 等.

在流程构建时, 由触发器和处理器形成了一个流, 可以认为是一个单向链表或者是树. 
除了起始节点(根节点)外, 都会有一个前节点(父节点), 节点都会声明入参类型和出参类型, 所以我们就可以根据声明来做自动的序列化和反序列化.

依然以上例说明:

```
接口 A: Rest Trigger (in:Serialized, out:Serialized) -> 
Mapper (in:Object, out:Object) -> 
接口 B: Soap Client (in:Serialized, out:Serialized)-> 
Mapper (in:Object, out:Object) -> 
返回 (就是 trigger 的 out: Serialized)
```

我们假设 接口 A 的 Rest Trigger 出入参都是 Json 格式, 所以我们的转化过程就是:

Trigger in: Json (`Serialized`) -> Mapper in: Object: (`Object`)

Mapper out: Object: (`Object`) -> Soap Client in: XML (`Serialized`)

Soap Client out: XML (`Serialized`) -> Mapper in: Object: (`Object`)

Mapper out: Object: (`Object`) -> Trigger out: XML (`Serialized`)

这样, 过程中的格式转换的问题, 就交给了组件声明, 而不是用户配置.

## 组件可接受配置类型

Trigger 和 Processor 的注解上, 除了 出入序列化类型之外, 还有一个 `allowBodyTypes` 配置, 类型是 BodyType[], 该参数声明了组件能够接收什么样的参数类型.

比如上文提到的 Rest Trigger, 就只能接受 Json 类型的模型, 选择时其他类型的模型会被屏蔽. 而 Soap Client 只支持 XML 类型.

如此一来, 用户配置的过程会根据组件要求的模型类型受到一些限制, 减少配置过程的干扰.
