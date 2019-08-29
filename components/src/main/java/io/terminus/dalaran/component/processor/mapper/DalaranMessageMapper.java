package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.component.processor.mapper.model.*;
import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranComponentValidator;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.context.DalaranFunctionContext;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.flow.FlowValidationBuilder;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import lombok.Data;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static io.terminus.dalaran.component.processor.mapper.MapperValidationMessages.*;

/**
 * Created by jingdi on 2019/3/18
 */
@Processor(
        value = "mapper-convert",
        name = "数据映射",
        order = 10,
        configType = DalaranMapperConfig.class,
        inputSerializeType = BodySerializeType.Object,
        outputSerializeType = BodySerializeType.Object
)
@Data
public class DalaranMessageMapper implements DalaranProcessor<DalaranMapperConfig>, DalaranComponentValidator<DalaranMapperConfig> {

    @Autowired
    private DalaranContext dalaranContext;

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

        List<String> sourcePaths = buildSourcePaths(mappingType, mapping.getValue(), messageMapping);
        Map<String, ModelField> inField = in.getModelSchema().getFields();
        Map<String, ModelField> outField = out.getModelSchema().getFields();

        SimpleMappingField destinationField = new SimpleMappingField();

        MappingProperty mappingProperty = buildMappingField(path, outField, destinationField, false);
        messageMapping.setStatus(mappingProperty.getStatus());
        if (mappingProperty.getStatus() == MappingStatus.ERROR) {
            return;
        }
        messageMapping.setDestinationField(destinationField);

        List<SourceField> sourceFields = new ArrayList<>();
        MappingProperty property = new MappingProperty();
        for (String sourcePath : sourcePaths) {
            SourceField sourceField = new SourceField();
            if (mappingType == MappingType.DEFAULT) {
                sourceField.setPath(sourcePath);
            } else {
                property = buildSourceField(sourcePath, inField, sourceField, mappingProperty.isComplex());
                if (property.getStatus() == MappingStatus.ERROR) {
                    break;
                }
            }
            sourceFields.add(sourceField);
        }
        messageMapping.setSourceFields(sourceFields);
        messageMapping.setComplex(property.isComplex());
        messageMapping.setStatus(property.getStatus());
    }

    private List<String> buildSourcePaths(MappingType mappingType, Object value, MessageMapping messageMapping) {
        List<String> sourcePaths = new ArrayList<>();

        if (mappingType == MappingType.FUNCTION) {
            MappingFunction function = (MappingFunction) value;
            messageMapping.setFunction(function);

            DalaranFunctionContext functionContext = dalaranContext.getDalaranFunctionContext();
            MappingFunctionInfo functionInfo = functionContext.getFunctionByKey(function.getId());
            if (functionInfo != null) {
                String[] params = functionInfo.getParams();
                String temKey = function.getTemKey();
                if (temKey == null) {
                    temKey = "";
                }
                Map<String, String> sourcePath = function.getParams();
                for (String param : params) {
                    String path = sourcePath.get(temKey + param);
                    if (path != null) {
                        sourcePaths.add(path);
                    }
                }
            }
        } else {
            sourcePaths.add(value.toString());
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
                return new MappingProperty(complex, MappingStatus.ERROR);
            }
            temporaryMappingField.setName(fieldName);
            temporaryMappingField.setType(field.getType());

            if (i == fields.length - 1) {
                temporaryMappingField.setLocal(FieldLocal.END);
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
        return new MappingProperty(complex, status);
    }

    @Override
    public List<FlowValidation> validate(DalaranMapperConfig config) {
        List<FlowValidation> validations = new ArrayList<>();
        MessageModel inModel = config.getInModel();
        MessageModel outModel = config.getOutModel();
        Map<String, SimpleMapping> mappings = config.getMessageMapping();

        if (inModel == null || outModel == null) {
            validations.add(FlowValidationBuilder.newBuilder()
                    .field(MapperConstants.MAPPER_MODEL)
                    .message(MODEL_NOT_NULL).build());
            return validations;
        }

        if (MapUtils.isEmpty(mappings)) {
            return validations;
        }
        mappings.forEach((destinationPath, simpleMapping) -> {
            if (simpleMapping.getMappingType() == MappingType.FUNCTION) {
                MappingFunction function = (MappingFunction) simpleMapping.getValue();
                Map<String, String> params = function.getParams();
                if (MapUtils.isEmpty(params)) {
                    return;
                }
                params.forEach((functionParam, sourcePath) -> {
                    if (StringUtils.isBlank(sourcePath)) {
                        FlowValidation validation = FlowValidationBuilder.newBuilder()
                                .field(destinationPath)
                                .message(MAPPER_FUNCTION_PARAM_NOT_NULL).build();
                        validations.add(validation);
                    } else {
                        FlowValidation validation = checkArrayFields(sourcePath, inModel, destinationPath, outModel);
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
                    FlowValidation validation = checkArrayFields(sourcePath, inModel, destinationPath, outModel);
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
        Integer sourceCount = calculateArrayCount(sourcePaths, inModel);
        String[] destinationPaths = StringUtils.split(destinationPath, ".");
        Integer destinationCount = calculateArrayCount(destinationPaths, outModel);

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
            if ((o1.getMappingType() == MappingType.DEFAULT || o1.getMappingType() == MappingType.FUNCTION) && o2.getMappingType() == MappingType.MAPPING) {
                return 1;
            }

            if (o1.getMappingType() == MappingType.DEFAULT && o2.getMappingType() == MappingType.FUNCTION) {
                return 1;
            }

            if (o1.getMappingType() == MappingType.FUNCTION && o2.getMappingType() == MappingType.DEFAULT) {
                return -1;
            }

            if (o1.getMappingType() == MappingType.MAPPING && (o2.getMappingType() == MappingType.DEFAULT || o2.getMappingType() == MappingType.FUNCTION)) {
                return -1;
            }
            return 0;
        }
    }
}
