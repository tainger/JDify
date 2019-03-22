package io.terminus.dalaran;

import io.terminus.dalaran.annotation.Component;

public interface DalaranComponentContext {

    void addTrigger(String triggerType, Component componentInfo, DalaranTrigger trigger);

    void addProcessor(String processorType, Component componentInfo, DalaranProcessor processor);

    DalaranTrigger getTrigger(String type);

    DalaranProcessor getProcessor(String type);

    Component getTriggerInfo(String type);

    Component getProcessorInfo(String type);
}
