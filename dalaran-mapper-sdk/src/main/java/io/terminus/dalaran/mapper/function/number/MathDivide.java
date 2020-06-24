package io.terminus.dalaran.mapper.function.number;


import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;

@FunctionFilter
@MappingFunction(value = "MathDivide", description = "将两个数字相除 (num1 / num2)")
public class MathDivide {
    public Number execute(Number num1, Number num2) {
        return num1.doubleValue() / num2.doubleValue();
    }
}
