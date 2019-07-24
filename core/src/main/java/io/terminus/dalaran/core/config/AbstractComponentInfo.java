package io.terminus.dalaran.core.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.model.BodyType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public abstract class AbstractComponentInfo implements ComponentInfo {

    private String type;

    private String name;

    private DalaranConfigField[] configFields;

    @NotNull
    private BodyType[] allowedBodyTypes;

    @JSONField(serialize = false)
    @JsonIgnore
    private ConnectorInfo connectorInfo;

    @JSONField(serialize = false)
    @JsonIgnore
    private Class configType;

    @JSONField(serialize = false)
    @JsonIgnore
    private BodySerializeType inputSerializeType;

    @JSONField(serialize = false)
    @JsonIgnore
    private BodySerializeType outputSerializeType;
}
