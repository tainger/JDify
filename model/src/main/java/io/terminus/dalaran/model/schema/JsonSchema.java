package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.annotation.Model;
import lombok.Data;

@Data
@Model(value = "JSON")
public class JsonSchema extends DalaranModelSchema {

}
