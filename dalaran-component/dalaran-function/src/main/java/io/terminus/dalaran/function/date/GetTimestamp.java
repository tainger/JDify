package io.terminus.dalaran.function.date;

import io.terminus.dalaran.core.component.annotation.MappingFunction;

@MappingFunction(value = "GetTimestamp", description = "获取当前时间戳")
public class GetTimestamp {
    public Long execute() {
        return System.currentTimeMillis();
    }
}
