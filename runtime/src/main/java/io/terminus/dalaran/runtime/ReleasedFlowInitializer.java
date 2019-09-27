package io.terminus.dalaran.runtime;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Swagger;
import io.terminus.dalaran.component.trigger.rest.RestConfig;
import io.terminus.dalaran.component.trigger.rest.model.ApiInfo;
import io.terminus.dalaran.component.trigger.rest.utils.SwaggerUtils;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.released.*;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ReleasedFlowInitializer {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private ReleasedResourceLoader resourceLoader;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private CamelContext camelContext;

    private Swagger swagger;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void init() throws Exception {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        RouteDefinition swaggerJsonRoute = new RouteDefinition();
        swaggerJsonRoute.from("netty4-http:http://0.0.0.0:8080/__dalaran/swagger.json?httpMethodRestrict=GET")
                .setBody().method(this, "getSwaggerJson").end();
        camelContext.addRouteDefinition(swaggerJsonRoute);
        RouteDefinition swaggerRoute = new RouteDefinition();
        swaggerRoute.from("netty4-http:http://0.0.0.0:8080/__dalaran/swagger?httpMethodRestrict=GET")
                .setBody().constant("<html><head><script src=\"//unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js\">" +
                "</script><link href=\"https://unpkg.com/swagger-ui-dist@3.23.5/swagger-ui.css\"  rel=\"stylesheet\"></head>" +
                "<body><div id=\"swagger-ui\"/></body>" +
                "<script>SwaggerUIBundle({url:\"/__dalaran/swagger.json\",dom_id:'#swagger-ui',presets:[SwaggerUIBundle.presets.apis,SwaggerUIBundle.SwaggerUIStandalonePreset]})</script>" +
                "</html>").end();
        camelContext.addRouteDefinition(swaggerRoute);
    }

    // TODO 临时每分钟 load 一下...
    // TODO 启动延时 5 秒, 因为目前 Component 的加载是根据 Spring Bean 的初始化, 有时候初始化流时, Component 还没有 ready
    // TODO 组件需要更好的加载方式, 更早加载或者有机制确保加载完成在初始化流
    @Scheduled(fixedDelay = 60 * 1000L, initialDelay = 5 * 1000L)
    private void loadResources() {
        ReleaseRecordEntity recordEntity = releaseRecordRepository.findByEnabledTrue();
        synchronized (this) {
            if (recordEntity == null || recordEntity.getVersion().equals(resourceLoader.getVersion())) {
                log.debug("version not change");
                return;
            }
            resourceLoader.setVersion(recordEntity.getVersion());

            // load client info
            List<ClientReleasedEntity> clients = resourceLoader.loadAllClient();
            for (ClientReleasedEntity client : clients) {
                dalaranContext.getDalaranClientContext().addClient(client.getAppKey(), client.getSecret());
            }

            // load mapping function
            List<FunctionReleasedEntity> functions = resourceLoader.loadAllFunctions();
            for (FunctionReleasedEntity function : functions) {
                dalaranContext.getDalaranFunctionContext().addCustomFunction(function.getOriginId(), function.getType(),
                        function.getScript(), function.getParams());
            }

            List<TriggerFlowReleasedEntity> triggerFlows = resourceLoader.loadAvailableTriggerFlow();
            for (TriggerFlowReleasedEntity triggerFlowEntity : triggerFlows) {
                TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
                dalaranContext.addTriggerFlow(triggerFlow);
                log.info("load released flow [{}]", triggerFlow.getId());
            }

            List<SubFlowReleasedEntity> subFLows = resourceLoader.loadAvailableSubFlow();
            for (SubFlowReleasedEntity subFlowEntity : subFLows) {
                SubFlow subFlow = resourceBuilder.buildSubFlow(subFlowEntity);
                dalaranContext.addSubFlow(subFlow);
                log.info("load released sub-flow {}", subFlow.getId());
            }

            List<ApiInfo> apiInfoList = getExportApiInfoList();
            swagger = SwaggerUtils.buildSwagger(apiInfoList);
        }
    }

    private List<ApiInfo> getExportApiInfoList() {
        List<TriggerFlowReleasedEntity> restFlowList = resourceLoader.loadAvailableTriggerFlowByTriggerType("http-rest-listener");
        return restFlowList.stream().map(flowEntity -> {
            ModuleEntity module = moduleRepository.findById(flowEntity.getModuleId()).get();
            RestConfig restConfig = JSON.parseObject(flowEntity.getTriggerConfig(), RestConfig.class);
            DalaranModelSchema inSchema = getModelSchema(flowEntity.getInModel());
            DalaranModelSchema outSchema = getModelSchema(flowEntity.getOutModel());
            return new ApiInfo(module.getName(), restConfig, flowEntity, inSchema, outSchema);
        }).collect(Collectors.toList());
    }

    private DalaranModelSchema getModelSchema(Long modelId) {
        ModelReleasedEntity modelEntity = resourceLoader.loadModel(modelId);
        Class<? extends DalaranModelSchema> schemaType = dalaranContext.getDalaranConverterContext().getModelSchema(modelEntity.getType());
        return JSON.parseObject(modelEntity.getModelSchema(), schemaType);
    }

    public String getSwaggerJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(swagger);
    }
}
