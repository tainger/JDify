package io.terminus.dalaran.config;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ServiceInfo {

    private String type;

    private DalaranConfigField[] configFields;

    @JSONField(serialize = false)
    private Class importConfigType;

    @JSONField(serialize = false)
    private Class serviceConfigType;

}
