package io.terminus.dalaran;

import io.terminus.dalaran.model.MessageModel;
import org.apache.camel.model.RouteDefinition;

public interface DalaranConverterContext {

    Class<? extends DalaranModelSchema> getSchemaType(BodyType modelType);

    void convert(RouteDefinition route, BodyType currentBodyType, BodyType nextBodyType);

    void convert(RouteDefinition route, BodyType currentBodyType, MessageModel model);
}
