package io.terminus.dalaran.mapper.function.number;

import io.terminus.dalaran.mapper.annotation.MappingFunction;

import java.text.DecimalFormat;

@MappingFunction(value = "NumberFormat", description = "将数字格式化")
public class NumberFormat {
    public String execute(Number num, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(num.doubleValue());
    }
}
