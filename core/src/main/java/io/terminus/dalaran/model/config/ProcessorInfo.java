package io.terminus.dalaran.model.config;

import io.terminus.dalaran.BodyType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class ProcessorInfo {

    private String type;

    private DalaranConfigField[] configFields;

    @NotNull
    private BodyType[] allowedBodyTypes;

    private transient boolean serializedBody;

    private transient Class configType;

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
