package io.terminus.dalaran;

public interface DalaranComponentContainer {

    void addTrigger(String triggerType, Class configType, DalaranTrigger trigger);

    void addProcessor(String processorType, Class configType, DalaranProcessor processor);

    DalaranTrigger getTrigger(String type);

    DalaranProcessor getProcessor(String type);

    Class getTriggerConfigType(String type);

    Class getProcessorConfigType(String type);
}
