package io.terminus.dalaran.mapper.function.string;

import io.terminus.dalaran.mapper.annotation.MappingFunction;
import org.apache.commons.lang3.StringUtils;

@MappingFunction(value = "StringTrim", description = "删除入参字符串的前后空格")
public class StringTrim {
    public Object execute(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.trim();
    }
}
