package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.ModelTargetType;
import io.terminus.dalaran.model.dto.basic.BasicModelInfo;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class ModelDTO extends BasicModelInfo {

    @Nullable
    private Map<String, Object> modelSchema;

    private String targetId;

    private ModelTargetType targetType = ModelTargetType.Normal;

    private String description;
}
