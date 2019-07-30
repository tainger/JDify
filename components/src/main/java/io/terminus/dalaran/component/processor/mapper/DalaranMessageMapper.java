package io.terminus.dalaran.component.processor.mapper;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.processor.mapper.model.*;
import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.context.DalaranFunctionContext;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.function.MappingFunctionInfo;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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
public class DalaranMessageMapper implements DalaranProcessor<DalaranMapperConfig> {

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

//        String[] sourcePaths = StringUtils.split(mapping.getValue().trim(), ",");
        List<String> sourcePaths = buildSourcePaths(mappingType, mapping.getValue(), messageMapping);
        Map<String, ModelField> inField = in.getModelSchema().getFields();
        Map<String, ModelField> outField = out.getModelSchema().getFields();

        SimpleMappingField destinationField = new SimpleMappingField();
        buildMappingField(path, outField, destinationField);
        messageMapping.setDestinationField(destinationField);

        List<SourceField> sourceFields = new ArrayList<>();
        boolean complex = false;
        for (String sourcePath : sourcePaths) {
            SourceField sourceField = new SourceField();
            if (messageMapping.getMappingType() == MappingType.DEFAULT) {
                sourceField.setPath(sourcePath);
            } else {
                complex = buildSourceField(sourcePath, inField, sourceField);
            }
            sourceFields.add(sourceField);
        }
        messageMapping.setSourceFields(sourceFields);
        messageMapping.setComplex(complex);
    }

    private List<String> buildSourcePaths(MappingType mappingType, Object value, MessageMapping messageMapping) {
        List<String> sourcePaths = new ArrayList<>();

        if (mappingType == MappingType.FUNCTION) {
            MappingFunction function = JSON.parseObject(JSON.toJSONString(value), MappingFunction.class);
            messageMapping.setFunction(function);

            DalaranFunctionContext functionContext = dalaranContext.getDalaranFunctionContext();
            MappingFunctionInfo functionInfo = functionContext.getFunctionByKey(function.getKey());
            if (functionInfo != null) {
                String[] params = functionInfo.getParams();
                String temKey = function.getTemKey();
                Map<String, String> sourcePath = function.getParams();
                for (String param: params) {
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

    private boolean buildSourceField(String sourcePath, Map<String, ModelField> in, SourceField sourceField) {
        SimpleMappingField simpleMappingField = new SimpleMappingField();
        boolean complex = buildMappingField(sourcePath, in, simpleMappingField);
        sourceField.setField(simpleMappingField);
        sourceField.setPath(StringUtils.substringAfter(sourcePath, MapperConstants.MODEL_ROOT + "."));
        return complex;
    }

    private boolean buildMappingField(String path, Map<String, ModelField> modelField, SimpleMappingField simpleMappingField) {
        boolean complex = false;

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
        return complex;
    }

    private class MessageMappingComparator implements Comparator<MessageMapping> {
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
