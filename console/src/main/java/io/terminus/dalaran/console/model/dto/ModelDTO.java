package io.terminus.dalaran.console.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class ModelDTO extends BasicModelInfo {

    private Map<String, Object> modelSchema;

    private String description;
}
