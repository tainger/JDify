package io.terminus.dalaran;

import io.terminus.dalaran.entity.basic.SubFlowAbstractEntity;
import io.terminus.dalaran.entity.basic.TriggerFlowAbstractEntity;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;

public interface DalaranLoader<TriggerFlowEntity extends TriggerFlowAbstractEntity, SubFlowEntity extends SubFlowAbstractEntity> {

    TriggerFlow loadTriggerFlow(TriggerFlowEntity flowEntity);

    SubFlow loadSubFlow(SubFlowEntity flowEntity);
}
