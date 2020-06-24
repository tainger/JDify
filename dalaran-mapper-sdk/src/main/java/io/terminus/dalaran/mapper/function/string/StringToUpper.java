package io.terminus.dalaran.mapper.function.string;

import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;
import org.apache.commons.lang3.StringUtils;

@FunctionFilter
@MappingFunction(value = "StringToUpper", description = "将入参字符串转换为大写")
public class StringToUpper {
    public Object execute(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return str.toUpperCase();
    }
}
