package io.terminus.dalaran.model;

import lombok.Data;

import java.util.LinkedHashMap;
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

    Map<String, ModelField> fields = new LinkedHashMap<>();

    public void addField(String name, ModelField field) {
        fields.put(name, field);
    }
}
