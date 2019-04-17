package io.terminus.dalaran.support.component;

import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.annotation.Processor;
import io.terminus.dalaran.annotation.Trigger;
import io.terminus.dalaran.model.config.DalaranConfigField;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.TriggerInfo;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranComponentContext implements DalaranComponentContext, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<String, DalaranTrigger> triggerMapping = new ConcurrentHashMap<>();
    private final Map<String, DalaranProcessor> processorMapping = new ConcurrentHashMap<>();

    private final Map<String, TriggerInfo> triggerInfoMapping = new ConcurrentHashMap<>();
    private final Map<String, ProcessorInfo> processorInfoMapping = new ConcurrentHashMap<>();

    @Override
    public DalaranTrigger getTrigger(String triggerType) {
        return triggerMapping.get(triggerType);
    }

    @Override
    public DalaranProcessor getProcessor(String processorType) {
        return processorMapping.get(processorType);
    }

    @Override
    public TriggerInfo getTriggerInfo(String triggerType) {
        return triggerInfoMapping.get(triggerType);
    }

    @Override
    public ProcessorInfo getProcessorInfo(String processorType) {
        return processorInfoMapping.get(processorType);
    }

    @Override
    public Collection<TriggerInfo> getAllTriggerInfo() {
        return triggerInfoMapping.values();
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

    private void addTrigger(DalaranTrigger trigger) {
        Trigger triggerAnnotation = trigger.getClass().getDeclaredAnnotation(Trigger.class);
        List<DalaranConfigField> configFields = buildConfigFields(triggerAnnotation.configType());

        TriggerInfo triggerInfo = new TriggerInfo();
        triggerInfo.setType(triggerAnnotation.value());
        triggerInfo.setConfigFields(configFields);
        triggerInfo.setIsVoid(triggerAnnotation.isVoid());
        triggerInfo.setConfigType(triggerAnnotation.configType());
        triggerInfo.setBodyMode(triggerAnnotation.bodyMode());

        triggerInfoMapping.put(triggerAnnotation.value(), triggerInfo);
        triggerMapping.put(triggerAnnotation.value(), trigger);
    }

    private void addProcessor(DalaranProcessor processor) {
        Processor processorAnnotation = processor.getClass().getDeclaredAnnotation(Processor.class);
        List<DalaranConfigField> configFields = buildConfigFields(processorAnnotation.configType());

        ProcessorInfo processorInfo = new ProcessorInfo();
        processorInfo.setType(processorAnnotation.value());
        processorInfo.setConfigFields(configFields);
        processorInfo.setConfigType(processorAnnotation.configType());
        processorInfo.setBodyMode(processorAnnotation.bodyMode());

        processorInfoMapping.put(processorAnnotation.value(), processorInfo);
        processorMapping.put(processorAnnotation.value(), processor);
    }

    private List<DalaranConfigField> buildConfigFields(Class configClass) {
        List<DalaranConfigField> configFields = new ArrayList<>();
        for (Field field : configClass.getDeclaredFields()) {
            ConfigFieldInfo configFieldInfo = field.getDeclaredAnnotation(ConfigFieldInfo.class);
            if (configFieldInfo != null) {
                DalaranConfigField configField = new DalaranConfigField();
                configField.setName(field.getName());
                configField.setInputType(configFieldInfo.inputType());
                configField.setExample(configFieldInfo.example());
                configField.setDefaultValue(configFieldInfo.defaultValue());
                configField.setLabel(configFieldInfo.label());
                configField.setEnum(configFieldInfo.isEnum());

                try {
                    if (configFieldInfo.isEnum()) {
                        List<Map<String, String>> enumValues = new ArrayList<>();
                        val type = Class.forName(field.getType().getName());
                        val fields = type.getFields();
                        for (Field field1 : fields) {
                            val name = field1.getName();
                            val map = new HashMap();
                            map.put(name, name);
                            enumValues.add(map);
                        }
                        configField.setEnumValues(enumValues);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                configFields.add(configField);
            }
        }
        return configFields;
    }
}
