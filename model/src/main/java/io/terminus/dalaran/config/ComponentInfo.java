package io.terminus.dalaran.config;


public interface ComponentInfo {
    String getType();

    String getName();

    int getOrder();

    DalaranConfigField[] getConfigFields();

    String getModelType();

    ConnectorInfo getConnectorInfo();

    Class getConfigType();

    boolean isOutdated();
}
