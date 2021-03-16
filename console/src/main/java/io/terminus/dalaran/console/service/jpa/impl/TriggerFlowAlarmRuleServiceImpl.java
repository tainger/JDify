package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.entity.TriggerFlowAlarmRuleEntity;
import io.terminus.dalaran.console.repository.TriggerFlowAlarmRuleRepository;
import io.terminus.dalaran.console.service.jpa.TriggerFlowAlarmRuleService;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.core.resource.redis.RedisUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TriggerFlowAlarmRuleServiceImpl implements TriggerFlowAlarmRuleService , InitializingBean {

    @Autowired
    private TriggerFlowAlarmRuleRepository triggerFlowAlarmRuleRepository;

    @Autowired
    private RedisService redisService;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<TriggerFlowAlarmRuleEntity> triggerFlowAlarmRuleEntities = triggerFlowAlarmRuleRepository.findByIsExistTrue();
        for (TriggerFlowAlarmRuleEntity triggerFlowAlarmRuleEntity : triggerFlowAlarmRuleEntities) {
            String triggerFlowId = triggerFlowAlarmRuleEntity.getTriggerFlowId();
            String alarmRuleId = triggerFlowAlarmRuleEntity.getAlarmRuleId();
            redisService.persistKey(RedisUtil.getAlarmConfigKey(triggerFlowId), alarmRuleId);
        }
    }
}
