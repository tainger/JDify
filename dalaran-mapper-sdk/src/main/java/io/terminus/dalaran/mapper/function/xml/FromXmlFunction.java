package io.terminus.dalaran.mapper.function.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.terminus.dalaran.mapper.annotation.MappingFunction;
import io.terminus.dalaran.mapper.function.model.FunctionConstants;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

@MappingFunction(value = "FromXml", description = "将入参 xml格式字符串转换为对象")
public class FromXmlFunction {

    private final ObjectMapper xmlMapper = new XmlMapper();

    public Object execute(String data, String type) throws IOException {
        Class javaClass = Object.class;
        switch (type) {
            case FunctionConstants.ARRAY:
                javaClass = List.class;
                break;
            case FunctionConstants.STRING:
                javaClass = String.class;
                break;
            case FunctionConstants.LONG:
                javaClass = Long.class;
        }
        return xmlMapper.readValue(format(data), javaClass);
    }

    private String format(String data) {
        if (!StringUtils.startsWith(data, "<![CDATA[")) {
            return data;
        }
        return StringUtils.substringBetween(data, "<![CDATA[", "]]");
    }
}
