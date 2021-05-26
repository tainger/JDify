package io.terminus.dalaran.core.util;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.config.DalaranConfigField;
import io.terminus.dalaran.config.ValidateConfig;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

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
            if (configFieldInfo != null && !StringUtils.equals(configFieldInfo.inputType(), FieldInputType.Hidden)) {
                DalaranConfigField configField = new DalaranConfigField();
                configField.setName(field.getName());
                configField.setInputType(configFieldInfo.inputType());
                configField.setExample(configFieldInfo.example());
                configField.setDefaultValue(configFieldInfo.defaultValue());
                configField.setLabel(configFieldInfo.label());
                configField.setRequired(configFieldInfo.required());
                configField.setReadonly(configFieldInfo.readonly());
                configField.setPath(configFieldInfo.path());
                configField.setParam(configFieldInfo.param());
                configField.setShow(configFieldInfo.show());
                configField.setDynamic(configFieldInfo.dynamic());

                //TODO 暂时放在这里，后面统一处理
                if (configFieldInfo.inputType().equals("String")) {
                    ValidateConfig validateConfig = new ValidateConfig();
                    if (field.getName().equals("expireTime")) {
                        validateConfig.setOnlyNumber(true);
                        validateConfig.setMinNumber(5);
                    }
                    configField.setValidateConfig(validateConfig);
                }

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
