package io.terminus.dalaran.core.component.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class ServiceOperationConfig extends ComponentModelConfig {

    @ConfigFieldInfo(label = "服务", inputType = FieldInputType.Service)
    private Long serviceId;

    @ConfigFieldInfo(label = "操作", inputType = FieldInputType.ServiceOperation)
    private String operation;

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private String serviceType;

}
