package io.terminus.dalaran.mapper.handler;

import io.terminus.dalaran.mapper.context.DalaranFunctionContext;
import io.terminus.dalaran.mapper.model.*;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.*;

public class DalaranMapperBuilder {

    private DalaranFunctionContext functionContext;

    public DalaranMapperBuilder(DalaranFunctionContext functionContext) {
        this.functionContext = functionContext;
    }

    public DalaranMappingConfig transfer(Map<String, SimpleMapping> simpleMapping, MessageModel in, MessageModel out) {
        DalaranMappingConfig mappingConfig = new DalaranMappingConfig();
        if (MapUtils.isEmpty(simpleMapping)) {
            return mappingConfig;
        }
        List<MessageMapping> messageMappings = new ArrayList<>();
        simpleMapping.forEach((path, mapping) -> {
            MessageMapping messageMapping = new MessageMapping();
            if (mapping.getMappingType() == MappingType.CONTEXT) {
                MappingContext mappingContext = (MappingContext) mapping.getValue();
                String contextKey = mappingContext.getContextKey();
                if (StringUtils.isBlank(contextKey)) {
                    return;
                }
                messageMapping.setStatus(MappingStatus.CORRECT);
                messageMapping.setMappingType(MappingType.CONTEXT);
                SourceField field = new SourceField();
                field.setPath(contextKey);
                messageMapping.setSourceFields(Collections.singletonList(field));
                messageMappings.add(messageMapping);
                return;
            }
            if (mapping.getMappingType() == MappingType.STATIC) {
                messageMapping.setStatus(MappingStatus.CORRECT);
                messageMapping.setMappingType(MappingType.STATIC);
                SourceField field = new SourceField();
                field.setPath(mapping.getValue().toString());
                messageMapping.setSourceFields(Collections.singletonList(field));
                messageMappings.add(messageMapping);
                return;
            }
            buildMapping(messageMapping, path, mapping, in, out);
            messageMappings.add(messageMapping);
        });
        messageMappings.sort(new MessageMappingComparator());
        mappingConfig.setMessageMappings(messageMappings);
        SimpleMappingField sourceRoot = new SimpleMappingField();
        sourceRoot.setType(in.getModelSchema().getFields().get(MapperConstants.MODEL_ROOT).getType());
        mappingConfig.setSourceRoot(sourceRoot);

        SimpleMappingField destinationRoot = new SimpleMappingField();
        destinationRoot.setType(out.getModelSchema().getFields().get(MapperConstants.MODEL_ROOT).getType());
        mappingConfig.setDestinationRoot(destinationRoot);

        return mappingConfig;
    }

    private void buildMapping(MessageMapping messageMapping, String path, SimpleMapping mapping, MessageModel in, MessageModel out) {
        String destinationPath = StringUtils.substringAfter(path, MapperConstants.MODEL_ROOT + ".");
        messageMapping.setPath(destinationPath);

        MappingType mappingType = mapping.getMappingType();
        messageMapping.setMappingType(mappingType);

        List<TemporarySourcePath> sourcePaths = buildSourcePaths(mappingType, mapping.getValue(), messageMapping);
        Map<String, ModelField> inField = in.getModelSchema().getFields();
        Map<String, ModelField> outField = out.getModelSchema().getFields();

        SimpleMappingField destinationField = new SimpleMappingField();
        MappingProperty mappingProperty = buildMappingField(path, outField, destinationField, false);
        FieldType destinationType = mappingProperty.getFieldType();
        messageMapping.setStatus(mappingProperty.getStatus());
        if (mappingProperty.getStatus() == MappingStatus.ERROR) {
            return;
        }
        messageMapping.setDestinationField(destinationField);

        List<SourceField> sourceFields = new ArrayList<>();
        MappingProperty property = new MappingProperty();
        for (TemporarySourcePath sourcePath : sourcePaths) {
            SourceField sourceField = new SourceField();
            if (sourcePath.getType() == ParamType.STATIC) {
                sourceField.setPath(sourcePath.getValue());
                sourceField.setParamType(ParamType.STATIC);
            } else {
                property = buildSourceField(sourcePath.getValue(), inField, sourceField, mappingProperty.isComplex());
                if (property.getStatus() == MappingStatus.ERROR) {
                    break;
                }
                sourceField.setParamType(ParamType.DYNAMIC);
            }
            sourceFields.add(sourceField);
        }
        messageMapping.setSourceFields(sourceFields);
        messageMapping.setComplex(property.isComplex());
        messageMapping.setStatus(property.getStatus());
        messageMapping.setType(destinationType);
    }

    private List<TemporarySourcePath> buildSourcePaths(MappingType mappingType, Object value, MessageMapping messageMapping) {
        List<TemporarySourcePath> sourcePaths = new ArrayList<>();

        if (mappingType == MappingType.FUNCTION) {
            MappingFunction function = (MappingFunction) value;
            function.setSourcePaths(buildFunctionSourcePaths(function));
            messageMapping.setFunction(function);

//            DalaranFunctionContext functionContext = dalaranContext.getDalaranFunctionContext();
            MappingFunctionInfo functionInfo = functionContext.getFunctionByKey(function.getId());
            if (functionInfo != null) {
                String[] params = functionInfo.getParams();
                String temKey = function.getTemKey();
                if (temKey == null) {
                    temKey = "";
                }
                Map<String, FunctionParam> functionParams = function.getParams();
                for (String param : params) {
                    FunctionParam functionParam = functionParams.get(temKey + param);
                    if (functionParam == null) {
                        continue;
                    }
                    sourcePaths.add(new TemporarySourcePath(functionParam.getType(), functionParam.getValue()));
                }
            }
        } else {
            sourcePaths.add(new TemporarySourcePath(ParamType.DYNAMIC, value.toString()));
        }
        return sourcePaths;
    }

    private MappingProperty buildSourceField(String sourcePath, Map<String, ModelField> in, SourceField sourceField, boolean complex) {
        SimpleMappingField simpleMappingField = new SimpleMappingField();
        MappingProperty mappingProperty = buildMappingField(sourcePath, in, simpleMappingField, complex);
        sourceField.setField(simpleMappingField);
        sourceField.setPath(StringUtils.substringAfter(sourcePath, MapperConstants.MODEL_ROOT + "."));
        return mappingProperty;
    }

    private MappingProperty buildMappingField(String path, Map<String, ModelField> modelField, SimpleMappingField simpleMappingField, boolean complex) {
        MappingStatus status = MappingStatus.CORRECT;
        FieldType type = null;
        ModelField root = modelField.get(MapperConstants.MODEL_ROOT);
        if (root.getType() == FieldType.ARRAY) {
            complex = true;
        }

        Map<String, ModelField> child = root.getFields();
        String[] fields = StringUtils.split(path, ".");
        Map<String, ModelField> temporaryField = child;
        SimpleMappingField temporaryMappingField = simpleMappingField;
        for (int i = 1; i < fields.length; i++) {
            String fieldName = fields[i];
            ModelField field = temporaryField.get(fieldName);
            if (field == null) {
                return new MappingProperty(complex, MappingStatus.ERROR, type);
            }
            temporaryMappingField.setName(fieldName);
            temporaryMappingField.setType(field.getType());

            if (i == fields.length - 1) {
                temporaryMappingField.setLocal(FieldLocal.END);
                type = field.getType();
            } else {
                if (field.getType() == FieldType.ARRAY) {
                    complex = true;
                }
                temporaryMappingField.setLocal(FieldLocal.MIDDLE);
                temporaryField = field.getFields();
                SimpleMappingField childField = new SimpleMappingField();
                temporaryMappingField.setChild(childField);
                temporaryMappingField = childField;
            }
        }
        return new MappingProperty(complex, status, type);
    }

    private Map<String, FunctionParam> buildFunctionSourcePaths(MappingFunction mappingFunction) {
        Map<String, FunctionParam> sourcePaths = new LinkedHashMap<>();
        if (mappingFunction != null && MapUtils.isNotEmpty(mappingFunction.getParams())) {
            mappingFunction.getParams().forEach((k, v) -> {
                if (v.getType() == ParamType.DYNAMIC) {
                    String path = StringUtils.substringAfter(v.getValue(), MapperConstants.MODEL_ROOT + ".");
                    sourcePaths.put(path, v);
                } else {
                    sourcePaths.put(v.getValue(), v);
                }
            });
        }
        return sourcePaths;
    }

    private static class MessageMappingComparator implements Comparator<MessageMapping> {
        @Override
        public int compare(MessageMapping o1, MessageMapping o2) {
            if ((o1.getMappingType() == MappingType.STATIC || o1.getMappingType() == MappingType.CONTEXT || o1.getMappingType() == MappingType.FUNCTION) && o2.getMappingType() == MappingType.MAPPING) {
                return 1;
            }

            if (o1.getMappingType() == MappingType.CONTEXT && o2.getMappingType() == MappingType.FUNCTION) {
                return 1;
            }

            if (o1.getMappingType() == MappingType.FUNCTION && o2.getMappingType() == MappingType.CONTEXT) {
                return -1;
            }

            if (o1.getMappingType() == MappingType.STATIC && o2.getMappingType() == MappingType.CONTEXT) {
                return 1;
            }

            if (o1.getMappingType() == MappingType.CONTEXT && o2.getMappingType() == MappingType.STATIC) {
                return -1;
            }

            if (o1.getMappingType() == MappingType.MAPPING && (o2.getMappingType() == MappingType.STATIC || o2.getMappingType() == MappingType.CONTEXT || o2.getMappingType() == MappingType.FUNCTION)) {
                return -1;
            }
            return 0;
        }
    }
}
