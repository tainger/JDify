package io.terminus.dalaran.mapper.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;

import java.io.IOException;

@FunctionFilter
@MappingFunction(value = "FromJson", description = "将入参 Json 字符串转换为对象")
public class FromJsonFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Object execute(String json) throws IOException {
        return objectMapper.readValue(json, Object.class);
    }
}
