package io.terminus.dalaran.core.resource;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.authenticator.AuthenticatorBasic;
import io.terminus.dalaran.component.authenticator.AuthenticatorConfigType;
import io.terminus.dalaran.component.authenticator.AuthenticatorSign;
import io.terminus.dalaran.config.*;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.Authenticator;
import io.terminus.dalaran.core.component.config.*;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.resource.entity.*;
import io.terminus.dalaran.core.resource.entity.basic.BasicFlowEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.entity.released.ReleasedEntity;
import io.terminus.dalaran.core.util.ConfigFieldUtils;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
//        flow.setInModel(buildModel(triggerFlowEntity.getInModel()));
//        flow.setOutModel(buildModel(triggerFlowEntity.getOutModel()));
        Object config = buildConfig(triggerFlowEntity.getTriggerConfig(), triggerInfo.getConfigType());

        flow.setTriggerConfig(config);

        if (config instanceof ConnectorConfig) {
            ConnectorConfig connectorConfig = (ConnectorConfig) config;
            String connectorId = connectorConfig.getConnectorId();
            if (StringUtils.isNotBlank(connectorId)) {
                Object connector = buildConnectorConfig(connectorId, triggerInfo.getConnectorInfo().getClassType());
                connectorConfig.setConnector(connector);
            }
        }

        if (config instanceof LimiterConfig) {
            LimiterConfig limiterConfig = (LimiterConfig) config;
            String limiterId = limiterConfig.getLimiterId();
            if (StringUtils.isNotBlank(limiterId)) {
                Object limiter = buildLimiterConfig(limiterId, triggerInfo.getLimiterInfo().getClassType());
                limiterConfig.setLimiter(limiter);
            }
        }

        if (config instanceof AuthenticatorConfig) {
            AuthenticatorConfig authenticatorConfig = (AuthenticatorConfig) config;
            String authenticatorId = authenticatorConfig.getAuthenticatorId();
            if (StringUtils.isNotBlank(authenticatorId)) {
//                AuthenticatorInfo authenticatorInfo = buildAuthenticatorInfo(authenticatorId);
                Object authenticator = buildAuthenticatorConfig(authenticatorId, AuthenticatorConfigType.class);
                authenticatorConfig.setAuthenticator(authenticator);
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
    public FlowFragment buildFlowFragment(List<ProcessorEntity> pipelineEntityList, MessageModel inModel, MessageModel outModel, String flowId, String fragmentId, Boolean tracing) {
        MessageModel fragmentLastOutModel = outModel;
        List<ProcessorModel> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : pipelineEntityList) {
            val processorModel = buildProcessorModel(processorEntity, fragmentLastOutModel);
            fragmentLastOutModel = processorModel.getOutModel();
            pipeline.add(processorModel);
        }

        FlowFragment fragment = new FlowFragment();
        fragment.setId(String.valueOf(flowId));
        fragment.setFragmentId(fragmentId);
        fragment.setPipeline(pipeline);
        fragment.setInModel(inModel);
        fragment.setOutModel(fragmentLastOutModel);
        fragment.setTracing(tracing);
        return fragment;
    }

    @Override
    public MessageModel buildModel(String modelId) {
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
    public Object buildConnectorConfig(String connectorId, Class connectorConfigType) {
        ConnectorAbstractEntity entity = resourceLoader.loadConnector(connectorId);
        if (entity == null) {
            // TODO throw ConnectorNotFound
            throw new RuntimeException("connector [" + connectorId + "] not found");
        }
        return buildConfig(entity.getConfig(), connectorConfigType);
    }

    @Override
    public Object buildLimiterConfig(String limiterId, Class limiterConfigType) {
        LimiterAbstractEntity entity = resourceLoader.loadLimiter(limiterId);
        if (entity == null) {
            throw new RuntimeException("limiter [" + limiterId + "] not found");
        }
        return buildConfig(entity.getConfig(), limiterConfigType);
    }

    @Override
    public Object buildAuthenticatorConfig(String authenticatorId, Class authenticatorConfigType) {
        AuthenticatorAbstractEntity entity = resourceLoader.loadAuthenticator(authenticatorId);
        if (entity == null) {
            throw new RuntimeException("authenticator [" + authenticatorId + "] not found");
        }
        return buildArrayConfig(entity.getConfig(), authenticatorConfigType, entity.getAuthenticatorType());
    }

    @Override
    public Object buildServiceConfig(ServiceAbstractEntity serviceEntity) {
        ServiceInfo serviceInfo = serviceContext.getServiceInfo(serviceEntity.getType());
        return buildConfig(serviceEntity.getServiceConfig(), serviceInfo.getServiceConfigType());
    }

    private void buildFlow(BasicFlow flow, BasicFlowEntity flowEntity) {
        if (flowEntity instanceof ReleasedEntity) {
            flow.setId(String.valueOf(((ReleasedEntity) flowEntity).getOriginId()));
        } else {
            flow.setId(String.valueOf(flowEntity.getResourceKey()));
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
        processor.setGroup(processorEntity.getGroup());
        processor.setVersion(processorEntity.getVersion());

        ProcessorInfo processorInfo = componentContext.getProcessorInfo(processorEntity.getGroup(), processorEntity.getType(), processorEntity.getVersion());
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
            String connectorId = connectorConfig.getConnectorId();
            if (StringUtils.isNotBlank(connectorId)) {
                Object connector = buildConnectorConfig(connectorId, processorInfo.getConnectorInfo().getClassType());
                connectorConfig.setConnector(connector);
            }
        }
        if (config instanceof ServiceOperationConfig) {
            ServiceOperationConfig serviceOperationConfig = (ServiceOperationConfig) config;
            String serviceId = null;
            if(StringUtils.isNotBlank(serviceOperationConfig.getServiceId())) {
                serviceId =  serviceOperationConfig.getServiceId();
            }
            if (StringUtils.isNotBlank(serviceId)) {
                ServiceOperation service = buildService(serviceId, serviceOperationConfig.getOperation());
                serviceOperationConfig.setInModel(buildModel(service.getInModelId()));
                serviceOperationConfig.setOutModel(buildModel(service.getOutModelId()));
            }
        }
        MessageModel outModel = injectModel(config, lastOutModel);
        processor.setOutModel(outModel);
        processor.setConfig(config);
        return processor;
    }

    @Override
    public  <T> T buildConfig(String configValue, Class<T> configType) {
        String replacedConfig = replaceProperties(configValue, getProperties());
        return JSON.parseObject(replacedConfig, configType);
    }

    public  <T> T buildArrayConfig(String configValue, Class<T> configType, String type) {
        String replacedConfig = replaceProperties(configValue, getProperties());
        AuthenticatorConfigType authenticator = new AuthenticatorConfigType();
        if (type.equals("BasicAuthenticator")) {
            List<AuthenticatorBasic> configs = JSON.parseArray(replacedConfig, AuthenticatorBasic.class);
            authenticator.setConfig(configs);
        } else if (type.equals("SignAuthenticator")) {
            List<AuthenticatorSign> configs = JSON.parseArray(replacedConfig, AuthenticatorSign.class);
            authenticator.setConfig(configs);
        }
        authenticator.setType(type);
        return JSON.parseObject(JSON.toJSONString(authenticator), configType);
    }

    private MessageModel injectModel(Object config, MessageModel lastOutModel) {
        if (config instanceof OutModelConfig) {
            OutModelConfig outModelConfig = (OutModelConfig) config;
            outModelConfig.setInModel(lastOutModel);
            if (StringUtils.isNotBlank(outModelConfig.getOutModelId())) {
                lastOutModel = buildModel(outModelConfig.getOutModelId());
                outModelConfig.setOutModel(lastOutModel);
            }
        } else if (config instanceof ImmutableModelConfig) {
            lastOutModel = ((ImmutableModelConfig) config).getOutModel();
        }
        if (config instanceof AllModelConfig) {
            AllModelConfig allModelConfig = (AllModelConfig) config;
            if(StringUtils.isNotBlank(allModelConfig.getInModelId())) {
                allModelConfig.setInModel(buildModel(allModelConfig.getInModelId()));
            }
            if (StringUtils.isNotBlank(allModelConfig.getOutModelId())) {
                lastOutModel = buildModel(allModelConfig.getOutModelId());
                allModelConfig.setOutModel(lastOutModel);
            }
        }
        if (config instanceof AllImmutableModelConfig) {
            AllImmutableModelConfig allImmutableModelConfig = (AllImmutableModelConfig) config;
            if(StringUtils.isNotBlank(allImmutableModelConfig.getInModelId())) {
                allImmutableModelConfig.setInModel(buildModel(allImmutableModelConfig.getInModelId()));
            }
            if (StringUtils.isNotBlank(allImmutableModelConfig.getOutModelId())) {
                lastOutModel = buildModel(allImmutableModelConfig.getOutModelId());
                allImmutableModelConfig.setOutModel(lastOutModel);
            }
        }
        if (config instanceof ImmutableInModelConfig) {
            ImmutableInModelConfig immutableInModelConfig = (ImmutableInModelConfig) config;
            if(StringUtils.isNotBlank(immutableInModelConfig.getInModelId())) {
                immutableInModelConfig.setInModel(buildModel(immutableInModelConfig.getInModelId()));
            }
            if (StringUtils.isNotBlank(immutableInModelConfig.getOutModelId())) {
                lastOutModel = buildModel(immutableInModelConfig.getOutModelId());
                immutableInModelConfig.setOutModel(lastOutModel);
            }
        }
        return lastOutModel;
    }



    private String replaceProperties(String configValue, Map<String, String> properties) {
        StringSubstitutor stringSubstitutor = new StringSubstitutor(properties, DalaranConstants.ENV_REPLACE_PREFIX, DalaranConstants.ENV_REPLACE_SUFFIX);
        return stringSubstitutor.replace(configValue);
    }

    private ServiceOperation buildService(String serviceId, String operationKey) {
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
