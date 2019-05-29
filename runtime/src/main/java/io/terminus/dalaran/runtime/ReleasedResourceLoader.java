package io.terminus.dalaran.runtime;

import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.resource.entity.ServiceAbstractEntity;
import io.terminus.dalaran.core.resource.entity.released.*;
import io.terminus.dalaran.core.resource.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ReleasedResourceLoader implements DalaranResourceLoader {

    private String version;

    @Autowired
    private TriggerFlowReleasedRepository releasedTriggerFlowRepository;

    @Autowired
    private SubFlowReleasedRepository releasedSubFlowRepository;

    @Autowired
    private ModelReleasedRepository modelRepository;

    @Autowired
    private ConnectorReleasedRepository connectorRepository;

    @Autowired
    private PropertyReleasedRepository propertyRepository;

    @Autowired
    private ServiceReleasedRepository serviceRepository;

    @Override
    public List<TriggerFlowReleasedEntity> loadAllTriggerFlow() {
        return releasedTriggerFlowRepository.findByVersion(version);
    }

    @Override
    public List<SubFlowReleasedEntity> loadAllSubFlow() {
        return releasedSubFlowRepository.findByVersion(version);
    }

    @Override
    public List<PropertyReleasedEntity> loadAllProperties() {
        return propertyRepository.findByVersion(version);
    }

    @Override
    public TriggerFlowReleasedEntity loadTriggerFlow(Long triggerFlowId) {
        return releasedTriggerFlowRepository.findByVersionAndOriginId(version, triggerFlowId);
    }

    @Override
    public SubFlowReleasedEntity loadSubFlow(Long subFlowId) {
        return releasedSubFlowRepository.findByVersionAndOriginId(version, subFlowId);
    }

    @Override
    public ModelReleasedEntity loadModel(Long modelId) {
        return modelRepository.findByVersionAndOriginId(version, modelId);
    }

    @Override
    public ConnectorReleasedEntity loadConnector(Long connectorId) {
        return connectorRepository.findByVersionAndOriginId(version, connectorId);
    }

    @Override
    public ServiceAbstractEntity loadService(Long serviceId) {
        return serviceRepository.findByVersionAndOriginId(version, serviceId);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
