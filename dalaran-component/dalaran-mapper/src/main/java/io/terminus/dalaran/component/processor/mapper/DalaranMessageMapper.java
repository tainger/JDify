package io.terminus.dalaran.component.processor.mapper;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.processor.mapper.model.*;
import io.terminus.dalaran.core.component.DalaranComponentValidator;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.config.DalaranMapperConfig;
import io.terminus.dalaran.core.component.model.*;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.context.DalaranFunctionContext;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.resource.entity.ModelAbstractEntity;
import io.terminus.dalaran.core.resource.entity.PropertyAbstractEntity;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.flow.FlowValidationBuilder;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import lombok.Data;
import lombok.val;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static io.terminus.dalaran.component.processor.mapper.MapperValidationMessages.*;

/**
 * Created by jingdi on 2019/3/18
 */
@Processor(
        value = "mapper-convert",
        order = 6,
        configType = DalaranMapperConfig.class,
        description = "模型字段映射：通过连线，在不同结构的模型字段间做数据映射"
)
@Data
public class DalaranMessageMapper implements DalaranProcessor<DalaranMapperConfig>, DalaranComponentValidator<DalaranMapperConfig> {

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private DalaranResourceLoader resourceLoader;

    @Autowired
    private DalaranModelTypeContext converterContext;

    @Override
    public void configure(ProcessorDefinition route, DalaranMapperConfig config) {
        Map<String, SimpleMapping> messageMapping = config.getMessageMapping();
        MessageModel in = config.getInModel();
        MessageModel out = config.getOutModel();
        DalaranMapperProcessor processor = new DalaranMapperProcessor(transfer(messageMapping, in, out), dalaranContext);
        route.process(processor);
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

            DalaranFunctionContext functionContext = dalaranContext.getDalaranFunctionContext();
            MappingFunctionInfo functionInfo = functionContext.getFunctionByKey(function.getId());
            if (functionInfo != null) {
                String[] params = functionInfo.getParams();
                String temKey = function.getTemKey();
                if (temKey == null) {
                    temKey = "";
                }
                Map<String, FunctionParam> functionParams = function.getParams();
                if (MapUtils.isEmpty(functionParams)) {
                    return sourcePaths;
                }
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

    public MessageModel buildModel(ModelAbstractEntity modelEntity) {
        if (modelEntity != null) {
            val model = new MessageModel();
            String modelType = modelEntity.getType();
            model.setModelType(modelType);
            model.setName(modelEntity.getName());
            Class<? extends DalaranModelSchema> schemaType = converterContext.getModelSchema(modelType);
            DalaranModelSchema modelSchema = buildConfig(modelEntity.getModelSchema(), schemaType);
            model.setModelSchema(modelSchema);
            return model;
        }
        return null;
    }

    private <T> T buildConfig(String configValue, Class<T> configType) {
        String replacedConfig = replaceProperties(configValue, getProperties());
        return JSON.parseObject(replacedConfig, configType);
    }

    private String replaceProperties(String configValue, Map<String, String> properties) {
        StringSubstitutor stringSubstitutor = new StringSubstitutor(properties, DalaranConstants.ENV_REPLACE_PREFIX, DalaranConstants.ENV_REPLACE_SUFFIX);
        return stringSubstitutor.replace(configValue);
    }

    private Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>(System.getenv());
        for (PropertyAbstractEntity propertyEntity : resourceLoader.loadAllProperties()) {
            properties.put(propertyEntity.getName(), propertyEntity.getValue());
        }
        return properties;
    }

    @Override
    public List<FlowValidation> validate(DalaranMapperConfig config) {
        List<FlowValidation> validations = new ArrayList<>();
        MessageModel inModel = config.getInModel();
        MessageModel outModel = config.getOutModel();
        String inModelId = config.getInModelId();
        String outModelId = config.getOutModelId();
        Map<String, SimpleMapping> mappings = config.getMessageMapping();

        if (inModel == null || outModel == null) {
            if(StringUtils.isBlank(inModelId) || StringUtils.isBlank(outModelId)) {
                validations.add(FlowValidationBuilder.newBuilder()
                        .field(MapperConstants.MAPPER_MODEL)
                        .message(MODEL_NOT_NULL).build());
                return validations;
            }
            ModelAbstractEntity inModelEntity = resourceLoader.loadModel(Long.parseLong(inModelId));
            inModel = buildModel(inModelEntity);
            ModelAbstractEntity outModelEntity = resourceLoader.loadModel(Long.parseLong(outModelId));
            outModel = buildModel(outModelEntity);
            if(inModel == null || outModel == null) {
                validations.add(FlowValidationBuilder.newBuilder()
                        .field(MapperConstants.MAPPER_MODEL)
                        .message(MODEL_NOT_NULL).build());
                return validations;
            }
        }

        if (MapUtils.isEmpty(mappings)) {
            return validations;
        }
        MessageModel finalInModel = inModel;
        MessageModel finalOutModel = outModel;
        mappings.forEach((destinationPath, simpleMapping) -> {
            if (simpleMapping.getMappingType() == MappingType.FUNCTION) {
                MappingFunction function = (MappingFunction) simpleMapping.getValue();
                Map<String, FunctionParam> params = function.getParams();
                if (MapUtils.isEmpty(params)) {
                    return;
                }
                params.forEach((functionParam, param) -> {
                    if (param.getType() == ParamType.STATIC) {
                        return;
                    }
                    if (param.getValue() == null) {
                        FlowValidation validation = FlowValidationBuilder.newBuilder()
                                .field(destinationPath)
                                .message(MAPPER_FUNCTION_PARAM_NOT_NULL).build();
                        validations.add(validation);
                    } else {
                        FlowValidation validation = checkArrayFields(param.getValue(), finalInModel, destinationPath, finalOutModel);
                        if (validation != null) {
                            validations.add(validation);
                        }
                    }
                });
            } else {
                String sourcePath = simpleMapping.getValue().toString();
                if (StringUtils.isBlank(sourcePath)) {
                    FlowValidation validation = FlowValidationBuilder.newBuilder()
                            .field(destinationPath)
                            .message(MAPPER_SOURCE_PATH_NOT_NULL).build();
                    validation.setField(destinationPath);
                    validations.add(validation);
                } else {
                    FlowValidation validation = checkArrayFields(sourcePath, finalInModel, destinationPath, finalOutModel);
                    if (validation != null) {
                        validations.add(validation);
                    }
                }
            }
        });
        return validations;
    }

    private FlowValidation checkArrayFields(String sourcePath, MessageModel inModel, String destinationPath, MessageModel outModel) {
        String[] sourcePaths = StringUtils.split(sourcePath, ".");
        Integer sourceCount = calculateArrayCount(ArrayUtils.subarray(sourcePaths, 0, sourcePaths.length -1), inModel);
        String[] destinationPaths = StringUtils.split(destinationPath, ".");
        Integer destinationCount = calculateArrayCount(ArrayUtils.subarray(destinationPaths, 0, destinationPaths.length - 1), outModel);

        if (sourceCount == -1 || destinationCount == -1) {
            FlowValidation validation = FlowValidationBuilder.newBuilder()
                    .field(destinationPath)
                    .message(PATH_NOT_IN_MODEL).build();
            validation.setField(destinationPath);
            return validation;
        }

        if (!sourceCount.equals(destinationCount)) {
            FlowValidation validation = FlowValidationBuilder.newBuilder()
                    .field(destinationPath)
                    .message(MAPPER_ARRAY_LEVEL_NOT_EQUALS).build();
            validation.setField(destinationPath);
            return validation;
        }
        return null;
    }

    private Integer calculateArrayCount(String[] paths, MessageModel model) {
        Integer count = 0;
        Map<String, ModelField> temporaryField = model.getModelSchema().getFields();
        for (String path : paths) {
            ModelField field = temporaryField.get(path);
            if (field == null) {
                return -1;
            }
            if (field.getType() == FieldType.ARRAY) {
                count++;
            }
            temporaryField = field.getFields();
        }
        return count;
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
