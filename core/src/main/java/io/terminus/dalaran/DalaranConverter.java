package io.terminus.dalaran;

import org.apache.camel.model.ProcessorDefinition;

public interface DalaranConverter<Schema extends DalaranModelSchema> {

    void toObject(ProcessorDefinition route, Schema schema);

    void fromObject(ProcessorDefinition route, Schema schema);

}
