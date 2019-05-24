package io.terminus.dalaran.util;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.model.config.DalaranConfigField;
import lombok.val;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigFieldUtils {
    private static void getConfigFields(List<DalaranConfigField> configFields, Class configClass) {
        if (configClass.getSuperclass() != null) {
            getConfigFields(configFields, configClass.getSuperclass());
        }

        for (Field field : configClass.getDeclaredFields()) {
            ConfigFieldInfo configFieldInfo = field.getDeclaredAnnotation(ConfigFieldInfo.class);
            if (configFieldInfo != null && configFieldInfo.inputType() != FieldInputType.Hidden) {
                DalaranConfigField configField = new DalaranConfigField();
                configField.setName(field.getName());
                configField.setInputType(configFieldInfo.inputType());
                configField.setExample(configFieldInfo.example());
                configField.setDefaultValue(configFieldInfo.defaultValue());
                configField.setLabel(configFieldInfo.label());
                configField.setRequired(configFieldInfo.required());
                configField.setReadonly(configFieldInfo.readonly());

                if (field.getType().isEnum()) {
                    val enumValueMap = new HashMap<String, String>();
                    for (Field element : field.getType().getFields()) {
                        val name = element.getName();
                        enumValueMap.put(name, name);
                    }
                    configField.setEnumValues(enumValueMap);
                }

                configFields.add(configField);
            }
        }
    }

    public static DalaranConfigField[] buildConfigFields(Class configClass) {
        List<DalaranConfigField> configFields = new ArrayList<>();
        getConfigFields(configFields, configClass);
        return configFields.toArray(new DalaranConfigField[]{});
    }
}
