package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.component.processor.mapper.model.SimpleMapping;
import io.terminus.dalaran.core.component.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ImmutableInModelConfig;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class DalaranMapperConfig extends ImmutableInModelConfig {

    private List<SimpleMapping> noDestinationMappings;

    private HashMap<String, SimpleMapping> messageMapping;
}
