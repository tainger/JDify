package io.terminus.dalaran.model;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.terminus.dalaran.core.component.annotation.ModelType;
import io.terminus.dalaran.core.component.model.DalaranModelType;
import io.terminus.dalaran.model.schema.CsvModelSchema;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.utils.ModelUtils;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileMessage;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
            if (schema == null) {
                return;
            }
            String data;
            Object body = exchange.getIn().getBody();
            if (schema.getType() == CSVModelType.CARSO) {
                data = carsoFromObject(body, schema);
            } else {
                if (body instanceof String) {
                    data = body + System.lineSeparator();
                } else if (body instanceof byte[]) {
                    data = IOUtils.toString((byte[])body, "UTF-8") + System.lineSeparator();
                } else {
                    data = JSON.toJSONString(body) + System.lineSeparator();
                }
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
        });
    }

    private String carsoFromObject(Object body, CsvModelSchema schema) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] columnArray = schema.getColumnSequence().trim().split(schema.getColumnDelimiter());
        Object in = JSON.toJSON(body);
        if (in instanceof Iterable) {
            for (Object ob: (List)in) {
                Map<String, Object> data = JSON.toJavaObject((JSON)ob, Map.class);
                for (int i = 0; i < columnArray.length; i++) {
                    String trimColumn = columnArray[i].trim();
                    if (data.containsKey(trimColumn)) {
                        stringBuilder.append(data.get(trimColumn));
                    } else {
                        stringBuilder.append(" ");
                    }
                    if (i < columnArray.length - 1) {
                        stringBuilder.append(schema.getDataDelimiter());
                    }
                    if (i == columnArray.length - 1 && schema.isRemainEOF()) {
                        stringBuilder.append(schema.getDataDelimiter());
                    }
                }
                stringBuilder.append("\n");
            }
            if (!schema.isRemainNewLine()) {
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
            }
        } else {
            stringBuilder.append(body.toString());
        }
        return stringBuilder.toString();
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
            if (schema == null) {
                return;
            }
            Object in = exchange.getIn().getBody();
//            if (!(in instanceof GenericFileMessage) && !(in instanceof GenericFile)) {
//                return;
//            }

            byte[] content;
            if (in instanceof GenericFile) {
                GenericFile file = exchange.getIn().getBody(GenericFile.class);
                content = (byte[])file.getBody();
            } else if (in instanceof String) {
                content = IOUtils.toByteArray((String)in);
            } else {
                GenericFileMessage body = exchange.getIn().getBody(GenericFileMessage.class);
                content = (byte[]) body.getGenericFile().getBody();
            }
            String contentStr = new String(content);
            String[] records = contentStr.split(System.lineSeparator());
            List out;
            if (schema.getType() == CSVModelType.CARSO) {
                out = carsoToObject(records, schema);
            } else {
//                out = new ArrayList<>();
//                for (String record : records) {
//                    Map<Integer, Object> recordObj = new HashMap<>();
//                    String[] recordValues = record.split(",");
//                    for (int i = 0; i < recordValues.length; i++) {
//                        recordObj.put(i, recordValues[i]);
//                    }
//                    out.add(recordObj);
//                }
                out = Lists.newArrayList(contentStr);
            }
            exchange.getOut().setBody(out);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        });
    }

    private List<Map<String, Object>> carsoToObject(String[] records, CsvModelSchema schema) {
        String[] columnArray = schema.getColumnSequence().trim().split(schema.getColumnDelimiter());
        List<Map<String, Object>> out = new ArrayList<>();
        for (String record : records) {
            Map<String, Object> recordObj = new HashMap<>();
            String[] recordValues = record.split(schema.getColumnDelimiter());
            for (int i = 0; i < recordValues.length; i++) {
                if (columnArray.length <= i) {
                    break;
                }
                recordObj.put(columnArray[i].trim(), recordValues[i]);
            }
            out.add(recordObj);
        }
        return out;
    }

    @Override
    public String buildTemplateData(Map fields) {
        CsvModelSchema schema = new CsvModelSchema();
        schema.setFields(fields);
        Object body = ModelUtils.buildBody(schema);
        if (body != null) {
            return JSON.toJSONString(body);
        }
        return null;
    }

    @Override
    public CsvModelSchema buildSchemaFromTemplateData(String dataStr) {
        return null;
    }

    @Override
    public CsvModelSchema importTemplateData(DataTemplate dataTemplate, String originSchema) {
        Object body = JSON.parse(dataTemplate.getDataTemplate());
        Map<String, ModelField> root = ModelUtils.parseDataTemplate(body);
        CsvModelSchema schema;
        if (StringUtils.isNotBlank(originSchema)) {
            schema = JSON.parseObject(originSchema, CsvModelSchema.class);
        } else {
            schema = new CsvModelSchema();
        }
        schema.setFields(root);
        return schema;
    }

    @Override
    public CsvModelSchema importDalaranSchema(CsvModelSchema schema) {
        return null;
    }
}
