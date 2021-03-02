package io.terminus.dalaran.core.resource;

import io.terminus.dalaran.core.resource.entity.*;
import io.terminus.dalaran.core.resource.entity.released.AlarmRuleReleasedEntity;

import java.util.List;

public interface DalaranResourceLoader {

    List<? extends TriggerFlowAbstractEntity> loadAllTriggerFlow();

    List<? extends SubFlowAbstractEntity> loadAllSubFlow();

    List<? extends TriggerFlowAbstractEntity> loadAvailableTriggerFlow();

    List<? extends TriggerFlowAbstractEntity> loadLastVersionAvailableTriggerFlow();

    List<? extends SubFlowAbstractEntity> loadAvailableSubFlow();

    List<? extends SubFlowAbstractEntity> loadLastVersionAvailableSubFlow();

    List<? extends TriggerFlowAbstractEntity> loadAvailableTriggerFlowByTriggerType(String triggerType);

    List<? extends PropertyAbstractEntity> loadAllProperties();

    List<? extends FunctionAbstractEntity> loadAllFunctions();

    List<? extends ClientAbstractEntity> loadAllClient();

    TriggerFlowAbstractEntity loadTriggerFlow(Long triggerFlowId);

    SubFlowAbstractEntity loadSubFlow(Long subFlowId);

    ModelAbstractEntity loadModel(Long modelId);

    ConnectorAbstractEntity loadConnector(Long connectorId);

    LimiterAbstractEntity loadLimiter(Long limiterId);

    ServiceAbstractEntity loadService(Long serviceId);

    AlarmRuleAbstractEntity loadAlarmRule(Long alarmRuleId);

    List<AlarmRuleReleasedEntity> loadAllMonitoredAlarmRule();
}
