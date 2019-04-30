package io.terminus.dalaran.model.config;

import io.terminus.dalaran.BodyType;

public interface ComponentInfo {
    String getType();

    DalaranConfigField[] getConfigFields();

    BodyType[] getAllowedBodyTypes();

    ConnectorInfo getConnectorInfo();

    boolean isSerializedBody();

    Class getConfigType();
}
