package io.terminus.dalaran;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.config.AllModelConfig;
import io.terminus.dalaran.config.OutModelConfig;
import io.terminus.dalaran.entity.BasicFlowEntity;
import io.terminus.dalaran.entity.basic.*;
import io.terminus.dalaran.entity.manage.ProcessorEntity;
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
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO 这里整体还是有点乱
@Slf4j
public abstract class AbstractDalaranLoader<TriggerFlowEntity extends TriggerFlowAbstractEntity, SubFlowEntity extends SubFlowAbstractEntity>
        implements DalaranLoader<TriggerFlowEntity, SubFlowEntity> {

    @Autowired
    private DalaranContext dalaranContext;

    private Map<String, String> properties;

    protected abstract ConnectorAbstractEntity getConnector(Long connectorId);

    protected abstract ModelAbstractEntity getModelEntity(Long modelId);

    protected abstract PropertyAbstractEntity[] getPropertyEntities();

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

    Object buildConfig(ComponentInfo componentInfo, String configJson) {
        Object triggerConfig = buildConfig(configJson, componentInfo.getConfigType());
        if (triggerConfig instanceof OutModelConfig) {
            injectOutModel((OutModelConfig) triggerConfig);
        }
        if (triggerConfig instanceof AllModelConfig) {
            injectInModel((AllModelConfig) triggerConfig);
        }
        if (triggerConfig instanceof ConnectorConfig && componentInfo.getConnectorInfo() != null) {
            injectConnector((ConnectorConfig) triggerConfig, componentInfo.getConnectorInfo());
        }
        return triggerConfig;
    }

    private MessageModel getMessageModel(Long modelId) {
        if (modelId == null) {
            return null;
        }
        ModelAbstractEntity modelEntity = getModelEntity(modelId);
        if (modelEntity == null) {
            return null;
        }
        val model = new MessageModel();
        val modelType = modelEntity.getType();
        model.setModelType(modelType);
        Class<? extends DalaranModelSchema> schemaType = dalaranContext.getDalaranConverterContext().getSchemaType(modelType);
        DalaranModelSchema modelSchema = buildConfig(modelEntity.getModelSchema(), schemaType);
        model.setModelSchema(modelSchema);
        return model;
    }

    private void buildFlow(BasicFlow flow, BasicFlowEntity flowEntity) {
        flow.setInModel(getMessageModel(flowEntity.getInModel()));
        flow.setOutModel(getMessageModel(flowEntity.getOutModel()));

        List<ProcessorModel> pipeline = new ArrayList<>();
        MessageModel lastOutModel = flow.getInModel();
        for (ProcessorEntity processorEntity : flowEntity.getPipeline()) {
            ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getType());
            Object config = buildConfig(processorInfo, processorEntity.getConfig());
            if (config instanceof OutModelConfig) {
                ((OutModelConfig) config).setInModel(lastOutModel);
                lastOutModel = ((OutModelConfig) config).getOutModel();
            }
            ProcessorModel processor = new ProcessorModel();
            processor.setId(processorEntity.getId());
            processor.setType(processorEntity.getType());
            processor.setConfig(config);

            pipeline.add(processor);
        }

        // TODO for test...
        flow.setPipeline(pipeline);
    }

    private void injectOutModel(OutModelConfig modelableConfig) {
        if (modelableConfig.getOutModelId() != null) {
            MessageModel outModel = getMessageModel(modelableConfig.getOutModelId());
            modelableConfig.setOutModel(outModel);
        }
    }

    private void injectInModel(AllModelConfig modelableConfig) {
        if (modelableConfig.getInModelId() != null) {
            MessageModel inModel = getMessageModel(modelableConfig.getInModelId());
            modelableConfig.setInModel(inModel);
        }
    }

    private void injectConnector(ConnectorConfig connectorConfig, ConnectorInfo connectorInfo) {
        Long connectorId = connectorConfig.getConnectorId();
        if (connectorId != null) {
            ConnectorAbstractEntity connectorEntity = getConnector(connectorId);
            Object connector = buildConfig(connectorEntity.getConfig(), connectorInfo.getConnectorType());
            connectorConfig.setConnector(connector);
        }
    }

    private <T> T buildConfig(String configValue, Class<T> configType) {
        String replacedConfig = replaceProperties(configValue, getProperties());
        return JSON.parseObject(replacedConfig, configType);
    }

    private String replaceProperties(String configValue, Map<String, String> properties) {
        properties.putAll(System.getenv());
        StrSubstitutor strSubstitutor = new StrSubstitutor(properties, "${{", "}}");
        return strSubstitutor.replace(configValue);
    }

    private Map<String, String> getProperties() {
        if (properties == null) {
            properties = new HashMap<>(System.getenv());
            for (PropertyAbstractEntity propertyEntity : getPropertyEntities()) {
                properties.put(propertyEntity.getName(), propertyEntity.getValue());
            }
        }
        return properties;
    }
}
