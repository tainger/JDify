package io.terminus.dalaran;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.entity.FlowEntity;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.StructureEntity;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.TriggerModel;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.repository.FlowRepository;
import io.terminus.dalaran.repository.ProcessorRepository;
import io.terminus.dalaran.repository.PropertyRepository;
import io.terminus.dalaran.repository.StructureRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DalaranLoader {
    // TODO use jackson
    private final Gson gson = new Gson();

    @Autowired
    private FlowRepository flowRepository;

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

    public void loadTestFlow(FlowEntity flowEntity) {
        DalaranFlow testFlow = buildDalaranFlow(flowEntity);
        dalaranContext.addTestFlow(testFlow);
    }

    // TODO 临时用入参处理一下 trigger 加载的开关
    @PostConstruct
    private void init() {
        // TODO load mock flow?
        loadAllFlow();
    }

    private void loadAllFlow() {
        List<DalaranFlow> flowList = new ArrayList<>();
        List<FlowEntity> flowEntities = flowRepository.findAll();
        for (FlowEntity flowEntity : flowEntities) {
            flowList.add(buildDalaranFlow(flowEntity));
            log.info("load flow[{}]", flowEntity.getId());
        }
        dalaranContext.addFlows(flowList);
    }

    private DalaranFlow buildDalaranFlow(FlowEntity flowEntity) {
        val flow = new DalaranFlow();
        Map<String, String> properties = new HashMap<>();
        Map<Long, ProcessorModel> processorMap = new HashMap<>();
        // TODO load env
        flowEntity.getProcessorIds().forEach(processorId -> {
            ProcessorEntity processorEntity = processorRepository.findOne(processorId);
            ProcessorModel processor = buildProcessor(processorEntity, properties);
            processorMap.put(processorId, processor);
        });

        Class configType = dalaranContext.getDalaranComponentContext().getTriggerInfo(flowEntity.getTriggerType()).getConfigType();
//        String jsonConfig = replaceProperties(triggerEntity.getConfig(), properties);
        Object triggerConfig = gson.fromJson(flowEntity.getTriggerConfig(), configType);

        flow.setTriggerType(flowEntity.getTriggerType());
        flow.setTriggerConfig(triggerConfig);

        // TODO for test...
        flow.setInModel(buildMessageModel(structureRepository.findOne(flowEntity.getInStructure())));
        flow.setOutModel(buildMessageModel(structureRepository.findOne(flowEntity.getOutStructure())));
        flow.setId(flowEntity.getId());
        flow.setProcessorMap(processorMap);
        flow.setProcessingPipeline(flowEntity.getProcessingPipeline());
        return flow;
    }

    // TODO 分开写是为了避免后期差异
    private ProcessorModel buildProcessor(ProcessorEntity processorEntity, Map<String, String> properties) {
        val processor = new ProcessorModel();
        // TODO check processor
        ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getType());
        if (processorInfo == null) {
            // TODO throw
        }
        Class configType = processorInfo.getConfigType();
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

    private TriggerModel buildTrigger(String type, String configJson) {
        val trigger = new TriggerModel();
        Class configType = dalaranContext.getDalaranComponentContext().getTriggerInfo(type).getConfigType();
//        String jsonConfig = replaceProperties(triggerEntity.getConfig(), properties);
        Object config = gson.fromJson(configJson, configType);
        if (config instanceof ModelableConfig) {
            ModelableConfig modelConfig = (ModelableConfig) config;
            if (modelConfig.getInModelId() != null) {
                StructureEntity structureEntity = structureRepository.findOne(modelConfig.getInModelId());
                val inModel = buildMessageModel(structureEntity);
                trigger.setInModel(inModel);
            }
            if (modelConfig.getOutModelId() != null) {
                StructureEntity structureEntity = structureRepository.findOne(modelConfig.getOutModelId());
                val outModel = buildMessageModel(structureEntity);
                trigger.setOutModel(outModel);
            }
        }
        return trigger;
    }
}
