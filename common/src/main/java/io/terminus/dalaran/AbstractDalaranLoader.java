package io.terminus.dalaran;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.processor.retry.RetryConfig;
import io.terminus.dalaran.component.processor.route.DalaranRouterConfig;
import io.terminus.dalaran.config.AllModelConfig;
import io.terminus.dalaran.config.OutModelConfig;
import io.terminus.dalaran.config.ServiceOperationConfig;
import io.terminus.dalaran.entity.BasicFlowEntity;
import io.terminus.dalaran.entity.basic.*;
import io.terminus.dalaran.entity.manage.ProcessorEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.ServiceOperation;
import io.terminus.dalaran.model.config.ComponentInfo;
import io.terminus.dalaran.model.config.ConnectorInfo;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.ServiceInfo;
import io.terminus.dalaran.model.flow.BasicFlow;
import io.terminus.dalaran.model.flow.FlowFragment;
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

import static io.terminus.dalaran.DalaranConstants.DELIMITER;
import static io.terminus.dalaran.DalaranConstants.DIRECT_PREFIX;

// TODO 这里整体还是有点乱
@Slf4j
public abstract class AbstractDalaranLoader<TriggerFlowEntity extends TriggerFlowAbstractEntity, SubFlowEntity extends SubFlowAbstractEntity>
        implements DalaranEntityLoader<TriggerFlowEntity, SubFlowEntity> {

    @Autowired
    private DalaranContext dalaranContext;

    private Map<String, String> properties;

    protected abstract ConnectorAbstractEntity getConnector(Long connectorId);

    protected abstract ModelAbstractEntity getModelEntity(Long modelId);

    protected abstract PropertyAbstractEntity[] getPropertyEntities();

    protected abstract ServiceAbstractEntity getServiceEntity(Long serviceId);

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
            ProcessorModel processor = new ProcessorModel();
            processor.setId(processorEntity.getId());
            processor.setType(processorEntity.getType());
            pipeline.add(processor);

            lastOutModel = buildProcessorModel(processor, processorEntity.getConfig(), lastOutModel, flowEntity.getId());
        }

        // TODO for test...
        flow.setPipeline(pipeline);
    }

    private MessageModel buildProcessorModel(ProcessorModel processor, Object processorConfig, MessageModel lastOutModel, Long flowId) {
        // TODO 这里太爆炸了, 需要抽出去, 各个分片也独立一下
        // TODO 这里真的是太丑了, 最好能揉到 processor 内, 搞一个独立的 interface, 允许自行处理 config 的转化, 难处理的是对 db 的读操作
        if (processorConfig instanceof ServiceOperationConfig) {
            ServiceOperationConfig serviceOperationConfig = (ServiceOperationConfig) processorConfig;
            ServiceAbstractEntity serviceEntity = getServiceEntity(serviceOperationConfig.getServiceId());
            ServiceInfo serviceInfo = dalaranContext.getDalaranServiceContext().getServiceInfo(serviceEntity.getType());
            Object serviceConfig = buildConfig(serviceEntity.getServiceConfig(), serviceInfo.getServiceConfigType());
            ServiceOperation operationConfig = dalaranContext.getDalaranServiceContext()
                    .buildOperationConfig(serviceEntity.getType(), serviceConfig, serviceOperationConfig);
            serviceOperationConfig.setServiceType(serviceEntity.getType());
            serviceOperationConfig.setOperationConfig(operationConfig);
            serviceOperationConfig.setInModel(operationConfig.getInModel());
            serviceOperationConfig.setOutModel(operationConfig.getOutModel());
        } else if (processorConfig instanceof DalaranRouterConfig) {
            DalaranRouterConfig routerConfig = (DalaranRouterConfig) processorConfig;
            routerConfig.setInModel(lastOutModel);
            val outModel = routerConfig.getOutModel();
            List<DalaranRouterConfig.Route> routes = routerConfig.getRoutes();
            for (int i = 0; i < routes.size(); i++) {
                val route = routes.get(i);
                String fragmentId = processor.getId() + DELIMITER + i;
                FlowFragment fragment = new FlowFragment();

                MessageModel fragmentLastOutModel = lastOutModel;
                for (ProcessorModel processorModel : route.getPipeline()) {
                    fragmentLastOutModel = buildProcessorModel(processorModel, (String) processorModel.getConfig(), fragmentLastOutModel, flowId);
                }

                fragment.setId(flowId);
                fragment.setFragmentId(fragmentId);
                fragment.setPipeline(route.getPipeline());
                fragment.setInModel(lastOutModel);
                fragment.setOutModel(outModel);

                route.setFragmentUri(DIRECT_PREFIX + fragment.getRouteId());

                dalaranContext.addFragmentFlow(fragment);
            }
            lastOutModel = outModel;
        } else if (processorConfig instanceof RetryConfig) {
            RetryConfig retryConfig = ((RetryConfig) processorConfig);
            FlowFragment fragment = new FlowFragment();

            MessageModel fragmentLastOutModel = lastOutModel;

            List<ProcessorModel> pipeline = retryConfig.getPipeline();
            for (ProcessorModel processorModel : pipeline) {
                fragmentLastOutModel = buildProcessorModel(processorModel, (String) processorModel.getConfig(), fragmentLastOutModel, flowId);
            }

            ProcessorModel<RetryConfig> retryFragmentProcessorModel = new ProcessorModel<>();
            retryFragmentProcessorModel.setId(processor.getId());
            retryFragmentProcessorModel.setType("retry-fragment");
            retryFragmentProcessorModel.setConfig(retryConfig);
            pipeline.add(retryFragmentProcessorModel);

            fragment.setId(flowId);
            fragment.setFragmentId(processor.getId());
            fragment.setPipeline(pipeline);
            fragment.setInModel(lastOutModel);
            fragment.setOutModel(fragmentLastOutModel);

            retryConfig.setFragmentUri(DIRECT_PREFIX + fragment.getRouteId());

            dalaranContext.addFragmentFlow(fragment);
        } else if (processorConfig instanceof OutModelConfig) {
            ((OutModelConfig) processorConfig).setInModel(lastOutModel);
            lastOutModel = ((OutModelConfig) processorConfig).getOutModel();
        }
        processor.setConfig(processorConfig);

        return lastOutModel;
    }

    private MessageModel buildProcessorModel(ProcessorModel processor, String processorConfigJson, MessageModel lastOutModel, Long flowId) {
        ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processor.getType());
        Object config = buildConfig(processorInfo, processorConfigJson);
        if (config == null) {
            return lastOutModel;
        }
        return buildProcessorModel(processor, config, lastOutModel, flowId);
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
