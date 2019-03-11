## 执行器开发

> 因为执行器默认为单例, 所以不推荐使用任何成员变量

编写一个执行器, 只需要实现 `DalaranProcessor` 接口, 并标记 `@DalaranComponent` 注解即可, `DalaranProcessor` 接口如下.

```java
public interface DalaranProcessor<T> {
    void configure(RouteDefinition route, T config);
}
```

接口中只有一个 configure 方法, 会传入 `RouteDefinition` 和该接口声明的配置实例, 在实现方法内通过 `Java DSL` 调用 `RouteDefinition` 即可完成 `camel` 相关路由配置.

下面例子为发起 Http 请求的 `http-client`, 便是在配置过程中, 声明了使用 `http4` 的 `component`, 并且完成了 `http method` 配置:

```java
@DalaranComponent(value = "http-client", configType = HttpClientConfig.class)
public class DalaranHttpClient implements DalaranProcessor<HttpClientConfig> {
    private static final String HTTP_URI = "%s4://%s:%s%s?bridgeEndpoint=true";

    @Override
    public void configure(RouteDefinition route, HttpClientConfig config) {
        String uri = String.format(HTTP_URI, config.getProtocol().getValue(), config.getHost(), config.getPort(), config.getPath());
        route.setHeader("CamelHttpMethod", Builder.constant(config.getMethod())).to(uri);
    }
}
```

> 如果是无配置执行器, 可以实现 `UnconfigurableDalaranProcessor` 接口, 该接口对 `DalaranProcessor` 进行了包装, `configure` 接口仅有 `RouteDefinition` 一个入参.

```java
public interface UnconfigurableDalaranProcessor extends DalaranProcessor {
    @Override
    default void configure(RouteDefinition route, Object config) {
        configure(route);
    }
    void configure(RouteDefinition route);
}
```

## 核心执行器

多数执行器都十分简单, 特别是 Camel 本身已经提供的 Component, 有些执行器开发会比较复杂, 在此单独记录设计文档:

* [数据映射](./core-components/mapper.md)
* [路由](./core-components/router.md)
* [Dubbo consumer](./core-components/dubbo-consumer.md)