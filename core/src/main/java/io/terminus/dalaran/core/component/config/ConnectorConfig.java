package io.terminus.dalaran.core.component.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface ConnectorConfig<T> {

    @JSONField(serialize = false)
    @JsonIgnore
    T getConnector();

    void setConnector(T connector);

    Long getConnectorId();

    void setConnectorId(Long connectorId);
}
