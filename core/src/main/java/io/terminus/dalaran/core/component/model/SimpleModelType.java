package io.terminus.dalaran.core.component.model;

import io.terminus.dalaran.model.DalaranModelSchema;
import org.apache.camel.model.ProcessorDefinition;

public interface SimpleModelType<T, Schema extends DalaranModelSchema> extends DalaranModelType<T, Schema> {

    @Override
    default void fromObject(ProcessorDefinition route, Schema schema) {
        route.process(exchange -> {
            exchange.getOut().copyFrom(exchange.getIn());
            Object outBody = fromObject(exchange.getIn().getBody(), schema);
            exchange.getOut().setBody(outBody);
        });
    }

    @Override
    default void toObject(ProcessorDefinition route, Schema schema) {
        route.process(exchange -> {
            exchange.getOut().copyFrom(exchange.getIn());
            Object outBody = toObject((T) exchange.getIn().getBody(), schema);
            exchange.getOut().setBody(outBody);
        });
    }

    T fromObject(Object obj, DalaranModelSchema schema);

    Object toObject(T data, Schema schema);
}
