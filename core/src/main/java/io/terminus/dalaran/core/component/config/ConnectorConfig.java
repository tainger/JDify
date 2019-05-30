package io.terminus.dalaran.core.component.config;

public interface ConnectorConfig<T> {
    T getConnector();

    void setConnector(T connector);

    Long getConnectorId();

    void setConnectorId(Long connectorId);
}
