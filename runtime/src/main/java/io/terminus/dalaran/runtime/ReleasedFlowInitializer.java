package io.terminus.dalaran.runtime;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
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
import io.terminus.dalaran.core.resource.entity.released.*;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.core.resource.redis.RedisUtil;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.core.resource.repository.TriggerFlowReleasedRepository;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.runtime.service.DalaranNoticeService;
import io.terminus.dalaran.runtime.service.TracingLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ReleasedFlowInitializer implements DalaranStarter {

    private static final Logger logger = LoggerFactory.getLogger(ReleasedFlowInitializer.class);

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

    @Autowired
    private DalaranNoticeService dalaranNoticeService;

    @Autowired
    private TracingLogService tracingLogService;

    @Autowired
    private TriggerFlowReleasedRepository triggerFlowReleasedRepository;

    @Autowired
    private RedisService redisService;

    private Swagger swagger;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, String> alarmConfig;

    private Long current = System.currentTimeMillis();

    @PostConstruct
    private void init() throws Exception {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        RouteDefinition swaggerJsonRoute = new RouteDefinition();
        swaggerJsonRoute.from("netty4-http:http://0.0.0.0:8080/__dalaran/swagger.json?httpMethodRestrict=GET&chunkedMaxContentLength=104857600")
                .setBody().method(this, "getSwaggerJson").end();
        camelContext.addRouteDefinition(swaggerJsonRoute);
        RouteDefinition swaggerRoute = new RouteDefinition();
        swaggerRoute.from("netty4-http:http://0.0.0.0:8080/__dalaran/swagger?httpMethodRestrict=GET&chunkedMaxContentLength=104857600")
                .setBody().constant("<html><head><script src=\"//unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js\">" +
                "</script><link href=\"https://unpkg.com/swagger-ui-dist@3.23.5/swagger-ui.css\"  rel=\"stylesheet\"></head>" +
                "<body><div id=\"swagger-ui\"/></body>" +
                "<script>SwaggerUIBundle({url:\"/__dalaran/swagger.json\",dom_id:'#swagger-ui',presets:[SwaggerUIBundle.presets.apis,SwaggerUIBundle.SwaggerUIStandalonePreset]})</script>" +
                "</html>").end();
        camelContext.addRouteDefinition(swaggerRoute);
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



            List<String> flowIds = new ArrayList<>();
            List<TriggerFlowReleasedEntity> triggerFlowReleasedEntities = resourceLoader.loadAllTriggerFlow();
            for (TriggerFlowReleasedEntity triggerFlowReleasedEntity : triggerFlowReleasedEntities) {
                if(triggerFlowReleasedEntity.isExist()&& triggerFlowReleasedEntity.isTracing() &&triggerFlowReleasedEntity.isOnline()){
                    String originId = triggerFlowReleasedEntity.getOriginId();
                    flowIds.add(originId);
                }
            }
            String join = String.join(",", flowIds);
            redisService.persistKey(RedisUtil.getReleasedFlowIdsKey(), join);



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
            ModuleEntity module = moduleRepository.findByResourceKey(flowEntity.getModuleId());
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            return new ApiInfo(module.getName(), triggerFlow);
        }).collect(Collectors.toList());
    }

    public String getSwaggerJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(swagger);
    }


//    private void monitor() {
//        if(null == alarmConfig) {
//            return;
//        }
//        logger.error("------------monitor------------");
//        Date oneMinBeforeCurrent = new Date(current - 60 * 1000L);
//        Date now = new Date(current);
//        logger.error("-------------报警配置:{}-----", alarmConfig);
//        for (Map.Entry<String, String> entry : alarmConfig.entrySet()) {
//            String flowId = entry.getKey();
//            String alarmRuleId = entry.getValue();
//            AlarmRuleConfig alarmRuleConfig = (AlarmRuleConfig) resourceBuilder.buildAlarmRuleConfig(alarmRuleId, AlarmRuleConfig.class);
//            Map<AlarmRuleConfig.ChannelType, String> alarmChannel = alarmRuleConfig.getAlarmChannel();
//            if (alarmChannel.isEmpty()) {
//                continue;
//            }
//            NoticeMessage noticeMessage = alarmRuleValidate(alarmRuleConfig, oneMinBeforeCurrent, now, flowId);
//            if (noticeMessage.getIsTouchFailureAlarm() || noticeMessage.getIsTouchTimeOutAlarm()) {
//                logger.error("------------产生报警消息{}，准备发送通知------------", noticeMessage);
//                TriggerFlowReleasedEntity triggerFlowReleasedEntity = triggerFlowReleasedRepository.findByVersionAndOriginId(resourceLoader.getVersion(), String.valueOf(flowId));
//                noticeMessage.setFlowName(triggerFlowReleasedEntity.getName());
//                noticeMessage.setCreateDate(now);
//                sendNotice(noticeMessage, alarmChannel);
//            }
//        }
//        current = current + 60 * 1000L;
//    }
//
////    private void sendNotice(NoticeMessage noticeMessage, Map<AlarmRuleConfig.ChannelType, String> alarmChannel) {
////        for (Map.Entry<AlarmRuleConfig.ChannelType, String> entry : alarmChannel.entrySet()) {
////            AlarmRuleConfig.ChannelType channelType = entry.getKey();
////            String contactWays = entry.getValue();
////            noticeMessage.setContactWays(contactWays.split(","));
////            logger.error("---------联系人的方式为 {} -------------", contactWays);
////            switch (channelType) {
////                case mail:
////                    dalaranNoticeService.sendEmail(noticeMessage);
////                    break;
////                case shortMessage:
////                    dalaranNoticeService.sendShortMessage(noticeMessage);
////                    break;
////                default:
////                    throw new RuntimeException();
////            }
////        }
////    }
//
//
//    private NoticeMessage alarmRuleValidate(AlarmRuleConfig alarmRuleConfig, Date oneMinBeforeCurrent, Date now, String flowId) {
//        NoticeMessage noticeMessage = new NoticeMessage();
//        AlarmRuleConfig.FailureAlarm failureAlarm = alarmRuleConfig.getFailureAlarm();
//        if (null != failureAlarm && failureAlarm.getIsOpen()) {
//            Long failureFrequency = failureAlarm.getFailureFrequency();
//            long failureCount = tracingLogService.countFailureLog(oneMinBeforeCurrent, now, flowId);
//            if (failureCount >= failureFrequency) {
//                noticeMessage.setIsTouchFailureAlarm(true);
//            }
//            noticeMessage.setFailureCount(failureCount);
//            noticeMessage.setFailureFrequency(failureFrequency);
//        }
//        AlarmRuleConfig.TimeOutAlarm timeOutAlarm = alarmRuleConfig.getTimeOutAlarm();
//        if (null != timeOutAlarm && timeOutAlarm.getIsOpen()) {
//            Long elapse = timeOutAlarm.getElapse();
//            Long overTimeFrequency = timeOutAlarm.getElapsedFrequency();
//            long elapseCount = tracingLogService.countElapseLog(oneMinBeforeCurrent, now, flowId, elapse);
//            if (overTimeFrequency >= elapseCount) {
//                noticeMessage.setIsTouchTimeOutAlarm(true);
//            }
//            noticeMessage.setTimeOutCount(elapseCount);
//            noticeMessage.setTimeOutFrequency(overTimeFrequency);
//        }
//        return noticeMessage;
//    }
//
//    private void initAlarmConfig(String version) {
//        alarmConfig = new HashMap<>();
//        List<TriggerFlowReleasedEntity> triggerFlowEntities = triggerFlowReleasedRepository.findByVersionAndIsMonitorTrue(version);
//        for (TriggerFlowReleasedEntity triggerFlowReleasedEntity : triggerFlowEntities) {
//            String id = triggerFlowReleasedEntity.getOriginId();
//            String alarmRuleId = triggerFlowReleasedEntity.getAlarmResourceKey();
//            alarmConfig.put(id, alarmRuleId);
//        }
//    }
}
