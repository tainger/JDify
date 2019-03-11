## 触发器开发

编写一个触发器, 和编写执行器非常相似, 只需要实现 `DalaranTrigger` 接口, 并标记 `@DalaranComponent` 注解即可, `DalaranTrigger` 接口如下.

```java
public interface DalaranTrigger<T> {
    String buildRouterUri(T config);
}
```

接口中只有一个 buildRouterUri 方法, 会传入该接口声明的配置实例, 在实现方法内返回 `Camel from uri` 即可.

下面例子为接收 Dubbo 请求的 `http-provider`, 本质上是在配置过程中, 声明了 `dubbo` 的 `component`, 以及相关配置, 最后返回 `Camel uri`:


```java
@DalaranComponent(value = "dubbo-provider", configType = DubboProviderConfig.class)
public class DalaranDubboProvider implements DalaranTrigger<DubboProviderConfig> {

    private static final String DUBBO_PROVIDER_URI = "dubbo:?registryAddress=%s&serviceId=%s&method=%s&version=%s";

    public String buildRouterUri(DubboProviderConfig config) {
        return String.format(DUBBO_PROVIDER_URI, config.getRegistryAddress(), config.getServiceId(), config.getMethod(), config.getVersion());
    }
}
```

> 理论上不存在无配置触发器, 所以不提供无配置触发器接口.