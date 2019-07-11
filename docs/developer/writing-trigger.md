## 触发器开发

> 因为触发器默认为单例, 所以不推荐使用任何成员变量

编写一个触发器, 和编写处理器非常相似, 只需要实现 `DalaranTrigger` 接口, 并标记 `@Trigger` 注解即可, `DalaranTrigger` 接口如下.

```java
public interface DalaranTrigger<T> extends DalaranComponent {
    void buildFromRoute(RouteDefinition route, T config);
}
```

接口中只有一个 buildFromRoute 方法, 会传入该接口声明的配置实例, 在实现方法内返回 `Camel from uri` 即可.

下面例子为接收 Dubbo 请求的 `http-listener`, 本质上是在配置过程中, 声明了 `netty4-http` 的 `component`, 以及相关配置, 最后返回 `Camel uri`:

```java
@Trigger(
        value = "netty-http-listener",
        configType = NettyHttpConfig.class,
        allowBodyTypes = {BodyType.JSON, BodyType.XML},
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
public class NettyHttpListener implements DalaranTrigger<NettyHttpConfig> {

    @Override
    public void buildFromRoute(RouteDefinition route, NettyHttpConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://0.0.0.0:" + config.getPort() + config.getPath() +
                "?httpMethodRestrict=" + config.getMethod();
        route.from(uri);
        if (config.getMethod().isNoBody()) {
            route.process(new QueryStringProcessor());
        } else {
            // TODO Stream to string
            route.convertBodyTo(String.class);
        }
    }
}
```

> `inputSerializeType` 和 `outputSerializeType` 是集成平台封装的模型转换声明, 具体详见 [模型](./model.md)

> 理论上不存在无配置触发器, 所以不提供无配置触发器接口.

### 核心触发器

多数触发器都十分简单, 特别是 Camel 本身已经提供的 Component, 有些触发器开发会比较复杂, 在此单独记录设计文档:

* [Dubbo provider](./core-components/dubbo-provider.md)