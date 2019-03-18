package io.terminus.dalaran.util;

import org.apache.commons.beanutils.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DalaranPropertyUtils {

    private static BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new ConvertUtilsBean() {
        @Override
        public Object convert(String value, Class clazz) {
            if (clazz.isEnum()) {
                return Enum.valueOf(clazz, value);
            }
            return super.convert(value, clazz);
        }
    });

    // TODO test
    private static Converter configObjectConverter = new Converter() {
        @Override
        public <T> T convert(Class<T> type, Object value) {
            if (!type.isAssignableFrom(Map.class) && value instanceof Map) {
                try {
                    T configObject = type.newInstance();
                    beanUtilsBean.populate(configObject, (Map<String, ? extends Object>) value);
                    return configObject;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    };

    public static <T> T convertConfig(Map<String, Object> configMap, Map<String, String> properties, Class<T> configType) {
        try {
            T config = configType.newInstance();
            replaceEnv(configMap, properties);
            // TODO 临时做一下 enum 的转换
            beanUtilsBean.populate(config, configMap);
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

    public static void registerConfigType(Class configType) {
        if (configType != null) {
            for (Field declaredField : configType.getDeclaredFields()) {
                Class type = declaredField.getType();
                if (type.isEnum()) {
                    continue;
                }
                if (type.getCanonicalName().startsWith("java.")) {
                    continue;
                }
                if (beanUtilsBean.getConvertUtils().lookup(type) == null) {
                    beanUtilsBean.getConvertUtils().register(configObjectConverter, type);
                    registerConfigType(type);
                }
            }

        }
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
