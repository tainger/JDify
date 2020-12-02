package io.terminus.dalaran.function.number;

import io.terminus.dalaran.core.component.annotation.MappingFunction;

@MappingFunction(value = "MathAbsolute", description = "返回数字的绝对值")
public class MathAbsolute {
    public Number execute(Number num1) {
        return (num1.doubleValue() < 0) ? -num1.doubleValue() : num1;
    }
}
