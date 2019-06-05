package io.terminus.dalaran.core.model;

import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/4/8
 */
@Data
public class ModelField {

    private FieldType type;

    private FieldType subType;

    private boolean nullable;

    private String description;

    Map<String, ModelField> fields;
}
