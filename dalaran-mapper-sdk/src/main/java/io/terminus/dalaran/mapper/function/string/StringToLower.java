package io.terminus.dalaran.mapper.function.string;

import io.terminus.dalaran.mapper.annotation.MappingFunction;
import org.apache.commons.lang3.StringUtils;

@MappingFunction(value = "StringToLower", description = "将入参字符串转换为小写")
public class StringToLower {
    public Object execute(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return str.toLowerCase();
    }
}
