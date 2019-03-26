package io.terminus.dalaran.component.message.convert.custom;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.component.message.convert.custom.model.FieldType;
import lombok.Data;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class CustomMapperConfig {

    private Map<String, String> messageMapping;

    private Map<String, FieldType> target;

    private BodyModelType targetType;

    private Map<String, FieldType> destination;

    private BodyModelType destinationType;
}
