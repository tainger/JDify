package io.terminus.dalaran.runtime;

import com.alibaba.fastjson.JSONObject;
import io.lettuce.core.RedisCommandTimeoutException;
import io.terminus.dalaran.core.component.config.AlarmRuleConfig;
import io.terminus.dalaran.core.flow.DalaranNoticeBuilder;
import io.terminus.dalaran.core.resource.DalaranStarter;
import io.terminus.dalaran.core.resource.log.RequestID;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.core.resource.redis.RedisUtil;
import io.terminus.dalaran.model.alarm.NoticeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public class AlarmManagerInitializer implements DalaranStarter {

    @Autowired
    private RedisService redisService;

    @Autowired
    private DalaranNoticeBuilder dalaranNotice;

    @Override
    public void start() {
        new Thread(() -> {
            for (; ; ) {
                String timeToMonitor = null;
                //something to be optimized
                try {
                    timeToMonitor = redisService.pop(RedisUtil.getTimeToMonitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != timeToMonitor) {
                    try {
                        monitor(timeToMonitor);
                    } catch (Exception e) {
                        log.error("报错:{}", RequestID.getExceptionStackTrace(e));
                    }
                }
            }
        }).start();
    }

    private void monitor(String timeToMonitor) {
        String flowInfosStr = redisService.getValue(RedisUtil.getReleasedFlowIdsKey());
        if (flowInfosStr == null) {
            return;
        }
        List<Map<String, Object>> flowInfos = JSONObject.parseObject(flowInfosStr, List.class);
        for (Map<String, Object> flow : flowInfos) {
            String id = (String) flow.get("id");
            String failureCountStr = redisService.getValue(RedisUtil.getFailureKey(id, timeToMonitor));
            redisService.deleteKey(RedisUtil.getFailureKey(id, timeToMonitor));
            String timeOutCountStr = redisService.getValue(RedisUtil.getTimeOutKey(id, timeToMonitor));
            redisService.deleteKey(RedisUtil.getTimeOutKey(id, timeToMonitor));
            String alarmConfigStr = getAlarmConfigIfAlarmConfigKey(id);
            if (null == alarmConfigStr) {
                continue;
            }
            AlarmRuleConfig alarmRuleConfig = JSONObject.parseObject(alarmConfigStr, AlarmRuleConfig.class);
            NoticeMessage noticeMessage = new NoticeMessage();
            if (failureCountStr != null) {
                int failureCount = Integer.parseInt(failureCountStr);
                if (alarmRuleConfig.getFailureAlarm().getIsOpen() && failureCount >= alarmRuleConfig.getFailureAlarm().getFailureFrequency()) {
                    noticeMessage.setIsTouchFailureAlarm(true);
                }
                noticeMessage.setFailureCount(failureCount);
            }
            noticeMessage.setFailureFrequency(alarmRuleConfig.getFailureAlarm().getFailureFrequency());
            if (null != timeOutCountStr) {
                int timeOutCount = Integer.parseInt(timeOutCountStr);
                if (alarmRuleConfig.getTimeOutAlarm().getIsOpen() && timeOutCount >= alarmRuleConfig.getTimeOutAlarm().getElapsedFrequency()) {
                    noticeMessage.setIsTouchTimeOutAlarm(true);
                }

                noticeMessage.setTimeOutCount(timeOutCount);
            }
            noticeMessage.setTimeOutFrequency(alarmRuleConfig.getTimeOutAlarm().getElapsedFrequency());
            if(noticeMessage.getIsTouchFailureAlarm() || noticeMessage.getIsTouchTimeOutAlarm()) {
                String name = (String) flow.get("name");
                noticeMessage.setCreateDate(timeToMonitor);
                noticeMessage.setFlowName(name);
                sendNotice(noticeMessage, alarmRuleConfig.getAlarmChannel());
            }
        }
    }

    private void sendNotice(NoticeMessage noticeMessage, Map<AlarmRuleConfig.ChannelType, String> alarmChannel) {
        for (Map.Entry<AlarmRuleConfig.ChannelType, String> entry : alarmChannel.entrySet()) {
            AlarmRuleConfig.ChannelType channelType = entry.getKey();
            String contactWays = entry.getValue();
            noticeMessage.setContactWays(contactWays.split(","));
            switch (channelType) {
                case mail:
                    dalaranNotice.sendEmail(noticeMessage);
                    break;
                case message:
                    dalaranNotice.sendShortMessage(noticeMessage);
                    break;
                case dingDingRobot:
                    dalaranNotice.sendDingMessage(noticeMessage);
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
