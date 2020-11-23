package io.terminus.dalaran.component.trigger.rest;

import io.swagger.models.Swagger;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.common.LimitOperation;
import io.terminus.dalaran.component.limiter.DalaranLimiter;
import io.terminus.dalaran.component.trigger.rest.model.ApiInfo;
import io.terminus.dalaran.component.trigger.rest.processor.MixMethodProcessor;
import io.terminus.dalaran.component.trigger.rest.processor.QueryStringConvertProcessor;
import io.terminus.dalaran.component.trigger.rest.processor.QueryStringSignProcessor;
import io.terminus.dalaran.component.trigger.rest.processor.SignProcessor;
import io.terminus.dalaran.component.trigger.rest.utils.RestWordUtils;
import io.terminus.dalaran.component.trigger.rest.utils.SwaggerUtils;
import io.terminus.dalaran.core.component.DalaranCircuitBreaker;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerApiDocExport;
import io.terminus.dalaran.core.component.DalaranTriggerWordDocExport;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.context.DalaranClientContext;
import io.terminus.dalaran.core.flow.DalaranRoute;
import io.terminus.dalaran.core.log.TracingErrorHandlerFactory;
import io.terminus.dalaran.model.HttpProtocol;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ThrottleDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.terminus.dalaran.DalaranConstants.DIRECT_PREFIX;

@Trigger(
        value = {"http-rest-listener", "netty-http-listener"},
        order = 10,
        configType = RestConfig.class,
        bodyType = "JSON"
)
public class RestListener implements DalaranTrigger<RestConfig>, DalaranTriggerApiDocExport<Swagger>, DalaranTriggerWordDocExport, DalaranCircuitBreaker<RestConfig> {

    @Autowired
    private DalaranClientContext clientContext;

    @Override
    public void buildFromRoute(RouteDefinition route, RestConfig config) {
        String uri = "netty4-http:" + config.getProtocol().name().toLowerCase() +
                "://0.0.0.0:" + config.getPort() + config.getPath() +
                "?chunkedMaxContentLength=104857600";
        if (config.getMethod() != HttpMethod.MIX) {
            uri += "&httpMethodRestrict=" + config.getMethod();
        } else {
            uri += "&httpMethodRestrict=GET,POST";
        }
        if (config.getProtocol().equals(HttpProtocol.HTTPS)) {
            uri += "&ssl=true";
        }
        route.from(uri);
        if (config.getMethod() == HttpMethod.MIX) {
            route.process(new MixMethodProcessor());
            route.convertBodyTo(String.class);
            return;
        }
        if (config.getMethod().isNoBody()) {
            if (config.isEnableSign()) {
                route.process(new QueryStringSignProcessor(clientContext.getAllClient(), config.isCheckSign()));
            } else {
                route.process(new QueryStringConvertProcessor());
            }
            // TODO 目前会多一次序列化, 如果下个节点要求的是非序列化对象, 会有额外的性能开销
            route.marshal().json(JsonLibrary.Fastjson);
        } else {
            if (config.isEnableSign()) {
                route.unmarshal().json(JsonLibrary.Fastjson);
                route.process(new SignProcessor(clientContext.getAllClient(), config.isCheckSign()));
            }
            route.convertBodyTo(String.class);
        }
    }

    @Override
    public Swagger exportApiDoc(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        return SwaggerUtils.buildSwagger(buildApiInfoListNew(moduleTriggerFlows));
    }

    @Override
    public File exportWord(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        return RestWordUtils.buildWordFile(buildApiInfoListNew(moduleTriggerFlows));
    }

    @Override
    public void buildBreakerConfig(RouteDefinition route, String to, RestConfig config, CamelContext camelContext, TracingErrorHandlerFactory errorHandlerFactory) {
        if (!config.isEnableBreaker() && !config.isEnableLimit()) {
            route.to(DIRECT_PREFIX + to);
            return;
        }
        DalaranLimiter limiter = config.getLimiter();
        if (config.isEnableBreaker() && !config.isEnableLimit()) {
            route.hystrix().hystrixConfiguration()
                    .circuitBreakerEnabled(true)
                    .circuitBreakerErrorThresholdPercentage(limiter.getCircuitBreakerErrorPercentage())
                    .executionTimeoutInMilliseconds(Integer.valueOf(config.getTimeout().toString()))
                    .circuitBreakerSleepWindowInMilliseconds(limiter.getCircuitBreakerSleepWindowInMilliseconds())
                    .circuitBreakerRequestVolumeThreshold(limiter.getCircuitBreakerRequestVolumeThreshold()).end()
                    .to(DIRECT_PREFIX + to).end();
            return;
        }
        if (config.isEnableLimit() && !config.isEnableBreaker()) {
            ThrottleDefinition throttleDefinition = route.throttle(limiter.getLimitRequestNum()).timePeriodMillis(limiter.getLimitPeriod());
            if (limiter.getOperation() == LimitOperation.DELAY) {
                throttleDefinition.asyncDelayed();
            }
            if (limiter.getOperation() == LimitOperation.REJECT) {
                throttleDefinition.rejectExecution(true);
            }
            throttleDefinition.to(DIRECT_PREFIX + to).end();
            return;
        }
        val hystrixRoute = new DalaranRoute();
        hystrixRoute.errorHandler(errorHandlerFactory);
        String hystrixRouteId = "Hystrix-" + to;
        hystrixRoute.setId(hystrixRouteId);
        hystrixRoute.from(DIRECT_PREFIX + hystrixRouteId);

        try {
            camelContext.removeRoute(hystrixRouteId);
            hystrixRoute.hystrix().hystrixConfiguration()
                    .circuitBreakerEnabled(true)
                    .circuitBreakerErrorThresholdPercentage(limiter.getCircuitBreakerErrorPercentage())
                    .executionTimeoutInMilliseconds(Integer.valueOf(config.getTimeout().toString()))
                    .circuitBreakerSleepWindowInMilliseconds(limiter.getCircuitBreakerSleepWindowInMilliseconds())
                    .circuitBreakerRequestVolumeThreshold(limiter.getCircuitBreakerRequestVolumeThreshold()).end()
                    .to(DIRECT_PREFIX + to).end();
            camelContext.addRouteDefinition(hystrixRoute);

            ThrottleDefinition throttleDefinition = route.throttle(limiter.getLimitRequestNum()).timePeriodMillis(limiter.getLimitPeriod());
            if (limiter.getOperation() == LimitOperation.DELAY) {
                throttleDefinition.asyncDelayed();
            }
            if (limiter.getOperation() == LimitOperation.REJECT) {
                throttleDefinition.rejectExecution(true);
            }
            throttleDefinition.to(DIRECT_PREFIX + hystrixRouteId).end();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ApiInfo> buildApiInfoList(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        return moduleTriggerFlows.entrySet().stream().flatMap(module ->
                module.getValue().stream().map(flow -> new ApiInfo(module.getKey(), flow))
        ).collect(Collectors.toList());
    }

    private List<ApiInfo> buildApiInfoListNew(Map<String, List<TriggerFlow>> moduleTriggerFlows) {
        List<ApiInfo> apiInfo = new ArrayList<>();
        moduleTriggerFlows.entrySet().stream().forEach(module -> {
            module.getValue().stream().forEach(flow -> {
                if(flow.getInModel()!=null && flow.getOutModel()!=null) {
                    apiInfo.add(new ApiInfo(module.getKey(),flow));
                }
            });
        });
        return apiInfo;
    }
}
