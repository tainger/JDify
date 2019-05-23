package io.terminus.dalaran.model.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ServiceInfo {

    private String type;

    private DalaranConfigField[] configFields;

    @JSONField(serialize = false)
    @JsonIgnore
    private Class importConfigType;

    @JSONField(serialize = false)
    @JsonIgnore
    private Class serviceConfigType;

}
