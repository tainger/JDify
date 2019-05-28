package io.terminus.dalaran;

import io.terminus.dalaran.entity.basic.*;
import io.terminus.dalaran.model.ConnectorModel;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ServiceModel;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;

import java.util.List;

public interface DalaranEntityLoader {

    List<TriggerFlow> getAllTriggerFlow();

    List<SubFlow> getAllSubFlow();

    TriggerFlowAbstractEntity getTriggerFlow(Long triggerFlowId);

    SubFlowAbstractEntity getSubFlow(Long subFlowId);

    ModelAbstractEntity getModel(Long modelId);

    ConnectorAbstractEntity getConnector(Long connectorId);

    ServiceAbstractEntity getService(Long serviceId);
}
