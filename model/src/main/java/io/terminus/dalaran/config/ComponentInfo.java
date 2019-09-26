package io.terminus.dalaran.config;

import io.terminus.dalaran.BodySerializeType;
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
