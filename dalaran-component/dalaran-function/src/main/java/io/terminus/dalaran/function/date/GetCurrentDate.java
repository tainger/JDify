package io.terminus.dalaran.function.date;

import io.terminus.dalaran.core.component.annotation.MappingFunction;

import java.util.Date;

@MappingFunction(value = "GetCurrentDate", description = "获取当前时间")
public class GetCurrentDate {
    public Date execute() {
        return new Date();
    }
}
