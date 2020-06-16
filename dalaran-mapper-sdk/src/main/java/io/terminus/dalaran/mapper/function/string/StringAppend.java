package io.terminus.dalaran.mapper.function.string;

import io.terminus.dalaran.mapper.annotation.MappingFunction;

@MappingFunction(value = "StringAppend", description = "拼接字符串")
public class StringAppend {
    public Object execute(String str1, String str2) {
        return str1 + str2;
    }
}
