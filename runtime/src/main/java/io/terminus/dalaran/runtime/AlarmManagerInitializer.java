package io.terminus.dalaran.runtime;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.core.component.config.AlarmRuleConfig;
import io.terminus.dalaran.core.log.DalaranTracingLog;
import io.terminus.dalaran.core.resource.DalaranStarter;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
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
    private DalaranNoticeService dalaranNoticeService;

    @Autowired
    private RedisService redisService;


    @Override
    public void start() {
        new Thread(() -> {
            for (; ; ) {
                try {
                    String timeToMonitor = redisService.getValue(RedisUtil.getTimeToMonitor());
                    if (null == timeToMonitor) {
                    log.error("没有要监控的日志报警时间戳");
                        continue;
                    }
                    monitor(timeToMonitor);
                    redisService.deleteKey(RedisUtil.getTimeToMonitor());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void monitor(String timeToMonitor) {
        String idNameStrs = redisService.getValue(RedisUtil.getReleasedFlowIdsKey());
        if (idNameStrs == null) {
            return;
        }
        List<String> idNameList = JSONObject.parseObject(idNameStrs, List.class);
        if (idNameList.size() == 0) {
            return;
        }
        for (String idName : idNameList) {
            String[] split = idName.split(",");
            String failureCountStr = redisService.getValue(RedisUtil.getFailureKey(split[0], timeToMonitor));
            redisService.deleteKey(RedisUtil.getFailureKey(split[0], timeToMonitor));
            String timeOutCountStr = redisService.getValue(RedisUtil.getTimeOutKey(split[0], timeToMonitor));
            redisService.deleteKey(RedisUtil.getTimeOutKey(split[0], timeToMonitor));
            String alarmConfigStr = getAlarmConfigIfAlarmConfigKey(split[0]);
            if (null == alarmConfigStr) {
                log.error("没有流程{}的报警配置", split[0]);
                continue;
            }
            if (null == timeOutCountStr && failureCountStr == null) {
                continue;
            }
            AlarmRuleConfig alarmRuleConfig = JSONObject.parseObject(alarmConfigStr, AlarmRuleConfig.class);
            log.error("-------------报警规则：----{}----", alarmRuleConfig);
            NoticeMessage noticeMessage = new NoticeMessage();
            log.error("-------------failureCount：----{}----", failureCountStr);
            if (failureCountStr != null) {
                int failureCount = Integer.parseInt(failureCountStr);
                if (failureCount >= alarmRuleConfig.getFailureAlarm().getFailureFrequency()) {
                    noticeMessage.setIsTouchFailureAlarm(true);
                    noticeMessage.setFailureCount(failureCount);
                    noticeMessage.setFailureFrequency(alarmRuleConfig.getTimeOutAlarm().getElapsedFrequency());
                }
            }
            if (null != timeOutCountStr) {
                int timeOutCount = Integer.parseInt(timeOutCountStr);
                if (timeOutCount >= alarmRuleConfig.getTimeOutAlarm().getElapsedFrequency()) {
                    noticeMessage.setIsTouchTimeOutAlarm(true);
                    noticeMessage.setTimeOutCount(timeOutCount);
                    noticeMessage.setTimeOutFrequency(alarmRuleConfig.getTimeOutAlarm().getElapsedFrequency());
                }
            }
            noticeMessage.setCreateDate(timeToMonitor);
            noticeMessage.setFlowName(split[1]);
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
