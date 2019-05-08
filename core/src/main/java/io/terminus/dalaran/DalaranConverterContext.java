package io.terminus.dalaran;

import io.terminus.dalaran.model.MessageModel;
import org.apache.camel.model.RouteDefinition;

public interface DalaranConverterContext {

    Class<? extends DalaranModelSchema> getSchemaType(BodyType modelType);

    void fromObject(RouteDefinition route, MessageModel model);

    void toObject(RouteDefinition route, MessageModel model);

    void fromObject(RouteDefinition route, BodyType modelType);

    void toObject(RouteDefinition route, BodyType modelType);
}
