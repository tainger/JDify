package io.terminus.dalaran.mapper.function.date;

import io.terminus.dalaran.mapper.annotation.FunctionFilter;
import io.terminus.dalaran.model.annotation.MappingFunction;

import java.util.Date;

@FunctionFilter
@MappingFunction(value = "GetCurrentDate", description = "获取当前时间")
public class GetCurrentDate {
    public Date execute() {
        return new Date();
    }
}
