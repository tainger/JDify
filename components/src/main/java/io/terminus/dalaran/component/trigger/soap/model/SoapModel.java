package io.terminus.dalaran.component.trigger.soap.model;

import io.terminus.dalaran.model.DalaranModelSchema;
import lombok.Data;

@Data
public class SoapModel {

    private String name;

    private DalaranModelSchema schema;

    public SoapModel(String name, DalaranModelSchema schema) {
        this.name = name;
        this.schema = schema;
    }
}
