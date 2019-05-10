package io.terminus.dalaran.component.processor.mapper;

import com.github.drapostolos.typeparser.TypeParser;
import io.terminus.dalaran.FieldType;
import io.terminus.dalaran.component.processor.mapper.jxpath.DalaranJXPathFactory;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.component.processor.mapper.model.MappingField;
import io.terminus.dalaran.component.processor.mapper.model.MappingType;
import io.terminus.dalaran.component.processor.mapper.model.SimpleMappingField;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringUtils;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by jingdi on 2019/3/18
 */
public class DalaranMapperProcessor implements Processor {

    private final DalaranMapperConfig mapperConfig;

    public DalaranMapperProcessor(DalaranMapperConfig mapperConfig) {
        this.mapperConfig = mapperConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, SimpleMappingField> messageMapping = mapperConfig.getMessageMapping();
        MessageModel<JsonSchema> in = mapperConfig.getInModel();
        MessageModel<JsonSchema> out = mapperConfig.getOutModel();
        Object targetBody = exchange.getIn().getBody();
        Object destinationBody = convert(transfer(messageMapping, in, out), targetBody);
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

    private Map<String, MappingField> transfer(Map<String, SimpleMappingField> simpleMapping, MessageModel<JsonSchema> in, MessageModel<JsonSchema> out) {
        Map<String, MappingField> messageMapping = new HashMap<>();
        MappingField mappingField = new MappingField();
        messageMapping.put(MapperConstants.MODEL_ROOT, mappingField);
        ModelField inModel = in.getModelSchema().getFields().get(MapperConstants.MODEL_ROOT);
        ModelField outModel = out.getModelSchema().getFields().get(MapperConstants.MODEL_ROOT);
        mappingField.setType(outModel.getType());
        mappingField.setSubType(outModel.getSubType());
        mappingField.setMapping(new HashMap<>());
        buildMessageMapping(simpleMapping, mappingField, inModel, outModel);
        return messageMapping;
    }

    private void buildMessageMapping(Map<String, SimpleMappingField> simpleMapping, MappingField mappingField, ModelField inModel, ModelField outModel) {
        TreeMap<String, SimpleMappingField> sortedMapping = new TreeMap<>(simpleMapping);
        sortedMapping.forEach((inPath, outField) -> {
            buildSubMapping(mappingField, outField, outModel, StringUtils.substringAfter(inPath, "."), inModel);
        });
    }

    private void buildSubMapping(MappingField mappingField, SimpleMappingField outMappingField, ModelField outModel, String inPath, ModelField inModel) {
        List<String> outFields = new ArrayList<>();
        String outPath = StringUtils.substringAfter(outMappingField.getValue(), ".");
        CollectionUtils.addAll(outFields, outPath.split("\\."));

        MappingField child = mappingField;
        for (int i = 0; i < outFields.size(); i++) {

            if (child.getMapping().containsKey(outFields.get(i))) {
                child = child.getMapping().get(outFields.get(i));
                continue;
            }

            MappingField field = new MappingField();
            List<String> subList;
            subList = outFields.subList(0, i + 1);
            ModelField outField = getField(subList, outModel);
            if (outField != null) {
                field.setType(outField.getType());
                field.setSubType(outField.getSubType());
                if (i == outFields.size() - 1) {
                    ModelField inField = getField(Arrays.asList(inPath.split("\\.")), inModel);
                    if (inField != null) {
                        field.setMappingFieldType(inField.getType());
                        field.setMappingType(outMappingField.getMappingType());
                        if (child.getType() == FieldType.ARRAY && child.getSubType() == FieldType.OBJECT) {
                            field.setValue(StringUtils.substringAfterLast(inPath, "."));
                        } else {
                            field.setValue(inPath.replaceAll("\\.", "/"));
                        }
                    }
                }
            }
            child.getMapping().put(outFields.get(i), field);
            child = field;
            if (child.getMapping() == null) {
                child.setMapping(new HashMap<>());
            }
        }
    }

    private ModelField getField(List<String> fields, ModelField model) {
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }
        ModelField child = model.getFields().get(fields.get(0));
        for (int i = 1; i < fields.size(); i++) {
            child = child.getFields().get(fields.get(i));
        }
        return child;
    }

    private Object convert(Map<String, MappingField> messageMapping, Object targetBody) {
        JXPathContext targetContext = JXPathContext.newContext(targetBody);
        Map<String, Object> destinationBody = new HashMap<>();
        JXPathContext destinationContext = JXPathContext.newContext(destinationBody);
        destinationContext.setFactory(new DalaranJXPathFactory());
        Boolean flag = false;
        subConvert(destinationContext, targetContext, messageMapping, "", flag);
        return handleContext(destinationContext, flag);
    }

    private void subConvert(JXPathContext destinationContext, JXPathContext targetContext, Map<String, MappingField> mapping, String parentPath, Boolean flag) {
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
                    List<Object> target;
                    if (path.equalsIgnoreCase(MapperConstants.MODEL_ROOT)) {
                        target = (List<Object>) targetContext.getContextBean();
                    } else {
                        target = (List<Object>) targetContext.getValue(path, List.class);
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
                        try {
                            Field field= flag.getClass().getDeclaredField("value");
                            field.setAccessible(true);
                            field.set(flag, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    List<Object> target = (List<Object>) targetContext.getValue(entry.getValue().getValue(), List.class);
//                    if (target == null) {
//                        target = new ArrayList<>();
//                    }
                    destinationContext.createPathAndSetValue(path, target);
                }
            } else if (type == FieldType.OBJECT) {
                subConvert(destinationContext, targetContext, mappingField.getMapping(), path, flag);
            } else {
                Object target;
                if (mappingField.getMappingType() == MappingType.MAPPING) {
                    target = targetContext.getValue(entry.getValue().getValue());
                } else {
                    target = mappingField.getValue();
                }
                Object destinationValue = null;
                if (target != null) {
                    destinationValue = parse(target, mappingField.getMappingFieldType());
                }
                destinationContext.createPathAndSetValue(path, destinationValue);
            }
        }
    }

    private Object handleContext(JXPathContext context, Boolean flag) {
        if (flag) {
            Map<String, Object> body = (Map<String, Object>)context.getContextBean();
            return body.get(MapperConstants.MODEL_ROOT);
        }
        return context.getContextBean();
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
