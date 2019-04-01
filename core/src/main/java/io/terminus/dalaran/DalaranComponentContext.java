package io.terminus.dalaran;

import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.TriggerInfo;

import java.util.Collection;

public interface DalaranComponentContext {

    DalaranTrigger getTrigger(String triggerType);

    DalaranProcessor getProcessor(String processorType);

    TriggerInfo getTriggerInfo(String triggerType);

    ProcessorInfo getProcessorInfo(String processorType);

    Collection<TriggerInfo> getAllTriggerInfo();

    Collection<ProcessorInfo> getAllProcessorInfo();
}
