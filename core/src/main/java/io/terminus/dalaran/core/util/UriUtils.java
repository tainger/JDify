package io.terminus.dalaran.core.util;

import java.util.Iterator;
import java.util.Map;

public class UriUtils {
    public static String buildOptionsQueryString(Map<String, Object> options) {
        Iterator<String> optionsQueryString = options.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue()).iterator();
        StringBuilder queryString = new StringBuilder();
        for (; optionsQueryString.hasNext(); queryString.append(optionsQueryString.next())) {
            if (queryString.length() != 0) {
                queryString.append("&");
            }
        }
        return "?" + queryString.toString();
    }
}
