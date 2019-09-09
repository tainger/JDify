package io.terminus.dalaran.console.model.dto;

import io.terminus.dalaran.console.model.dto.basic.BasicModelInfo;
import io.terminus.dalaran.model.ModelTargetType;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class ModelDTO extends BasicModelInfo {

    private Map<String, Object> modelSchema;

    private String modelKey;

    private String targetId;

    private ModelTargetType targetType = ModelTargetType.Normal;

    private String description;
}
