package io.terminus.dalaran.mapper.function.number;


import io.terminus.dalaran.mapper.annotation.MappingFunction;

@MappingFunction(value = "MathAdd", description = "将两个数字相加 (num1 + num2)")
public class MathAdd {
    public Number execute(Number num1, Number num2) {
        return num1.doubleValue() + num2.doubleValue();
    }
}
