package io.terminus.dalaran.core.resource;

import io.terminus.dalaran.core.resource.entity.*;

import java.util.List;

public interface DalaranResourceLoader {

    List<? extends TriggerFlowAbstractEntity> loadAllTriggerFlow();

    List<? extends SubFlowAbstractEntity> loadAllSubFlow();

    List<? extends PropertyAbstractEntity> loadAllProperties();

    List<? extends FunctionAbstractEntity> loadAllFunctions();

    List<? extends ClientAbstractEntity> loadAllClient();

    TriggerFlowAbstractEntity loadTriggerFlow(Long triggerFlowId);

    SubFlowAbstractEntity loadSubFlow(Long subFlowId);

    ModelAbstractEntity loadModel(Long modelId);

    ConnectorAbstractEntity loadConnector(Long connectorId);

    ServiceAbstractEntity loadService(Long serviceId);

}
