package io.terminus.dalaran.core.config;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.model.BodyType;

public interface ComponentInfo {
    String getType();

    String getName();

    int getOrder();

    DalaranConfigField[] getConfigFields();

    BodyType[] getAllowedBodyTypes();

    ConnectorInfo getConnectorInfo();

    BodySerializeType getInputSerializeType();

    BodySerializeType getOutputSerializeType();

    Class getConfigType();

    boolean isOutdated();
}
