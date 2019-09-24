package io.terminus.dalaran.config;

import com.alibaba.fastjson.annotation.JSONField;
import io.terminus.dalaran.ComponentType;
import lombok.Data;

@Data
public class ConnectorInfo {

    private ComponentType componentType;

    private String component;

    private DalaranConfigField[] configFields;

    @JSONField(serialize = false)
    private Class connectorType;
}
