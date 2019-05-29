package io.terminus.dalaran.core.config;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.model.BodyType;

public interface ComponentInfo {
    String getType();

    DalaranConfigField[] getConfigFields();

    BodyType[] getAllowedBodyTypes();

    ConnectorInfo getConnectorInfo();

    BodySerializeType getSerializeType();

    Class getConfigType();
}
