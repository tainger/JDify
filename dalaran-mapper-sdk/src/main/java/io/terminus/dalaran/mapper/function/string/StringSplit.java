package io.terminus.dalaran.mapper.function.string;

import io.terminus.dalaran.mapper.annotation.MappingFunction;
import org.apache.commons.lang3.StringUtils;

@MappingFunction(value = "StringSplit", description = "分割入参字符串")
public class StringSplit {
    public Object execute(String str, String regex) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.split(regex);
    }
}
