package io.terminus.dalaran.component.processor.mapper.jsonPath;

import com.alibaba.fastjson.JSONPath;
import io.terminus.dalaran.component.common.exception.FieldParseException;
import io.terminus.dalaran.component.common.exception.MapperFunctionExecuteException;
import io.terminus.dalaran.component.processor.mapper.model.*;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.model.FieldType;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jingdi on 2019/7/16
 */
public class Converter {

    public static Map<String, Object> convert(DalaranMappingConfig mappingConfig, Object source, DalaranContext dalaranContext) {
        Map<String, Object> destination = new HashMap<>();
        if (isSingleXMLString(mappingConfig, source)) {
            handleSingleXMLString(mappingConfig, source, dalaranContext, destination);
            return destination;
        }
        List<MessageMapping> messageMappings = mappingConfig.getMessageMappings();
        SimpleMappingField sourceRoot = mappingConfig.getSourceRoot();
        SimpleMappingField destinationRoot = mappingConfig.getDestinationRoot();
        messageMappings.forEach(messageMapping -> {
            if (messageMapping.getStatus() == MappingStatus.ERROR) {
                return;
            }
            if (messageMapping.isComplex()) {
                SourceFieldDetail sourceFieldDetail = buildSource(source, messageMapping, sourceRoot);
                Map<String, PathDetail> destinationPaths = buildPathMapping(messageMapping, sourceFieldDetail.getArrayFieldSize(), destinationRoot);
                buildValue(source, sourceFieldDetail.getSourcePaths(), destinationPaths, messageMapping, destination, dalaranContext);
            } else {
                buildValue(source, messageMapping, destination, dalaranContext);
            }
        });
        return destination;
    }

    private static SourceFieldDetail buildSource(Object source, MessageMapping mapping, SimpleMappingField sourceRoot) {
        Map<String, List<SourcePath>> sourcePaths = new HashMap<>();
        List<Integer> arrayFieldSize = new ArrayList<>();
        Integer lastArray = 0;
        List<SourceField> fields = mapping.getSourceFields();
        if (sourceRoot.getType() == FieldType.ARRAY) {
            List<Object> body = (List) source;
            int bodySize = body.size();
            Integer level = lastArray + 1;
            arrayFieldSize.add(bodySize);
            for (int i = 0; i < bodySize; i++) {
                String path = "$[" + i + "]";
                StringBuilder indexes = new StringBuilder();
                indexes.append(i).append(".");
                for (SourceField sourceField : fields) {
                    if (sourceField.getParamType() == ParamType.DYNAMIC) {
                        buildSourcePaths(path, sourceField.getField(), arrayFieldSize, level, source, sourcePaths, sourceField.getPath(), indexes);
                    }
                }
            }
        } else {
            String path = "$";
            StringBuilder indexes = new StringBuilder();
            for (SourceField sourceField : fields) {
                if (sourceField.getParamType() == ParamType.DYNAMIC) {
                    buildSourcePaths(path, sourceField.getField(), arrayFieldSize, lastArray, source, sourcePaths, sourceField.getPath(), indexes);
                }
            }
        }
        return new SourceFieldDetail(arrayFieldSize, sourcePaths);
    }

    private static void buildSourcePaths(String parentPath, SimpleMappingField field, List<Integer> arrayFieldSize, Integer lastArrayLevel, Object source, Map<String, List<SourcePath>> sourcePaths, String rootPath, StringBuilder indexes) {
        String name = parentPath + "." + field.getName();
        if (field.getLocal() == FieldLocal.MIDDLE) {
            Object body = JSONPath.eval(source, name);
            if (body == null) {
                return;
            }
            SimpleMappingField childField = field.getChild();
            if (field.getType() == FieldType.ARRAY) {
                List<Object> child = (List) body;
                int bodySize = child.size();
                Integer level = lastArrayLevel + 1;
                arrayFieldSize.add(bodySize);

                for (int i = 0; i < bodySize; i++) {
                    StringBuilder index = new StringBuilder();
                    index.append(indexes);
                    index.append(i).append(".");
                    String path = name + "[" + i + "]";
                    buildSourcePaths(path, childField, arrayFieldSize, level, source, sourcePaths, rootPath, index);
                }
            }
            if (field.getType() == FieldType.OBJECT) {
                buildSourcePaths(name, childField, arrayFieldSize, lastArrayLevel, source, sourcePaths, rootPath, indexes);
            }
        } else if (field.getLocal() == FieldLocal.END) {
            PathDetail pathDetail = new PathDetail();
            String index = indexes.toString();
            pathDetail.setPath(name);
            pathDetail.setType(field.getType());
            pathDetail.setIndexes(index);
            SourcePath sourcePath = new SourcePath(rootPath, pathDetail);
            if (sourcePaths.containsKey(index)) {
                sourcePaths.get(index).add(sourcePath);
            } else {
                List<SourcePath> sourcePathList = new ArrayList<>();
                sourcePathList.add(sourcePath);
                sourcePaths.put(index, sourcePathList);
            }
        }
    }

    private static Map<String, PathDetail> buildPathMapping(MessageMapping mapping, List<Integer> arrayFieldSize, SimpleMappingField destinationRoot) {
        int level = 0;
        Map<String, PathDetail> paths = new HashMap<>();
        SimpleMappingField field = mapping.getDestinationField();
        if (destinationRoot.getType() == FieldType.ARRAY) {
            int size = arrayFieldSize.get(level++);
            for (int i = 0; i < size; i++) {
                StringBuilder indexes = new StringBuilder();
                indexes.append(i).append(".");
                String path = "$" + MapperConstants.MODEL_ROOT + "[" + i + "]";
                buildDestinationPaths(path, field, arrayFieldSize, level, paths, indexes);
            }
        } else {
            String path = "$" + MapperConstants.MODEL_ROOT;
            StringBuilder indexes = new StringBuilder();
            buildDestinationPaths(path, field, arrayFieldSize, level, paths, indexes);
        }
        return paths;
    }

    private static void buildDestinationPaths(String parentPath, SimpleMappingField field, List<Integer> arrayFieldSize, int level, Map<String, PathDetail> paths, StringBuilder indexes) {
        String name = parentPath + "." + field.getName();
        if (field.getLocal() == FieldLocal.MIDDLE) {
            SimpleMappingField child = field.getChild();
            if (field.getType() == FieldType.ARRAY) {
                int bodySize = arrayFieldSize.get(level++);
                for (int i = 0; i < bodySize; i++) {
                    StringBuilder index = new StringBuilder();
                    index.append(indexes);
                    index.append(i).append(".");
                    String path = name + "[" + i + "]";
                    buildDestinationPaths(path, child, arrayFieldSize, level, paths, index);
                }
            }
            if (field.getType() == FieldType.OBJECT) {
                buildDestinationPaths(name, child, arrayFieldSize, level, paths, indexes);
            }
        } else if (field.getLocal() == FieldLocal.END) {
            PathDetail detail = new PathDetail();
            detail.setPath(name);
            detail.setType(field.getType());
            detail.setIndexes(indexes.toString());
            paths.put(indexes.toString(), detail);
        }
    }

    private static void buildValue(Object source, Map<String, List<SourcePath>> sourcePaths, Map<String, PathDetail> destinationPaths, MessageMapping messageMapping, Object destination, DalaranContext dalaranContext) {
        if (sourcePaths == null || sourcePaths.size() == 0) {
            return;
        }

        for (Map.Entry<String, List<SourcePath>> entry : sourcePaths.entrySet()) {
            List<Object> values = new ArrayList<>();
            String indexes = entry.getKey();
            Map<String, SourcePath> dynamicParams = new HashMap<>();
            entry.getValue().forEach(path -> {
                dynamicParams.put(path.getPath(), path);
            });

            MappingFunction function = messageMapping.getFunction();
            if (function != null) {
                Map<String, FunctionParam> functionParams = function.getSourcePaths();
                Set<String> paths = functionParams.keySet();
                paths.forEach(path -> {
                    Object value = null;
                    if (dynamicParams.containsKey(path)) {
                        SourcePath sourcePath = dynamicParams.get(path);
                        PathDetail pathDetail = sourcePath.getDetail();
                        if (pathDetail != null && pathDetail.getPath() != null) {
                            value = JSONPath.eval(source, pathDetail.getPath());
                        }
                    } else {
                        FunctionParam functionParam = functionParams.get(path);
                        value = functionParam.getValue();
                    }
                    values.add(value);
                });
            } else {
                entry.getValue().forEach(v -> {
                    Object value = null;
                    PathDetail pathDetail = v.getDetail();
                    if (pathDetail != null && pathDetail.getPath() != null) {
                        value = JSONPath.eval(source, pathDetail.getPath());
                    }
                    values.add(value);
                });
            }

            PathDetail pathDetail = destinationPaths.get(indexes);
            Object value;
            if (function != null) {
                value = execute(dalaranContext, function, values);
            } else {
                value = values.get(0);
            }

            if (pathDetail != null) {
                FieldType type = pathDetail.getType();
                value = parse(value, type, entry.getValue(), pathDetail.getPath());
            }

            if (pathDetail != null && pathDetail.getPath() != null) {
                JSONPath.set(destination, pathDetail.getPath(), value);
            }
        }
    }

    private static void buildValue(Object source, MessageMapping messageMapping, Object destination, DalaranContext dalaranContext) {
        List<Object> values = new ArrayList<>();
        List<SourceField> sourceFields = messageMapping.getSourceFields();
        List<SourcePath> sourcePaths = new ArrayList<>();

        sourceFields.forEach(sourceField -> {
            Object value;
            if (sourceField.getParamType() == ParamType.STATIC) {
                value = sourceField.getPath();
            } else {
                value = JSONPath.eval(source, sourceField.getPath());
            }
            values.add(value);
            sourcePaths.add(new SourcePath(sourceField.getPath(), null));
        });

        String destinationPath = messageMapping.getPath();
        MappingFunction function = messageMapping.getFunction();
        Object value;
        if (function != null) {
            value = execute(dalaranContext, function, values);
        } else {
            value = values.get(0);
        }
        FieldType type = messageMapping.getType();
        value = parse(value, type, sourcePaths, destinationPath);
        JSONPath.set(destination, "$" + MapperConstants.MODEL_ROOT + "." + destinationPath, value);
    }

    private static Object parse(Object value, FieldType type, List<SourcePath> sourcePaths, String destinationPath) {
        try {
            value = parse(value, type);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            String fields = sourcePaths.stream().map(SourcePath::getPath).collect(Collectors.toList()).toString();
            throw new FieldParseException(fields, value, " Field value parse error! Destination field: " + destinationPath + ", type: " + type);
        }
    }

    private static Object parse(Object target, FieldType destination) {
        if (target == null) {
            return null;
        }
        if (destination != null) {
            switch (destination) {
                case INTEGER:
                    return ConvertUtils.convert(target, Long.class);
                case FLOAT:
                    return ConvertUtils.convert(target, Double.class);
                case BOOLEAN:
                    return ConvertUtils.convert(target, Boolean.class);
                case STRING:
                    return ConvertUtils.convert(target, String.class);
                default:
                    return target;
            }
        }
        return target;
    }

    private static Object execute(DalaranContext dalaranContext, MappingFunction function, List<Object> values) {
        try {
            switch (function.getType()) {
                case STATIC:
                    return dalaranContext.getDalaranFunctionContext().executeStaticFunction(function.getId(), values.toArray());
                case CUSTOM:
                    return dalaranContext.getDalaranFunctionContext().executeCustomFunction(Long.valueOf(function.getId()), values.toArray());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MapperFunctionExecuteException("Mapper function execute error. function: " + function.getId() + ", params: " + Arrays.toString(values.toArray()) + ", message: " + e.getMessage());
        }
    }

    private static boolean isSingleXMLString(DalaranMappingConfig dalaranMappingConfig, Object source) {
        if (!(source instanceof String)) {
            return false;
        }
        if (CollectionUtils.isEmpty(dalaranMappingConfig.getMessageMappings()) || dalaranMappingConfig.getMessageMappings().size() > 1) {
            return false;
        }
        MessageMapping mapping = dalaranMappingConfig.getMessageMappings().get(0);
        return mapping.getFunction() != null && mapping.getFunction().getTemKey().startsWith("FromXml");
    }

    private static void handleSingleXMLString(DalaranMappingConfig dalaranMappingConfig, Object source, DalaranContext dalaranContext, Object destination) {
        MessageMapping mapping = dalaranMappingConfig.getMessageMappings().get(0);
        List<Object> values = new ArrayList<>();
        values.add(source);
        Object value =  dalaranContext.getDalaranFunctionContext().executeStaticFunction(mapping.getFunction().getId(), values.toArray());
        String destinationPath = mapping.getPath();
        JSONPath.set(destination, "$" + MapperConstants.MODEL_ROOT + "." + destinationPath, value);
    }
}
