package io.terminus.dalaran.core.util;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.config.DalaranConfigField;
import io.terminus.dalaran.model.annotation.ModelFieldInfo;
import lombok.val;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelFieldUtils {
    private static void getConfigFields(List<DalaranConfigField> configFields, Class configClass) {
        if (configClass.getSuperclass() != null) {
            getConfigFields(configFields, configClass.getSuperclass());
        }

        for (Field field : configClass.getDeclaredFields()) {
            ModelFieldInfo modelFieldInfo = field.getDeclaredAnnotation(ModelFieldInfo.class);
            if (modelFieldInfo != null && modelFieldInfo.inputType() != FieldInputType.Hidden) {
                DalaranConfigField modelField = new DalaranConfigField();
                modelField.setName(field.getName());
                modelField.setInputType(modelFieldInfo.inputType());
                modelField.setExample(modelFieldInfo.example());
                modelField.setDefaultValue(modelFieldInfo.defaultValue());
                modelField.setLabel(modelFieldInfo.label());
                modelField.setRequired(modelFieldInfo.required());
                modelField.setReadonly(modelFieldInfo.readonly());

                if (field.getType().isEnum()) {
                    val enumValueMap = new HashMap<String, String>();
                    for (Field element : field.getType().getFields()) {
                        val name = element.getName();
                        enumValueMap.put(name, name);
                    }
                    modelField.setEnumValues(enumValueMap);
                }

                configFields.add(modelField);
            }
        }
    }

    public static DalaranConfigField[] buildModelFields(Class configClass) {
        List<DalaranConfigField> configFields = new ArrayList<>();
        getConfigFields(configFields, configClass);
        return configFields.toArray(new DalaranConfigField[]{});
    }
}
