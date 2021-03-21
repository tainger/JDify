package io.terminus.dalaran.config;


public interface ComponentInfo {
    String getType();

    String getName();

    int getOrder();

    DalaranConfigField[] getConfigFields();

    String getModelType();

    String getOrigin();

    ConnectorInfo getConnectorInfo();

    Class getConfigType();

    boolean isOutdated();
}
