package io.terminus.dalaran.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.terminus.dalaran.core.component.annotation.MappingFunction;

@MappingFunction(value = "ToJson", description = "将入参转换为 Json 格式")
public class ToJsonFunction {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Object execute(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
