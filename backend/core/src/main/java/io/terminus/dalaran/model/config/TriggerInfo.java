package io.terminus.dalaran.model.config;

import io.terminus.dalaran.BodyMode;
import lombok.Data;

import java.util.List;

@Data
public class TriggerInfo {

    private String type;

    private Boolean isVoid;

    private List<DalaranConfigField> configFields;

    private transient BodyMode bodyMode;

    private transient Class configType;
}
