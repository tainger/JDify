package io.terminus.dalaran.model.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.BodySerializeType;
import io.terminus.dalaran.BodyType;
import lombok.Data;

@Data
public class TriggerInfo implements ComponentInfo {

    private String type;

    private Boolean isVoid;

    private DalaranConfigField[] configFields;

    private BodyType[] allowedBodyTypes;

    @JSONField(serialize = false)
    @JsonIgnore
    private ConnectorInfo connectorInfo;

    @JSONField(serialize = false)
    @JsonIgnore
    private BodySerializeType serializeType;

    @JSONField(serialize = false)
    @JsonIgnore
    private Class configType;

}
