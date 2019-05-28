package io.terminus.dalaran;

import io.terminus.dalaran.entity.basic.*;
import io.terminus.dalaran.entity.manage.ProcessorEntity;
import io.terminus.dalaran.model.ConnectorModel;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ProcessorModel;
import io.terminus.dalaran.model.ServiceModel;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.TriggerFlow;

import java.util.List;
import java.util.stream.Collectors;

public class DatabaseResourceLoader implements DalaranResourceLoader {

    private DalaranEntityLoader entityLoader;

    private DalaranConfigBuilder configBuilder;

    @Override
    public List<TriggerFlow> loadAllTriggerFlow() {
        return entityLoader.getAllTriggerFlow();
    }

    @Override
    public List<SubFlow> loadAllSubFlow() {
        return entityLoader.getAllSubFlow();
    }

    @Override
    public TriggerFlow loadTriggerFlow(Long triggerFlowId) {
        TriggerFlowAbstractEntity entity = entityLoader.getTriggerFlow(triggerFlowId);

        TriggerFlow flow = new TriggerFlow();
        flow.setTriggerType(entity.getTriggerType());

        Object config = configBuilder.buildTriggerConfig(entity.getTriggerConfig(), entity.getTriggerType());
        flow.setTriggerConfig(config);

        flow.setPipeline(buildPipeline(entity.getPipeline()));
        return flow;
    }

    @Override
    public SubFlow loadSubFlow(Long subFlowId) {
        SubFlowAbstractEntity entity = entityLoader.getSubFlow(subFlowId);

        SubFlow flow = new SubFlow();
        flow.setPipeline(buildPipeline(entity.getPipeline()));
        return flow;
    }

    @Override
    public MessageModel loadModel(Long modelId) {
        ModelAbstractEntity entity = entityLoader.getModel(modelId);

        MessageModel model = new MessageModel();
        model.setModelType(entity.getType());
        model.setModelSchema(configBuilder.buildModelSchema(entity.getModelSchema(), entity.getType()));
        return model;
    }

    @Override
    public ConnectorModel loadConnector(Long connectorId) {
        ConnectorAbstractEntity entity = entityLoader.getConnector(connectorId);

        ConnectorModel connector = new ConnectorModel();
        connector.setConfig(configBuilder.buildConnectorConfig(entity.getConfig(), entity.getComponentType()));
        return connector;
    }

    @Override
    public ServiceModel loadService(Long serviceId) {
        ServiceAbstractEntity entity = entityLoader.getService(serviceId);

        ServiceModel service = new ServiceModel();
        service.setServiceConfig(configBuilder.buildServiceConfig(entity.getServiceConfig(), entity.getType()));
        return service;
    }

    private List<ProcessorModel> buildPipeline(List<ProcessorEntity> pipeline) {
        return pipeline.stream().map(this::buildProcessor).collect(Collectors.toList());
    }

    private ProcessorModel buildProcessor(ProcessorEntity processorEntity) {
        ProcessorModel model = new ProcessorModel();
        model.setId(processorEntity.getId());
        model.setType(processorEntity.getType());
        model.setConfig(configBuilder.buildProcessorConfig(processorEntity.getConfig(), processorEntity.getType()));
        return model;
    }
}
