package io.terminus.dalaran.console.service.impl;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranModelSchema;
import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.model.FlowModel;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.TriggerModel;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlowManagementServiceImpl implements FlowManagementService, InitializingBean {

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

    @Autowired
    private FlowQueryService flowQueryService;

    @Autowired
    private ModuleRepository moduleRepository;

    // TODO use jackson
    private Gson gson = new Gson();

    @Override
    public List<FlowModel> queryFlows(FlowQuery query) {
        List<FlowEntity> entities = flowQueryService.query(query);
        List<FlowModel> models = new LinkedList<>();
        for (FlowEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public void saveFlow(FlowEntity flowEntity) {
        flowRepository.save(flowEntity);
    }

    @Override
    public void createFlow(FlowModel flowModel) {
        flowRepository.save(buildEntity(flowModel));
    }

    @Override
    public void deleteFlow(Long flowId) {
        flowRepository.delete(flowId);
    }

    @Override
    public void updateFlow(FlowModel flowModel) {
        flowRepository.save(buildEntity(flowModel));
    }

    @Override
    public List<FlowModel> list() {
        List<FlowEntity> entities = flowRepository.findAll();
        List<FlowModel> models = new LinkedList<>();
        for (FlowEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<FlowModel> queryByProcessorIds(List<Long> processorIds) {
        List<FlowEntity> entities = flowQueryService.queryByProcessorIds(processorIds);
        List<FlowModel> models = new LinkedList<>();
        for (FlowEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
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

            List<ProcessorModel> processors = flowEntity.getProcessors().stream()
                    .map(processorId -> {
                        ProcessorEntity processorEntity = processorRepository.findOne(processorId);
                        return buildProcessor(processorEntity, properties);
                    }).collect(Collectors.toList());

            flow.setId(flowEntity.getId().toString());
            flow.setProcessors(processors);
            flow.setMaxRetry(flowEntity.getMaxRetry());
            flow.setRetryDelay(flowEntity.getRetryDelay());
            flow.setRetryable(flowEntity.getRetryable());

            flowList.add(flow);
        }
        val triggerEntities = triggerRepository.findAll();
        for (TriggerEntity triggerEntity : triggerEntities) {
            val trigger = buildTrigger(triggerEntity);
            dalaranContext.addTrigger(trigger);
        }
        dalaranContext.addFlows(flowList);
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
        trigger.setFlowId(triggerEntity.getFlow().getId());
        trigger.setType(triggerEntity.getType());
        trigger.setConfig(config);
        return trigger;
    }

    // TODO 分开写是为了避免后期差异
    private ProcessorModel buildProcessor(ProcessorEntity processorEntity, Map<String, String> properties) {
        val processor = new ProcessorModel();
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

    private FlowEntity buildEntity(FlowModel model) {
        FlowEntity flowEntity = new FlowEntity();
        ModuleEntity moduleEntity = moduleRepository.findOne(model.getModuleId());
        List<ProcessorEntity> processors = new LinkedList<>();
        for (Long id: model.getProcessorIds()) {
            processors.add(processorRepository.findOne(id));
        }

        flowEntity.setName(model.getName());
        flowEntity.setModule(moduleEntity);
        flowEntity.setProcessors(model.getProcessorIds());
        flowEntity.setDescription(model.getDescription());
        flowEntity.setMaxRetry(model.getMaxRetry());
        flowEntity.setRetryable(model.getRetryable());
        flowEntity.setRetryDelay(model.getRetryDelay());
        flowEntity.setProperties(model.getPropertyIds());

        return flowEntity;
    }

    private FlowModel buildModel(FlowEntity entity) {
        FlowModel flowModel = new FlowModel();
        flowModel.setId(entity.getId());
        flowModel.setName(entity.getName());
        flowModel.setModuleId(entity.getModule().getId());
        flowModel.setMaxRetry(entity.getMaxRetry());
        flowModel.setProcessorIds(entity.getProcessors());
        flowModel.setPropertyIds(entity.getProperties());
        flowModel.setRetryable(entity.getRetryable());
        flowModel.setRetryDelay(entity.getRetryDelay());
        flowModel.setDescription(entity.getDescription());
        return flowModel;
    }

    /**
     * startup auto publish
     */
    @Override
    public void afterPropertiesSet() {
//        publish();
    }
}
