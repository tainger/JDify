package io.terminus.dalaran.impl;

import io.terminus.dalaran.DalaranComponentContainer;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.DalaranTrigger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranComponentContainer implements DalaranComponentContainer {

    private final Map<String, DalaranTrigger> triggerMapping = new ConcurrentHashMap<>();
    private final Map<String, DalaranProcessor> processorMapping = new ConcurrentHashMap<>();

    private final Map<String, Class> triggerConfigTypeMapping = new ConcurrentHashMap<>();
    private final Map<String, Class> processorConfigTypeMapping = new ConcurrentHashMap<>();


    @Override
    public void addTrigger(String triggerType, Class configType, DalaranTrigger trigger) {
        triggerConfigTypeMapping.put(triggerType, configType);
        triggerMapping.put(triggerType, trigger);
    }

    @Override
    public void addProcessor(String processorType, Class configType, DalaranProcessor processor) {
        processorConfigTypeMapping.put(processorType, configType);
        processorMapping.put(processorType, processor);
    }

    @Override
    public DalaranTrigger getTrigger(String type) {
        return triggerMapping.get(type);
    }

    @Override
    public DalaranProcessor getProcessor(String type) {
        return processorMapping.get(type);
    }

    @Override
    public Class getTriggerConfigType(String type) {
        return triggerConfigTypeMapping.get(type);
    }

    @Override
    public Class getProcessorConfigType(String type) {
        return processorConfigTypeMapping.get(type);
    }
}
