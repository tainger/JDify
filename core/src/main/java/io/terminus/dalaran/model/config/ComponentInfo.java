package io.terminus.dalaran.model.config;

import io.terminus.dalaran.BodySerializeType;
import io.terminus.dalaran.BodyType;

public interface ComponentInfo {
    String getType();

    DalaranConfigField[] getConfigFields();

    BodyType[] getAllowedBodyTypes();

    ConnectorInfo getConnectorInfo();

    BodySerializeType getSerializeType();

    Class getConfigType();
}
