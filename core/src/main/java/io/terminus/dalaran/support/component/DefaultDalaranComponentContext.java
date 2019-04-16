package io.terminus.dalaran.support.component;

import io.terminus.dalaran.DalaranComponent;
import io.terminus.dalaran.DalaranComponentContext;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.DalaranTrigger;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.annotation.Processor;
import io.terminus.dalaran.annotation.Trigger;
import io.terminus.dalaran.model.config.DalaranConfigField;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.TriggerInfo;
import javafx.util.Pair;
import lombok.val;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDalaranComponentContext implements DalaranComponentContext {

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
        // TODO 可以用 spring 的 annotation, 可以少些一个 service load file
        ServiceLoader.load(DalaranComponent.class).forEach(component -> {
            Class componentClass = component.getClass();
            if (component instanceof DalaranTrigger) {
                Trigger triggerInfo = (Trigger) componentClass.getDeclaredAnnotation(Trigger.class);
                addTrigger(triggerInfo, (DalaranTrigger) component);
            } else if (component instanceof DalaranProcessor) {
                Processor processorInfo = (Processor) componentClass.getDeclaredAnnotation(Processor.class);
                addProcessor(processorInfo, (DalaranProcessor) component);
            }
        });
    }

    private void addTrigger(Trigger triggerAnnotation, DalaranTrigger trigger) {
        List<DalaranConfigField> configFields = new ArrayList<>();

        TriggerInfo triggerInfo = new TriggerInfo();

        triggerInfo.setType(triggerAnnotation.value());
        triggerInfo.setConfigFields(configFields);
        triggerInfo.setIsVoid(triggerAnnotation.isVoid());
        triggerInfo.setConfigType(triggerAnnotation.configType());
        triggerInfo.setBodyMode(triggerAnnotation.bodyMode());

        for (Field field : triggerAnnotation.configType().getDeclaredFields()) {
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

        triggerInfoMapping.put(triggerAnnotation.value(), triggerInfo);
        triggerMapping.put(triggerAnnotation.value(), trigger);
    }

    private void addProcessor(Processor processorAnnotation, DalaranProcessor processor) {
        List<DalaranConfigField> configFields = new ArrayList<>();

        ProcessorInfo processorInfo = new ProcessorInfo();

        processorInfo.setType(processorAnnotation.value());
        processorInfo.setConfigFields(configFields);
        processorInfo.setConfigType(processorAnnotation.configType());
        processorInfo.setBodyMode(processorAnnotation.bodyMode());

        for (Field field : processorAnnotation.configType().getDeclaredFields()) {
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

        processorInfoMapping.put(processorAnnotation.value(), processorInfo);
        processorMapping.put(processorAnnotation.value(), processor);
    }
}
