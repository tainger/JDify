package io.terminus.dalaran.core.context;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.config.ConnectorInfo;
import io.terminus.dalaran.core.config.ProcessorInfo;
import io.terminus.dalaran.core.config.TriggerInfo;

import java.util.Collection;

public interface DalaranComponentContext {

    DalaranTrigger getTrigger(String triggerType);

    DalaranProcessor getProcessor(String processorType);

    TriggerInfo getTriggerInfo(String triggerType);

    ProcessorInfo getProcessorInfo(String processorType);

    Collection<TriggerInfo> getAllTriggerInfo();

    Collection<ConnectorInfo> getAllConnectorInfo();

    Collection<ProcessorInfo> getAllProcessorInfo();

    void addProcessor(DalaranProcessor bean);

    void addTrigger(DalaranTrigger bean);
}
