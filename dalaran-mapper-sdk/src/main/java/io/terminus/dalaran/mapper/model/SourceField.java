package io.terminus.dalaran.mapper.model;

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
