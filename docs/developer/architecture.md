## 架构设计

集成平台整体设计是基于 [Apache Camel](http://camel.apache.org) 的, Camel 的介绍本文不在赘述.
其作为集成平台底层的路由调度框架, 上述 `触发器/执行器` 等逻辑组件, 也是基于 Camel 的 component 标准进行扩展和配置封装.
上述的 `集成流程` 本质上也是 Camel DSL 的封装.

> Apache Camel 基于 Apache License 2.0 授权协议, 商业友好.

### 可扩展设计

本产品最核心的可扩展点在于 `触发器` 和 `执行器`, 因为我们设计上都是基于 Camel 进行封装开发, 所以本质上 `触发器/执行器` 都是 Camel 中的 DSL 配置过程.

Trigger 只需要实现 DalaranTrigger 接口, 配置其所需要产生的 camel uri 即可, 这部分还有再封装的空间.

如 `netty-http-listener`:

```java
@DalaranComponent("netty-http-listener", configType = NettyHttpConfig::class)
class NettyHttpListener : DalaranTrigger<NettyHttpConfig> {
    private val camelComponentScheme = "netty4-http"

    override fun getUri(properties: Map<String, String>, config: NettyHttpConfig): String =
            "$camelComponentScheme:${config.protocol.value}://${config.host}:${config.port}${config.path}?httpMethodRestrict=${config.method}"
}
```

又如发起 Http 请求的 `http-request`:

```java
@DalaranComponent("http-request", configType = HttpRequestConfig::class)
class HttpRequestExecutor : DalaranExecutor<HttpRequestConfig> {
    private val uri = "%s4://%s:%s%s?bridgeEndpoint=true"
    override fun configure(route: RouteDefinition, properties: Map<String, String>, config: HttpRequestConfig) {
        val uri = DalaranPropertyUtils.uriFormat(uri, properties, config.protocol.value, config.host, config.port, config.path)
        route.setHeader("CamelHttpMethod", constant(config.method)).to(uri)
    }
}
```

如果现有的 Camel component 不能满足需求, 可以自行定制 camel component, 在新的 trigger/executor 内加入其依赖即可, 如我们自行编写的 `Dubbo Component`:

```xml
<pom>
  <artifactId>dalaran-dubbo-provider</artifactId>
  <dependencies>
      <dependency>
          <groupId>io.terminus</groupId>
          <artifactId>camel-dubbo</artifactId>
          <version>${project.version}</version>
      </dependency>
  </dependencies>
</pom>
```

> camel component 的编写标准详见[Writing Camel components](http://camel.apache.org/writing-components.html)
