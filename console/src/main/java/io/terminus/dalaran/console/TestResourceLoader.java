package io.terminus.dalaran.console;

import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.model.flow.FlowStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class TestResourceLoader implements DalaranResourceLoader {

    @Autowired
    private TriggerFlowRepository triggerFlowRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DalaranContext dalaranContext;

    @Override
    public List<TriggerFlowEntity> loadAllTriggerFlow() {
        return triggerFlowRepository.findAll();
    }

    @Override
    public List<SubFlowEntity> loadAllSubFlow() {
        return subFlowRepository.findAll();
    }

    @Override
    public List<TriggerFlowEntity> loadAvailableTriggerFlow() {
        return triggerFlowRepository.findByStatusNot(FlowStatus.Error);
    }

    @Override
    public List<SubFlowEntity> loadAvailableSubFlow() {
        return subFlowRepository.findByStatusNot(FlowStatus.Error);
    }

    @Override
    public List<TriggerFlowEntity> loadAvailableTriggerFlowByTriggerType(String triggerType) {
        return triggerFlowRepository.findByStatusNotAndTriggerType(FlowStatus.Error, triggerType);
    }

    @Override
    public List<PropertyEntity> loadAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public List<FunctionEntity> loadAllFunctions() {
        return functionRepository.findAll();
    }

    @Override
    public List<ClientEntity> loadAllClient() {
        return clientRepository.findAll();
    }

    @Override
    public TriggerFlowEntity loadTriggerFlow(Long triggerFlowId) {
        return triggerFlowRepository.findOne(triggerFlowId);
    }

    @Override
    public SubFlowEntity loadSubFlow(Long subFlowId) {
        return subFlowRepository.findOne(subFlowId);
    }

    @Override
    public ModelEntity loadModel(Long modelId) {
        return modelRepository.findOne(modelId);
    }

    @Override
    public ConnectorEntity loadConnector(Long connectorId) {
        return connectorRepository.findOne(connectorId);
    }

    @Override
    public ServiceEntity loadService(Long serviceId) {
        return serviceRepository.findOne(serviceId);
    }
}
