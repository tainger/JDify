package io.terminus.dalaran.console.service.impl;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.console.entity.FlowEntity;
import io.terminus.dalaran.console.entity.ProcessorEntity;
import io.terminus.dalaran.console.entity.PropertyEntity;
import io.terminus.dalaran.console.entity.TriggerEntity;
import io.terminus.dalaran.console.repository.FlowRepository;
import io.terminus.dalaran.console.repository.ProcessorRepository;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.model.DalaranFlow;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlowManagementServiceImpl implements FlowManagementService, InitializingBean {

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private DalaranContext dalaranContext;

    // TODO use jackson
    private Gson gson = new Gson();

    @Override
    public void saveFlow(FlowEntity flowEntity) {
        flowRepository.save(flowEntity);
    }

    @Override
    public void publish() {
        List<DalaranFlow> flowList = new ArrayList<>();
        List<FlowEntity> flowEntities = flowRepository.findAll();
        for (FlowEntity flowEntity : flowEntities) {
            val flow = new DalaranFlow();
            Map<String, String> properties = new HashMap<>();
            // TODO 加载全局变量, 局部覆盖
            for (Long propertyId : flowEntity.getProperties()) {
                PropertyEntity property = propertyRepository.findOne(propertyId);
                properties.put(property.getName(), property.getValue());
            }
            val trigger = buildTrigger(flowEntity.getTrigger(), properties);
            List<DalaranFlow.Processor> processors = flowEntity.getProcessors().stream()
                    .map(processorId -> {
                        ProcessorEntity processorEntity = processorRepository.findOne(processorId);
                        return buildProcessor(processorEntity, properties);
                    }).collect(Collectors.toList());

            flow.setId(flowEntity.getId().toString());
            flow.setTrigger(trigger);
            flow.setProcessors(processors);
            flow.setMaxRetry(flowEntity.getMaxRetry());
            flow.setRetryDelay(flowEntity.getRetryDelay());
            flow.setRetryable(flowEntity.getRetryable());

            flowList.add(flow);
        }
        dalaranContext.addFlows(flowList);
    }

    private DalaranFlow.Trigger buildTrigger(TriggerEntity triggerEntity, Map<String, String> properties) {
        val trigger = new DalaranFlow.Trigger();
        Class configType = dalaranContext.getDalaranComponentContainer().getTriggerConfigType(triggerEntity.getType());
        String jsonConfig = replaceProperties(triggerEntity.getConfig(), properties);
        Object config = gson.fromJson(jsonConfig, configType);

        trigger.setId(triggerEntity.getId());
        trigger.setType(triggerEntity.getType());
        trigger.setConfig(config);
        return trigger;
    }

    // TODO 分开写是为了避免后期差异
    private DalaranFlow.Processor buildProcessor(ProcessorEntity processorEntity, Map<String, String> properties) {
        val processor = new DalaranFlow.Processor();
        Class configType = dalaranContext.getDalaranComponentContainer().getProcessorConfigType(processorEntity.getType());
        String jsonConfig = replaceProperties(processorEntity.getConfig(), properties);
        Object config = gson.fromJson(jsonConfig, configType);

        processor.setId(processorEntity.getId());
        processor.setType(processorEntity.getType());
        processor.setConfig(config);
        return processor;
    }

    private static String replaceProperties(String configValue, Map<String, String> properties) {
        // TODO 性能问题...
        Jinjava jinjava = new Jinjava();
        return jinjava.render(configValue, properties);
    }

    /**
     * startup auto publish
     */
    @Override
    public void afterPropertiesSet() {
        publish();
    }
}
