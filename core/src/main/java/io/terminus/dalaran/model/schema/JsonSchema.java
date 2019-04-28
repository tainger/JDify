package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.DalaranModelSchema;
import io.terminus.dalaran.model.schema.model.ModelField;
import lombok.Data;

import java.util.Map;

@Data
public class JsonSchema implements DalaranModelSchema {
    private Map<String, ModelField> fields;
}
