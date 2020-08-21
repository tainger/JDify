package io.terminus.dalaran.model.soap.processor;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.terminus.dalaran.function.xml.DuplicateToArrayJsonNodeDeserializer;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.soap.jackson.DalaranObjectDeserializer;
import io.terminus.dalaran.model.soap.jackson.DalaranXMLStreamReader;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/6
 */
public class SoapToObjectProcessor implements Processor, Traceable {

    private final Map<String, ModelField> fields;

    public SoapToObjectProcessor(Map<String, ModelField> fields) {
        this.fields = fields;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String in = exchange.getIn().getBody(String.class);
        Object body = parseSoapBody(in);
        exchange.getOut().setBody(body);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }

    public Object parseSoapBody(String body) throws Exception {
        InputStream is = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        DalaranXMLStreamReader sr = new DalaranXMLStreamReader(XMLInputFactory.newFactory().createXMLStreamReader(is));
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new SimpleModule().
                addDeserializer(Object.class, new DalaranObjectDeserializer()).
                addDeserializer(JsonNode.class, new DuplicateToArrayJsonNodeDeserializer()
        ));
        Object in = xmlMapper.readValue(sr, Object.class);
        Map map;
        if (in instanceof String) {
            map = JSON.parseObject((String)in, Map.class);
        } else {
            map = (Map)in;
        }
        if (MapUtils.isEmpty(fields) || map.get("Body") == null) {
            return new HashMap<>();
        }
        Object destination = buildBody((Map)map.get("Body"), new HashMap(), fields);
        return destination;
    }

    private Object buildBody(Map origin, Map destination,  Map<String, ModelField> fields) {
        if (MapUtils.isEmpty(fields)) {
            return origin;
        }
        fields.forEach((name, field) -> {
            if (!origin.containsKey(name)) {
                return;
            }
            Object data = origin.get(name);
            if (data == null) {
                return;
            }
            if (data instanceof String && StringUtils.isBlank((String)data)) {
                return;
            }
            if (field.getType() == FieldType.ARRAY) {
                List currentDestination;
                if (data instanceof Iterable) {
                    currentDestination = (List)data;
                } else {
                    currentDestination = Collections.singletonList(data);
                }
                destination.put(name, currentDestination);
                if (field.getSubType() == FieldType.OBJECT) {
                    if (data instanceof Iterable) {
                        List currentOrigin = (List) data;
                        for (int i = 0; i < currentOrigin.size(); i++) {
                            buildBody((Map) currentOrigin.get(i), (Map)currentDestination.get(i), field.getFields());
                        }
                    } else {
                        if (CollectionUtils.isNotEmpty(currentDestination)) {
                            buildBody((Map)data, (Map)currentDestination.get(0), field.getFields());
                        }
                    }
                }
            } else {
                destination.put(name, data);
                if (field.getType() == FieldType.OBJECT) {
                    buildBody((Map)data, (Map)data, field.getFields());
                }
            }
        });
        return destination;
    }

    @Override
    public String getTraceLabel() {
        return "SoapConvert: SoapToObject";
    }
}
