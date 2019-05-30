package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.component.processor.mapper.model.*;
import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.model.FieldType;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.model.ModelField;
import io.terminus.dalaran.core.model.schema.JsonSchema;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by jingdi on 2019/3/18
 */
@Processor(
        value = "mapper-convert", configType = DalaranMapperConfig.class,
        inputSerializeType = BodySerializeType.Object,
        outputSerializeType = BodySerializeType.Object
)
public class DalaranMessageMapper implements DalaranProcessor<DalaranMapperConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranMapperConfig config) {
        Map<String, String> arrayFieldMapping = new HashMap<>();
        Map<String, SimpleMappingField> messageMapping = config.getMessageMapping();
        MessageModel<JsonSchema> in = config.getInModel();
        MessageModel<JsonSchema> out = config.getOutModel();
        DalaranMapperProcessor processor = new DalaranMapperProcessor(transfer(messageMapping, in, out, arrayFieldMapping));
        route.process(processor);
    }

    private Map<String, MappingField> transfer(Map<String, SimpleMappingField> simpleMapping, MessageModel<JsonSchema> in, MessageModel<JsonSchema> out, Map<String, String> arrayMapping) {
        Map<String, MappingField> messageMapping = new HashMap<>();
        MappingField mappingField = new MappingField();
        messageMapping.put(MapperConstants.MODEL_ROOT, mappingField);
        ModelField inModel = in.getModelSchema().getFields().get(MapperConstants.MODEL_ROOT);
        ModelField outModel = out.getModelSchema().getFields().get(MapperConstants.MODEL_ROOT);
        mappingField.setType(outModel.getType());
        mappingField.setSubType(outModel.getSubType());
        mappingField.setMapping(new HashMap<>());
        buildArrayMapping(simpleMapping, inModel, outModel, arrayMapping);
        buildMessageMapping(simpleMapping, mappingField, inModel, outModel, arrayMapping);
        return messageMapping;
    }

    private void buildMessageMapping(Map<String, SimpleMappingField> simpleMapping, MappingField mappingField, ModelField inModel, ModelField outModel, Map<String, String> arrayMapping) {
        TreeMap<String, SimpleMappingField> sortedMapping = new TreeMap<>(simpleMapping);
        sortedMapping.forEach((outPath, inField) -> {
            buildSubMapping(mappingField, inField, inModel, StringUtils.substringAfter(outPath, "."), outModel, arrayMapping);
        });
    }

    private void buildSubMapping(MappingField mappingField, SimpleMappingField inMappingField, ModelField inModel, String outPath, ModelField outModel, Map<String, String> arrayFieldMapping) {
        MappingType mappingType = inMappingField.getMappingType();
        String inPath;
        if (mappingType == MappingType.MAPPING) {
            inPath = StringUtils.substringAfter(inMappingField.getValue(), ".");
        } else {
            inPath = inMappingField.getValue();
        }

        List<String> outFields = new ArrayList<>();
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
                    if (inField != null && mappingType == MappingType.MAPPING) {
                        field.setMappingFieldType(inField.getType());
                        field.setMappingType(inMappingField.getMappingType());

                        if (child.getType() == FieldType.ARRAY && child.getSubType() == FieldType.OBJECT) {
                            field.setValue(StringUtils.substringAfterLast(inPath, "."));
                            String path;
                            if (subList.size() > 1) {
                                path = buildFieldPath(subList.subList(0, subList.size() - 1));
                            } else {
                                path = subList.get(0);
                            }
                            if (arrayFieldMapping.containsKey(path)) {
                                child.setArrayFieldPath(arrayFieldMapping.get(path));
                            }
                        } else {
                            field.setValue(inPath.replaceAll("\\.", "/"));
                        }
                    } else {
                        field.setValue(inPath);
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

    private void buildArrayMapping(Map<String, SimpleMappingField> fieldMap, ModelField inModel, ModelField outModel, Map<String, String> arrayFieldMapping) {
        Flag flag = new Flag(false);
        fieldMap.forEach((outPath, inField) -> {
            if (inField.getMappingType() == MappingType.MAPPING) {
                List<String> outSubFields = new ArrayList<>();
                CollectionUtils.addAll(outSubFields, StringUtils.substringAfter(outPath, ".").split("\\."));
//                CollectionUtils.addAll(outSubFields, outPath.split("\\."));

                List<String> inSubFields = new ArrayList<>();
                CollectionUtils.addAll(inSubFields, StringUtils.substringAfter(inField.getValue(), ".").split("\\."));
//                CollectionUtils.addAll(inSubFields, inField.getValue().split("\\."));

                int startIdx = 0;
                int level = 0;
                for (int i = 0; i < outSubFields.size(); i++) {
                    List<String> outSubPath = outSubFields.subList(0, i + 1);
                    if (arrayFieldMapping.containsKey(buildFieldPath(outSubPath))) {
                        level++;
                        continue;
                    }
                    ModelField outField = getField(outSubPath, outModel);
                    if ((outModel.getType() == FieldType.ARRAY && outModel.getSubType() == FieldType.OBJECT && i == 0) || (outField != null && outField.getType() == FieldType.ARRAY && outField.getSubType() == FieldType.OBJECT)) {

                        if (level == 0 && inModel.getType() == FieldType.ARRAY && inModel.getSubType() == FieldType.OBJECT) {
                            arrayFieldMapping.put(buildFieldPath(outSubPath), "");
                            level++;
                            flag.setValue(true);
                            continue;
                        }

                        int subLevel = 0;
                        if (inModel.getType() == FieldType.ARRAY && inModel.getSubType() == FieldType.OBJECT) {
                            subLevel++;
                        }

                        for (int j = startIdx; j < inSubFields.size(); j++) {
                            List<String> inSubPath = inSubFields.subList(0, j + 1);
                            ModelField field = getField(inSubPath, inModel);
                            if (field != null && field.getType() == FieldType.ARRAY && field.getSubType() == FieldType.OBJECT) {
                                if (subLevel == level) {
                                    if (level > 0) {
                                        if (flag.isValue() && level < 2) {
                                            arrayFieldMapping.put(buildFieldPath(outSubPath), buildFieldPath(inSubPath));
                                        } else {
                                            arrayFieldMapping.put(buildFieldPath(outSubPath), inSubPath.get(inSubPath.size() - 1));
                                        }
                                    } else {
                                        arrayFieldMapping.put(buildFieldPath(outSubPath), buildFieldPath(inSubPath));
                                    }
                                    startIdx = j;
                                    level++;
                                    break;
                                }
                                subLevel++;
                            }
                        }
                    }
                }
            }
        });
    }

    private String buildFieldPath(List<String> subPaths) {
        StringBuilder fieldPath = new StringBuilder();
        for (int i = 0; i < subPaths.size(); i++) {
            fieldPath.append(subPaths.get(i));
            if (i < subPaths.size() - 1) {
                fieldPath.append("/");
            }
        }
        return fieldPath.toString();
    }
}
