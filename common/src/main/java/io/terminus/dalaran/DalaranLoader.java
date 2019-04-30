package io.terminus.dalaran;

import io.terminus.dalaran.entity.ConnectorSuperEntity;
import io.terminus.dalaran.entity.ModelSuperEntity;
import io.terminus.dalaran.entity.flow.SubFlowSuperEntity;
import io.terminus.dalaran.entity.flow.TriggerFlowSuperEntity;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;

public interface DalaranLoader<TriggerFlowEntity extends TriggerFlowSuperEntity, SubFlowEntity extends SubFlowSuperEntity> {
    ConnectorSuperEntity getConnector(Long connectorId);

    ModelSuperEntity getModelEntity(Long modelId);

    void afterBuildTriggerFlow(TriggerFlow flow, TriggerFlowEntity flowEntity);

    void afterBuildSubFlow(SubFlow flow, SubFlowEntity flowEntity);

    void loadTriggerFlow(TriggerFlowEntity flowEntity);

    void loadSubFlow(SubFlowEntity flowEntity);
}
