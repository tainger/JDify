package io.terminus.dalaran.function.date;


import io.terminus.dalaran.core.component.annotation.MappingFunction;

import java.text.SimpleDateFormat;
import java.util.Date;

@MappingFunction(value = "DateParse", description = "将日期格式化")
public class DateParse {
    public String execute(Object date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date(date.toString()));
    }
}
