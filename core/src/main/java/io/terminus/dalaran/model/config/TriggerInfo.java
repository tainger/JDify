package io.terminus.dalaran.model.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.BodyType;
import lombok.Data;

@Data
public class TriggerInfo {

    private String type;

    private Boolean isVoid;

    private DalaranConfigField[] configFields;

    private BodyType[] allowBodyTypes;

    @JsonIgnore
    private ConnectorInfo connectorInfo;

    @JsonIgnore
    private transient boolean serializedBody;

    @JsonIgnore
    private transient Class configType;

}
