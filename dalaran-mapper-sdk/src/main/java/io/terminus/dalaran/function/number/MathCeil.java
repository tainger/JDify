package io.terminus.dalaran.function.number;


import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;

@FunctionFilter
@MappingFunction(value = "MathCeil", description = "将数字向上舍去小数")
public class MathCeil {
    public Number execute(Number num) {
        return Math.ceil(num.doubleValue());
    }
}
