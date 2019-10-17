package io.terminus.dalaran.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.terminus.dalaran.model.convert.ModelDeserializer;
import lombok.Data;

@Data
public class MessageModel<Schema extends DalaranModelSchema> {

    private String name;

    private String modelType;

    // TODO 此字段默认在 modelType 之后, 在反序列化时, 可以直接根据已处理对象获取 type
    @JSONField(ordinal = 1, deserializeUsing = ModelDeserializer.class)
    private Schema modelSchema;
}
