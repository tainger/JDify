package io.terminus.dalaran;

import io.terminus.dalaran.model.MessageModel;
import org.apache.camel.model.ProcessorDefinition;

public interface DalaranConverterContext {

    Class<? extends DalaranModelSchema> getSchemaType(BodyType modelType);

    void fromObject(ProcessorDefinition route, MessageModel model);

    void toObject(ProcessorDefinition route, MessageModel model);

    void fromObject(ProcessorDefinition route, BodyType modelType);

    void toObject(ProcessorDefinition route, BodyType modelType);
}
