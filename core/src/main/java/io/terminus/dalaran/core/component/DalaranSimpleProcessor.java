package io.terminus.dalaran.core.component;

import org.apache.camel.model.ProcessorDefinition;

import java.lang.reflect.ParameterizedType;

public interface DalaranSimpleProcessor<Config, Body> extends DalaranProcessor<Config> {
    @Override
    default void configure(ProcessorDefinition route, Config config) {
        Class<Body> clazz = getBodyClass();
        route.process(exchange -> {
            Body body = exchange.getIn(clazz);
            Object outBody = process(body, config);
            exchange.getOut().copyFrom(exchange.getIn());
            exchange.getOut().setBody(outBody);
        });
    }

    default Class<Body> getBodyClass() {
        return (Class<Body>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    Object process(Body body, Config config);

}
