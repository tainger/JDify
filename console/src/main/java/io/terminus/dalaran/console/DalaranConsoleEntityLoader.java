package io.terminus.dalaran.console;

import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranEntityLoader;
import io.terminus.dalaran.entity.basic.*;
import io.terminus.dalaran.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

public class DalaranConsoleEntityLoader implements DalaranEntityLoader {

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
    private DalaranContext dalaranContext;


    @Override
    public TriggerFlowAbstractEntity loadTriggerFlow(Long triggerFlowId) {
        return triggerFlowRepository.findOne(triggerFlowId);
    }

    @Override
    public SubFlowAbstractEntity loadSubFlow(Long subFlowId) {
        return subFlowRepository.findOne(subFlowId);
    }

    @Override
    public ModelAbstractEntity loadModel(Long modelId) {
        return modelRepository.findOne(modelId);
    }

    @Override
    public ConnectorAbstractEntity loadConnector(Long connectorId) {
        return connectorRepository.findOne(connectorId);
    }

    @Override
    public ServiceAbstractEntity loadService(Long serviceId) {
        return serviceRepository.findOne(serviceId);
    }
}
