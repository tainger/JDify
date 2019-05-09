package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.ModelRequiredConfig;
import io.terminus.dalaran.component.processor.mapper.model.SimpleMappingField;
import lombok.Data;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class DalaranMapperConfig extends ModelRequiredConfig {

    private Map<String, SimpleMappingField> messageMapping;
}
