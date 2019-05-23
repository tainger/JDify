package io.terminus.dalaran.console.model.dto;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class ServiceDTO {

    @Nullable
    private Long id;

    private Long moduleId;

    private String type;

    private String name;

    private Map<String, Object> importConfig;

    private String description;
}
