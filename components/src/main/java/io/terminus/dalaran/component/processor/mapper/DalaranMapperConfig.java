package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.ModelRequiredConfig;
import io.terminus.dalaran.component.processor.mapper.model.FieldType;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class DalaranMapperConfig extends ModelRequiredConfig {

    private Map<String, String> messageMapping;

    private Map<String, FieldType> target;

    private BodyModelType targetType;

    private Map<String, FieldType> destination;

    private BodyModelType destinationType;
}
