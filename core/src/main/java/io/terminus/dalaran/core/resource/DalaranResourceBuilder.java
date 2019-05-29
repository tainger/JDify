package io.terminus.dalaran.core.resource;

import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.SubFlow;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.resource.entity.ModelAbstractEntity;
import io.terminus.dalaran.core.resource.entity.SubFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;

public interface DalaranResourceBuilder {

    TriggerFlow buildTriggerFlow(TriggerFlowAbstractEntity triggerFlowEntity);

    SubFlow buildSubFlow(SubFlowAbstractEntity subFlowEntity);

    MessageModel buildModel(Long modelId);

    MessageModel buildModel(ModelAbstractEntity modelEntity);

    ProcessorModel buildProcessorModel(ProcessorEntity processorEntity, MessageModel lastOutModel, BasicFlow flow);

    Object buildConnectorConfig(Long connectorId, Class connectorConfigType);

    Object buildServiceConfig(Long serviceId);

}
