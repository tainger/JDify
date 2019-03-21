package io.terminus.dalaran.util;

import com.sun.deploy.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UriUtils {
    public static String buildOptionsQueryString(Map<String, Object> options) {
        List<String> optionsQueryString = options.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.toList());
        return "?" + StringUtils.join(optionsQueryString, "&");
    }
}
