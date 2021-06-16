package io.terminus.dalaran.runtime;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.core.alarm.AlarmManager;
import io.terminus.dalaran.core.flow.DalaranNoticeBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.core.resource.redis.RedisUtil;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.model.alarm.AlarmRuleConfig;
import io.terminus.dalaran.model.alarm.NoticeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
public class DefaultAlarmManager implements AlarmManager {

    @Autowired
    private RedisService redisService;

    @Autowired
    private DalaranNoticeBuilder dalaranNotice;

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private DalaranResourceLoader dalaranResourceLoader;


    @Override
    public void alarm(NoticeMessage noticeMessage) {
        String value = JSONObject.toJSONString(noticeMessage);
        redisService.push(RedisUtil.getNoticeQueue(), value);
    }

    @PostConstruct
    public void start() {
        new Thread(() -> {
            for (; ; ) {
                NoticeMessage noticeMessage = null;
                try {
                    noticeMessage = redisService.pop(RedisUtil.getNoticeQueue(), NoticeMessage.class);
                    handle(noticeMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handle(NoticeMessage noticeMessage) {
        TriggerFlowReleasedEntity triggerFlowReleasedEntity = (TriggerFlowReleasedEntity) dalaranResourceLoader.loadTriggerFlow(noticeMessage.getFlowId());
        String name = triggerFlowReleasedEntity.getName();
        noticeMessage.setFlowName(name);
        sendNotice(noticeMessage);
    }

    private void sendNotice(NoticeMessage noticeMessage) {
        Map<AlarmRuleConfig.ChannelType, String> alarmChannel = noticeMessage.getAlarmChannel();
        for (Map.Entry<AlarmRuleConfig.ChannelType, String> entry : alarmChannel.entrySet()) {
            AlarmRuleConfig.ChannelType channelType = entry.getKey();
            String contactWays = entry.getValue();
            String[] connectWays = contactWays.split(",");
            switch (channelType) {
                case mail:
                    dalaranNotice.sendEmail(noticeMessage, connectWays);
                    break;
                case message:
                    dalaranNotice.sendShortMessage(noticeMessage, connectWays);
                    break;
                case dingDingRobot:
                    dalaranNotice.sendDingMessage(noticeMessage, connectWays);
                    break;
                default:
                    break;
            }
        }
    }

}
