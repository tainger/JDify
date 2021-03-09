package io.terminus.dalaran.model.dto;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Data
public class ModuleDTO {

    @Nullable
    private String id;

    private String name;

    private List<Long> dependencies;

    private String description;

    private boolean isExist;
}
