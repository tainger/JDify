package io.terminus.dalaran.component.processor.mapper.model.convert;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;

/**
 * Created by jingdi on 2019/7/31
 */
public class MapperValueDeserializer implements ObjectDeserializer {

    @Override
    public <T> T deserialze(DefaultJSONParser defaultJSONParser, Type type, Object o) {

        return null;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
