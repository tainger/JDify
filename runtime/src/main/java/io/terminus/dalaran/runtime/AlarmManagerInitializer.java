package io.terminus.dalaran.runtime;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.core.component.config.AlarmRuleConfig;
import io.terminus.dalaran.core.resource.DalaranStarter;
import io.terminus.dalaran.core.resource.entity.NoticeMessage;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.core.resource.redis.RedisUtil;
import io.terminus.dalaran.core.resource.repository.TriggerFlowReleasedRepository;
import io.terminus.dalaran.runtime.service.DalaranNoticeService;
import io.terminus.dalaran.runtime.service.TracingLogService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class AlarmManagerInitializer implements DalaranStarter {

    private static final Logger logger = LoggerFactory.getLogger(AlarmManagerInitializer.class);


    @Autowired
    private ReleasedResourceLoader resourceLoader;

    @Autowired
    private TracingLogService tracingLogService;

    @Autowired
    private DalaranNoticeService dalaranNoticeService;

    @Autowired
    private TriggerFlowReleasedRepository triggerFlowReleasedRepository;

    @Autowired
    private RedisService redisService;

    private Long current = System.currentTimeMillis();


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void start() {
        logger.error("------------start------------");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.error("------------monitor2------------");
                    monitor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60 * 1000L);
    }

    private void monitor() {
        String alarmConfigCache = redisService.getValue(RedisUtil.getAlarmConfigKey());
        Map<String, String> alarmConfig = JSONObject.parseObject(alarmConfigCache, Map.class);
        if (alarmConfig.isEmpty()) {
            return;
        }
        Date oneMinBeforeCurrent = new Date(current - 60 * 1000L);
        Date now = new Date(current);
        logger.error("-------------报警配置:{}-----", alarmConfig);
        for (Map.Entry<String, String> entry : alarmConfig.entrySet()) {
            String flowId = entry.getKey();
            String alarmRuleId = entry.getValue();
            String value = redisService.getValue(alarmRuleId);
            logger.error("-------------报警审核：{}----{}----", flowId, alarmRuleId);
            logger.error("-------------报警规则String：----{}----", value);
            AlarmRuleConfig alarmRuleConfig = JSONObject.parseObject(value, AlarmRuleConfig.class);
            logger.error("-------------报警规则：----{}----", alarmRuleConfig);
            Map<AlarmRuleConfig.ChannelType, String> alarmChannel = alarmRuleConfig.getAlarmChannel();
            if (alarmChannel.isEmpty()) {
                continue;
            }
            NoticeMessage noticeMessage = alarmRuleValidate(alarmRuleConfig, oneMinBeforeCurrent, now, flowId);
            if (noticeMessage.getIsTouchFailureAlarm() || noticeMessage.getIsTouchTimeOutAlarm()) {
                logger.error("------------产生报警消息{}，准备发送通知------------", noticeMessage);
                TriggerFlowReleasedEntity triggerFlowReleasedEntity = triggerFlowReleasedRepository.findByVersionAndOriginId(resourceLoader.getVersion(), String.valueOf(flowId));
                noticeMessage.setFlowName(triggerFlowReleasedEntity.getName());
                noticeMessage.setCreateDate(dateFormat.format(now));
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
            //todo refactor
            if (elapseCount >= overTimeFrequency) {
                noticeMessage.setIsTouchTimeOutAlarm(true);
            }
            noticeMessage.setTimeOutCount(elapseCount);
            noticeMessage.setTimeOutFrequency(overTimeFrequency);
        }
        logger.error("-------------消息通知：----{}----", noticeMessage);
        return noticeMessage;
    }
}
