package io.terminus.dalaran;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.release.ReleaseRecordEntity;
import io.terminus.dalaran.entity.release.ReleasedConnectorEntity;
import io.terminus.dalaran.entity.release.ReleasedModelEntity;
import io.terminus.dalaran.entity.release.ReleasedTriggerFlowEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.TriggerInfo;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.repository.PropertyRepository;
import io.terminus.dalaran.repository.release.ReleaseRecordRepository;
import io.terminus.dalaran.repository.release.ReleasedConnectorRepository;
import io.terminus.dalaran.repository.release.ReleasedModelRepository;
import io.terminus.dalaran.repository.release.ReleasedTriggerFlowRepository;
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
    private ReleasedConnectorRepository connectorRepository;

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

            // TODO 重复的, 需要被抽象的
            ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getType());
            Class processorConfigType = processorInfo.getConfigType();
            Class connectorType = processorInfo.getConnectorType();
            Object config = gson.fromJson(processorEntity.getConfig(), processorConfigType);
            if (config instanceof ModelableConfig) {
                ModelableConfig modelableConfig = (ModelableConfig) config;
                if (modelableConfig.getInModelId() != null) {
                    modelableConfig.setInModel(getMessageModel(modelableConfig.getInModelId()));
                }
                if (modelableConfig.getOutModelId() != null) {
                    modelableConfig.setOutModel(getMessageModel(modelableConfig.getOutModelId()));
                }
            }
            if (config instanceof ConnectorConfig) {
                Long connectorId = ((ConnectorConfig) config).getConnectorId();
                if (connectorId != null) {
                    ReleasedConnectorEntity connectorEntity = connectorRepository.findByVersionAndOriginId(version, connectorId);
                    Object connector = gson.fromJson(connectorEntity.getConfig(), connectorType);
                    ((ConnectorConfig) config).setConnector(connector);
                }
            }
            processor.setConfig(config);
            pipeline.add(processor);
        }

        // TODO 重复的, 需要被抽象的
        TriggerInfo triggerInfo = dalaranContext.getDalaranComponentContext().getTriggerInfo(flowEntity.getTriggerType());
        Class triggerConfigType = triggerInfo.getConfigType();
        Class connectorType = triggerInfo.getConnectorType();
        Object triggerConfig = gson.fromJson(flowEntity.getTriggerConfig(), triggerConfigType);

        flow.setTriggerType(flowEntity.getTriggerType());
        flow.setTriggerConfig(triggerConfig);

        if (triggerConfig instanceof ModelableConfig) {
            ModelableConfig modelableConfig = (ModelableConfig) triggerConfig;
            if (modelableConfig.getInModelId() != null) {
                modelableConfig.setInModel(getMessageModel(modelableConfig.getInModelId()));
            }
            if (modelableConfig.getOutModelId() != null) {
                modelableConfig.setOutModel(getMessageModel(modelableConfig.getOutModelId()));
            }
        }
        if (triggerConfig instanceof ConnectorConfig) {
            Long connectorId = ((ConnectorConfig) triggerConfig).getConnectorId();
            if (connectorId != null) {
                ReleasedConnectorEntity connectorEntity = connectorRepository.findByVersionAndOriginId(version, connectorId);
                Object connector = gson.fromJson(connectorEntity.getConfig(), connectorType);
                ((ConnectorConfig) triggerConfig).setConnector(connector);
            }
        }

        // TODO for test...
        flow.setInModel(getMessageModel(flowEntity.getInModel()));
        flow.setOutModel(getMessageModel(flowEntity.getOutModel()));
        flow.setPipeline(pipeline);
        flow.setId(flowEntity.getOriginId());
        return flow;
    }

    private static String replaceProperties(String configValue, Map<String, String> properties) {
        // TODO 性能问题...
        Jinjava jinjava = new Jinjava();
        return jinjava.render(configValue, properties);
    }

    private MessageModel getMessageModel(Long modelId) {
        ReleasedModelEntity modelEntity = modelRepository.findByVersionAndOriginId(version, modelId);
        val model = new MessageModel();
        val modelType = modelEntity.getType();
        model.setModelType(modelType);
        Class<? extends DalaranModelSchema> schemaType = dalaranContext.getDalaranConverterContext().getSchemaType(modelType);
        model.setModelSchema(gson.fromJson(modelEntity.getModelSchema(), schemaType));
        return model;
    }
}
