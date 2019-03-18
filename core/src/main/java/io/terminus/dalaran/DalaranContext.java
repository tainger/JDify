package io.terminus.dalaran;

import io.terminus.dalaran.impl.DalaranComponentContainer;
import io.terminus.dalaran.model.DalaranFlow;

import java.util.List;

public interface DalaranContext {

    // TODO 这些 add 接口其实都没有必要, 目前没有场景
    void addFlow(DalaranFlow flow);

    void addFlows(List<DalaranFlow> flows);

    void addTrigger(String triggerType, Class configType, DalaranTrigger trigger);

    void addProcessor(String processorType, Class configType, DalaranProcessor processor);

    void loadFlows();

    void loadComponents();

    DalaranComponentContainer<DalaranTrigger> getTriggerContainer(String type);

    DalaranComponentContainer<DalaranProcessor> getProcessorContainer(String type);
}
