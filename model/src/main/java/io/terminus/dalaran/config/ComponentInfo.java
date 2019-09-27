package io.terminus.dalaran.config;

import io.terminus.dalaran.model.BodyType;

public interface ComponentInfo {
    String getType();

    String getName();

    int getOrder();

    DalaranConfigField[] getConfigFields();

    BodyType getBodyType();

    ConnectorInfo getConnectorInfo();

    Class getConfigType();

    boolean isOutdated();
}
