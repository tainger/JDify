package io.terminus.dalaran.mapper.function.string;

import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;
import org.apache.commons.lang3.StringUtils;

@FunctionFilter
@MappingFunction(value = "StringReplace", description = "替换字符串")
public class StringReplace {
    public Object execute(String str, String pattern, String replacement) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.replace(pattern, replacement);
    }
}
