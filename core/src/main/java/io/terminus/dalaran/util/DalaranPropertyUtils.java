package io.terminus.dalaran.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DalaranPropertyUtils {

    public static String getProperty(Map<String, String> properties, String configValue) {
        // TODO 性能问题...
        Pattern pattern = Pattern.compile("^(?:\\{\\{)(?<key>.*)(?:\\}\\})$");
        Matcher matcher = pattern.matcher(configValue);
        if (matcher.find()) {
            String key = matcher.group("key");
            if (properties.containsKey(key)) {
                return properties.get(key);
            }
        }
        return configValue;
    }

    public static String uriFormat(String uri, Map<String, String> properties, String... args) {
        String[] propertyArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            propertyArgs[i] = getProperty(properties, args[i]);
        }
        return String.format(uri, propertyArgs);
    }

    public static Class getComponentConfigType(String type){

        return null;
    }
}
