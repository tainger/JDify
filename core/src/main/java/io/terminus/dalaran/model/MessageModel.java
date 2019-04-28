package io.terminus.dalaran.model;

import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranModelSchema;
import lombok.Data;

@Data
public class MessageModel<Schema extends DalaranModelSchema> {

    private BodyType modelType;

    private Schema modelSchema;
}
