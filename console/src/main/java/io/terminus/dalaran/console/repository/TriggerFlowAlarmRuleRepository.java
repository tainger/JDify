package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.TriggerFlowAlarmRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TriggerFlowAlarmRuleRepository extends JpaRepository<TriggerFlowAlarmRuleEntity, Long>, JpaSpecificationExecutor<TriggerFlowAlarmRuleEntity> {

    List<TriggerFlowAlarmRuleEntity> findByIsExistTrue();


    TriggerFlowAlarmRuleEntity findByTriggerFlowIdAndIsExistTrue(String triggerFlowId);

    List<TriggerFlowAlarmRuleEntity> findByAlarmRuleIdAndIsExistTrue(String alarmRuleId);

}
