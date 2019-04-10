package io.terminus.dalaran.component.processor.mapper.model;

import io.terminus.dalaran.BodyModelType;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/12
 */
@Data
public class DalaranMessage {

    private Map<String, Object> fields;

    private BodyModelType type;
}
