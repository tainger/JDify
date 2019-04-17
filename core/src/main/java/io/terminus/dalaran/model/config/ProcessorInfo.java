package io.terminus.dalaran.model.config;

import io.terminus.dalaran.BodyMode;
import lombok.Data;

import java.util.List;

@Data
public class ProcessorInfo {

    private String type;

    private List<DalaranConfigField> configFields;

    private transient BodyMode bodyMode;

    private transient Class configType;
}
