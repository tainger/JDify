package io.terminus.dalaran;

import io.terminus.dalaran.annotation.Processor;
import io.terminus.dalaran.annotation.Trigger;

public interface DalaranComponentContext {

    void addTrigger(String triggerType, Trigger triggerInfo, DalaranTrigger trigger);

    void addProcessor(String processorType, Processor processorInfo, DalaranProcessor processor);

    DalaranTrigger getTrigger(String triggerType);

    DalaranProcessor getProcessor(String processorType);

    Trigger getTriggerInfo(String triggerType);

    Processor getProcessorInfo(String processorType);
}
