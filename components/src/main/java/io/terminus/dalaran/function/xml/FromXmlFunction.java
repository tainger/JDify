package io.terminus.dalaran.function.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.terminus.dalaran.core.component.annotation.MappingFunction;

import java.io.IOException;

@MappingFunction(value = "FromXml", description = "将入参 xml格式字符串转换为对象")
public class FromXmlFunction {

    private final ObjectMapper xmlMapper = new XmlMapper();

    public Object execute(String data) throws IOException {
        return xmlMapper.readValue(data, Object.class);
    }
}
