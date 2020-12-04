package io.terminus.dalaran.model.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.ModelField;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

import static io.terminus.dalaran.ComponentConstants.MODEL_FIELD_ROOT;

public class ModelUtils {

    public static Map<String, ModelField> parseDataTemplate(Object body) {
        SortedMap<String, ModelField> root = new TreeMap<>();
        ModelField modelField = new ModelField();
        root.put(MODEL_FIELD_ROOT, modelField);
        String type = body.getClass().getTypeName();
        if (isComplexType(type)) {
            buildModel(body, type, modelField);
        }
        return root;
    }

    private static boolean isComplexType(String type) {
        switch (type) {
            case ComponentConstants.JSON_OBJECT:
            case ComponentConstants.JSON_ARRAY:
                return true;
        }
        return false;
    }

    private static void buildModel(Object body, String type, ModelField modelField) {
        SortedMap<String, ModelField> child = new TreeMap<>();
        modelField.setFields(child);
        if (type.equalsIgnoreCase(ComponentConstants.JSON_OBJECT)) {
            modelField.setType(FieldType.OBJECT);
            buildChildren(body, child);
        } else if (type.equalsIgnoreCase(ComponentConstants.JSON_ARRAY)) {
            modelField.setType(FieldType.ARRAY);
            JSONArray jsonArray = (JSONArray) body;
            if (CollectionUtils.isNotEmpty(jsonArray)) {
                jsonArray.forEach(element -> {
                    String elementType = element.getClass().getTypeName();
                    modelField.setSubType(getFiledType(elementType));
                    if (isComplexType(elementType) && elementType.equalsIgnoreCase(ComponentConstants.JSON_OBJECT)) {
                        buildChildren(element, child);
                    }
                });
            }
        }
    }

    private static void buildChildren(Object element, SortedMap<String, ModelField> child) {
        JSONObject jsonObject = (JSONObject) element;
        jsonObject.forEach((name, value) -> {
            ModelField field = new ModelField();
            child.put(name, field);
            if (value != null) {
                String fileType = value.getClass().getTypeName();
                if (!isComplexType(fileType)) {
                    field.setType(getFiledType(fileType));
                } else {
                    buildModel(value, fileType, field);
                }
            }
        });
    }

    private static FieldType getFiledType(String type) {
        switch (type) {
            case ComponentConstants.JAVA_INTEGER:
            case ComponentConstants.JAVA_LONG:
                return FieldType.INTEGER;
            case ComponentConstants.JAVA_STRING:
                return FieldType.STRING;
            case ComponentConstants.JSON_OBJECT:
                return FieldType.OBJECT;
            case ComponentConstants.JSON_ARRAY:
                return FieldType.ARRAY;
            case ComponentConstants.JAVA_FLOAT:
            case ComponentConstants.JAVA_DOUBLE:
            case ComponentConstants.JAVA_MATH_BIGDECIMAL:
                return FieldType.FLOAT;
            case ComponentConstants.JAVA_BOOLEAN:
                return FieldType.BOOLEAN;
        }
        return FieldType.STRING;
    }

    public static Object buildBody(DalaranModelSchema schema) {
        Map<String, ModelField> modelField = schema.getFields();
        ModelField root = modelField.get(MODEL_FIELD_ROOT);
        return buildTemplateBody(root, "");
    }

    private static Object buildTemplateBody(ModelField parent, String parentFieldName) {
        if (parent == null) {
            return Maps.newHashMap();
        }
        FieldType parentType = parent.getType();
        if (parentType == FieldType.OBJECT) {
            return handleChildBody(parent);
        } else if (parentType == FieldType.ARRAY) {
            FieldType subType = parent.getSubType();
            if (subType == FieldType.OBJECT) {
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < ComponentConstants.MODEL_ARRAY_SIZE; i++) {
                    list.add(handleChildBody(parent));
                }
                return list;
            } else {
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < ComponentConstants.MODEL_ARRAY_SIZE; i++) {
                    list.add(getBasicValue(subType, parentFieldName, i));
                }
                return list;
            }
        } else {
            return getBasicValue(parentType, parentFieldName, 0);
        }
    }

    private static Object handleChildBody(ModelField parentField) {
        Map<String, Object> request = new HashMap<>();
        Map<String, ModelField> child = parentField.getFields();
        if (child != null) {
            child.forEach((name, field) -> {
                Object value = buildTemplateBody(field, name);
                request.put(name, value);
            });
        }
        return request;
    }

    private static Object getBasicValue(FieldType type, String fieldName, int index) {
        if (type != null) {
            switch (type) {
                case STRING:
                    return fieldName;
                case INTEGER:
                    return index;
                case BOOLEAN:
                    return true;
                case DATE:
                    return new Date();
                case FLOAT:
                    return (float) index + 1.1;
            }
        }
        return "";
    }
}
