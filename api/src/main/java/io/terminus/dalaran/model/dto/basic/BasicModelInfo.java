package io.terminus.dalaran.model.dto.basic;

import io.terminus.dalaran.model.BodyType;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class BasicModelInfo {

    @Nullable
    private Long id;

    private Long moduleId;

    private String name;

    private BodyType modelType;

    public BasicModelInfo() {
    }

    public BasicModelInfo(@Nullable Long id, Long moduleId, String name, BodyType modelType) {
        this.id = id;
        this.moduleId = moduleId;
        this.name = name;
        this.modelType = modelType;
    }
}

