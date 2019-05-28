package io.terminus.dalaran;

import io.terminus.dalaran.model.ConnectorModel;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ServiceModel;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;

import java.util.List;

public interface DalaranResourceLoader {

    List<TriggerFlow> loadAllTriggerFlow();

    List<SubFlow> loadAllSubFlow();

    TriggerFlow loadTriggerFlow(Long triggerFlowId);

    SubFlow loadSubFlow(Long subFlowId);

    MessageModel loadModel(Long modelId);

    ConnectorModel loadConnector(Long connectorId);

    ServiceModel loadService(Long serviceId);
}
