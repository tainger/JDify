package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.core.flow.model.*;

import java.util.List;

public interface DalaranFlowBuilder<T> {

    T buildTriggerFlow(TriggerFlow flow);

    T buildSubFLow(SubFlow flow);

    T buildFlowFragment(FlowFragment flow);

    T buildTestFLow(BasicFlow flow);

    List<FlowValidation> validateFlow(BasicFlow flow);

}
