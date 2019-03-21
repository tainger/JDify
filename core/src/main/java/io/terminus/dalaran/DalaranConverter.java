package io.terminus.dalaran;

import org.apache.camel.model.ProcessorDefinition;

public interface DalaranConverter {

    void toObject(ProcessorDefinition route);

    void fromObject(ProcessorDefinition route);

}
