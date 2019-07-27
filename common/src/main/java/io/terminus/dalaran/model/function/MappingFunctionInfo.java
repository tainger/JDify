package io.terminus.dalaran.model.function;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.lang.reflect.Method;

@Data
public class MappingFunctionInfo {

    private String name;
    private String description;
    private MappingFunctionType type;

    private String[] params;

    @JSONField(serialize = false)
    @JsonIgnore
    private Object bean;

    @JSONField(serialize = false)
    @JsonIgnore
    private Method method;
}
