package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.model.flow.*;
import org.apache.camel.CamelContext;

import java.util.List;

public interface DalaranFlowBuilder<T> {

    T buildTriggerFlow(TriggerFlow flow, CamelContext camelContext);

    T buildSubFLow(SubFlow flow);

    T buildFlowFragment(FlowFragment flow);

    T buildTestFLow(BasicFlow flow);

    T buildTestSubFLow(SubFlow flow);

    List<FlowValidation> validateFlow(BasicFlow flow);

}
