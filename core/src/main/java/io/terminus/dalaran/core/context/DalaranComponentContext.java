package io.terminus.dalaran.core.context;

import io.terminus.dalaran.config.BasicComponentInfo;
import io.terminus.dalaran.config.ConnectorInfo;
import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.config.TriggerInfo;
import io.terminus.dalaran.core.component.DalaranBasicComponent;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranTrigger;

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

    void addBasicComponent(DalaranBasicComponent bean);

    Collection<BasicComponentInfo> getAllBasicComponentInfo();
}
