package io.terminus.dalaran.core.resource;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.config.ServiceInfo;
import io.terminus.dalaran.config.TriggerInfo;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.config.*;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.resource.entity.*;
import io.terminus.dalaran.core.resource.entity.basic.BasicFlowEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.entity.released.ReleasedEntity;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.val;
import org.apache.commons.text.StringSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDalaranResourceBuilder implements DalaranResourceBuilder {

    private final DalaranResourceLoader resourceLoader;

    private final DalaranComponentContext componentContext;

    private final DalaranModelTypeContext converterContext;

    private final DalaranServiceContext serviceContext;

    public DefaultDalaranResourceBuilder(
            DalaranResourceLoader resourceLoader, DalaranComponentContext componentContext,
            DalaranModelTypeContext converterContext, DalaranServiceContext serviceContext
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
        TriggerInfo triggerInfo = componentContext.getTriggerInfo(triggerFlowEntity.getTriggerType());
        buildFlow(flow, triggerFlowEntity);
        flow.setName(triggerFlowEntity.getName());
        flow.setDescription(triggerFlowEntity.getDescription());
        flow.setTracing(triggerFlowEntity.isTracing());
        flow.setTriggerType(triggerFlowEntity.getTriggerType());


        Object config = buildConfig(triggerFlowEntity.getTriggerConfig(), triggerInfo.getConfigType());

        flow.setTriggerConfig(config);

        if (config instanceof ConnectorConfig) {
            ConnectorConfig connectorConfig = (ConnectorConfig) config;
            Long connectorId = connectorConfig.getConnectorId();
            if (connectorId != null) {
                Object connector = buildConnectorConfig(connectorId, triggerInfo.getConnectorInfo().getClassType());
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
    public FlowFragment buildFlowFragment(List<ProcessorEntity> pipelineEntityList, MessageModel inModel, MessageModel outModel, Long flowId, String fragmentId, Boolean tracing) {
        MessageModel fragmentLastOutModel = outModel;
        List<ProcessorModel> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : pipelineEntityList) {
            val processorModel = buildProcessorModel(processorEntity, fragmentLastOutModel);
            fragmentLastOutModel = processorModel.getOutModel();
            pipeline.add(processorModel);
        }

        FlowFragment fragment = new FlowFragment();
        fragment.setId(flowId);
        fragment.setFragmentId(fragmentId);
        fragment.setPipeline(pipeline);
        fragment.setInModel(inModel);
        fragment.setOutModel(fragmentLastOutModel);
        fragment.setTracing(tracing);
        return fragment;
    }

    @Override
    public MessageModel buildModel(Long modelId) {
        if (modelId != null) {
            return buildModel(resourceLoader.loadModel(modelId));
        }
        return null;
    }

    @Override
    public MessageModel buildModel(ModelAbstractEntity modelEntity) {
        if (modelEntity != null) {
            val model = new MessageModel();
            String modelType = modelEntity.getType();
            model.setModelType(modelType);
            model.setName(modelEntity.getName());
            Class<? extends DalaranModelSchema> schemaType = converterContext.getModelSchema(modelType);
            DalaranModelSchema modelSchema = buildConfig(modelEntity.getModelSchema(), schemaType);
            model.setModelSchema(modelSchema);
            return model;
        }
        return null;
    }

    @Override
    public Object buildConnectorConfig(Long connectorId, Class connectorConfigType) {
        ConnectorAbstractEntity entity = resourceLoader.loadConnector(connectorId);
        if (entity == null) {
            // TODO throw ConnectorNotFound
            throw new RuntimeException("connector [" + connectorId + "] not found");
        }
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
        flow.setModuleId(flowEntity.getModuleId());

        List<ProcessorModel> pipeline = new ArrayList<>();
        MessageModel lastOutModel = flow.getInModel();
        for (ProcessorEntity processorEntity : flowEntity.getPipeline()) {
            ProcessorModel processor = buildProcessorModel(processorEntity, lastOutModel);
            pipeline.add(processor);
            if (processor.getOutModel() != null) {
                lastOutModel = processor.getOutModel();
            }
        }
        flow.setPipeline(pipeline);
    }

    @Override
    public ProcessorModel buildProcessorModel(ProcessorEntity processorEntity, MessageModel lastOutModel) {
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

        if (config instanceof ConnectorConfig) {
            ConnectorConfig connectorConfig = (ConnectorConfig) config;
            Long connectorId = connectorConfig.getConnectorId();
            if (connectorId != null) {
                Object connector = buildConnectorConfig(connectorId, processorInfo.getConnectorInfo().getClassType());
                connectorConfig.setConnector(connector);
            }
        }
        if (config instanceof ServiceOperationConfig) {
            ServiceOperationConfig serviceOperationConfig = (ServiceOperationConfig) config;
            Long serviceId = serviceOperationConfig.getServiceId();
            if (serviceId != null) {
                ServiceOperation service = buildService(serviceId, serviceOperationConfig.getOperation());
                serviceOperationConfig.setInModel(buildModel((service.getInModelId())));
                serviceOperationConfig.setOutModel(buildModel(service.getOutModelId()));
            }
        }
        MessageModel outModel = injectModel(config, lastOutModel);
        processor.setOutModel(outModel);
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
        if (config instanceof AllImmutableModelConfig) {
            AllImmutableModelConfig allImmutableModelConfig = (AllImmutableModelConfig) config;
            allImmutableModelConfig.setInModel(buildModel(allImmutableModelConfig.getInModelId()));
            if (allImmutableModelConfig.getOutModelId() != null) {
                lastOutModel = buildModel(allImmutableModelConfig.getOutModelId());
                allImmutableModelConfig.setOutModel(lastOutModel);
            }
        }
        if (config instanceof ImmutableInModelConfig) {
            ImmutableInModelConfig immutableInModelConfig = (ImmutableInModelConfig) config;
            immutableInModelConfig.setInModel(buildModel(immutableInModelConfig.getInModelId()));
            if (immutableInModelConfig.getOutModelId() != null) {
                lastOutModel = buildModel(immutableInModelConfig.getOutModelId());
                immutableInModelConfig.setOutModel(lastOutModel);
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

    private ServiceOperation buildService(Long serviceId, String operationKey) {
        ServiceAbstractEntity serviceAbstractEntity = resourceLoader.loadService(serviceId);
        if (serviceAbstractEntity == null) {
            throw new RuntimeException("service [" + serviceId + "] not found");
        }
        Object service = buildServiceConfig(serviceAbstractEntity);
        DalaranService dalaranService = serviceContext.getService(serviceAbstractEntity.getType());
        return dalaranService.getOperationConfig(service, operationKey);
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
