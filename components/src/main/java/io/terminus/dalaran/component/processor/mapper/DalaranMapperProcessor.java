package io.terminus.dalaran.component.processor.mapper;

import com.github.drapostolos.typeparser.TypeParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.terminus.dalaran.component.processor.mapper.jxpath.DalaranJXPathFactory;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.component.processor.mapper.model.MappingField;
import io.terminus.dalaran.component.processor.mapper.model.MappingFieldType;
import io.terminus.dalaran.model.schema.structure.FieldType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
public class DalaranMapperProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, MappingField>>(){}.getType();
        Map<String, MappingField> messageMapping = gson.fromJson(gson.toJson(exchange.getIn().getHeader(MapperConstants.MESSAGE_MAPPING)), type);
        Map<String, Object> targetBody = exchange.getIn().getBody(Map.class);
        Object destinationBody = convert(messageMapping, targetBody);
        exchange.getOut().setBody(destinationBody);
    }

    private Object convertWithJXPath(Map<String, String> messageMapping, Map<String, String> destination, Map<String, Object> targetBody) {
        JXPathContext targetContext = JXPathContext.newContext(targetBody);

        Map<String, Object> destinationBody = new HashMap<>();
        JXPathContext destinationContext = JXPathContext.newContext(destinationBody);
        destinationContext.setFactory(new DalaranJXPathFactory());

        for (Map.Entry<String, String> entry: messageMapping.entrySet()) {
            Object target = targetContext.getValue(entry.getValue());
            destinationContext.createPathAndSetValue(entry.getKey(),
                    parse(target, FieldType.valueOf(destination.get(entry.getKey()).toUpperCase())));
        }
        return destinationContext.getContextBean();
    }

    private Object convert(Map<String, MappingField> messageMapping, Map<String, Object> targetBody) {
        JXPathContext targetContext = JXPathContext.newContext(targetBody);
        Map<String, Object> destinationBody = new HashMap<>();
        JXPathContext destinationContext = JXPathContext.newContext(destinationBody);
        destinationContext.setFactory(new DalaranJXPathFactory());
        subConvert(destinationContext, targetContext, messageMapping, "");
        return destinationContext.getContextBean();
    }

    private void subConvert(JXPathContext destinationContext, JXPathContext targetContext, Map<String, MappingField> mapping, String parentPath) {
        for (Map.Entry<String, MappingField> entry : mapping.entrySet()) {
            MappingField mappingField = entry.getValue();
            FieldType type = mappingField.getType();

            String path;
            if (StringUtils.isNotBlank(parentPath)) {
                path = parentPath + "/" + entry.getKey();
            } else {
                path = entry.getKey();
            }
            if (type == FieldType.ARRAY) {
                List<Object> target = (List<Object>) targetContext.getValue(entry.getKey(), List.class);
                for (Object ob : target) {
                    JXPathContext subContext = JXPathContext.newContext(ob);
                    subConvert(destinationContext, subContext, mappingField.getMapping(), path);
                }
            } else if (type == FieldType.OBJECT) {
                subConvert(destinationContext, targetContext, mappingField.getMapping(), path);
            } else {
                Object target;
                if (mappingField.getMappingType() == MappingFieldType.MAPPING) {
                    target = targetContext.getValue(entry.getValue().getValue());
                } else {
                    target = mappingField.getValue();
                }
                destinationContext.createPathAndSetValue(path, parse(target, mappingField.getMappingFieldType()));
            }
        }
    }

    private Object parse(Object target, FieldType destination) {
        String input = target.toString();
        TypeParser parser = TypeParser.newBuilder().build();
        switch (destination) {
            case INTEGER:
                return parser.parse(input, Integer.class);
            case LONG:
                return parser.parse(input, Long.class);
            case SHORT:
                return parser.parse(input, Short.class);
            case FLOAT:
                return parser.parse(input, Float.class);
            case DOUBLE:
                return parser.parse(input, Double.class);
            case NUMBER:
                return parser.parse(input, Number.class);
            case BOOLEAN:
                return parser.parse(input, Boolean.class);
            default:
                return target;
        }
    }
}
