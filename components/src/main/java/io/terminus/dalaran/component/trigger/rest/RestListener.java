package io.terminus.dalaran.component.trigger.rest;

import io.terminus.dalaran.component.trigger.rest.processor.QueryStringConvertProcessor;
import io.terminus.dalaran.component.trigger.rest.processor.QueryStringSignProcessor;
import io.terminus.dalaran.component.trigger.rest.processor.SignProcessor;
import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.context.DalaranClientContext;
import io.terminus.dalaran.model.BodyType;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;

@Trigger(
        value = {"http-rest-listener", "netty-http-listener"},
        name = "Rest 监听器",
        order = 10,
        configType = RestConfig.class,
        allowBodyTypes = {BodyType.JSON, BodyType.XML},
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
public class RestListener implements DalaranTrigger<RestConfig> {

    @Autowired
    private DalaranClientContext clientContext;

    @Override
    public void buildFromRoute(RouteDefinition route, RestConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://0.0.0.0:" + config.getPort() + config.getPath() +
                "?httpMethodRestrict=" + config.getMethod();
        route.from(uri);
        if (config.getMethod().isNoBody()) {
            if (config.isEnableSign()) {
                route.process(new QueryStringSignProcessor(clientContext.getAllClient()));
            } else {
                route.process(new QueryStringConvertProcessor());
            }
            // TODO 目前会多一次序列化, 如果下个节点要求的是非序列化对象, 会有额外的性能开销
            route.marshal().json(JsonLibrary.Fastjson);
        } else {
            if (config.isEnableSign()) {
                route.unmarshal().json(JsonLibrary.Fastjson);
                route.process(new SignProcessor(clientContext.getAllClient()));
            } else {
                // TODO Stream to string
                route.convertBodyTo(String.class);
            }
        }
    }
}
