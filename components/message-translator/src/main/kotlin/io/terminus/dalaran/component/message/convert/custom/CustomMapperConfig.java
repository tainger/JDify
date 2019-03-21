package io.terminus.dalaran.component.message.convert.custom;

import io.terminus.dalaran.component.message.convert.custom.model.ModelType;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class CustomMapperConfig {

    private Map<String, String> messageMapping;

    private List<String> target;

    private ModelType targetType;

    private List<String> destination;

    private ModelType destinationType;
}
