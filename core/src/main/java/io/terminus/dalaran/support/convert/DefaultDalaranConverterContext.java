package io.terminus.dalaran.support.convert;

import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranConverter;
import io.terminus.dalaran.DalaranConverterContext;
import io.terminus.dalaran.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import io.terminus.dalaran.model.schema.XMLSchema;
import io.terminus.dalaran.support.convert.converter.JsonConverter;
import io.terminus.dalaran.support.convert.converter.XMLConverter;
import org.apache.camel.model.RouteDefinition;

import java.util.HashMap;
import java.util.Map;

public class DefaultDalaranConverterContext implements DalaranConverterContext {
    private final Map<BodyType, DalaranConverter> converterMapping;
    private final Map<BodyType, Class<? extends DalaranModelSchema>> converterSchemaMapping;

    public DefaultDalaranConverterContext() {
        converterMapping = new HashMap<>();
        converterSchemaMapping = new HashMap<>();
        // TODO 这个扩展面也很窄, 先写死吧...
        converterMapping.put(BodyType.JSON, new JsonConverter());
        converterSchemaMapping.put(BodyType.JSON, JsonSchema.class);

        converterMapping.put(BodyType.XML, new XMLConverter());
        converterSchemaMapping.put(BodyType.XML, XMLSchema.class);

        converterSchemaMapping.put(BodyType.OBJECT, ObjectSchema.class);
    }

    @Override
    public Class<? extends DalaranModelSchema> getSchemaType(BodyType modelType) {
        return converterSchemaMapping.get(modelType);
    }

    @Override
    public void convert(RouteDefinition route, BodyType currentBodyType, BodyType nextBodyType) {
        convert(route, currentBodyType, nextBodyType, null);
    }

    @Override
    public void convert(RouteDefinition route, BodyType currentBodyType, MessageModel model) {
        convert(route, currentBodyType, model.getModelType(), model.getModelSchema());
    }

    // TODO 这里还是比较奇怪的, 主要是想屏蔽不同类型的处理
    private void convert(RouteDefinition route, BodyType currentBodyType, BodyType targetBodyType, DalaranModelSchema modelSchema) {
        if (targetBodyType.isSerialized()) {
            if (!currentBodyType.isSerialized()) {
                converterMapping.get(targetBodyType).fromObject(route, modelSchema);
            }
            // TODO else... 理论上这里只会出现序列化和反序列化的场景, 不会出现类似 Json -> XML 的场景, 后续如果有需求, 可以处理 A->B
        } else {
            // 根据当前类型转为 Object
            if (currentBodyType != BodyType.OBJECT) {
                converterMapping.get(currentBodyType).toObject(route, modelSchema);
            }
        }
    }
}
