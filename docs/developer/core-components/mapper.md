##数据映射处理器

Camel DSL支持dozer，所以该处理器的接口实现如下：

```kotlin
@DalaranComponent("message-convert", configType = MessageConvertConfig::class)
class MessageConvertProcessor : DalaranProcessor<MessageConvertConfig> {

    private val uri = "dozer?targetModel=%s&mappingFile=%s"

    override fun configure(route: RouteDefinition, properties: Map<String, String>, config: MessageConvertConfig) {
        val uri = DalaranPropertyUtils.uriFormat(uri, properties, config.targetModel, config.mappingFile)
        route.to(uri)
    }
}
```

与之前几个处理器不同的是，数据映射处理器需要根据业务要求生成mapper文件，如下：

```xml
<mappings xmlns="http://dozermapper.github.io/schema/bean-mapping"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://dozermapper.github.io/schema/bean-mapping http://dozermapper.github.io/schema/bean-mapping.xsd">

    <mapping>
        <class-a>org.apache.camel.component.dozer.ExpressionMapper</class-a>
        <class-b>io.terminus.dalaran.example.ExtOrderItem</class-b>
        <field custom-converter-id="_expressionMapping" custom-converter-param="simple:\${header.name}">
            <a>expression</a>
            <b>itemName</b>
        </field>
        <field custom-converter-id="_expressionMapping" custom-converter-param="simple:\${body.price}">
            <a>expression</a>
            <b>itemPrice</b>
        </field>
        <field custom-converter-id="_expressionMapping" custom-converter-param="simple:\${header.Content-Type}">
            <a>expression</a>
            <b>test</b>
        </field>
    </mapping>
</mappings>
```
