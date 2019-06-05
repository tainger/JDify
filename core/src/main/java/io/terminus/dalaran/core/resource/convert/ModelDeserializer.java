package io.terminus.dalaran.core.resource.convert;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.model.schema.JsonSchema;
import io.terminus.dalaran.core.model.schema.XMLSchema;

import java.lang.reflect.Type;

public class ModelDeserializer implements ObjectDeserializer {
    @Override
    public Object deserialze(DefaultJSONParser parser, Type type, Object obj) {
        if (parser.getContext().object instanceof MessageModel) {
            switch (((MessageModel) parser.getContext().object).getModelType()) {
                case JSON:
                    return parser.parseObject(JsonSchema.class);
                case XML:
                    return parser.parseObject(XMLSchema.class);
                case OBJECT:
                    return parser.parseObject();
            }
        }
        return null;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
