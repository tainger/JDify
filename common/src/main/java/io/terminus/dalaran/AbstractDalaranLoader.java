package io.terminus.dalaran;

import com.google.gson.Gson;
import com.hubspot.jinjava.Jinjava;
import io.terminus.dalaran.entity.ConnectorSuperEntity;
import io.terminus.dalaran.entity.ModelSuperEntity;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.flow.BasicFlowEntity;
import io.terminus.dalaran.entity.flow.SubFlowSuperEntity;
import io.terminus.dalaran.entity.flow.TriggerFlowSuperEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.config.ComponentInfo;
import io.terminus.dalaran.model.config.ConnectorInfo;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO 这里整体还是有点乱
@Slf4j
public abstract class AbstractDalaranLoader<TriggerFlowEntity extends TriggerFlowSuperEntity, SubFlowEntity extends SubFlowSuperEntity>
        implements DalaranLoader<TriggerFlowEntity, SubFlowEntity> {
    // TODO use jackson
    private final Gson gson = new Gson();

    @Autowired
    private DalaranContext dalaranContext;

    @Override
    public TriggerFlow loadTriggerFlow(TriggerFlowEntity flowEntity) {
        TriggerFlow flow = new TriggerFlow();
        buildFlow(flow, flowEntity);
        return flow;
    }

    @Override
    public SubFlow loadSubFlow(SubFlowEntity flowEntity) {
        SubFlow flow = new SubFlow();
        buildFlow(flow, flowEntity);
        return flow;
    }

    protected Object buildConfig(ComponentInfo componentInfo, String configJson) {
        Object triggerConfig = gson.fromJson(configJson, componentInfo.getConfigType());
        if (triggerConfig instanceof ModelableConfig) {
            injectModel((ModelableConfig) triggerConfig);
        }
        if (triggerConfig instanceof ConnectorConfig && componentInfo.getConnectorInfo() != null) {
            injectConnector((ConnectorConfig) triggerConfig, componentInfo.getConnectorInfo());
        }
        return triggerConfig;
    }

    private MessageModel getMessageModel(Long modelId) {
        ModelSuperEntity modelEntity = getModelEntity(modelId);
        val model = new MessageModel();
        val modelType = modelEntity.getType();
        model.setModelType(modelType);
        Class<? extends DalaranModelSchema> schemaType = dalaranContext.getDalaranConverterContext().getSchemaType(modelType);
        model.setModelSchema(gson.fromJson(modelEntity.getModelSchema(), schemaType));
        return model;
    }

    private void buildFlow(BasicFlow flow, BasicFlowEntity flowEntity) {
        List<ProcessorModel> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : flowEntity.getPipeline()) {
            ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getType());
            Object config = buildConfig(processorInfo, processorEntity.getConfig());

            ProcessorModel processor = new ProcessorModel();
            processor.setId(processorEntity.getId());
            processor.setType(processorEntity.getType());
            processor.setConfig(config);

            pipeline.add(processor);
        }

        // TODO for test...
        flow.setInModel(getMessageModel(flowEntity.getInModel()));
        flow.setOutModel(getMessageModel(flowEntity.getOutModel()));
        flow.setPipeline(pipeline);
    }

    private void injectModel(ModelableConfig modelableConfig) {
        if (modelableConfig.getInModelId() != null) {
            MessageModel inModel = getMessageModel(modelableConfig.getInModelId());
            modelableConfig.setInModel(inModel);
        }
        if (modelableConfig.getOutModelId() != null) {
            MessageModel outModel = getMessageModel(modelableConfig.getOutModelId());
            modelableConfig.setOutModel(outModel);
        }
    }

    private void injectConnector(ConnectorConfig connectorConfig, ConnectorInfo connectorInfo) {
        Long connectorId = connectorConfig.getConnectorId();
        if (connectorId != null) {
            ConnectorSuperEntity connectorEntity = getConnector(connectorId);
            Object connector = gson.fromJson(connectorEntity.getConfig(), connectorInfo.getConnectorType());
            connectorConfig.setConnector(connector);
        }
    }

    private String replaceProperties(String configValue, Map<String, String> properties) {
        // TODO 性能问题...
        Jinjava jinjava = new Jinjava();
        return jinjava.render(configValue, properties);
    }

}
