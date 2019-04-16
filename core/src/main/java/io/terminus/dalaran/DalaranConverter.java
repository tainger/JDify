package io.terminus.dalaran;

import org.apache.camel.model.ProcessorDefinition;

// TODO 其实可以搞到 DataFormat 接口的实现
public interface DalaranConverter<Schema extends DalaranModelSchema> {

    void toObject(ProcessorDefinition route, Schema schema);

    void fromObject(ProcessorDefinition route, Schema schema);

}
