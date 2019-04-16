package io.terminus.dalaran.model.config;

import io.terminus.dalaran.BodyMode;
import javafx.util.Pair;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProcessorInfo {

    private String type;

    private List<DalaranConfigField> configFields;

    private transient BodyMode bodyMode;

    private transient Class configType;
}
