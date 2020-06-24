package io.terminus.dalaran.mapper.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;

@FunctionFilter
@MappingFunction(value = "ToJson", description = "将入参转换为 Json 格式")
public class ToJsonFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Object execute(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
