package io.terminus.dalaran.model;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.CsvModelSchema;
import io.terminus.dalaran.model.schema.DataTemplate;
import org.apache.camel.component.file.GenericFileMessage;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@ModelType(value = "CSV", modelSchema = CsvModelSchema.class)
public class CsvModelType implements DalaranModelType<String, CsvModelSchema> {

    private Logger logger = LoggerFactory.getLogger(CsvModelType.class);

    @Override
    public void fromObject(ProcessorDefinition route, CsvModelSchema schema) {
        route.process(exchange -> {
            exchange.getOut().copyFrom(exchange.getIn());
            List list = exchange.getIn().getBody(List.class);
            String data;
            Object body = exchange.getIn().getBody();
            if (body instanceof String) {
                data = body + System.lineSeparator();
            } else if (body instanceof byte[]) {
                data = IOUtils.toString((byte[])body, "UTF-8") + System.lineSeparator();
            } else {
                data = JSON.toJSONString(body) + System.lineSeparator();
            }
//            if (list == null) {
//                Object body = exchange.getIn().getBody();
//                logger.info(body.getClass().getName());
//                if (body instanceof String) {
//                    data = body + System.lineSeparator();
//                }
//                if (body instanceof byte[]) {
//                    data = IOUtils.toString((byte[])body, "UTF-8") + System.lineSeparator();
//                } else {
////                    data = objectToString(exchange.getIn().getBody()) + System.lineSeparator();
//                    data = JSON.toJSONString(body) + System.lineSeparator();
//                }
//            } else {
//                Object body = exchange.getIn().getBody();
//                if (body instanceof String) {
//                    data = body + System.lineSeparator();
//                } else if (body instanceof byte[]) {
//                    data = IOUtils.toString((byte[])body, "UTF-8") + System.lineSeparator();
//                } else {
//                    data = JSON.toJSONString(body) + System.lineSeparator();
//                }
////                data = (String) list.stream().map(this::objectToString).collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
//            }
            logger.info("data: " + data);
            exchange.getOut().setBody(data.getBytes());
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        });
    }

    // TODO 临时处理一下先, 还有很多场景没有考虑到
    private String objectToString(Object obj) {
        List<String> values = new ArrayList<>();
        if (obj instanceof Iterable) {
            for (Object value : (Iterable) obj) {
                values.add((String) value);
            }
        } else {
            Map<Object, String> mapData;
            if (obj instanceof Map) {
                mapData = ((Map) obj);
                Optional<Long> maxIndex = mapData.keySet().stream().map(key -> {
                    if (key instanceof Number) {
                        return ((Number) key).longValue();
                    }
                    if (key instanceof String) {
                        return Long.parseLong((String) key);
                    }
                    throw new RuntimeException("CSV data index error[" + key.getClass() + ":" + key + "]");
                }).max(Long::compare);
                if (maxIndex.isPresent()) {
                    for (int i = 0; i <= maxIndex.get(); i++) {
                        String value = mapData.getOrDefault(Integer.toString(i), "");
                        values.add(value);
                    }
                }
            }
        }
        return String.join(",", values);
    }

    @Override
    public void toObject(ProcessorDefinition route, CsvModelSchema schema) {
        route.process(exchange -> {
            exchange.getOut().copyFrom(exchange.getIn());
            GenericFileMessage body = exchange.getIn().getBody(GenericFileMessage.class);
            byte[] content = (byte[]) body.getGenericFile().getBody();
            String contentStr = new String(content);
            String[] records = contentStr.split(System.lineSeparator());
            List<Map<Integer, Object>> data = new ArrayList<>();
            for (String record : records) {
                Map<Integer, Object> recordObj = new HashMap<>();
                String[] recordValues = record.split(",");
                for (int i = 0; i < recordValues.length; i++) {
                    recordObj.put(i, recordValues[i]);
                }
                data.add(recordObj);
            }
            exchange.getOut().setBody(data);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        });
    }

    @Override
    public String buildTemplateData(CsvModelSchema schema) {
        return null;
    }

    @Override
    public CsvModelSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }

    @Override
    public CsvModelSchema importTemplateData(DataTemplate dataTemplate) {
        return null;
    }

    @Override
    public CsvModelSchema importDalaranSchema(CsvModelSchema schema) {
        return null;
    }
}
