package io.terminus.dalaran.model.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.BodyType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class ProcessorInfo implements ComponentInfo {

    private String type;

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
    private boolean serializedBody;

    public boolean allowedBodyType(BodyType bodyType) {
        for (BodyType allowedBodyType : allowedBodyTypes) {
            if (allowedBodyType == bodyType) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public BodyType firstAllowedBodyType() {
        if (allowedBodyTypes.length == 0) {
            throw new RuntimeException("processor[" + type + "] allowed body type is empty");
        }
        return allowedBodyTypes[0];
    }
}
