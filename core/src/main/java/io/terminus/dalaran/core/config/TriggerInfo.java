package io.terminus.dalaran.core.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.model.BodyType;
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
    private BodySerializeType inputSerializeType;

    @JSONField(serialize = false)
    @JsonIgnore
    private BodySerializeType outputSerializeType;

    @JSONField(serialize = false)
    @JsonIgnore
    private Class configType;

}
