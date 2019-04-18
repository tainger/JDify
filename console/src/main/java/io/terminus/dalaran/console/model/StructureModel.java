package io.terminus.dalaran.console.model;

import io.terminus.dalaran.BodyModelType;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class StructureModel {

    @Nullable
    private Long id;

    private Long moduleId;

    private String name;

    private BodyModelType structureType;

    private Map<String, Object> structureSchema;

    private String description;
}
