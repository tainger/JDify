package io.terminus.dalaran.mapper.model;

import io.terminus.dalaran.model.FieldType;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/18
 */
@Data
public class PathDetail {

    private String path;

    private String indexes;

    private FieldType type;
}
