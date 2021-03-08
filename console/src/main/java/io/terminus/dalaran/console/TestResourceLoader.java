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
    private LimiterRepository limiterRepository;

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
        return triggerFlowRepository.findByIsExistTrue();
    }

    @Override
    public List<SubFlowEntity> loadAllSubFlow() {
        return subFlowRepository.findByIsExistTrue();
    }

    @Override
    public List<TriggerFlowEntity> loadAvailableTriggerFlow() {
        return triggerFlowRepository.findByStatusNotAndIsExistTrue(FlowStatus.Error);
    }

    @Override
    public List<TriggerFlowEntity> loadLastVersionAvailableTriggerFlow() {
        return triggerFlowRepository.findByStatusNotAndIsExistTrue(FlowStatus.Error);
    }

    @Override
    public List<SubFlowEntity> loadAvailableSubFlow() {
        return subFlowRepository.findByStatusNotAndIsExistTrue(FlowStatus.Error);
    }

    @Override
    public List<SubFlowEntity> loadLastVersionAvailableSubFlow() {
        return subFlowRepository.findByStatusNotAndIsExistTrue(FlowStatus.Error);
    }

    @Override
    public List<TriggerFlowEntity> loadAvailableTriggerFlowByTriggerType(String triggerType) {
        return triggerFlowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, triggerType);
    }

    @Override
    public List<PropertyEntity> loadAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public List<FunctionEntity> loadAllFunctions() {
        return functionRepository.findByIsExistTrue();
    }

    @Override
    public List<ClientEntity> loadAllClient() {
        return clientRepository.findByIsExistTrue();
    }

    @Override
    public TriggerFlowEntity loadTriggerFlow(String triggerFlowId) {
        return triggerFlowRepository.findByResourceKey(triggerFlowId);
    }

    @Override
    public SubFlowEntity loadSubFlow(String subFlowId) {
        return subFlowRepository.findByResourceKey(subFlowId);
    }

    @Override
    public ModelEntity loadModel(String modelId) {
        return modelRepository.findByResourceKey(modelId);
    }

    @Override
    public ConnectorEntity loadConnector(String connectorId) {
        return connectorRepository.findByResourceKey(connectorId);
    }

    @Override
    public LimiterEntity loadLimiter(String limiterId) {
        return limiterRepository.findByResourceKey(limiterId);
    }

    @Override
    public ServiceEntity loadService(String serviceId) {
        return serviceRepository.findByResourceKey(serviceId);
    }
}
