package io.terminus.dalaran.component.processor.mapper.jsonPath;

import com.alibaba.fastjson.JSONPath;
import com.github.drapostolos.typeparser.TypeParser;
import io.terminus.dalaran.component.processor.mapper.model.*;
import io.terminus.dalaran.core.context.support.DefaultDalaranFunctionContext;
import io.terminus.dalaran.core.model.FieldType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/7/16
 */
public class Converter {

    public static Map<String, Object> convert(DalaranMappingConfig mappingConfig, Object source) {
        Map<String, Object> destination = new HashMap<>();
        List<MessageMapping> messageMappings = mappingConfig.getMessageMappings();
        SimpleMappingField sourceRoot = mappingConfig.getSourceRoot();
        SimpleMappingField destinationRoot = mappingConfig.getDestinationRoot();
        messageMappings.forEach(messageMapping -> {
            if (messageMapping.isComplex()) {
                SourceFieldDetail sourceFieldDetail = buildSource(source, messageMapping, sourceRoot);
                List<PathDetail> destinationPaths = buildPathMapping(messageMapping, sourceFieldDetail.getArrayFieldSize(), destinationRoot);
                buildValue(source, sourceFieldDetail.getSourcePaths(), destinationPaths, messageMapping, destination);
            } else {
                buildValue(source, messageMapping, destination);
            }
        });
        return destination;
    }

    private static void buildValue(Object source, MessageMapping messageMapping, Object destination) {
        List<Object> values = new ArrayList<>();
        List<SourceField> sourceFields = messageMapping.getSourceFields();
        sourceFields.forEach(sourceField -> {
            Object value = JSONPath.eval(source, sourceField.getPath());
            values.add(value);
        });

        String destinationPath = messageMapping.getPath();
        MappingFunction function = messageMapping.getFunction();
        DefaultDalaranFunctionContext functionContext = new DefaultDalaranFunctionContext();
        if (function != null) {
            if (function.getType() == FunctionType.STANDARD) {
                functionContext.executeStaticFunction(function.getKey(), values.toArray());
            } else {
                functionContext.executeCustomFunction(Long.valueOf(function.getKey()), values.toArray());
            }
        } else if (values.size() == 1) {
            FieldType type = sourceFields.get(0).getField().getType();
            JSONPath.set(destination, "$root." + destinationPath, parse(values.get(0), type));
        }
    }

    private static SourceFieldDetail buildSource(Object source, MessageMapping mapping, SimpleMappingField sourceRoot) {
        SourceFieldDetail sourceFieldDetail = new SourceFieldDetail();
        List<SourcePath> sourcePaths = new ArrayList<>();
        List<Integer> arrayFieldSize = new ArrayList<>();
        Integer lastArray = 0;
        List<SourceField> fields = mapping.getSourceFields();
        if (sourceRoot.getType() == FieldType.ARRAY) {
            List<Object> body = (List) source;
            int bodySize = body.size();
            Integer level = lastArray + 1;
            arrayFieldSize.add(level, bodySize);
            for (int i = 0; i < bodySize; i++) {
                String path = "$[" + i + "]";
                for (SourceField sourceField: fields) {
                    List<PathDetail> paths = new ArrayList<>();
                    buildSourcePaths(path, sourceField.getField(), arrayFieldSize, level, source, paths);
                    SourcePath sourcePath = new SourcePath(sourceField.getPath(), paths);
                    sourcePaths.add(sourcePath);
                }
            }
        } else {
            String path = "$";
            for (SourceField sourceField: fields) {
                List<PathDetail> paths = new ArrayList<>();
                buildSourcePaths(path, sourceField.getField(), arrayFieldSize, lastArray, source, paths);
                SourcePath sourcePath = new SourcePath(sourceField.getPath(), paths);
                sourcePaths.add(sourcePath);
            }
        }
        sourceFieldDetail.setSourcePaths(sourcePaths);
        sourceFieldDetail.setArrayFieldSize(arrayFieldSize);
        return sourceFieldDetail;
    }

    private static void buildSourcePaths(String parentPath, SimpleMappingField field, List<Integer> arrayFieldSize, Integer lastArrayLevel, Object source, List<PathDetail> paths) {
        String name = parentPath + "." + field.getName();
        if (field.getLocal() == FieldLocal.MIDDLE) {
            Object body = JSONPath.eval(source, name);
            SimpleMappingField childField = field.getChild();
            if (field.getType() == FieldType.ARRAY) {
                List<Object> child = (List) body;
                int bodySize = child.size();
                Integer level = lastArrayLevel + 1;
                arrayFieldSize.add(level, bodySize);

                for (int i = 0; i < bodySize; i++) {
                    String path = name + "[" + i + "]";
                    buildSourcePaths(path, childField, arrayFieldSize, level, source, paths);
                }
            }
            if (field.getType() == FieldType.OBJECT) {
                buildSourcePaths(name, childField, arrayFieldSize, lastArrayLevel, source, paths);
            }
        } else if (field.getLocal() == FieldLocal.END) {
            PathDetail detail = new PathDetail();
            detail.setPath(name);
            detail.setType(field.getType());
            paths.add(detail);
        }
    }

    private static List<PathDetail> buildPathMapping(MessageMapping mapping, List<Integer> arrayFieldSize, SimpleMappingField destinationRoot) {
        int level = 0;
        List<PathDetail> paths = new ArrayList<>();
        SimpleMappingField field = mapping.getDestinationField();
        if (destinationRoot.getType() == FieldType.ARRAY) {
            int size = arrayFieldSize.get(++level);
            for (int i = 0; i < size; i++) {
                String path = "$root[" + i + "]";
                buildDestinationPaths(path, field, arrayFieldSize, level, paths);
            }
        } else {
            String path = "$root";
            buildDestinationPaths(path, field, arrayFieldSize, level, paths);
        }
        return paths;
    }

    private static void buildDestinationPaths(String parentPath, SimpleMappingField field, List<Integer> arrayFieldSize, int level, List<PathDetail> paths) {
        String name = parentPath + "." + field.getName();
        if (field.getLocal() == FieldLocal.MIDDLE) {
            SimpleMappingField child = field.getChild();
            if (field.getType() == FieldType.ARRAY) {
                ++level;
                int bodySize = arrayFieldSize.get(level);
                for (int i = 0; i < bodySize; i++) {
                    String path = name + "[" + i + "]";
                    buildDestinationPaths(path, child, arrayFieldSize, level, paths);
                }
            }
            if (field.getType() == FieldType.OBJECT) {
                buildDestinationPaths(name, child, arrayFieldSize, level, paths);
            }
        } else if (field.getLocal() == FieldLocal.END) {
            PathDetail detail = new PathDetail();
            detail.setPath(name);
            detail.setType(field.getType());
            paths.add(detail);
        }
    }

    private static void buildValue(Object source, List<SourcePath> sourcePaths, List<PathDetail> destinationPaths, MessageMapping messageMapping, Object destination) {
        MappingFunction function = messageMapping.getFunction();
        DefaultDalaranFunctionContext functionContext = new DefaultDalaranFunctionContext();
        if (sourcePaths != null && sourcePaths.size() > 0) {
            int size = sourcePaths.get(0).getDetails().size();
            for (int i = 0; i < size; i++) {
                List<Object> values = new ArrayList<>();
                for (SourcePath sourcePath: sourcePaths) {
                    Object value = JSONPath.eval(source, sourcePath.getDetails().get(i).getPath());
                    values.add(value);
                }

                Object value = null;
                String destinationPath = destinationPaths.get(i).getPath();
                if (function != null) {
                    if (function.getType() == FunctionType.STANDARD) {
                        value = functionContext.executeStaticFunction(function.getKey(), values.toArray());
                    } else {
                        value = functionContext.executeCustomFunction(Long.valueOf(function.getKey()), values.toArray());
                    }
                } else if (values.size() == 1) {
                    FieldType type = destinationPaths.get(i).getType();
                    value = parse(values.get(0), type);
                }
                JSONPath.set(destination, destinationPath, value);
            }
        }
    }

    private static Object parse(Object target, FieldType destination) {
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
}
