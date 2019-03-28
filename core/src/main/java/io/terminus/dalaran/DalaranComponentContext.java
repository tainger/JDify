package io.terminus.dalaran;

import io.terminus.dalaran.annotation.Processor;
import io.terminus.dalaran.annotation.Trigger;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.TriggerInfo;

import java.util.Collection;

public interface DalaranComponentContext {

    void addTrigger(Trigger triggerInfo, DalaranTrigger trigger);

    void addProcessor(Processor processorInfo, DalaranProcessor processor);

    DalaranTrigger getTrigger(String triggerType);

    DalaranProcessor getProcessor(String processorType);

    TriggerInfo getTriggerInfo(String triggerType);

    ProcessorInfo getProcessorInfo(String processorType);

    Collection<TriggerInfo> getAllTriggerInfo();

    Collection<ProcessorInfo> getAllProcessorInfo();
}
