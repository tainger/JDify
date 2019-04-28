package io.terminus.dalaran.console.model.dto;

import io.terminus.dalaran.BodyType;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class ModelDTO {

    @Nullable
    private Long id;

    private Long moduleId;

    private String name;

    private BodyType modelType;

    private Map<String, Object> modelSchema;

    private String description;
}
