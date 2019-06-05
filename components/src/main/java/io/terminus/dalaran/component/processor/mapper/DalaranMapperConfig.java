package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.component.processor.mapper.model.SimpleMappingField;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class DalaranMapperConfig extends OutModelConfig {

    private Map<String, SimpleMappingField> messageMapping;
}
