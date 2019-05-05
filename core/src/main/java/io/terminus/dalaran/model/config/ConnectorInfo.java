package io.terminus.dalaran.model.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.ComponentType;
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
