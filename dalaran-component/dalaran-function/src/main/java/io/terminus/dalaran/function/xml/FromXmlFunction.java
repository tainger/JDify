package io.terminus.dalaran.function.xml;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.terminus.dalaran.core.component.annotation.MappingFunction;
import org.apache.commons.lang3.StringUtils;
import org.json.XML;

import java.io.IOException;

@MappingFunction(value = "FromXml", description = "将入参 xml格式字符串转换为对象")
public class FromXmlFunction {

    private final ObjectMapper xmlMapper = new XmlMapper();

    public Object execute(String data, String type) throws IOException {
//        xmlMapper.registerModule(new SimpleModule().addDeserializer(
//                JsonNode.class,
//                new DuplicateToArrayJsonNodeDeserializer()
//        ));
//        JsonNode node = xmlMapper.readTree(format(data));
//        ObjectMapper jsonMapper = new ObjectMapper();
//        String json = jsonMapper.writeValueAsString(node);
        return JSON.parse(XML.toJSONObject(format(data)).toString());
    }

    private String format(String data) {
        if (!StringUtils.startsWith(data, "<![CDATA[")) {
            return data;
        }
        return StringUtils.substringBetween(data, "<![CDATA[", "]]");
    }
}
