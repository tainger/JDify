package io.terminus.dalaran.runtime;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.core.component.config.AlarmRuleConfig;
import io.terminus.dalaran.core.resource.DalaranStarter;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.core.resource.redis.RedisUtil;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.core.resource.repository.TriggerFlowReleasedRepository;
import io.terminus.dalaran.model.alarm.NoticeMessage;
import io.terminus.dalaran.runtime.service.DalaranNoticeService;
import io.terminus.dalaran.runtime.service.TracingLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class AlarmManagerInitializer implements DalaranStarter {

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

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    private Long current = System.currentTimeMillis();


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void start() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    log.error("------------monitor------------");
                    monitor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60 * 1000L);
    }

    private void monitor() {
        String flowIds = redisService.getValue(RedisUtil.getReleasedFlowIdsKey());
        String[] ids = flowIds.split(",");
        if (ids.length == 0) {
            return;
        }
        Date oneMinBeforeCurrent = new Date(current - 60 * 1000L);
        Date now = new Date(current);
        log.error("-------------统计时间:{}-----", now);
        List<String> strList = Arrays.asList(ids);
        log.error("-------------报警配置的id:{}-----", strList);
        for (String flowId : strList) {
//            String alarmRuleId = redisService.getValue(RedisUtil.getAlarmConfigKey(flowId));
//            if (alarmRuleId == null) {
//                continue;
//            }
//            log.error("-------------报警审核：{}----{}----", flowId, alarmRuleId);
//            String alarmConfig = redisService.getValue(RedisUtil.getAlarmRuleKey(alarmRuleId));
//            if (alarmConfig == null) {
//                continue;
//            }
            String alarmConfig = getAlarmConfigIfAlarmConfigKey(flowId);
            if (null == alarmConfig) {
                continue;
            }
            AlarmRuleConfig alarmRuleConfig = JSONObject.parseObject(alarmConfig, AlarmRuleConfig.class);
            log.error("-------------报警规则：----{}----", alarmRuleConfig);
            Map<AlarmRuleConfig.ChannelType, String> alarmChannel = alarmRuleConfig.getAlarmChannel();
            if (alarmChannel.isEmpty()) {
                continue;
            }
            NoticeMessage noticeMessage = alarmRuleValidate(alarmRuleConfig, oneMinBeforeCurrent, now, flowId);
            if (noticeMessage.getIsTouchFailureAlarm() || noticeMessage.getIsTouchTimeOutAlarm()) {
                log.error("------------产生报警消息{}，准备发送通知------------", noticeMessage);
                TriggerFlowReleasedEntity triggerFlowReleasedEntity = triggerFlowReleasedRepository.findByVersionAndOriginId(resourceLoader.getVersion(), String.valueOf(flowId));
                noticeMessage.setFlowName(triggerFlowReleasedEntity.getName());
                noticeMessage.setCreateDate(dateFormat.format(now));
                sendNotice(noticeMessage, alarmChannel);
            }
        }
        current = current + 60 * 1000L;
    }

    private void sendNotice(NoticeMessage noticeMessage, Map<AlarmRuleConfig.ChannelType, String> alarmChannel) {
        log.error("---------通知渠道 {} -------------", alarmChannel);
        for (Map.Entry<AlarmRuleConfig.ChannelType, String> entry : alarmChannel.entrySet()) {
            AlarmRuleConfig.ChannelType channelType = entry.getKey();
            String contactWays = entry.getValue();
            noticeMessage.setContactWays(contactWays.split(","));
            log.error("---------联系人的方式为 {} -------------", contactWays);
            switch (channelType) {
                case mail:
                    dalaranNoticeService.sendEmail(noticeMessage);
                    break;
                case message:
                    dalaranNoticeService.sendShortMessage(noticeMessage);
                    break;
                default:
                    break;
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
        return noticeMessage;
    }


    private String getAlarmConfigIfAlarmConfigKey(String flowId) {
        String alarmRuleId = redisService.getValue(RedisUtil.getAlarmConfigKey(flowId));
        if (alarmRuleId == null) {
            return null;
        }
        String alarmConfig = redisService.getValue(RedisUtil.getAlarmRuleKey(alarmRuleId));
        if (alarmConfig == null) {
            redisService.deleteKey(RedisUtil.getAlarmConfigKey(flowId));
            return null;
        }
        return alarmConfig;
    }
}
