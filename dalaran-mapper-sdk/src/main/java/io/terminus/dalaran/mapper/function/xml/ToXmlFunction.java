package io.terminus.dalaran.mapper.function.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;

import java.io.IOException;

@FunctionFilter
@MappingFunction(value = "ToXml", description = "将入参转换为xml格式的字符串")
public class ToXmlFunction {

    private final ObjectMapper xmlMapper = new XmlMapper();

    public Object execute(Object data) throws IOException {
        return xmlMapper.writeValueAsString(data);
    }
}
