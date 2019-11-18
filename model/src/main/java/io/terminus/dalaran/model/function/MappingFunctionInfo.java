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
    private Boolean containsContext = false;

    private String[] params;

    @JsonIgnore
    @JSONField(serialize = false)
    private Object bean;

    @JsonIgnore
    @JSONField(serialize = false)
    private Method method;
}
