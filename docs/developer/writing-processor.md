## 触发器开发

> 因为触发器默认为单例, 所以不推荐使用任何成员变量

编写一个触发器, 和编写处理器非常相似, 只需要实现 `DalaranTrigger` 接口, 并标记 `@DalaranComponent` 注解即可, `DalaranTrigger` 接口如下.

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

### 核心触发器

多数触发器都十分简单, 特别是 Camel 本身已经提供的 Component, 有些触发器开发会比较复杂, 在此单独记录设计文档:

* [Dubbo provider](./core-components/dubbo-provider.md)