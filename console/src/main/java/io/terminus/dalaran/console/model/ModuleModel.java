package io.terminus.dalaran.console.model;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Data
public class ModuleModel {

    @Nullable
    private Long id;

    private String name;

    private List<Long> dependencies;

    private String description;
}
