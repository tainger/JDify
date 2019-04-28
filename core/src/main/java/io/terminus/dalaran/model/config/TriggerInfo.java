package io.terminus.dalaran.model.config;

import io.terminus.dalaran.BodyType;
import lombok.Data;

@Data
public class TriggerInfo {

    private String type;

    private Boolean isVoid;

    private DalaranConfigField[] configFields;

    private BodyType[] allowBodyTypes;

    private transient boolean serializedBody;

    private transient Class configType;
}
