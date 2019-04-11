package io.terminus.dalaran;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.entity.*;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.TriggerModel;
import io.terminus.dalaran.repository.FlowRepository;
import io.terminus.dalaran.repository.ProcessorRepository;
import io.terminus.dalaran.repository.PropertyRepository;
import io.terminus.dalaran.repository.TriggerRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DalaranLoader {
    // TODO use jackson
    private final Gson gson = new Gson();

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private DalaranContext dalaranContext;

    private final boolean enableTrigger;

    public DalaranLoader(boolean enableTrigger) {
        this.enableTrigger = enableTrigger;
    }

    // TODO 临时用入参处理一下 trigger 加载的开关
    @PostConstruct
    private void init() {
        loadFlow();
        if (enableTrigger) {
            loadTrigger();
        }
    }

    public void loadFlow() {
        List<DalaranFlow> flowList = new ArrayList<>();
        List<FlowEntity> flowEntities = flowRepository.findAll();
        for (FlowEntity flowEntity : flowEntities) {
            flowList.add(buildDalaranFlow(flowEntity));
            log.info("load flow[{}]", flowEntity.getId());
        }
        dalaranContext.addFlows(flowList);
    }

    public void loadTrigger() {
        val triggerEntities = triggerRepository.findAll();
        for (TriggerEntity triggerEntity : triggerEntities) {
            val trigger = buildTrigger(triggerEntity);
            dalaranContext.addTrigger(trigger);
            log.info("load trigger[{}]{type={},flowId={}}", trigger.getId(), trigger.getType(), trigger.getFlow().getId());
        }
    }

    private TriggerModel buildTrigger(TriggerEntity triggerEntity) {
        val trigger = new TriggerModel();
        Class configType = dalaranContext.getDalaranComponentContext().getTriggerInfo(triggerEntity.getType()).getConfigType();
//        String jsonConfig = replaceProperties(triggerEntity.getConfig(), properties);
        Object config = gson.fromJson(triggerEntity.getConfig(), configType);

        if (triggerEntity.getInStructure() != null) {
            val inModel = buildMessageModel(triggerEntity.getInStructure());
            trigger.setInModel(inModel);
        }
        if (triggerEntity.getOutStructure() != null) {
            val outModel = buildMessageModel(triggerEntity.getOutStructure());
            trigger.setOutModel(outModel);
        }

        trigger.setId(triggerEntity.getId());
        trigger.setFlow(buildDalaranFlow(triggerEntity.getFlow()));
        trigger.setType(triggerEntity.getType());
        trigger.setConfig(config);
        return trigger;
    }

    // TODO 分开写是为了避免后期差异
    private ProcessorModel buildProcessor(ProcessorEntity processorEntity, Map<String, String> properties) {
        val processor = new ProcessorModel();
        // TODO check processor
        Class configType = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getType()).getConfigType();
        String jsonConfig = replaceProperties(processorEntity.getConfig(), properties);
        Object config = gson.fromJson(jsonConfig, configType);

        if (processorEntity.getInStructure() != null) {
            val inModel = buildMessageModel(processorEntity.getInStructure());
            processor.setInModel(inModel);
        }
        if (processorEntity.getOutStructure() != null) {
            val outModel = buildMessageModel(processorEntity.getOutStructure());
            processor.setOutModel(outModel);
        }

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

    private MessageModel buildMessageModel(StructureEntity structureEntity) {
        val model = new MessageModel();
        val modelType = structureEntity.getStructureType();
        model.setModelType(modelType);
        Class<? extends DalaranModelSchema> schemaType = dalaranContext.getDalaranConverterContext().getSchemaType(modelType);
        model.setModelSchema(gson.fromJson(structureEntity.getStructureSchema(), schemaType));
        return model;
    }

    private DalaranFlow buildDalaranFlow(FlowEntity flowEntity) {
        val flow = new DalaranFlow();
        Map<String, String> properties = new HashMap<>();
        // TODO 加载全局变量, 局部覆盖
        for (Long propertyId : flowEntity.getProperties()) {
            PropertyEntity property = propertyRepository.findOne(propertyId);
            properties.put(property.getName(), property.getValue());
        }

        List<ProcessorModel> processors = flowEntity.getProcessors().stream()
                .map(processorId -> {
                    ProcessorEntity processorEntity = processorRepository.findOne(processorId);
                    return buildProcessor(processorEntity, properties);
                }).collect(Collectors.toList());

        flow.setId(flowEntity.getId());
        flow.setProcessors(processors);
        flow.setMaxRetry(flowEntity.getMaxRetry());
        flow.setRetryDelay(flowEntity.getRetryDelay());
        flow.setRetryable(flowEntity.getRetryable());
        return flow;
    }

}
