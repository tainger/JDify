package io.terminus.dalaran.model.dto;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * Created by jingdi on 2019/4/16
 */
@Data
public class PropertyDTO {

    @Nullable
    private Long id;

    private String name;

    private String value;

    private String description;
}
