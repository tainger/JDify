package io.terminus.dalaran.model.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.BodyType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class ServiceInfo {

    private String type;

    @JSONField(serialize = false)
    @JsonIgnore
    private Class importConfigType;

    @JSONField(serialize = false)
    @JsonIgnore
    private Class serviceConfigType;

}
