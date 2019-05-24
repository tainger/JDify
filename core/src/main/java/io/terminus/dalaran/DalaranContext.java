package io.terminus.dalaran;

import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;

import java.util.List;

public interface DalaranContext {

    void removeFlow(Long id);

    void removeFlows(List<Long> id);

    void removeAllFlow() throws Exception;

    void addTriggerFlow(TriggerFlow flow);

    void addTriggerFlows(List<TriggerFlow> flows);

    void addTestFlow(BasicFlow flow);

    void addTestFlows(List<BasicFlow> flows);

    void testFlow(Long flowId, Object body, String recordId);

    DalaranComponentContext getDalaranComponentContext();

    DalaranConverterContext getDalaranConverterContext();
    
    DalaranServiceContext getDalaranServiceContext();

}
