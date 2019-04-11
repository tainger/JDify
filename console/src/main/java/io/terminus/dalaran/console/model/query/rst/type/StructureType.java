package io.terminus.dalaran.console.model.query.rst.type;

import io.terminus.dalaran.BodyModelType;
import lombok.Data;

/**
 * Created by jingdi on 2019/4/10
 */
@Data
public class StructureType {

    private BodyModelType type;

    public StructureType(BodyModelType type) {
        this.type = type;
    }
}
