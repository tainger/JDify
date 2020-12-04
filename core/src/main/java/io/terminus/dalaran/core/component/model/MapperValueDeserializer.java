package io.terminus.dalaran.core.component.model;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;

public class MapperValueDeserializer implements ObjectDeserializer {

    @Override
    public Object deserialze(DefaultJSONParser parser, Type type, Object o) {
        if (parser.getContext().object instanceof SimpleMapping) {
            switch (((SimpleMapping)parser.getContext().object).getMappingType()) {
                case FUNCTION:
                    return parser.parseObject(MappingFunction.class);
                case MAPPING:
                case STATIC:
                    return parser.parseObject(String.class);
            }
        }
        return null;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
