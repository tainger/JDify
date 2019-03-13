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

###数据映射流程处理

(1)XXXToObject -> (2)mapping -> (3)ObjectToXXX

(1)(3) 这两个流程的关键点在于需要通过前端配置信息获取操作对象的结构，并将其转换成dalaran的内部对象（可能是我们根据其结构生成的class，也可能直接是map结构）

（2）该流程的关键点在于通过前端配置信息获取字段映射的详细内容，并根据该内容生成相应的xml文件 

涉及的数据模型：

MessageModel： 用于描述字段信息
```java
public class MessageModel {

    private String fieldName;

    private String fieldType;
}
```

DalaranMessage: 用于描述具体对象，modelType为输入数据的类型，比如JSON，XML等
```java
public class  DalaranMessage {

    private List<MessageModel> fields;

    private ModelType type;
}
```

MessageMapping: 用于描述字段映射
```java
public class MessageMapping {

    private List<MessageModel> targetModel;

    private List<MessageModel> destinationModel;

    private MessageProcessFunction function;
}
```

MessageMappingSet: 用于描述一组映射关系
```java
public class MessageMappingSet {
    
    private DalaranMessage target;
    
    private DalaranMessage destination;

    private List<MessageMapping> mappings;
}
```

我们数据映射中使用的xml文件通过解析MessageMappingSet结构生成
