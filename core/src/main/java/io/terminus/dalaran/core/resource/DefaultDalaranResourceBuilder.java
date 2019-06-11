package io.terminus.dalaran.core.resource;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.core.DalaranConstants;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.DalaranProcessorConfigCustomConverter;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerFlowConfigCustomConverter;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.ImmutableModelConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.config.ProcessorInfo;
import io.terminus.dalaran.core.config.ServiceInfo;
import io.terminus.dalaran.core.config.TriggerInfo;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.SubFlow;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import io.terminus.dalaran.core.model.DalaranModelSchema;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.resource.entity.*;
import io.terminus.dalaran.core.resource.entity.basic.BasicFlowEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.entity.released.ReleasedEntity;
import lombok.val;
import org.apache.commons.text.StringSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDalaranResourceBuilder implements DalaranResourceBuilder {

    private final DalaranResourceLoader resourceLoader;

    private final DalaranComponentContext componentContext;

    private final DalaranConverterContext converterContext;

    private final DalaranServiceContext serviceContext;

    public DefaultDalaranResourceBuilder(
            DalaranResourceLoader resourceLoader, DalaranComponentContext componentContext,
            DalaranConverterContext converterContext, DalaranServiceContext serviceContext
    ) {
        this.resourceLoader = resourceLoader;
        this.componentContext = componentContext;
        this.converterContext = converterContext;
        this.serviceContext = serviceContext;
    }

    @Override
    public BasicFlow buildTestFlow(BasicFlowEntity flowEntity) {
        BasicFlow flow = new BasicFlow();
        buildFlow(flow, flowEntity);
        return flow;
    }

    @Override
    public TriggerFlow buildTriggerFlow(TriggerFlowAbstractEntity triggerFlowEntity) {
        TriggerFlow flow = new TriggerFlow();
        DalaranTrigger triggerBean = componentContext.getTrigger(triggerFlowEntity.getTriggerType());
        TriggerInfo triggerInfo = componentContext.getTriggerInfo(triggerFlowEntity.getTriggerType());
        buildFlow(flow, triggerFlowEntity);
        flow.setTriggerType(triggerFlowEntity.getTriggerType());

        Object config = buildConfig(triggerFlowEntity.getTriggerConfig(), triggerInfo.getConfigType());
        if (triggerBean instanceof DalaranTriggerFlowConfigCustomConverter) {
            config = ((DalaranTriggerFlowConfigCustomConverter) triggerBean).convert(config, flow);
        }

        flow.setTriggerConfig(config);

        if (config instanceof ConnectorConfig) {
            ConnectorConfig connectorConfig = (ConnectorConfig) config;
            Long connectorId = connectorConfig.getConnectorId();
            if (connectorId != null) {
                Object connector = buildConnectorConfig(connectorId, triggerInfo.getConnectorInfo().getConnectorType());
                connectorConfig.setConnector(connector);
            }
        }
        return flow;
    }

    @Override
    public SubFlow buildSubFlow(SubFlowAbstractEntity subFlowEntity) {
        SubFlow flow = new SubFlow();
        buildFlow(flow, subFlowEntity);
        return flow;
    }

    @Override
    public MessageModel buildModel(Long modelId) {
        return buildModel(resourceLoader.loadModel(modelId));
    }

    @Override
    public MessageModel buildModel(ModelAbstractEntity modelEntity) {
        val model = new MessageModel();
        val modelType = modelEntity.getType();
        model.setModelType(modelType);
        Class<? extends DalaranModelSchema> schemaType = converterContext.getSchemaType(modelType);
        DalaranModelSchema modelSchema = buildConfig(modelEntity.getModelSchema(), schemaType);
        model.setModelSchema(modelSchema);
        return model;
    }

    @Override
    public Object buildConnectorConfig(Long connectorId, Class connectorConfigType) {
        ConnectorAbstractEntity entity = resourceLoader.loadConnector(connectorId);
        return buildConfig(entity.getConfig(), connectorConfigType);
    }

    @Override
    public Object buildServiceConfig(ServiceAbstractEntity serviceEntity) {
        ServiceInfo serviceInfo = serviceContext.getServiceInfo(serviceEntity.getType());
        return buildConfig(serviceEntity.getServiceConfig(), serviceInfo.getServiceConfigType());
    }

    private void buildFlow(BasicFlow flow, BasicFlowEntity flowEntity) {
        if (flowEntity instanceof ReleasedEntity) {
            flow.setId(((ReleasedEntity) flowEntity).getOriginId());
        } else {
            flow.setId(flowEntity.getId());
        }
        flow.setInModel(buildModel(flowEntity.getInModel()));
        flow.setOutModel(buildModel(flowEntity.getOutModel()));

        List<ProcessorModel> pipeline = new ArrayList<>();
        MessageModel lastOutModel = flow.getInModel();
        for (ProcessorEntity processorEntity : flowEntity.getPipeline()) {
            ProcessorModel processor = buildProcessorModel(processorEntity, lastOutModel, flow);
            pipeline.add(processor);
            if (processor.getOutModel() != null) {
                lastOutModel = processor.getOutModel();
            }
        }
        flow.setPipeline(pipeline);
    }

    @Override
    public ProcessorModel buildProcessorModel(ProcessorEntity processorEntity, MessageModel lastOutModel, BasicFlow flow) {
        ProcessorModel processor = new ProcessorModel();
        processor.setId(processorEntity.getId());
        processor.setType(processorEntity.getType());

        ProcessorInfo processorInfo = componentContext.getProcessorInfo(processorEntity.getType());
        if (processorInfo == null) {
            return processor;
        }
        Object config = buildConfig(processorEntity.getConfig(), processorInfo.getConfigType());
        if (config == null) {
            return processor;
        }

        processor.setInModel(lastOutModel);
        MessageModel outModel = injectModel(config, lastOutModel);
        processor.setOutModel(outModel);

        DalaranProcessor processorBean = componentContext.getProcessor(processorEntity.getType());

        if (processorBean instanceof DalaranProcessorConfigCustomConverter) {
            config = ((DalaranProcessorConfigCustomConverter) processorBean).convert(config, processor, flow);
        }
        if (config instanceof ConnectorConfig) {
            ConnectorConfig connectorConfig = (ConnectorConfig) config;
            Long connectorId = connectorConfig.getConnectorId();
            if (connectorId != null) {
                Object connector = buildConnectorConfig(connectorId, processorInfo.getConnectorInfo().getConnectorType());
                connectorConfig.setConnector(connector);
            }
        }
        processor.setConfig(config);

        return processor;
    }

    private MessageModel injectModel(Object config, MessageModel lastOutModel) {
        if (config instanceof OutModelConfig) {
            OutModelConfig outModelConfig = (OutModelConfig) config;
            outModelConfig.setInModel(lastOutModel);
            if (outModelConfig.getOutModelId() != null) {
                lastOutModel = buildModel(outModelConfig.getOutModelId());
                outModelConfig.setOutModel(lastOutModel);
            }
        } else if (config instanceof ImmutableModelConfig) {
            lastOutModel = ((ImmutableModelConfig) config).getOutModel();
        }
        if (config instanceof AllModelConfig) {
            AllModelConfig allModelConfig = (AllModelConfig) config;
            allModelConfig.setInModel(buildModel(allModelConfig.getInModelId()));
            if (allModelConfig.getOutModelId() != null) {
                lastOutModel = buildModel(allModelConfig.getOutModelId());
                allModelConfig.setOutModel(lastOutModel);
            }
        }
        return lastOutModel;
    }

    private <T> T buildConfig(String configValue, Class<T> configType) {
        String replacedConfig = replaceProperties(configValue, getProperties());
        return JSON.parseObject(replacedConfig, configType);
    }

    private String replaceProperties(String configValue, Map<String, String> properties) {
        StringSubstitutor stringSubstitutor = new StringSubstitutor(properties, DalaranConstants.ENV_REPLACE_PREFIX, DalaranConstants.ENV_REPLACE_SUFFIX);
        return stringSubstitutor.replace(configValue);
    }

    // TODO cache
    private Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>(System.getenv());
        for (PropertyAbstractEntity propertyEntity : resourceLoader.loadAllProperties()) {
            properties.put(propertyEntity.getName(), propertyEntity.getValue());
        }
        return properties;
    }
}
