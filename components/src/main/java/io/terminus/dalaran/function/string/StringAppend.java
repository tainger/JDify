package io.terminus.dalaran.function.string;

import io.terminus.dalaran.core.component.annotation.MappingFunction;
import org.apache.commons.lang3.StringUtils;

@MappingFunction(value = "StringAppend", description = "拼接字符串")
public class StringAppend {
    public Object execute(String str1, String str2) {
        return str1 + str2;
    }
}
