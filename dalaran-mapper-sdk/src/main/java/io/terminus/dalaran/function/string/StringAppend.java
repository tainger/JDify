package io.terminus.dalaran.function.string;


import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;

@FunctionFilter
@MappingFunction(value = "StringAppend", description = "拼接字符串")
public class StringAppend {
    public Object execute(String str1, String str2) {
        return str1 + str2;
    }
}
