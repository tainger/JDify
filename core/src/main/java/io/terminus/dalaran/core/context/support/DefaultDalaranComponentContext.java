package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.core.component.ComponentType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.config.ConnectorInfo;
import io.terminus.dalaran.core.config.DalaranConfigField;
import io.terminus.dalaran.core.config.ProcessorInfo;
import io.terminus.dalaran.core.config.TriggerInfo;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.util.ConfigFieldUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultDalaranComponentContext implements DalaranComponentContext {

    private final Map<String, DalaranTrigger> triggerMapping = new ConcurrentHashMap<>();
    private final Map<String, DalaranProcessor> processorMapping = new ConcurrentHashMap<>();

    private final Map<String, TriggerInfo> triggerInfoMapping = new ConcurrentHashMap<>();
    private final Map<String, ProcessorInfo> processorInfoMapping = new ConcurrentHashMap<>();

    private final List<ConnectorInfo> connectorInfoList = new ArrayList<>();

    @Override
    public DalaranTrigger getTrigger(String triggerType) {
        // TODO check null
        return triggerMapping.get(triggerType);
    }

    @Override
    public DalaranProcessor getProcessor(String processorType) {

        // TODO check null
        return processorMapping.get(processorType);
    }

    @Override
    public TriggerInfo getTriggerInfo(String triggerType) {
        // TODO check null
        return triggerInfoMapping.get(triggerType);
    }

    @Override
    public ProcessorInfo getProcessorInfo(String processorType) {
        // TODO check null
        return processorInfoMapping.get(processorType);
    }

    @Override
    public Collection<TriggerInfo> getAllTriggerInfo() {
        return triggerInfoMapping.values();
    }

    @Override
    public Collection<ConnectorInfo> getAllConnectorInfo() {
        return connectorInfoList;
    }

    @Override
    public Collection<ProcessorInfo> getAllProcessorInfo() {
        return processorInfoMapping.values();
    }

    // TODO 很多重复性的代码
    public void addTrigger(DalaranTrigger trigger) {
        Trigger triggerAnnotation = trigger.getClass().getDeclaredAnnotation(Trigger.class);
        DalaranConfigField[] configFields = ConfigFieldUtils.buildConfigFields(triggerAnnotation.configType());

        TriggerInfo triggerInfo = new TriggerInfo();
        triggerInfo.setType(triggerAnnotation.value());
        triggerInfo.setName(triggerAnnotation.name());
        triggerInfo.setConfigFields(configFields);
        triggerInfo.setConfigType(triggerAnnotation.configType());
        triggerInfo.setAllowedBodyTypes(triggerAnnotation.allowBodyTypes());
        triggerInfo.setInputSerializeType(triggerAnnotation.inputSerializeType());
        triggerInfo.setOutputSerializeType(triggerAnnotation.outputSerializeType());

        triggerInfo.setIsVoid(triggerAnnotation.isVoid());

        Class connectorType = getConnectorType(triggerAnnotation.configType());
        if (connectorType != null) {
            ConnectorInfo connectorInfo = buildConnectorInfo(ComponentType.Trigger, connectorType, triggerAnnotation.value());
            triggerInfo.setConnectorInfo(connectorInfo);
        }

        triggerInfoMapping.put(triggerAnnotation.value(), triggerInfo);
        triggerMapping.put(triggerAnnotation.value(), trigger);

        log.info("load trigger {}", triggerAnnotation);
    }

    // TODO 很多重复性的代码
    public void addProcessor(DalaranProcessor processor) {
        Processor processorAnnotation = processor.getClass().getDeclaredAnnotation(Processor.class);
        DalaranConfigField[] configFields = ConfigFieldUtils.buildConfigFields(processorAnnotation.configType());

        ProcessorInfo processorInfo = new ProcessorInfo();
        processorInfo.setType(processorAnnotation.value());
        processorInfo.setName(processorAnnotation.name());
        processorInfo.setConfigFields(configFields);
        processorInfo.setConfigType(processorAnnotation.configType());
        processorInfo.setInputSerializeType(processorAnnotation.inputSerializeType());
        processorInfo.setOutputSerializeType(processorAnnotation.outputSerializeType());
        processorInfo.setAllowedBodyTypes(processorAnnotation.allowBodyTypes());

        Class connectorType = getConnectorType(processorAnnotation.configType());
        if (connectorType != null) {
            ConnectorInfo connectorInfo = buildConnectorInfo(ComponentType.Processor, connectorType, processorAnnotation.value());
            processorInfo.setConnectorInfo(connectorInfo);
        }

        processorInfoMapping.put(processorAnnotation.value(), processorInfo);
        processorMapping.put(processorAnnotation.value(), processor);

        log.info("load processor {}", processorAnnotation);
    }

    private ConnectorInfo buildConnectorInfo(ComponentType component, Class connectorType, String componentName) {
        DalaranConfigField[] connectorConfigFields = ConfigFieldUtils.buildConfigFields(connectorType);
        ConnectorInfo connectorInfo = new ConnectorInfo();
        connectorInfo.setComponentType(component);
        connectorInfo.setConnectorType(connectorType);
        connectorInfo.setComponent(componentName);
        connectorInfo.setConfigFields(connectorConfigFields);
        connectorInfoList.add(connectorInfo);
        return connectorInfo;
    }

    private Class getConnectorType(Class configType) {
        for (Type genericInterface : configType.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                Class rawType = (Class) ((ParameterizedType) genericInterface).getRawType();
                if (rawType == ConnectorConfig.class) {
                    Type[] parameterizedType = ((ParameterizedType) genericInterface).getActualTypeArguments();
                    if (parameterizedType.length == 1) {
                        return (Class) parameterizedType[0];
                    }
                }
            }
        }
        return null;
    }
}
