package io.terminus.dalaran.config;


public interface ComponentInfo {
    String getType();

    String getName();

    int getOrder();

    DalaranConfigField[] getConfigFields();

    String getBodyType();

    ConnectorInfo getConnectorInfo();

    Class getConfigType();

    boolean isOutdated();
}
