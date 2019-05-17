package io.terminus.dalaran.component.processor.mapper;

import com.github.drapostolos.typeparser.TypeParser;
import io.terminus.dalaran.FieldType;
import io.terminus.dalaran.component.processor.mapper.jxpath.DalaranJXPathFactory;
import io.terminus.dalaran.component.processor.mapper.model.Flag;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.component.processor.mapper.model.MappingField;
import io.terminus.dalaran.component.processor.mapper.model.MappingType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
public class DalaranMapperProcessor implements Processor, Traceable {

    private final Map<String, MappingField> messageMapping;

    public DalaranMapperProcessor(Map<String, MappingField> messageMapping) {
        this.messageMapping = messageMapping;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object targetBody = exchange.getIn().getBody();
        Object destinationBody = convert(messageMapping, targetBody);
        exchange.getOut().setBody(destinationBody);
    }

    private Object convertWithJXPath(Map<String, String> messageMapping, Map<String, String> destination, Map<String, Object> targetBody) {
        JXPathContext targetContext = JXPathContext.newContext(targetBody);

        Object destinationBody = new Object();
        JXPathContext destinationContext = JXPathContext.newContext(destinationBody);
        destinationContext.setFactory(new DalaranJXPathFactory());

        for (Map.Entry<String, String> entry: messageMapping.entrySet()) {
            Object target = targetContext.getValue(entry.getValue());
            destinationContext.createPathAndSetValue(entry.getKey(),
                    parse(target, FieldType.valueOf(destination.get(entry.getKey()).toUpperCase())));
        }
        return destinationContext.getContextBean();
    }

    private Object convert(Map<String, MappingField> messageMapping, Object targetBody) {
        JXPathContext targetContext = JXPathContext.newContext(targetBody);
        Map<String, Object> destinationBody = new HashMap<>();
        JXPathContext destinationContext = JXPathContext.newContext(destinationBody);
        destinationContext.setFactory(new DalaranJXPathFactory());
        Flag flag = new Flag(false);
        subConvert(destinationContext, targetContext, messageMapping, "", flag);
        return handleContext(destinationContext, flag);
    }

    private void subConvert(JXPathContext destinationContext, JXPathContext targetContext, Map<String, MappingField> mapping, String parentPath, Flag flag) {
        for (Map.Entry<String, MappingField> entry : mapping.entrySet()) {
            MappingField mappingField = entry.getValue();
            FieldType type = mappingField.getType();
            FieldType subType = mappingField.getSubType();

            String path = entry.getKey();
            if (parentPath.equalsIgnoreCase(MapperConstants.MODEL_ROOT)) {
                path = entry.getKey();
            } else if (StringUtils.isNotBlank(parentPath)) {
                path = parentPath + "/" + entry.getKey();
            }

            if (type == FieldType.ARRAY) {
                if (subType == FieldType.OBJECT) {
                    List<Object> target = null;
                    String arrayPath = mappingField.getArrayFieldPath();
                    try {
                        if (path.equalsIgnoreCase(MapperConstants.MODEL_ROOT)) {
                            if (StringUtils.isNotBlank(arrayPath)) {
                                target = (List<Object>) targetContext.getValue(arrayPath, List.class);
                            } else {
                                target = (List<Object>) targetContext.getContextBean();
                            }
                        } else {
                            target = (List<Object>) targetContext.getValue(arrayPath, List.class);
                        }
                    } catch (JXPathNotFoundException e) {
//                        e.printStackTrace();
                    }

                    List<Object> subList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(target)) {
                        for (Object ob : target) {
                            JXPathContext subContext = JXPathContext.newContext(ob);

                            Map<String, Object> subBody = new HashMap<>();
                            JXPathContext subDestinationContext = JXPathContext.newContext(subBody);
                            subDestinationContext.setFactory(new DalaranJXPathFactory());
                            subConvert(subDestinationContext, subContext, mappingField.getMapping(), "", flag);
                            subList.add(subDestinationContext.getContextBean());
                        }
                    }
                    destinationContext.createPathAndSetValue(path, subList);

                    if (path.equalsIgnoreCase(MapperConstants.MODEL_ROOT)) {
                       flag.setValue(true);
                    }
                } else {
                    List<Object> target = (List<Object>) targetContext.getValue(entry.getValue().getValue(), List.class);
                    destinationContext.createPathAndSetValue(path, target);
                }
            } else if (type == FieldType.OBJECT) {
                subConvert(destinationContext, targetContext, mappingField.getMapping(), path, flag);
            } else {
                Object target = null;
                if (mappingField.getMappingType() == MappingType.MAPPING) {
                    try {
                        target = targetContext.getValue(entry.getValue().getValue());
                    } catch (JXPathNotFoundException e) {
//                        e.printStackTrace();
                    }
                } else {
                    target = mappingField.getValue();
                }
                Object destinationValue = null;
                if (target != null) {
                    destinationValue = parse(target, mappingField.getMappingFieldType());
                    destinationContext.createPathAndSetValue(path, destinationValue);
                }
            }
        }
    }

    private Object handleContext(JXPathContext context, Flag flag) {
        if (flag.isValue()) {
            Map<String, Object> body = (Map<String, Object>)context.getContextBean();
            return body.get(MapperConstants.MODEL_ROOT);
        }
        return context.getContextBean();
    }

    private Object parse(Object target, FieldType destination) {
        String input = target.toString();
        TypeParser parser = TypeParser.newBuilder().build();
        if (destination != null) {
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
        return target;
    }

    @Override
    public String getTraceLabel() {
        return "DalaranMapper";
    }
}
