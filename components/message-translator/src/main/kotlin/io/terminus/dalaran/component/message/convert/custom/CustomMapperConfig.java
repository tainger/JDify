package io.terminus.dalaran.component.message.convert.custom;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.component.message.convert.custom.model.DataType;
import lombok.Data;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class CustomMapperConfig {

    private Map<String, String> messageMapping;

    private Map<String, DataType> target;

    private BodyModelType targetType;

    private Map<String, DataType> destination;

    private BodyModelType destinationType;
}
