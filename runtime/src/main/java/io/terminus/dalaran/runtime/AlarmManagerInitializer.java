package io.terminus.dalaran.runtime;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.core.component.config.AlarmRuleConfig;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.DalaranStarter;
import io.terminus.dalaran.core.resource.entity.NoticeMessage;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.core.resource.repository.TriggerFlowReleasedRepository;
import io.terminus.dalaran.runtime.service.DalaranNoticeService;
import io.terminus.dalaran.runtime.service.TracingLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

public class AlarmManagerInitializer implements DalaranStarter {

    private static  final Logger logger = LoggerFactory.getLogger(AlarmManagerInitializer.class);


    @Autowired
    private ReleasedResourceLoader resourceLoader;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private TracingLogService tracingLogService;

    @Autowired
    private DalaranNoticeService dalaranNoticeService;

    @Autowired
    private TriggerFlowReleasedRepository triggerFlowReleasedRepository;

    private RedisService redisService;

    private Map<String, String> alarmConfig = new HashMap(16);

    private Long current = System.currentTimeMillis();

    @Override
    public void start() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    monitor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60 * 1000L);
    }

    private void monitor() {
        String monitor_key_value = redisService.getValue("monitor_key_value");
        Map<String, String> map = JSONObject.parseObject(monitor_key_value, Map.class);
        logger.error("------------monitor------------");
        Date oneMinBeforeCurrent = new Date(current - 60 * 1000L);
        Date now = new Date(current);
        logger.error("-------------报警配置:{}-----", alarmConfig);
        for (Map.Entry<String, String> entry : alarmConfig.entrySet()) {
            String flowId = entry.getKey();
            String alarmRuleId = entry.getValue();
           AlarmRuleConfig alarmRuleConfig =  new AlarmRuleConfig();// (AlarmRuleConfig) resourceBuilder.buildAlarmRuleConfig(alarmRuleId, AlarmRuleConfig.class);
            Map<AlarmRuleConfig.ChannelType, String> alarmChannel = alarmRuleConfig.getAlarmChannel();
            if (alarmChannel.isEmpty()) {
                continue;
            }
            NoticeMessage noticeMessage = alarmRuleValidate(alarmRuleConfig, oneMinBeforeCurrent, now, flowId);
            if (noticeMessage.getIsTouchFailureAlarm() || noticeMessage.getIsTouchTimeOutAlarm()) {
                logger.error("------------产生报警消息{}，准备发送通知------------", noticeMessage);
                TriggerFlowReleasedEntity triggerFlowReleasedEntity = triggerFlowReleasedRepository.findByVersionAndOriginId(resourceLoader.getVersion(), String.valueOf(flowId));
                noticeMessage.setFlowName(triggerFlowReleasedEntity.getName());
                noticeMessage.setCreateDate(now);
                sendNotice(noticeMessage, alarmChannel);
            }
        }
        current = current + 60 * 1000L;
    }

    private void sendNotice(NoticeMessage noticeMessage, Map<AlarmRuleConfig.ChannelType, String> alarmChannel) {
        for (Map.Entry<AlarmRuleConfig.ChannelType, String> entry : alarmChannel.entrySet()) {
            AlarmRuleConfig.ChannelType channelType = entry.getKey();
            String contactWays = entry.getValue();
            noticeMessage.setContactWays(contactWays.split(","));
            logger.error("---------联系人的方式为 {} -------------", contactWays);
            switch (channelType) {
                case mail:
                    dalaranNoticeService.sendEmail(noticeMessage);
                    break;
                case shortMessage:
                    dalaranNoticeService.sendShortMessage(noticeMessage);
                    break;
                default:
                    throw new RuntimeException();
            }
        }
    }


    private NoticeMessage alarmRuleValidate(AlarmRuleConfig alarmRuleConfig, Date oneMinBeforeCurrent, Date now, String flowId) {
        NoticeMessage noticeMessage = new NoticeMessage();
        AlarmRuleConfig.FailureAlarm failureAlarm = alarmRuleConfig.getFailureAlarm();
        if (null != failureAlarm && failureAlarm.getIsOpen()) {
            Long failureFrequency = failureAlarm.getFailureFrequency();
            long failureCount = tracingLogService.countFailureLog(oneMinBeforeCurrent, now, flowId);
            if (failureCount >= failureFrequency) {
                noticeMessage.setIsTouchFailureAlarm(true);
            }
            noticeMessage.setFailureCount(failureCount);
            noticeMessage.setFailureFrequency(failureFrequency);
        }
        AlarmRuleConfig.TimeOutAlarm timeOutAlarm = alarmRuleConfig.getTimeOutAlarm();
        if (null != timeOutAlarm && timeOutAlarm.getIsOpen()) {
            Long elapse = timeOutAlarm.getElapse();
            Long overTimeFrequency = timeOutAlarm.getElapsedFrequency();
            long elapseCount = tracingLogService.countElapseLog(oneMinBeforeCurrent, now, flowId, elapse);
            if (overTimeFrequency >= elapseCount) {
                noticeMessage.setIsTouchTimeOutAlarm(true);
            }
            noticeMessage.setTimeOutCount(elapseCount);
            noticeMessage.setTimeOutFrequency(overTimeFrequency);
        }
        return noticeMessage;
    }
}
