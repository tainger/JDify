## 处理器开发

> 因为处理器默认为单例, 所以不推荐使用任何成员变量

编写一个处理器, 只需要实现 `DalaranProcessor` 接口, 并标记 `@Processor` 注解即可, `DalaranProcessor` 接口如下.

```java
public interface DalaranProcessor<T> extends DalaranComponent<T> {
    void configure(ProcessorDefinition route, T config);
}
```

接口中只有一个 configure 方法, 会传入 `RouteDefinition` 和该接口声明的配置实例, 在实现方法内通过 `Java DSL` 调用 `RouteDefinition` 即可完成 `camel` 相关路由配置.

下面例子为发起 Dubbo 请求的 `dubbo-consumer`, 便是在配置过程中, 声明了使用 `dubbo` 的 `component`, 并且完成了相关配置:

```java
@Processor(
        value = "dubbo-consumer", configType = DalaranDubboConsumerConfig.class,
        inputSerializeType = BodySerializeType.Object,
        outputSerializeType = BodySerializeType.Object
)
public class DalaranDubboConsumer implements DalaranProcessor<DalaranDubboConsumerConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?application=%s&registryAddress=%s&serviceId=%s&method=%s&version=%s&parameterType=%s";

    @Override
    public void configure(ProcessorDefinition route, DalaranDubboConsumerConfig config) {
        String uri = String.format(
            DUBBO_PROVIDER_URI, 
            config.getConnector().getApplication(),
            config.getConnector().getAddress(),
            config.getServiceId(),
            config.getMethod(),
            config.getVersion(),
            config.getParameterType()
        );
        route.to(uri);
    }
}
```

> `inputSerializeType` 和 `outputSerializeType` 是集成平台封装的模型转换声明, 具体详见 [模型](./model.md)

> 如果是无配置处理器, 可以实现 `UnconfigurableDalaranProcessor` 接口, 该接口对 `DalaranProcessor` 进行了包装, `configure` 接口仅有 `RouteDefinition` 一个入参.

```java
public interface UnconfigurableDalaranProcessor extends DalaranProcessor {
    @Override
    default void configure(ProcessorDefinition route, Object config) {
        configure(route);
    }
    void configure(ProcessorDefinition route);
}
```

## 核心处理器

多数处理器都十分简单, 特别是 Camel 本身已经提供的 Component, 有些处理器开发会比较复杂, 在此单独记录设计文档:

* [数据映射](./core-components/mapper.md)
* [路由](./core-components/router.md)
* [子流程](./core-components/sub-flow.md)
* [Dubbo consumer](./core-components/dubbo-consumer.md)
* [Exception](./core-components/exception.md)

