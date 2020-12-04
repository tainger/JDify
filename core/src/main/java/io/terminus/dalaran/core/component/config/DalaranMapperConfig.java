package io.terminus.dalaran.core.component.config;

import io.terminus.dalaran.core.component.model.SimpleMapping;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class DalaranMapperConfig extends ImmutableInModelConfig {

    private List<SimpleMapping> noDestinationMappings;

    private HashMap<String, SimpleMapping> messageMapping;
}
