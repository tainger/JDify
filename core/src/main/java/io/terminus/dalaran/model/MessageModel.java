package io.terminus.dalaran.model;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.DalaranModelSchema;
import lombok.Data;

@Data
public class MessageModel<Schema extends DalaranModelSchema> {

    private BodyModelType modelType;

    private Schema modelSchema;
}
