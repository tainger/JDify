package io.terminus.dalaran.core.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.core.component.ComponentType;
import lombok.Data;

@Data
public class ConnectorInfo {

    private ComponentType componentType;

    private String component;

    private DalaranConfigField[] configFields;

    @JSONField(serialize = false)
    @JsonIgnore
    private Class connectorType;
}
