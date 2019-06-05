package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.FlowFragment;
import io.terminus.dalaran.core.flow.model.SubFlow;
import io.terminus.dalaran.core.flow.model.TriggerFlow;

public interface DalaranFlowBuilder<T> {

    T buildTriggerFlow(TriggerFlow flow);

    T buildSubFLow(SubFlow flow);

    T buildFlowFragment(FlowFragment flow);

    T buildTestFLow(BasicFlow flow);

}
