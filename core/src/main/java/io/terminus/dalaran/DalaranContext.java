package io.terminus.dalaran;

import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;

import java.util.List;

public interface DalaranContext {

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

    void testFlow(Long flowId, Object body, String recordId);

    DalaranComponentContext getDalaranComponentContext();

    DalaranConverterContext getDalaranConverterContext();

    DalaranServiceContext getDalaranServiceContext();

}
