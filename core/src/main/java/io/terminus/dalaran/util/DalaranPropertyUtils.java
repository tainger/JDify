package io.terminus.dalaran.util;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DalaranPropertyUtils {

    public static <T> T convertConfig(Map<String, Object> configMap, Map<String, String> properties, Class<T> configType) {
        try {
            T config = configType.newInstance();
            replaceEnv(configMap, properties);
            BeanUtils.populate(config, configMap);
            return config;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPropertyKey(String configValue) {
        // TODO 性能问题...
        Pattern pattern = Pattern.compile("^(?:\\{\\{)(?<key>.*)(?:\\}\\})$");
        Matcher matcher = pattern.matcher(configValue);
        if (matcher.find()) {
            return matcher.group("key");
        }
        return null;
    }

    public static void replaceEnv(Map<String, Object> configMap, Map<String, String> properties) {
        configMap.forEach((k, v) -> {
            if (v instanceof String) {
                String propertyKey = getPropertyKey((String) v);
                if (properties.containsKey(propertyKey)) {
                    configMap.put(k, properties.get(propertyKey));
                }
            }
            if (v instanceof Map) {
                replaceEnv((Map<String, Object>) v, properties);
            }
        });
    }
}
