package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.ComponentType;
import io.terminus.dalaran.config.*;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerApiDocExport;
import io.terminus.dalaran.core.component.DalaranTriggerWordDocExport;
import io.terminus.dalaran.core.component.annotation.Connector;
import io.terminus.dalaran.core.component.annotation.Limiter;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.util.ConfigFieldUtils;
import io.terminus.dalaran.core.util.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class DefaultDalaranComponentContext implements DalaranComponentContext {

    @Autowired
    private I18nUtils i18nUtils;

    private final Map<String, DalaranTrigger> triggerMapping = new ConcurrentHashMap<>();
    private final Map<String, DalaranProcessor> processorMapping = new ConcurrentHashMap<>();

    private final Map<String, TriggerInfo> triggerInfoMapping = new ConcurrentHashMap<>();
    private final Map<String, ProcessorInfo> processorInfoMapping = new ConcurrentHashMap<>();

    private final Map<String, ConnectorInfo> connectorInfoMapping = new ConcurrentHashMap<>();

    private final Map<String, LimiterInfo> limiterInfoMap = new ConcurrentHashMap<>();

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
        return triggerInfoMapping.values().stream().map(triggerInfo -> {
            TriggerInfo newTriggerInfo = new TriggerInfo();
            BeanUtils.copyProperties(triggerInfo, newTriggerInfo);
            newTriggerInfo.setName(i18nUtils.getComponentName(triggerInfo.getType()));
            List<DalaranConfigField> fields = new ArrayList<>();
            for (DalaranConfigField configField : newTriggerInfo.getConfigFields()) {
                DalaranConfigField i18nField = new DalaranConfigField();
                fields.add(i18nField);
                BeanUtils.copyProperties(configField, i18nField);
                i18nField.setLabel(i18nUtils.getComponentFieldLabel(newTriggerInfo.getType(), configField.getName()));
            }
            newTriggerInfo.setConfigFields(fields.toArray(new DalaranConfigField[0]));
            return newTriggerInfo;
        }).sorted(Comparator.comparingInt(AbstractComponentInfo::getOrder))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ConnectorInfo> getAllConnectorInfo() {
        return connectorInfoMapping.values().stream().map(connectorInfo -> {
            ConnectorInfo newConnectorInfo = new ConnectorInfo();
            BeanUtils.copyProperties(connectorInfo, newConnectorInfo);
            List<DalaranConfigField> fields = new ArrayList<>();
            for (DalaranConfigField configField : newConnectorInfo.getConfigFields()) {
                DalaranConfigField i18nField = new DalaranConfigField();
                fields.add(i18nField);
                BeanUtils.copyProperties(configField, i18nField);
                i18nField.setLabel(i18nUtils.getConnectorFieldLabel(connectorInfo.getName(), configField.getName()));
            }
            newConnectorInfo.setConfigFields(fields.toArray(new DalaranConfigField[0]));
            return newConnectorInfo;
        }).sorted(Comparator.comparingInt(ConnectorInfo::getOrder))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<LimiterInfo> getAllLimiterInfo() {
        return limiterInfoMap.values().stream().map(limiterInfo -> {
            LimiterInfo newLimiterInfo = new LimiterInfo();
            BeanUtils.copyProperties(limiterInfo, newLimiterInfo);
            List<DalaranConfigField> fields = new ArrayList<>();
            for (DalaranConfigField configField : newLimiterInfo.getConfigFields()) {
                DalaranConfigField i18nField = new DalaranConfigField();
                fields.add(i18nField);
                BeanUtils.copyProperties(configField, i18nField);
                i18nField.setLabel(i18nUtils.getLimiterFieldLabel(limiterInfo.getName(), configField.getName()));
            }
            newLimiterInfo.setConfigFields(fields.toArray(new DalaranConfigField[0]));
            return newLimiterInfo;
        }).sorted(Comparator.comparingInt(LimiterInfo::getOrder))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProcessorInfo> getAllProcessorInfo() {
        return processorInfoMapping.values().stream().map(processorInfo -> {
            ProcessorInfo newProcessorInfo = new ProcessorInfo();
            BeanUtils.copyProperties(processorInfo, newProcessorInfo);
            newProcessorInfo.setName(i18nUtils.getComponentName(processorInfo.getType()));
            List<DalaranConfigField> fields = new ArrayList<>();
            for (DalaranConfigField configField : newProcessorInfo.getConfigFields()) {
                DalaranConfigField i18nField = new DalaranConfigField();
                fields.add(i18nField);
                BeanUtils.copyProperties(configField, i18nField);
                i18nField.setLabel(i18nUtils.getComponentFieldLabel(newProcessorInfo.getType(), configField.getName()));
            }
            newProcessorInfo.setConfigFields(fields.toArray(new DalaranConfigField[0]));
            return newProcessorInfo;
        }).sorted(Comparator.comparingInt(AbstractComponentInfo::getOrder))
                .collect(Collectors.toList());
    }

    // TODO 很多重复性的代码
    public void addTrigger(DalaranTrigger trigger) {
        Trigger triggerAnnotation = trigger.getClass().getDeclaredAnnotation(Trigger.class);
        DalaranConfigField[] configFields = ConfigFieldUtils.buildConfigFields(triggerAnnotation.configType());
        for (int i = 0; i < triggerAnnotation.value().length; i++) {
            String triggerType = triggerAnnotation.value()[i];
            TriggerInfo triggerInfo = new TriggerInfo();
            triggerInfo.setOutdated(i != 0);
            triggerInfo.setType(triggerType);
            triggerInfo.setName(triggerType);
            triggerInfo.setOrder(triggerAnnotation.order());
            triggerInfo.setConfigFields(configFields);
            triggerInfo.setConfigType(triggerAnnotation.configType());
            triggerInfo.setModelType(triggerAnnotation.bodyType());

//            triggerInfo.setIsVoid(triggerAnnotation.isVoid());

            Class connectorType = getConfigType(triggerAnnotation.configType(), ConnectorConfig.class);
            if (connectorType != null) {
                Connector connector = (Connector) connectorType.getDeclaredAnnotation(Connector.class);
                if (connector != null) {
                    ConnectorInfo connectorInfo = buildConnectorInfo(ComponentType.Trigger, connectorType, connector, triggerType);
                    triggerInfo.setConnectorInfo(connectorInfo);
                    triggerInfo.setConnectorType(connector.value());
                }
            }

            Class limiterType = getConfigType(triggerAnnotation.configType(), Limiter.class);
            if (limiterType != null) {
                Limiter limiter = (Limiter) limiterType.getDeclaredAnnotation(Limiter.class);
                if (limiter != null) {
                    LimiterInfo limiterInfo = buildLimiterInfo(limiterType, limiter, triggerType);
                    triggerInfo.setLimiterInfo(limiterInfo);
                    triggerInfo.setLimiterType(limiter.value());
                }
            }

            if (trigger instanceof DalaranTriggerApiDocExport) {
                triggerInfo.setApiDocs(true);
            }
            if (trigger instanceof DalaranTriggerWordDocExport) {
                triggerInfo.setWordDocs(true);
            }
            triggerInfoMapping.put(triggerType, triggerInfo);
            triggerMapping.put(triggerType, trigger);
        }
        log.info("load trigger {}", triggerAnnotation);
    }

    // TODO 很多重复性的代码
    public void addProcessor(DalaranProcessor processor) {
        Processor processorAnnotation = processor.getClass().getDeclaredAnnotation(Processor.class);
        DalaranConfigField[] configFields = ConfigFieldUtils.buildConfigFields(processorAnnotation.configType());
        for (int i = 0; i < processorAnnotation.value().length; i++) {
            String processorType = processorAnnotation.value()[i];
            ProcessorInfo processorInfo = new ProcessorInfo();
            processorInfo.setOutdated(i != 0);
            processorInfo.setType(processorType);

            processorInfo.setName(processorType);
            processorInfo.setOrder(processorAnnotation.order());
            processorInfo.setConfigFields(configFields);
            processorInfo.setConfigType(processorAnnotation.configType());
            processorInfo.setModelType(processorAnnotation.bodyType());

            Class connectorType = getConfigType(processorAnnotation.configType(), ConnectorConfig.class);
            if (connectorType != null) {
                Connector connector = (Connector) connectorType.getDeclaredAnnotation(Connector.class);
                if (connector != null) {
                    ConnectorInfo connectorInfo = buildConnectorInfo(ComponentType.Processor, connectorType, connector, processorType);
                    processorInfo.setConnectorInfo(connectorInfo);
                    processorInfo.setConnectorType(connector.value());
                }
            }

            processorInfoMapping.put(processorType, processorInfo);
            processorMapping.put(processorType, processor);
        }
        log.info("load processor {}", processorAnnotation);
    }

    private ConnectorInfo buildConnectorInfo(ComponentType component, Class connectorType, Connector connector, String componentName) {
        ConnectorInfo connectorInfo = connectorInfoMapping.computeIfAbsent(connector.value(), key -> {
            DalaranConfigField[] connectorConfigFields = ConfigFieldUtils.buildConfigFields(connectorType);
            ConnectorInfo newConnectorInfo = new ConnectorInfo();
            newConnectorInfo.setName(connector.value());
            newConnectorInfo.setOrder(connector.order());
            newConnectorInfo.setConnectorType(connectorType);
            newConnectorInfo.setConfigFields(connectorConfigFields);
            return newConnectorInfo;
        });
        connectorInfo.addComponent(componentName);
        return connectorInfo;
    }

    private LimiterInfo buildLimiterInfo(Class limiterType, Limiter limiter, String componentName) {
        LimiterInfo limiterInfo = limiterInfoMap.computeIfAbsent(limiter.value(), key -> {
            DalaranConfigField[] connectorConfigFields = ConfigFieldUtils.buildConfigFields(limiterType);
            LimiterInfo newLimiterInfo = new LimiterInfo();
            newLimiterInfo.setName(limiter.value());
            newLimiterInfo.setOrder(limiter.order());
            newLimiterInfo.setConnectorType(limiterType);
            newLimiterInfo.setConfigFields(connectorConfigFields);
            return newLimiterInfo;
        });
        limiterInfo.addComponent(componentName);
        return limiterInfo;
    }

    private Class getConfigType(Class type, Class configType) {
        for (Type genericInterface : type.getGenericInterfaces()) {
            if (!(genericInterface instanceof ParameterizedType)) {
                return null;
            }
            Class rawType = (Class) ((ParameterizedType) genericInterface).getRawType();
            if (rawType != configType) {
                return null;
            }
            Type[] parameterizedType = ((ParameterizedType) genericInterface).getActualTypeArguments();
            if (parameterizedType.length == 1) {
                return (Class) parameterizedType[0];
            }
        }
        return null;
    }
}
