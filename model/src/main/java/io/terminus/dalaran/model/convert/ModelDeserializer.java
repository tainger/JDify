package io.terminus.dalaran.model.convert;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import io.terminus.dalaran.model.schema.SoapSchema;
import io.terminus.dalaran.model.schema.XMLSchema;

import java.lang.reflect.Type;

public class ModelDeserializer implements ObjectDeserializer {
    // TODO 这里有时间改成注册制, 写死太蛋疼
    @Override
    public Object deserialze(DefaultJSONParser parser, Type type, Object obj) {
        if (parser.getContext().object instanceof MessageModel) {
            switch (((MessageModel) parser.getContext().object).getModelType()) {
                case JSON:
                    return parser.parseObject(JsonSchema.class);
                case XML:
                    return parser.parseObject(XMLSchema.class);
                case OBJECT:
                    return parser.parseObject(ObjectSchema.class);
                case SOAP:
                    return parser.parseObject(SoapSchema.class);
            }
        }
        return null;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
