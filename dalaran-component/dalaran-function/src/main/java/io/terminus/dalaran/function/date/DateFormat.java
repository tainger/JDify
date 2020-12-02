package io.terminus.dalaran.function.date;

import io.terminus.dalaran.core.component.annotation.MappingFunction;

import java.text.SimpleDateFormat;

@MappingFunction(value = "DateFormat", description = "将时间格式化")
public class DateFormat {
    public String execute(Object date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }
}
