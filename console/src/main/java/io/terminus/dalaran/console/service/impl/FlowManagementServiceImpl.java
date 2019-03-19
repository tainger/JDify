package io.terminus.dalaran.console.service.impl;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.console.entity.ProcessorEntity;
import io.terminus.dalaran.console.entity.PropertyEntity;
import io.terminus.dalaran.console.entity.TriggerEntity;
import io.terminus.dalaran.console.repository.FlowRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.model.DalaranFlow;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlowManagementServiceImpl implements FlowManagementService {

    @Autowired
    private FlowRepository flowRepository;

    private DalaranContext dalaranContext;

    // TODO use jackson
    private Gson gson;

    @Override
    public void publish() {
        List<DalaranFlow> flowList = flowRepository.findAll().stream().map(flowEntity -> {
            val flow = new DalaranFlow();
            Map<String, String> properties = new HashMap<>();
            // TODO 加载全局变量, 局部覆盖
            for (PropertyEntity property : flowEntity.getProperties()) {
                properties.put(property.getName(), property.getValue());
            }
            val trigger = buildTrigger(flowEntity.getTrigger(), properties);
            List<DalaranFlow.Processor> processors = flowEntity.getProcessors().stream().
                    map(processorEntity -> buildProcessor(processorEntity, properties)).collect(Collectors.toList());

            flow.setId(flowEntity.getName() + "-" + flowEntity.getId());
            flow.setTrigger(trigger);
            flow.setProcessors(processors);
            return flow;
        }).collect(Collectors.toList());

        dalaranContext.addFlows(flowList);
    }

    private DalaranFlow.Trigger buildTrigger(TriggerEntity triggerEntity, Map<String, String> properties) {
        val trigger = new DalaranFlow.Trigger();
        Class configType = dalaranContext.getDalaranComponentContainer().getTriggerConfigType(triggerEntity.getType());
        String jsonConfig = replaceProperties(triggerEntity.getConfig(), properties);
        trigger.setType(triggerEntity.getType());
        Object config = gson.fromJson(jsonConfig, configType);
        trigger.setConfig(config);
        return trigger;
    }

    // TODO 分开写是为了避免后期差异
    private DalaranFlow.Processor buildProcessor(ProcessorEntity processorEntity, Map<String, String> properties) {
        val processor = new DalaranFlow.Processor();
        Class configType = dalaranContext.getDalaranComponentContainer().getProcessorConfigType(processorEntity.getType());
        String jsonConfig = replaceProperties(processorEntity.getConfig(), properties);
        processor.setType(processorEntity.getType());
        Object config = gson.fromJson(jsonConfig, configType);
        processor.setConfig(config);
        return processor;
    }

    private static String replaceProperties(String configValue, Map<String, String> properties) {
        // TODO 性能问题...
        Jinjava jinjava = new Jinjava();
        return jinjava.render(configValue, properties);
    }
}
