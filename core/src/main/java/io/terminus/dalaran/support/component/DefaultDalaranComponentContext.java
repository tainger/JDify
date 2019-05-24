package io.terminus.dalaran.support.component;

import io.terminus.dalaran.*;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.annotation.Processor;
import io.terminus.dalaran.annotation.Trigger;
import io.terminus.dalaran.model.config.ConnectorInfo;
import io.terminus.dalaran.model.config.DalaranConfigField;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.TriggerInfo;
import io.terminus.dalaran.util.ConfigFieldUtils;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranComponentContext implements DalaranComponentContext, ApplicationContextAware {

    private ApplicationContext applicationContext;

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

    @PostConstruct
    public void loadComponents() {
        Map<String, DalaranTrigger> triggerBeanMap = applicationContext.getBeansOfType(DalaranTrigger.class);
        triggerBeanMap.values().forEach(this::addTrigger);

        Map<String, DalaranProcessor> processorBeanMap = applicationContext.getBeansOfType(DalaranProcessor.class);
        processorBeanMap.values().forEach(this::addProcessor);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // TODO 很多重复性的代码
    private void addTrigger(DalaranTrigger trigger) {
        Trigger triggerAnnotation = trigger.getClass().getDeclaredAnnotation(Trigger.class);
        DalaranConfigField[] configFields = ConfigFieldUtils.buildConfigFields(triggerAnnotation.configType());

        TriggerInfo triggerInfo = new TriggerInfo();
        triggerInfo.setType(triggerAnnotation.value());
        triggerInfo.setConfigFields(configFields);
        triggerInfo.setConfigType(triggerAnnotation.configType());
        triggerInfo.setAllowedBodyTypes(triggerAnnotation.allowBodyTypes());
        triggerInfo.setSerializedBody(triggerAnnotation.serializedBody());

        triggerInfo.setIsVoid(triggerAnnotation.isVoid());

        Class connectorType = getConnectorType(triggerAnnotation.configType());
        if (connectorType != null) {
            ConnectorInfo connectorInfo = buildConnectorInfo(ComponentType.Trigger, connectorType, triggerAnnotation.value());
            triggerInfo.setConnectorInfo(connectorInfo);
        }

        triggerInfoMapping.put(triggerAnnotation.value(), triggerInfo);
        triggerMapping.put(triggerAnnotation.value(), trigger);
    }

    // TODO 很多重复性的代码
    private void addProcessor(DalaranProcessor processor) {
        Processor processorAnnotation = processor.getClass().getDeclaredAnnotation(Processor.class);
        DalaranConfigField[] configFields = ConfigFieldUtils.buildConfigFields(processorAnnotation.configType());

        ProcessorInfo processorInfo = new ProcessorInfo();
        processorInfo.setType(processorAnnotation.value());
        processorInfo.setConfigFields(configFields);
        processorInfo.setConfigType(processorAnnotation.configType());
        processorInfo.setSerializedBody(processorAnnotation.serializedBody());
        processorInfo.setAllowedBodyTypes(processorAnnotation.allowBodyTypes());

        Class connectorType = getConnectorType(processorAnnotation.configType());
        if (connectorType != null) {
            ConnectorInfo connectorInfo = buildConnectorInfo(ComponentType.Processor, connectorType, processorAnnotation.value());
            processorInfo.setConnectorInfo(connectorInfo);
        }

        processorInfoMapping.put(processorAnnotation.value(), processorInfo);
        processorMapping.put(processorAnnotation.value(), processor);
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
