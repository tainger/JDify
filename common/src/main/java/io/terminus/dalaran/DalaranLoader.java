package io.terminus.dalaran;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.entity.ModelEntity;
import io.terminus.dalaran.entity.flow.TriggerFlowEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.TriggerModel;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.repository.ModelRepository;
import io.terminus.dalaran.repository.PropertyRepository;
import io.terminus.dalaran.repository.TriggerFlowRepository;
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
    private TriggerFlowRepository flowRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ModelRepository modelRepository;

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
        // TODO load mock flow?
        loadAllFlow();
    }

    private void loadAllFlow() {
        List<TriggerFlow> flowList = new ArrayList<>();
        List<TriggerFlowEntity> flowEntities = flowRepository.findAll();
        for (TriggerFlowEntity flowEntity : flowEntities) {
            flowList.add(buildDalaranFlow(flowEntity));
            log.info("load flow[{}]", flowEntity.getId());
        }
        dalaranContext.addTriggerFlows(flowList);
    }

    private TriggerFlow buildDalaranFlow(TriggerFlowEntity flowEntity) {
        val flow = new TriggerFlow();
        List<ProcessorModel> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : flowEntity.getPipeline()) {
            ProcessorModel processor = new ProcessorModel();
            processor.setId(processorEntity.getId());
            processor.setType(processorEntity.getType());
            Class processorConfigType = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getType()).getConfigType();
            processor.setConfig(gson.fromJson(processorEntity.getConfig(), processorConfigType));
            pipeline.add(processor);
        }

        Class configType = dalaranContext.getDalaranComponentContext().getTriggerInfo(flowEntity.getTriggerType()).getConfigType();
        Object triggerConfig = gson.fromJson(flowEntity.getTriggerConfig(), configType);

        flow.setTriggerType(flowEntity.getTriggerType());
        flow.setTriggerConfig(triggerConfig);

        // TODO for test...
        flow.setInModel(buildMessageModel(modelRepository.findOne(flowEntity.getInModel())));
        flow.setOutModel(buildMessageModel(modelRepository.findOne(flowEntity.getOutModel())));
        flow.setId(flowEntity.getId());
        flow.setPipeline(pipeline);
        return flow;
    }


    private static String replaceProperties(String configValue, Map<String, String> properties) {
        // TODO 性能问题...
        Jinjava jinjava = new Jinjava();
        return jinjava.render(configValue, properties);
    }

    private MessageModel buildMessageModel(ModelEntity modelEntity) {
        val model = new MessageModel();
        val modelType = modelEntity.getType();
        model.setModelType(modelType);
        Class<? extends DalaranModelSchema> schemaType = dalaranContext.getDalaranConverterContext().getSchemaType(modelType);
        model.setModelSchema(gson.fromJson(modelEntity.getModelSchema(), schemaType));
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
                ModelEntity modelEntity = modelRepository.findOne(modelConfig.getInModelId());
                val inModel = buildMessageModel(modelEntity);
                trigger.setInModel(inModel);
            }
            if (modelConfig.getOutModelId() != null) {
                ModelEntity modelEntity = modelRepository.findOne(modelConfig.getOutModelId());
                val outModel = buildMessageModel(modelEntity);
                trigger.setOutModel(outModel);
            }
        }
        return trigger;
    }
}
