package io.terminus.dalaran;

import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.TriggerModel;

import java.util.List;

public interface DalaranContext {

    void removeFlow(Long id);

    void removeFlows(List<Long> id);

    void removeAllFlow() throws Exception;

    void addFlow(DalaranFlow flow);

    void addFlows(List<DalaranFlow> flows);

    void addTrigger(TriggerModel trigger);

    void addTriggers(List<TriggerModel> triggers);

    void addTestFlow(DalaranFlow dalaranFlow);

    void addTestFlows(List<DalaranFlow> flows);

    Object testFlow(Long id, Object body);

    DalaranComponentContext getDalaranComponentContext();

    DalaranConverterContext getDalaranConverterContext();

}
