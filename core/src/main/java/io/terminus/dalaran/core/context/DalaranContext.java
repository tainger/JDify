package io.terminus.dalaran.core.context;

import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.FlowFragment;
import io.terminus.dalaran.core.flow.model.SubFlow;
import io.terminus.dalaran.core.flow.model.TriggerFlow;

import java.util.List;

public interface DalaranContext<T> {

    void removeFlow(String id);

    void removeFlows(List<String> id);

    void removeAllFlow() throws Exception;

    void addTriggerFlow(TriggerFlow flow);

    void addTriggerFlows(List<TriggerFlow> flows);

    void addTestFlow(BasicFlow flow);

    void addTestFlows(List<BasicFlow> flows);

    void addSubFlow(SubFlow flow);

    void addSubFlows(List<SubFlow> flows);

    void addFragmentFlow(FlowFragment flow);

    void addFragmentFlows(List<FlowFragment> flows);

    void testFlow(Long flowId, String body, String recordId);

    void addRoute(T route);

    DalaranComponentContext getDalaranComponentContext();

    DalaranConverterContext getDalaranConverterContext();

    DalaranServiceContext getDalaranServiceContext();

}
