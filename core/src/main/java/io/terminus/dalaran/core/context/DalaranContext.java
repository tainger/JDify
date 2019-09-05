package io.terminus.dalaran.core.context;

import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;

import java.util.List;

public interface DalaranContext<T> {

    void removeFlow(String id);

    void removeFlows(List<String> id);

    void removeAllFlow() throws Exception;

    void addTriggerFlow(TriggerFlow flow);

    void addTriggerFlows(List<TriggerFlow> flows);

    void addTestFlow(BasicFlow flow);

    void addTestFlows(List<BasicFlow> flows);

    void addTestSubFLow(SubFlow flow);

    void addTestSubFLows(List<SubFlow> flows);

    void addSubFlow(SubFlow flow);

    void addSubFlows(List<SubFlow> flows);

    void addFragmentFlow(FlowFragment flow);

    String testFlow(Long flowId, String body);

    String testSubFlow(Long subFlowId, String body);

    void addRoute(T route);

    DalaranComponentContext getDalaranComponentContext();

    DalaranConverterContext getDalaranConverterContext();

    DalaranServiceContext getDalaranServiceContext();

    DalaranFunctionContext getDalaranFunctionContext();

    DalaranClientContext getDalaranClientContext();

}
