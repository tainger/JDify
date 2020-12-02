package io.terminus.dalaran.function.xml;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.terminus.dalaran.core.component.annotation.MappingFunction;
import io.terminus.dalaran.function.model.FunctionConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;

@Slf4j
@MappingFunction(value = "ToXml", description = "将入参转换为xml格式的字符串")
public class ToXmlFunction {

    private final ObjectMapper xmlMapper = new XmlMapper();

    public Object execute(Object data, String type) throws IOException {
        if (data instanceof byte[]) {
            data = IOUtils.toString((byte[])data, "utf-8");
        }
        switch (type) {
            case FunctionConstants.ARRAY:
                JSONArray jsonArray = new JSONArray(data);
                return XML.toString(jsonArray);
            case FunctionConstants.OBJECT:
                JSONObject jsonObject = new JSONObject(JSON.toJSONString(JSON.parse((String) data)));
                return XML.toString(jsonObject);
        }
        return xmlMapper.writeValueAsString(data);
    }
}
