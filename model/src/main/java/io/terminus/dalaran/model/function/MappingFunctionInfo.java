package io.terminus.dalaran.model.function;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.lang.reflect.Method;

@Data
public class MappingFunctionInfo {

    private String name;
    private String description;
    private MappingFunctionType type;

    private String[] params;

    @JSONField(serialize = false)
    private Object bean;

    @JSONField(serialize = false)
    private Method method;
}
