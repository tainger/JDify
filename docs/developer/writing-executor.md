## 执行器开发

编写一个执行器, 只需要实现 `DalaranExecutor` 接口, 并标记 `@DalaranComponent` 注解即可, `DalaranExecutor` 接口如下.

```java
public interface DalaranExecutor<T> {
    void configure(RouteDefinition route, T config);
}
```

接口中只有一个 configure 方法, 会传入 `RouteDefinition` 和该接口声明的配置实例, 在实现方法内通过 `Java DSL` 调用 `RouteDefinition` 即可完成 `camel` 相关路由配置.

下面例子为发起 Http 请求的 `http-client`, 便是在配置过程中, 声明了使用 `http4` 的 `component`, 并且完成了 `http method` 配置:

```java
@DalaranComponent(value = "http-client", configType = HttpClientConfig.class)
public class DalaranHttpClient implements DalaranExecutor<HttpClientConfig> {
    private static final String HTTP_URI = "%s4://%s:%s%s?bridgeEndpoint=true";

    @Override
    public void configure(RouteDefinition route, HttpClientConfig config) {
        String uri = String.format(HTTP_URI, config.getProtocol().getValue(), config.getHost(), config.getPort(), config.getPath());
        route.setHeader("CamelHttpMethod", Builder.constant(config.getMethod())).to(uri);
    }
}
```

> 如果是无配置执行器, 可以实现 `UnconfigurableDalaranExecutor` 接口, 该接口对 `DalaranExecutor` 进行了包装, `configure` 接口仅有 `RouteDefinition` 一个入参.

```java
public interface UnconfigurableDalaranExecutor extends DalaranExecutor {
    @Override
    default void configure(RouteDefinition route, Object config) {
        configure(route);
    }
    void configure(RouteDefinition route);
}
```

## 核心执行器

有些执行器开发会比较复杂, 在此记录设计文档:

* [数据映射](./core-components/mapper.md)
* [路由](./core-components/router.md)