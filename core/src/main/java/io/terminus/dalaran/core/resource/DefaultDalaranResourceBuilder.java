package io.terminus.dalaran.core.resource;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.core.component.DalaranComponentConfigConverter;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.config.AllModelConfig;
import io.terminus.dalaran.core.component.config.ComponentModelConfig;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.config.ProcessorInfo;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.flow.model.BasicFlow;
import io.terminus.dalaran.core.flow.model.SubFlow;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import io.terminus.dalaran.core.model.DalaranModelSchema;
import io.terminus.dalaran.core.model.MessageModel;
import io.terminus.dalaran.core.resource.entity.*;
import io.terminus.dalaran.core.resource.entity.basic.BasicFlowEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
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

    public DefaultDalaranResourceBuilder(DalaranResourceLoader resourceLoader, DalaranComponentContext componentContext, DalaranConverterContext converterContext) {
        this.resourceLoader = resourceLoader;
        this.componentContext = componentContext;
        this.converterContext = converterContext;
    }

    @Override
    public TriggerFlow buildTriggerFlow(TriggerFlowAbstractEntity triggerFlowEntity) {
        TriggerFlow flow = new TriggerFlow();
        buildFlow(flow, triggerFlowEntity);
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
    public Object buildServiceConfig(Long serviceId) {
        return null;
    }

    private void buildFlow(BasicFlow flow, BasicFlowEntity flowEntity) {
        flow.setId(flowEntity.getId());
        flow.setInModel(buildModel(flowEntity.getInModel()));
        flow.setOutModel(buildModel(flowEntity.getOutModel()));

        List<ProcessorModel> pipeline = new ArrayList<>();
        MessageModel lastOutModel = flow.getInModel();
        for (ProcessorEntity processorEntity : flowEntity.getPipeline()) {
            ProcessorModel processor = buildProcessorModel(processorEntity, lastOutModel, flow);
            pipeline.add(processor);
            lastOutModel = processor.getOutModel();
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
        if (processorBean instanceof DalaranComponentConfigConverter) {
            config = ((DalaranComponentConfigConverter) processorBean).convert(config, processor, flow);
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
        }
        if (config instanceof AllModelConfig) {
            AllModelConfig allModelConfig = (AllModelConfig) config;
            allModelConfig.setInModel(buildModel(allModelConfig.getInModelId()));
            allModelConfig.setOutModel(buildModel(allModelConfig.getOutModelId()));
        }
        if (config instanceof ComponentModelConfig) {
            lastOutModel = ((ComponentModelConfig) config).getOutModel();
        }
        return lastOutModel;
    }

    private <T> T buildConfig(String configValue, Class<T> configType) {
        String replacedConfig = replaceProperties(configValue, getProperties());
        return JSON.parseObject(replacedConfig, configType);
    }

    private String replaceProperties(String configValue, Map<String, String> properties) {
        StringSubstitutor stringSubstitutor = new StringSubstitutor(properties, "${{", "}}");
        return stringSubstitutor.replace(configValue);
    }

    // TODO cache
    private Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>(System.getenv());
        for (PropertyAbstractEntity propertyEntity : resourceLoader.loadAllProperties()) {
            properties.put(propertyEntity.getName(), propertyEntity.getValue());
        }
        properties.putAll(System.getenv());
        return properties;
    }
}
