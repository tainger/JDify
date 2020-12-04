package io.terminus.dalaran.component.processor.mapper.model;

import io.terminus.dalaran.core.component.model.ParamType;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/18
 */
@Data
public class SourceField {

    private String path;

    private ParamType paramType;

    private SimpleMappingField field;
}
