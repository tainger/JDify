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


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
        String flowIds = redisService.getValue(RedisUtil.getReleasedFlowIdsKey());
        if (flowIds == null) {
            return;
        }
        String[] ids = flowIds.split(",");
        if (ids.length == 0) {
            return;
        }
        for (String flowId : ids) {
            String failureCountStr = redisService.getValue(RedisUtil.getFailureKey(flowId, dateFormat.format(new Date())));
            String timeOutCountStr = redisService.getValue(RedisUtil.getTimeOutKey(flowId, dateFormat.format(new Date())));
            //todo 可优化
            String alarmConfigStr = getAlarmConfigIfAlarmConfigKey(flowId);
            if (null == alarmConfigStr) {
                continue;
            }
            if (null == timeOutCountStr && failureCountStr == null) {
                continue;
            }
            AlarmRuleConfig alarmRuleConfig = JSONObject.parseObject(alarmConfigStr, AlarmRuleConfig.class);
            log.error("-------------报警规则：----{}----", alarmRuleConfig);
            NoticeMessage noticeMessage = new NoticeMessage();
            if (failureCountStr != null) {
                int failureCount = Integer.parseInt(failureCountStr);
                if(failureCount >= alarmRuleConfig.getFailureAlarm().getFailureFrequency()){
                    noticeMessage.setIsTouchFailureAlarm(true);
                    noticeMessage.setFailureCount(failureCount);
                }
            }
            noticeMessage.setTimeOutFrequency(alarmRuleConfig.getTimeOutAlarm().getElapsedFrequency());
            if (null != timeOutCountStr) {
                int timeOutCount = Integer.parseInt(timeOutCountStr);
                if(timeOutCount  >= alarmRuleConfig.getFailureAlarm().getFailureFrequency())
                noticeMessage.setIsTouchTimeOutAlarm(true);
                noticeMessage.setTimeOutCount(timeOutCount);
            }
            noticeMessage.setTimeOutFrequency(alarmRuleConfig.getTimeOutAlarm().getElapsedFrequency());
            sendNotice(noticeMessage, alarmRuleConfig.getAlarmChannel());
        }
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
