package io.terminus.dalaran.component.processor.mapper.jsonPath;

import com.alibaba.fastjson.JSONPath;
import io.terminus.dalaran.component.processor.mapper.model.*;
import io.terminus.dalaran.core.model.FieldType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/7/16
 */
public class Converter {

    public void convert(List<MessageMapping> messageMappings, Object source, Object destination) {
        messageMappings.forEach(messageMapping -> {
            if (messageMapping.isComplex()) {
                List<SourcePath> sourcePaths = new ArrayList<>();
                Map<Integer, Integer> arraySize = new HashMap<>();
                buildSource(source, messageMapping, sourcePaths, arraySize);
                List<String> destinationPaths = new ArrayList<>();
                buildPathMapping(messageMapping, arraySize, destinationPaths);
                buildValue(source, sourcePaths, destinationPaths, messageMapping, destination);
            } else {
                buildValue(source, messageMapping.getPath(), messageMapping.getSourceFields(), destination);
            }
        });
    }

    private void buildValue(Object source, String destinationPath, List<SourceField> sourceFields, Object destination) {
        List<Object> values = new ArrayList<>();
        sourceFields.forEach(sourceField -> {
            Object value = JSONPath.eval(source, sourceField.getPath());
            values.add(value);
        });

        /**
         * todo call function  val = execute(values, function-key)
         */
        Object val = values.toString();
        JSONPath.set(destination, "$." + destinationPath, val);
    }

    private void buildSource(Object source, MessageMapping mapping, List<SourcePath> pathMapping, Map<Integer, Integer> arraySize) {
        Integer lastArray = 0;
        List<SourceField> fields = mapping.getSourceFields();
        SimpleMappingField sourceRoot = mapping.getSourceRoot();
        if (sourceRoot.getType() == FieldType.ARRAY) {
            List<Object> body = (List) source;
            int bodySize = body.size();
            Integer level = lastArray + 1;
            arraySize.put(level, bodySize);
            for (int i = 0; i < bodySize; i++) {
                String path = "$[" + i + "]";
                for (SourceField sourceField: fields) {
                    List<String> paths = new ArrayList<>();
                    buildSourcePaths(path, sourceField.getField(), arraySize, level, source, paths);
                    SourcePath sourcePath = new SourcePath(sourceField.getPath(), paths);
                    pathMapping.add(sourcePath);
                }
            }
        } else {
            String path = "$";
            for (SourceField sourceField: fields) {
                List<String> paths = new ArrayList<>();
                buildSourcePaths(path, sourceField.getField(), arraySize, lastArray, source, paths);
                SourcePath sourcePath = new SourcePath(sourceField.getPath(), paths);
                pathMapping.add(sourcePath);
            }
        }
    }

    private void buildSourcePaths(String parentPath, SimpleMappingField field, Map<Integer, Integer> arraySize, Integer lastArrayLevel, Object source, List<String> paths) {
        String name = parentPath + "." + field.getName();
        if (field.getLocal() == FieldLocal.MIDDLE) {
            Object body = JSONPath.eval(source, name);
            SimpleMappingField childField = field.getChild();
            if (field.getType() == FieldType.ARRAY) {
                List<Object> child = (List) body;
                int bodySize = child.size();
                Integer level = lastArrayLevel + 1;
                arraySize.put(level, bodySize);

                for (int i = 0; i < bodySize; i++) {
                    String path = name + "[" + i + "]";
                    buildSourcePaths(path, childField, arraySize, level, source, paths);
                }
            }
            if (field.getType() == FieldType.OBJECT) {
                buildSourcePaths(name, childField, arraySize, lastArrayLevel, source, paths);
            }
        } else if (field.getLocal() == FieldLocal.END) {
            paths.add(name);
        }
    }

    private void buildPathMapping(MessageMapping mapping, Map<Integer, Integer> arraySize, List<String> paths) {
        SimpleMappingField root = mapping.getRootField();
        int level = 0;
        SimpleMappingField field = mapping.getDestinationField();
        if (root.getType() == FieldType.ARRAY) {
            int size = arraySize.get(++level);
            for (int i = 0; i < size; i++) {
                String path = "$[" + i + "]";
                buildDestinationPaths(path, field, arraySize, level, paths);
            }
        } else {
            String path = "$";
            buildDestinationPaths(path, field, arraySize, level, paths);
        }
    }

    private void buildDestinationPaths(String parentPath, SimpleMappingField field, Map<Integer, Integer> arraySize, int level, List<String> paths) {
        String name = parentPath + "." + field.getName();
        if (field.getLocal() == FieldLocal.MIDDLE) {
            SimpleMappingField child = field.getChild();
            if (field.getType() == FieldType.ARRAY) {
                ++level;
                int bodySize = arraySize.get(level);
                for (int i = 0; i < bodySize; i++) {
                    String path = name + "[" + i + "]";
                    buildDestinationPaths(path, child, arraySize, level, paths);
                }
            }
            if (field.getType() == FieldType.OBJECT) {
                buildDestinationPaths(name, child, arraySize, level, paths);
            }
        } else if (field.getLocal() == FieldLocal.END) {
            paths.add(name);
        }
    }

    private void buildValue(Object source, List<SourcePath> sourcePaths, List<String> destinationPaths, MessageMapping messageMapping, Object destination) {
        if (sourcePaths != null && sourcePaths.size() > 0) {
            int size = sourcePaths.get(0).getDetails().size();
            for (int i = 0; i < size; i++) {
                List<Object> values = new ArrayList<>();
                for (SourcePath sourcePath: sourcePaths) {
                    Object value = JSONPath.eval(source, sourcePath.getDetails().get(i));
                    values.add(value);
                }

                /**
                 * todo call function  val = execute(values, function-key)
                 */
                Object val = values.toString();
                JSONPath.set(destination, destinationPaths.get(i), val);
            }
        }
    }
}
