package io.terminus.dalaran.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class ServiceOperationConfig extends ImmutableModelConfig {

    @ConfigFieldInfo(label = "服务", inputType = FieldInputType.Service)
    private Long serviceId;

    @ConfigFieldInfo(label = "操作", inputType = FieldInputType.ServiceOperation)
    private String operation;

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private String serviceType;

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private ImmutableModelConfig operationConfig;

}
