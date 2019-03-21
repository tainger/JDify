package io.terminus.dalaran;

import io.terminus.dalaran.annotation.DalaranComponent;

public interface DalaranComponentContainer {

    void addTrigger(String triggerType, DalaranComponent componentInfo, DalaranTrigger trigger);

    void addProcessor(String processorType, DalaranComponent componentInfo, DalaranProcessor processor);

    DalaranTrigger getTrigger(String type);

    DalaranProcessor getProcessor(String type);

    DalaranComponent getTriggerInfo(String type);

    DalaranComponent getProcessorInfo(String type);
}
