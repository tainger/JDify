package io.terminus.dalaran.console.model.dto;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Data
public class ModuleDTO {

    @Nullable
    private Long id;

    private String name;

    private List<Long> dependencies;

    private String description;
}
