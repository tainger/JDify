//package io.terminus.dalaran.runtime;
//
//import com.alibaba.fastjson.JSONObject;
//import io.terminus.dalaran.core.component.config.AlarmRuleConfig;
//import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
//import io.terminus.dalaran.core.resource.DalaranStarter;
//import io.terminus.dalaran.core.resource.entity.NoticeMessage;
//import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
//import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
//import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
//import io.terminus.dalaran.core.resource.repository.TriggerFlowReleasedRepository;
//import io.terminus.dalaran.runtime.service.DalaranNoticeService;
//import io.terminus.dalaran.runtime.service.TracingLogService;
//import org.apache.commons.collections.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import javax.annotation.PostConstruct;
//import java.util.*;
//
//public class AlarmManagerInitializer implements DalaranStarter {
//
//    private static  final Logger logger = LoggerFactory.getLogger(AlarmManagerInitializer.class);
//    @Autowired
//    private TriggerFlowReleasedRepository triggerFlowReleasedRepository;
//
//    @Autowired
//    private DalaranResourceBuilder resourceBuilder;
//
//    @Autowired
//    private ReleasedResourceLoader resourceLoader;
//
//    @Autowired
//    private TracingLogService tracingLogService;
//
//    @Autowired
//    private DalaranNoticeService dalaranNoticeService;
//
//    @Autowired
//    private ReleaseRecordRepository releaseRecordRepository;
//
//    private String currentVersion;
//
//
//
//    private Map<Long, Long> alarmConfig = new HashMap(16);
//
//    private Long current = System.currentTimeMillis();
//
//    @PostConstruct
//    private void init() {
//
//        String version = resourceLoader.getVersion();
//        List<TriggerFlowReleasedEntity> triggerFlowEntities = triggerFlowReleasedRepository.findByVersionAndIsMonitorTrue(version);
//        for (TriggerFlowReleasedEntity triggerFlowReleasedEntity : triggerFlowEntities) {
//            Long id = triggerFlowReleasedEntity.getOriginId();
//            Long alarmRuleId = (Long) JSONObject.parseObject(triggerFlowReleasedEntity.getTriggerConfig(), Map.class).get("alarmRuleId");
//            alarmConfig.put(id, alarmRuleId);
//        }
//    }
//
//    @Override
//    public void start() {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    monitor();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 0, 60 * 1000L);
//    }
//
//    private void monitor() {
//        Date oneMinBeforeCurrent = new Date(current - 60 * 1000L);
//        Date now = new Date(current);
//        for (Map.Entry<Long, Long> entry : alarmConfig.entrySet()) {
//            Long flowId = entry.getKey();
//            Long alarmRuleId = entry.getValue();
//            AlarmRuleConfig alarmRuleConfig = (AlarmRuleConfig) resourceBuilder.buildAlarmRuleConfig(alarmRuleId, AlarmRuleConfig.class);
//            Map<AlarmRuleConfig.ChannelType, String[]> alarmChannel = alarmRuleConfig.getAlarmChannel();
//            if (alarmChannel.isEmpty()) {
//                continue;
//            }
//            NoticeMessage noticeMessage = alarmRuleValidate(alarmRuleConfig, oneMinBeforeCurrent, now, flowId);
//            logger.error("本次报警信息为"+noticeMessage.toString());
//            noticeMessage.setCreateDate(now);
//            sendNotice(noticeMessage, alarmChannel);
//        }
//        current = current + 60 * 1000L;
//    }
//
//    private void sendNotice(NoticeMessage noticeMessage, Map<AlarmRuleConfig.ChannelType, String[]> alarmChannel) {
//        for (Map.Entry<AlarmRuleConfig.ChannelType, String[]> entry : alarmChannel.entrySet()) {
//            AlarmRuleConfig.ChannelType channelType = entry.getKey();
//            String[] contactWays = entry.getValue();
//            noticeMessage.setContactWays(contactWays);
//            switch (channelType) {
//                case mail:
//                    dalaranNoticeService.sendEmail(noticeMessage);
//                    return;
//                case shortMessage:
//                    dalaranNoticeService.sendShortMessage(noticeMessage);
//                    return;
//            }
//        }
//    }
//
//
//    private NoticeMessage alarmRuleValidate(AlarmRuleConfig alarmRuleConfig, Date oneMinBeforeCurrent, Date now, Long flowId) {
//        NoticeMessage  noticeMessage = new NoticeMessage();
//        AlarmRuleConfig.FailureAlarm failureAlarm = alarmRuleConfig.getFailureAlarm();
//        if (null != failureAlarm && failureAlarm.getIsOpen()) {
//            Long failureFrequency = failureAlarm.getFailureFrequency();
//            Long failureCount = tracingLogService.countFailureLog(oneMinBeforeCurrent, now, flowId);
//            if (failureCount >= failureFrequency) {
//                noticeMessage.setFailureCount(failureCount);
//                noticeMessage.setIsTouchFailureAlarm(true);
//                noticeMessage.setFailureFrequency(failureFrequency);
//            }
//        }
//        AlarmRuleConfig.TimeOutAlarm timeOutAlarm = alarmRuleConfig.getTimeOutAlarm();
//        if (null != timeOutAlarm && timeOutAlarm.getIsOpen()) {
//            Long elapse = timeOutAlarm.getElapse();
//            Long overTimeFrequency = timeOutAlarm.getElapsedFrequency();
//            Long elapseCount = tracingLogService.countElapseLog(oneMinBeforeCurrent, now, flowId, elapse);
//            if (overTimeFrequency >= elapseCount) {
//                noticeMessage.setFailureCount(elapseCount);
//                noticeMessage.setIsTouchFailureAlarm(true);
//                noticeMessage.setFailureFrequency(elapseCount);
//            }
//        }
//        TriggerFlowReleasedEntity triggerFlowReleasedEntity = triggerFlowReleasedRepository.findByVersionAndOriginId(version, flowId);
//        noticeMessage.setFlowName(triggerFlowReleasedEntity.getName());
//        return noticeMessage;
//    }
//
//
//
//}
