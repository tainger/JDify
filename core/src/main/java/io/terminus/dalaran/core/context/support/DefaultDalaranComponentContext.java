package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.ComponentType;
import io.terminus.dalaran.config.*;
import io.terminus.dalaran.core.component.*;
import io.terminus.dalaran.core.component.annotation.*;
import io.terminus.dalaran.core.component.config.AuthenticatorConfig;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.LimiterConfig;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.util.ConfigFieldUtils;
import io.terminus.dalaran.core.util.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
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

    private final Map<String, String> triggerConfig = new ConcurrentHashMap<>();
    private final Map<String, String> processorConfig = new ConcurrentHashMap<>();

    private final Map<String, ConnectorInfo> connectorInfoMapping = new ConcurrentHashMap<>();

    private final Map<String, BasicComponentInfo> basicComponentInfoMap = new ConcurrentHashMap<>();

    private final Map<String, LimiterInfo> limiterInfoMap = new ConcurrentHashMap<>();

    private final Map<String, AuthenticatorInfo> authenticatorInfoMap = new ConcurrentHashMap<>();

    private final Map<String, Map<String, Map<String, ProcessorInfo>>> groupProcessorInfo = new ConcurrentHashMap<>();

    private final Map<String, Map<String, Map<String, DalaranProcessor>>> groupProcessor = new ConcurrentHashMap<>();

    @Override
    public DalaranTrigger getTrigger(String triggerType) {
        // TODO check null
        return triggerMapping.get(triggerType);
    }

    @Override
    public DalaranProcessor getProcessor(String group, String processorType, String version) {
        // TODO check null
        if (StringUtils.isNotBlank(group) && StringUtils.isNotBlank(version)) {
            return groupProcessor.get(group).get(processorType).get(version);
        } else {
            return processorMapping.get(processorType);
        }
    }

    @Override
    public TriggerInfo getTriggerInfo(String triggerType) {
        // TODO check null
        return triggerInfoMapping.get(triggerType);
    }

    @Override
    public ProcessorInfo getProcessorInfo(String group, String processorType, String version) {
        // TODO check null
        if (StringUtils.isNotBlank(group) && StringUtils.isNotBlank(version)) {
            return groupProcessorInfo.get(group).get(processorType).get(version);
        } else {
            return processorInfoMapping.get(processorType);
        }
    }

    @Override
    public boolean removeProcessorInfo(String group, String processorType, String version) {
        try {
            groupProcessorInfo.get(group).get(processorType).remove(version);
            if (MapUtils.isEmpty(groupProcessorInfo.get(group).get(processorType))) {
                groupProcessorInfo.get(group).remove(processorType);
                if (MapUtils.isEmpty(groupProcessorInfo.get(group))) {
                    groupProcessorInfo.remove(group);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
    public Collection<AuthenticatorInfo> getAllAuthenticatorInfo() {
        return authenticatorInfoMap.values().stream().map(authenticatorInfo -> {
            AuthenticatorInfo newAuthenticatorInfo = new AuthenticatorInfo();
            BeanUtils.copyProperties(authenticatorInfo, newAuthenticatorInfo);
            List<DalaranConfigField> fields = new ArrayList<>();
            for (DalaranConfigField configField : newAuthenticatorInfo.getConfigFields()) {
                DalaranConfigField i18nField = new DalaranConfigField();
                fields.add(i18nField);
                BeanUtils.copyProperties(configField, i18nField);
                i18nField.setLabel(i18nUtils.getAuthenticatorFieldLabel(authenticatorInfo.getName(), configField.getName()));
            }
            newAuthenticatorInfo.setConfigFields(fields.toArray(new DalaranConfigField[0]));
            return newAuthenticatorInfo;
        }).sorted(Comparator.comparingInt(AuthenticatorInfo::getOrder))
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

    @Override
    public Collection<BasicComponentInfo> getAllBasicComponentInfo() {
        return basicComponentInfoMap.values().stream().map(basicComponentInfo -> {
            BasicComponentInfo newComponentInfo = new BasicComponentInfo();
            BeanUtils.copyProperties(basicComponentInfo, newComponentInfo);
            newComponentInfo.setName(basicComponentInfo.getType());
            List<DalaranConfigField> fields = new ArrayList<>();
            for (DalaranConfigField configField : newComponentInfo.getConfigFields()) {
                DalaranConfigField i18nField = new DalaranConfigField();
                fields.add(i18nField);
                BeanUtils.copyProperties(configField, i18nField);
                i18nField.setLabel(i18nUtils.getBasicComponentFieldName(newComponentInfo.getType(), configField.getName()));
            }
            newComponentInfo.setConfigFields(fields.toArray(new DalaranConfigField[0]));
            return newComponentInfo;
        }).collect(Collectors.toList());
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
            triggerInfo.setOrigin(triggerAnnotation.origin());
            triggerInfo.setOrder(triggerAnnotation.order());
            triggerInfo.setConfigFields(configFields);
            triggerInfo.setConfigType(triggerAnnotation.configType());
            triggerInfo.setModelType(triggerAnnotation.bodyType());
            triggerInfo.setDescription(triggerAnnotation.description());
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

            Class limiterType = getConfigType(triggerAnnotation.configType(), LimiterConfig.class);
            if (limiterType != null) {
                Limiter limiter = (Limiter) limiterType.getDeclaredAnnotation(Limiter.class);
                if (limiter != null) {
                    LimiterInfo limiterInfo = buildLimiterInfo(limiterType, limiter, triggerType);
                    triggerInfo.setLimiterInfo(limiterInfo);
                    triggerInfo.setLimiterType(limiter.value());
                }
            }

            Class authenticatorType = getConfigType(triggerAnnotation.configType(), AuthenticatorConfig.class);
            if (authenticatorType != null) {
                Authenticator authenticator = (Authenticator) authenticatorType.getDeclaredAnnotation(Authenticator.class);
                if (authenticator != null) {
                    AuthenticatorInfo authenticatorInfo = buildAuthenticatorInfo(authenticatorType, authenticator, triggerType);
                    triggerInfo.setAuthenticatorInfo(authenticatorInfo);
                    triggerInfo.setAuthenticatorType(authenticator.value());
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
            triggerMapping.put(triggerType, trigger);
            triggerConfig.put(triggerType, triggerAnnotation.configType().getName());
        }
        log.info("load trigger {}", triggerAnnotation);
    }

    // TODO 很多重复性的代码
    public void addProcessor(DalaranProcessor processor, String group, String version) {
        Processor processorAnnotation = processor.getClass().getDeclaredAnnotation(Processor.class);
        DalaranConfigField[] configFields = ConfigFieldUtils.buildConfigFields(processorAnnotation.configType());
        for (int i = 0; i < processorAnnotation.value().length; i++) {
            String processorType = processorAnnotation.value()[i];
            ProcessorInfo processorInfo = new ProcessorInfo();
            processorInfo.setOutdated(i != 0);
            processorInfo.setType(processorType);

            processorInfo.setName(processorType);
            processorInfo.setOrigin(processorAnnotation.origin());
            processorInfo.setOrder(processorAnnotation.order());
            processorInfo.setConfigFields(configFields);
            processorInfo.setConfigType(processorAnnotation.configType());
            processorInfo.setModelType(processorAnnotation.bodyType());
            processorInfo.setDescription(processorAnnotation.description());

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

            processorConfig.put(processorType, processorAnnotation.configType().getName());

            Map<String, Map<String, ProcessorInfo>> processorInfos = groupProcessorInfo.get(group);
            if (MapUtils.isEmpty(processorInfos)) {
                processorInfos = new ConcurrentHashMap<>();
                groupProcessorInfo.put(group, processorInfos);
            }
            Map<String, ProcessorInfo> versionedProcessorInfo = processorInfos.get(processorType);
            if (MapUtils.isEmpty(versionedProcessorInfo)) {
                versionedProcessorInfo = new ConcurrentHashMap<>();
            }
            ProcessorInfo newProcessorInfo = processorTransfer(processorInfo);
            versionedProcessorInfo.put(version, newProcessorInfo);
            processorInfos.put(processorType, versionedProcessorInfo);

            Map<String, Map<String, DalaranProcessor>> processors = groupProcessor.get(group);
            if (MapUtils.isEmpty(processors)) {
                processors = new ConcurrentHashMap<>();
                groupProcessor.put(group, processors);
            }
            Map<String, DalaranProcessor> versionedProcessor = processors.get(processorType);
            if (MapUtils.isEmpty(versionedProcessor)) {
                versionedProcessor = new ConcurrentHashMap<>();
            }
            versionedProcessor.put(version, processor);
            processors.put(processorType, versionedProcessor);
        }
        log.info("load processor {}", processorAnnotation);
    }



    @Override
    public void addBasicComponent(DalaranBasicComponent component) {
        DynamicModel componentAnnotation = component.getClass().getDeclaredAnnotation(DynamicModel.class);
        DalaranConfigField[] configFields = ConfigFieldUtils.buildConfigFields(component.getClass());
        BasicComponentInfo componentInfo = new BasicComponentInfo();
        componentInfo.setConfigFields(configFields);
        componentInfo.setName(componentAnnotation.value());
        componentInfo.setType(componentAnnotation.value());
        basicComponentInfoMap.put(componentAnnotation.value(), componentInfo);
    }

    @Override
    public Map<String, Map<String, Map<String, ProcessorInfo>>> listAllGroupProcessor() {
        return groupProcessorInfo;
    }

    @Override
    public Map<String, String> getTriggerConfigMap() {
        return triggerConfig;
    }

    @Override
    public Map<String, String> getProcessorConfigMap() {
        return processorConfig;
    }

    private ConnectorInfo buildConnectorInfo(ComponentType component, Class classType, Connector connector, String componentName) {
        ConnectorInfo connectorInfo = connectorInfoMapping.computeIfAbsent(connector.value(), key -> {
            DalaranConfigField[] connectorConfigFields = ConfigFieldUtils.buildConfigFields(classType);
            ConnectorInfo newConnectorInfo = new ConnectorInfo();
            newConnectorInfo.setName(classType.getSimpleName());
            newConnectorInfo.setOrder(connector.order());
            newConnectorInfo.setClassType(classType);
            newConnectorInfo.setType(connector.value());
            newConnectorInfo.setConfigFields(connectorConfigFields);
            newConnectorInfo.setDescription(connector.description());
            return newConnectorInfo;
        });
        connectorInfo.addComponent(componentName);
        return connectorInfo;
    }

    private LimiterInfo buildLimiterInfo(Class limiterType, Limiter limiter, String componentName) {
        LimiterInfo limiterInfo = limiterInfoMap.computeIfAbsent(limiter.value(), key -> {
            DalaranConfigField[] limiterFields = ConfigFieldUtils.buildConfigFields(limiterType);
            LimiterInfo newLimiterInfo = new LimiterInfo();
            newLimiterInfo.setName(limiter.value());
            newLimiterInfo.setOrder(limiter.order());
            newLimiterInfo.setClassType(limiterType);
            newLimiterInfo.setType(limiter.value());
            newLimiterInfo.setConfigFields(limiterFields);
            return newLimiterInfo;
        });
        limiterInfo.addComponent(componentName);
        return limiterInfo;
    }

    private AuthenticatorInfo buildAuthenticatorInfo(Class authenticatorType, Authenticator authenticator, String componentName) {
        AuthenticatorInfo authenticatorInfo = authenticatorInfoMap.computeIfAbsent(authenticator.value(), key -> {
            DalaranConfigField[] authenticatorFields = ConfigFieldUtils.buildConfigFields(authenticatorType);
            AuthenticatorInfo newAuthenticatorInfo = new AuthenticatorInfo();
            newAuthenticatorInfo.setName(authenticator.value());
            newAuthenticatorInfo.setOrder(authenticator.order());
            newAuthenticatorInfo.setClassType(authenticatorType);
            newAuthenticatorInfo.setType(authenticator.value());
            newAuthenticatorInfo.setConfigFields(authenticatorFields);
            return newAuthenticatorInfo;
        });
        authenticatorInfo.addComponent(componentName);
        return authenticatorInfo;
    }

    private ProcessorInfo processorTransfer(ProcessorInfo processorInfo) {
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
    }

    private Class getConfigType(Class type, Class configType) {
        for (Type genericInterface : type.getGenericInterfaces()) {
            if (!(genericInterface instanceof ParameterizedType)) {
                return null;
            }
            Class rawType = (Class) ((ParameterizedType) genericInterface).getRawType();
            if (rawType != configType) {
                continue;
            }
            Type[] parameterizedType = ((ParameterizedType) genericInterface).getActualTypeArguments();
            if (parameterizedType.length == 1) {
                return (Class) parameterizedType[0];
            }
        }
        return null;
    }
}
