package io.terminus.dalaran.function.number;

import io.terminus.dalaran.core.component.annotation.MappingFunction;

@MappingFunction(value = "MathFloor", description = "将数字向下舍去小数")
public class MathFloor {
    public Number execute(Number num) {
        return Math.floor(num.doubleValue());
    }
}
