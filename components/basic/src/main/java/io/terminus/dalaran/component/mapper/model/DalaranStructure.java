package io.terminus.dalaran.component.mapper.model;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.model.schema.structure.ModelField;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/4/8
 */
@Data
public class DalaranStructure {

    private BodyModelType type;

    private Map<String, ModelField> fields;
}
