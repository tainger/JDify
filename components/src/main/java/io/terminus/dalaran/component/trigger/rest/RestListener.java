package io.terminus.dalaran.component.trigger.rest;

import io.swagger.models.Swagger;
import io.terminus.dalaran.component.trigger.rest.model.ApiInfo;
import io.terminus.dalaran.component.trigger.rest.processor.QueryStringConvertProcessor;
import io.terminus.dalaran.component.trigger.rest.processor.QueryStringSignProcessor;
import io.terminus.dalaran.component.trigger.rest.processor.SignProcessor;
import io.terminus.dalaran.component.trigger.rest.utils.RestWordUtils;
import io.terminus.dalaran.component.trigger.rest.utils.SwaggerUtils;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerApiDocExport;
import io.terminus.dalaran.core.component.DalaranTriggerWordDocExport;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.context.DalaranClientContext;
import io.terminus.dalaran.model.flow.TriggerFlow;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Trigger(
        value = {"http-rest-listener", "netty-http-listener"},
        order = 10,
        configType = RestConfig.class,
        bodyType = "JSON"
)
public class RestListener implements DalaranTrigger<RestConfig>, DalaranTriggerApiDocExport<Swagger>, DalaranTriggerWordDocExport {

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

    @Override
    public Swagger exportApiDoc(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        return SwaggerUtils.buildSwagger(buildApiInfoList(moduleTriggerFlows));
    }

    @Override
    public File exportWord(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        return RestWordUtils.buildWordFile(buildApiInfoList(moduleTriggerFlows));
    }

    private List<ApiInfo> buildApiInfoList(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        return moduleTriggerFlows.entrySet().stream().flatMap(module ->
                module.getValue().stream().map(flow -> new ApiInfo(module.getKey(), flow))
        ).collect(Collectors.toList());
    }
}
