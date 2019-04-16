package io.terminus.dalaran;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.entity.*;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.TriggerModel;
import io.terminus.dalaran.repository.*;
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
    private StructureRepository structureRepository;

    @Autowired
    private DalaranContext dalaranContext;

    // TODO test mode
    private final boolean enableTest;

    public DalaranLoader(boolean enableTest) {
        this.enableTest = enableTest;
    }

    // TODO 临时用入参处理一下 trigger 加载的开关
    @PostConstruct
    private void init() {
//        loadFlow();
        if (enableTest) {
            loadTestFlow();
        } else {
            loadTrigger();
        }
    }

    private void loadTestFlow() {
        List<DalaranFlow> flowList = new ArrayList<>();
        List<FlowEntity> flowEntities = flowRepository.findAll();
        for (FlowEntity flowEntity : flowEntities) {
            flowList.add(buildDalaranFlow(flowEntity));
            log.info("load test flow[{}]", flowEntity.getId());
        }
        dalaranContext.addTestFlows(flowList);
    }

    private void loadFlow() {
        List<DalaranFlow> flowList = new ArrayList<>();
        List<FlowEntity> flowEntities = flowRepository.findAll();
        for (FlowEntity flowEntity : flowEntities) {
            flowList.add(buildDalaranFlow(flowEntity));
            log.info("load flow[{}]", flowEntity.getId());
        }
        dalaranContext.addFlows(flowList);
    }

    private void loadTrigger() {
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
            StructureEntity structureEntity = structureRepository.findOne(triggerEntity.getInStructure());
            val inModel = buildMessageModel(structureEntity);
            trigger.setInModel(inModel);
        }
        if (triggerEntity.getOutStructure() != null) {
            StructureEntity structureEntity = structureRepository.findOne(triggerEntity.getOutStructure());
            val outModel = buildMessageModel(structureEntity);
            trigger.setOutModel(outModel);
        }
        trigger.setId(triggerEntity.getId());
        trigger.setType(triggerEntity.getType());
        trigger.setConfig(config);

        FlowEntity flowEntity = flowRepository.findOne(triggerEntity.getFlowId());
        trigger.setFlow(buildDalaranFlow(flowEntity));
        return trigger;
    }

    // TODO 分开写是为了避免后期差异
    private ProcessorModel buildProcessor(ProcessorEntity processorEntity, Map<String, String> properties) {
        val processor = new ProcessorModel();
        // TODO check processor
        Class configType = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getType()).getConfigType();
        String jsonConfig = replaceProperties(processorEntity.getConfig(), properties);
        Object config = gson.fromJson(jsonConfig, configType);

        if (config instanceof ModelableConfig) {
            ModelableConfig modelConfig = (ModelableConfig) config;
            if (modelConfig.getInModelId() != null) {
                val inStructureEntity = structureRepository.findOne(modelConfig.getInModelId());
                // TODO null check
                val inModel = buildMessageModel(inStructureEntity);
                processor.setInModel(inModel);
            }
            if (modelConfig.getOutModelId() != null) {
                val inStructureEntity = structureRepository.findOne(modelConfig.getOutModelId());
                // TODO null check
                val outModel = buildMessageModel(inStructureEntity);
                processor.setOutModel(outModel);
            }
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
        val modelType = structureEntity.getType();
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

        // TODO for test...
        flow.setInModel(buildMessageModel(flowEntity.getInStructure()));
        flow.setOutModel(buildMessageModel(flowEntity.getOutStructure()));
        flow.setId(flowEntity.getId());
        flow.setProcessors(processors);
        flow.setMaxRetry(flowEntity.getMaxRetry());
        flow.setRetryDelay(flowEntity.getRetryDelay());
        flow.setRetryable(flowEntity.getRetryable());
        return flow;
    }

}
