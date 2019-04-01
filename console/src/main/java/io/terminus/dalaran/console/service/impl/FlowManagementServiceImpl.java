package io.terminus.dalaran.console.service.impl;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranModelSchema;
import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.FlowRepository;
import io.terminus.dalaran.console.repository.ProcessorRepository;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.repository.TriggerRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import io.terminus.dalaran.model.DalaranFlow;
import io.terminus.dalaran.model.MessageModel;
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
    private TriggerRepository triggerRepository;

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private FlowQueryService flowQueryService;

    // TODO use jackson
    private Gson gson = new Gson();

    @Override
    public List<FlowEntity> queryFlows(FlowQuery query) {
        return flowQueryService.query(query);
    }

    @Override
    public void saveFlow(FlowEntity flowEntity) {
        flowRepository.save(flowEntity);
    }

    @Override
    public void deleteFlow(Long flowId) {
        flowRepository.delete(flowId);
    }

    @Override
    public void updateFlow(FlowEntity flowEntity) {
        flowRepository.save(flowEntity);
    }

    @Override
    public List<FlowEntity> list() {
        return flowRepository.findAll();
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
            val triggerEntity = triggerRepository.findByFlowId(flowEntity.getId());
            try {
                val trigger = buildTrigger(triggerEntity, properties);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dalaranContext.addFlows(flowList);
    }

    private DalaranFlow.Trigger buildTrigger(TriggerEntity triggerEntity, Map<String, String> properties) throws Exception {
        if (triggerEntity == null) {
            throw new  Exception();
        }

        val trigger = new DalaranFlow.Trigger();
        Class configType = dalaranContext.getDalaranComponentContext().getTriggerInfo(triggerEntity.getType()).getConfigType();
        String jsonConfig = replaceProperties(triggerEntity.getConfig(), properties);
        Object config = gson.fromJson(jsonConfig, configType);

        if (triggerEntity.getInStructure() != null) {
            val inModel = buildMessageModel(triggerEntity.getInStructure());
            trigger.setInModel(inModel);
        }
        if (triggerEntity.getOutStructure() != null) {
            val outModel = buildMessageModel(triggerEntity.getOutStructure());
            trigger.setOutModel(outModel);
        }

        trigger.setId(triggerEntity.getId());
        trigger.setType(triggerEntity.getType());
        trigger.setConfig(config);
        return trigger;
    }

    // TODO 分开写是为了避免后期差异
    private DalaranFlow.Processor buildProcessor(ProcessorEntity processorEntity, Map<String, String> properties) {
        val processor = new DalaranFlow.Processor();
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

    /**
     * startup auto publish
     */
    @Override
    public void afterPropertiesSet() {
        publish();
    }
}
