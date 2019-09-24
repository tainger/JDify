package io.terminus.dalaran.model.trantor;

import io.terminus.dalaran.model.schema.JsonSchema;
import lombok.Data;

@Data
public class DalaranIntegrationPoint {

    private String key;

    private String name;

    private String description;

    private JsonSchema returnType;

    private JsonSchema paramType;

}
