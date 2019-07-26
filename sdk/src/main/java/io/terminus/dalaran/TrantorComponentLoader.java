package io.terminus.dalaran;

import io.terminus.dalaran.model.*;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import io.terminus.dalaran.model.trantor.DalaranIntegrationInfo;
import io.terminus.dalaran.model.trantor.DalaranIntegrationPoint;
import org.springframework.util.TypeUtils;

import java.lang.reflect.*;
import java.util.*;

public class TrantorComponentLoader {

    public static DalaranIntegrationInfo buildTrantorActionInfo(Class clazz) {
        DalaranIntegration dalaranIntegration = (DalaranIntegration) clazz.getAnnotation(DalaranIntegration.class);
        if (dalaranIntegration == null || !clazz.isInterface()) {
            return null;
        }
        DalaranIntegrationInfo integrationInfo = new DalaranIntegrationInfo();
        integrationInfo.setKey(dalaranIntegration.key());
        integrationInfo.setName(dalaranIntegration.name());
        integrationInfo.setDescription(dalaranIntegration.description());

        List<DalaranIntegrationPoint> actions = new ArrayList<>();
        integrationInfo.setIntegrationPoints(actions);

        for (Method method : clazz.getDeclaredMethods()) {
            DalaranIntegrationAction integrationAction = method.getAnnotation(DalaranIntegrationAction.class);
            DalaranIntegrationPoint integrationPoint = new DalaranIntegrationPoint();
            if (integrationAction == null) {
                integrationPoint.setKey(method.getName());
                integrationPoint.setName(method.getName());
            } else {
                integrationPoint.setKey(integrationAction.key());
                integrationPoint.setName(integrationAction.name());
                integrationPoint.setDescription(integrationAction.description());
            }
            integrationPoint.setReturnType(buildReturnModel(method.getGenericReturnType()));
            integrationPoint.setParamType(buildParameters(method.getParameters()));
            actions.add(integrationPoint);
        }
        return integrationInfo;
    }

    private static JsonSchema buildReturnModel(Type type) {
        JsonSchema schema = new JsonSchema();
        ModelField rootField = buildField(type);
        schema.setRootField(rootField);
        return schema;
    }

    private static JsonSchema buildParameters(Parameter[] parameters) {
        JsonSchema schema = new JsonSchema();
        ModelField rootField = new ModelField();
        rootField.setType(FieldType.OBJECT);
        for (Parameter parameter : parameters) {
            rootField.addField(parameter.getName(), buildField(parameter.getParameterizedType()));
        }
        schema.setRootField(rootField);
        return schema;
    }

    // TODO 这个方法简直了... 回头再细理一下吧
    private static ModelField buildField(Type type) {
        ModelField field = new ModelField();
        if (type == String.class) {
            field.setType(FieldType.STRING);
        } else if (type == byte.class || type == short.class || type == int.class || type == long.class) {
            field.setType(FieldType.INTEGER);
        } else if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class) {
            field.setType(FieldType.INTEGER);
        } else if (type == float.class || type == double.class) {
            field.setType(FieldType.FLOAT);
        } else if (type == Float.class || type == Double.class) {
            field.setType(FieldType.FLOAT);
        } else if (type == boolean.class) {
            field.setType(FieldType.BOOLEAN);
        } else if (type == Boolean.class) {
            field.setType(FieldType.BOOLEAN);
        } else if (TypeUtils.isAssignable(Date.class, type)) {
            field.setType(FieldType.DATE);
        } else if (TypeUtils.isAssignable(Collection.class, type)) {
            field.setType(FieldType.ARRAY);
            // 如果是数组并且是参数类型, 根据泛型获取子类型
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    Class subClass = (Class) actualTypeArguments[0];
                    ModelField subField = buildField(subClass);
                    field.setSubType(subField.getType());
                    field.setFields(subField.getFields());
                }
            }
        } else if (TypeUtils.isAssignable(Number.class, type)) {
            field.setType(FieldType.NUMBER);
        } else if (TypeUtils.isAssignable(Map.class, type)) {
            field.setType(FieldType.OBJECT);
        } else if (type instanceof Class) {
            if (((Class) type).isArray()) {
                // 如果是数组, 根据数组类型获取子类型
                field.setType(FieldType.ARRAY);
                ModelField subField = buildField(((Class) type).getComponentType());
                field.setSubType(subField.getType());
                field.setFields(subField.getFields());
            } else {
                // 无泛型且不是基本类型, 认为是模型, 读取字段
                field.setType(FieldType.OBJECT);
                for (Field objField : ((Class) type).getDeclaredFields()) {
                    field.addField(objField.getName(), buildField(objField.getType()));
                }
            }
        } else {
            field.setType(FieldType.OBJECT);
        }
        return field;
    }

}
