package io.terminus.dalaran.core.resource;

import io.terminus.dalaran.core.resource.entity.ModelAbstractEntity;
import io.terminus.dalaran.core.resource.entity.ServiceAbstractEntity;
import io.terminus.dalaran.core.resource.entity.SubFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.basic.BasicFlowEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;

import java.util.List;

public interface DalaranResourceBuilder {

    BasicFlow buildTestFlow(BasicFlowEntity flowEntity);

    TriggerFlow buildTriggerFlow(TriggerFlowAbstractEntity triggerFlowEntity);

    SubFlow buildSubFlow(SubFlowAbstractEntity subFlowEntity);

    FlowFragment buildFlowFragment(List<ProcessorEntity> pipelineEntityList, MessageModel inModel, MessageModel outModel, Long flowId, String fragmentId, Boolean tracing);

    MessageModel buildModel(Long modelId);

    MessageModel buildModel(ModelAbstractEntity modelEntity);

    ProcessorModel buildProcessorModel(ProcessorEntity processorEntity, MessageModel lastOutModel);

    Object buildConnectorConfig(Long connectorId, Class connectorConfigType);

    Object buildLimiterConfig(Long limiterId, Class limiterConfigType);

    Object buildServiceConfig(ServiceAbstractEntity serviceEntity);

    Object buildAlarmRuleConfig(Long alarmRuleId, Class alarmRuleType);
}
