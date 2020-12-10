package io.terminus.dalaran.function.date;


import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;

@FunctionFilter
@MappingFunction(value = "GetTimestamp", description = "获取当前时间戳")
public class GetTimestamp {
    public Long execute() {
        return System.currentTimeMillis();
    }
}
