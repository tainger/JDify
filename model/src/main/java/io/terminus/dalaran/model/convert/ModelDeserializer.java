package io.terminus.dalaran.model.convert;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;

import java.lang.reflect.Type;

public class ModelDeserializer implements ObjectDeserializer {
    // TODO 这里有时间改成注册制, 写死太蛋疼
    @Override
    public Object deserialze(DefaultJSONParser parser, Type type, Object obj) {
        if (parser.getContext().object instanceof MessageModel) {
            Class<? extends DalaranModelSchema> modelSchemaClass = DalaranModelSchema.getModelSchemaClass(((MessageModel) parser.getContext().object).getModelType());
            return parser.parseObject(modelSchemaClass);
        }
        return null;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
