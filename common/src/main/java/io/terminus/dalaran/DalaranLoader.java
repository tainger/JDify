package io.terminus.dalaran;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.release.ReleaseRecordEntity;
import io.terminus.dalaran.entity.release.ReleasedModelEntity;
import io.terminus.dalaran.entity.release.ReleasedTriggerFlowEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.repository.PropertyRepository;
import io.terminus.dalaran.repository.ReleaseRecordRepository;
import io.terminus.dalaran.repository.ReleasedModelRepository;
import io.terminus.dalaran.repository.ReleasedTriggerFlowRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class DalaranLoader {
    // TODO use jackson
    private final Gson gson = new Gson();

    @Autowired
    private ReleasedTriggerFlowRepository flowRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ReleasedModelRepository modelRepository;

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private DalaranContext dalaranContext;

    private String version;

    public DalaranLoader(boolean enableTest) {
    }

    // TODO 临时每分钟 load 一下...
    @PostConstruct
    @Scheduled(cron = "0 * * * * *")
    private void init() {
        ReleaseRecordEntity recordEntity = releaseRecordRepository.findByEnabledTrue();
        synchronized (this) {
            if (recordEntity != null && !recordEntity.getVersion().equals(version)) {
                log.info("load version {}", recordEntity.getVersion());
                version = recordEntity.getVersion();
                loadAllFlow();
            } else {
                log.info("version not change");
            }
        }
    }

    private void loadAllFlow() {
        List<TriggerFlow> flowList = new ArrayList<>();
        List<ReleasedTriggerFlowEntity> flowEntities = flowRepository.findByVersion(version);
        for (ReleasedTriggerFlowEntity flowEntity : flowEntities) {
            flowList.add(buildDalaranFlow(flowEntity));
            log.info("load flow[{}]", flowEntity.getId());
        }
        dalaranContext.addTriggerFlows(flowList);
    }

    private TriggerFlow buildDalaranFlow(ReleasedTriggerFlowEntity flowEntity) {
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
        flow.setInModel(buildMessageModel(modelRepository.findByVersionAndOriginId(version, flowEntity.getInModel())));
        flow.setOutModel(buildMessageModel(modelRepository.findByVersionAndOriginId(version, flowEntity.getOutModel())));
        flow.setPipeline(pipeline);
        flow.setId(flowEntity.getOriginId());
        return flow;
    }

    private static String replaceProperties(String configValue, Map<String, String> properties) {
        // TODO 性能问题...
        Jinjava jinjava = new Jinjava();
        return jinjava.render(configValue, properties);
    }

    private MessageModel buildMessageModel(ReleasedModelEntity modelEntity) {
        val model = new MessageModel();
        val modelType = modelEntity.getType();
        model.setModelType(modelType);
        Class<? extends DalaranModelSchema> schemaType = dalaranContext.getDalaranConverterContext().getSchemaType(modelType);
        model.setModelSchema(gson.fromJson(modelEntity.getModelSchema(), schemaType));
        return model;
    }
}
