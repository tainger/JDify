package io.terminus.dalaran;

import io.terminus.dalaran.model.MessageModel;
import org.apache.camel.model.RouteDefinition;

public interface DalaranConverterContext {

    Class<? extends DalaranModelSchema> getSchemaType(BodyModelType modelType);

    void unmarshal(RouteDefinition route, MessageModel model);

    void marshal(RouteDefinition route, MessageModel model);
}
