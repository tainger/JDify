package io.terminus.dalaran.as2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Swagger;
import io.terminus.dalaran.component.http.trigger.model.ApiInfo;
import io.terminus.dalaran.component.http.trigger.utils.SwaggerUtils;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.DalaranStarter;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.released.ClientReleasedEntity;
import io.terminus.dalaran.core.resource.entity.released.FunctionReleasedEntity;
import io.terminus.dalaran.core.resource.entity.released.SubFlowReleasedEntity;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

@Slf4j
public class ReleasedFlowInitializer implements DalaranStarter {

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
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        RouteDefinition swaggerJsonRoute = new RouteDefinition();
//        swaggerJsonRoute.from("netty4-http:http://0.0.0.0:8080/__dalaran/swagger.json?httpMethodRestrict=GET")
//                .setBody().method(this, "getSwaggerJson").end();
//        camelContext.addRouteDefinition(swaggerJsonRoute);
//        RouteDefinition swaggerRoute = new RouteDefinition();
//        swaggerRoute.from("netty4-http:http://0.0.0.0:8080/__dalaran/swagger?httpMethodRestrict=GET")
//                .setBody().constant("<html><head><script src=\"//unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js\">" +
//                "</script><link href=\"https://unpkg.com/swagger-ui-dist@3.23.5/swagger-ui.css\"  rel=\"stylesheet\"></head>" +
//                "<body><div id=\"swagger-ui\"/></body>" +
//                "<script>SwaggerUIBundle({url:\"/__dalaran/swagger.json\",dom_id:'#swagger-ui',presets:[SwaggerUIBundle.presets.apis,SwaggerUIBundle.SwaggerUIStandalonePreset]})</script>" +
//                "</html>").end();
//        camelContext.addRouteDefinition(swaggerRoute);
    }

    @Override
    public void start() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    loadResources();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60 * 1000L);
    }

    private void loadResources() {
        ReleaseRecordEntity recordEntity = releaseRecordRepository.findByEnabledTrue();
        synchronized (this) {
            if (recordEntity == null || recordEntity.getVersion().equals(resourceLoader.getVersion())) {
                log.debug("version not change");
                return;
            }
            resourceLoader.setVersion(recordEntity.getVersion());
            resourceLoader.setLastVersion(recordEntity.getLastVersion());

            // load client info
            List<ClientReleasedEntity> clients = resourceLoader.loadAllClient();
            for (ClientReleasedEntity client : clients) {
                try {
                    dalaranContext.getDalaranClientContext().addClient(client.getAppKey(), client.getSecret());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // load mapping function
            List<FunctionReleasedEntity> functions = resourceLoader.loadAllFunctions();
            for (FunctionReleasedEntity function : functions) {
                try {
                    dalaranContext.getDalaranFunctionContext().addCustomFunction(String.valueOf(function.getOriginId()), function.getType(),
                            function.getScript(), function.getParams());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<TriggerFlowReleasedEntity> triggerFlows = resourceLoader.loadAvailableTriggerFlow();
            List<TriggerFlowReleasedEntity> lastVersionTriggerFlows = resourceLoader.loadLastVersionAvailableTriggerFlow();

            for (TriggerFlowReleasedEntity triggerFlowEntity : lastVersionTriggerFlows) {
                if (triggerFlowEntity.getTriggerType().equalsIgnoreCase("as2-server")) {
                    continue;
                }
                try {
                    TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
                    dalaranContext.removeTriggerFlow(triggerFlow);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (TriggerFlowReleasedEntity triggerFlowEntity : triggerFlows) {
                if (triggerFlowEntity.getTriggerType().equalsIgnoreCase("as2-server")) {
                    continue;
                }
                try {
                    TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(triggerFlowEntity);
                    dalaranContext.addTriggerFlow(triggerFlow);
                    log.info("load released flow [{}]", triggerFlow.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<SubFlowReleasedEntity> subFLows = resourceLoader.loadAvailableSubFlow();
            List<SubFlowReleasedEntity> lastVersionSubFLows = resourceLoader.loadLastVersionAvailableSubFlow();
            for (SubFlowReleasedEntity subFlowEntity : lastVersionSubFLows) {
                try {
                    SubFlow subFlow = resourceBuilder.buildSubFlow(subFlowEntity);
                    dalaranContext.removeSubFlow(subFlow);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (SubFlowReleasedEntity subFlowEntity : subFLows) {
                try {
                    SubFlow subFlow = resourceBuilder.buildSubFlow(subFlowEntity);
                    dalaranContext.addSubFlow(subFlow);
                    log.info("load released sub-flow {}", subFlow.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<ApiInfo> apiInfoList = getExportApiInfoList();
            swagger = SwaggerUtils.buildSwagger(apiInfoList);
        }
    }

    private List<ApiInfo> getExportApiInfoList() {
        List<TriggerFlowReleasedEntity> restFlowList = resourceLoader.loadAvailableTriggerFlowByTriggerType("http-rest-listener");
        return restFlowList.stream().map(flowEntity -> {
            ModuleEntity module = moduleRepository.findById(flowEntity.getModuleId()).get();
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            return new ApiInfo(module.getName(), triggerFlow);
        }).collect(Collectors.toList());
    }

    public String getSwaggerJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(swagger);
    }

}
