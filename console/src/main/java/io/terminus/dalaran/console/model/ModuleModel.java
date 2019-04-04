package io.terminus.dalaran.console.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Data
public class ModuleModel {
    private Long id;

    private String name;

    private List<Long> dependencies;

    private String description;
}
