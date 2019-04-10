package io.terminus.dalaran.processor.mapper;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.processor.mapper.model.FieldType;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class DalaranMapperConfig {

    private Map<String, String> messageMapping;

    private Map<String, FieldType> target;

    private BodyModelType targetType;

    private Map<String, FieldType> destination;

    private BodyModelType destinationType;
}
