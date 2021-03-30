package io.terminus.dalaran.runtime;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.entity.released.*;
import io.terminus.dalaran.core.resource.repository.*;
import io.terminus.dalaran.model.flow.FlowStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ReleasedResourceLoader implements DalaranResourceLoader {

    private String version;

    private String lastVersion;

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

    @Autowired
    private FunctionReleasedRepository functionRepository;

    @Autowired
    private ClientReleasedRepository clientRepository;

    @Autowired
    private LimiterReleasedRepository limiterRepository;

    @Autowired
    private AuthenticatorReleasedRepository authenticatorRepository;

    @Autowired
    private PrivateRepositoryRepository privateRepositoryRepository;


    @Override
    public List<TriggerFlowReleasedEntity> loadAllTriggerFlow() {
        return releasedTriggerFlowRepository.findByVersion(version);
    }

    @Override
    public List<SubFlowReleasedEntity> loadAllSubFlow() {
        return releasedSubFlowRepository.findByVersion(version);
    }

    @Override
    public List<TriggerFlowReleasedEntity> loadAvailableTriggerFlow() {
        return releasedTriggerFlowRepository.findByVersionAndStatusNotAndIsOnlineTrue(version, FlowStatus.Error);
    }

    @Override
    public List<TriggerFlowReleasedEntity> loadLastVersionAvailableTriggerFlow() {
        return releasedTriggerFlowRepository.findByVersionAndStatusNotAndIsOnlineTrue(lastVersion, FlowStatus.Error);
    }

    @Override
    public List<SubFlowReleasedEntity> loadAvailableSubFlow() {
        return releasedSubFlowRepository.findByVersionAndStatusNotAndIsOnlineTrue(version, FlowStatus.Error);
    }

    @Override
    public List<SubFlowReleasedEntity> loadLastVersionAvailableSubFlow() {
        return releasedSubFlowRepository.findByVersionAndStatusNotAndIsOnlineTrue(lastVersion, FlowStatus.Error);
    }

    @Override
    public List<TriggerFlowReleasedEntity> loadAvailableTriggerFlowByTriggerType(String triggerType) {
        return releasedTriggerFlowRepository.findByVersionAndStatusNotAndTriggerType(version, FlowStatus.Error, triggerType);
    }

    @Override
    public List<PropertyReleasedEntity> loadAllProperties() {
        return propertyRepository.findByVersion(version);
    }

    @Override
    public List<FunctionReleasedEntity> loadAllFunctions() {
        return functionRepository.findByVersion(version);
    }

    @Override
    public List<ClientReleasedEntity> loadAllClient() {
        return clientRepository.findByVersion(version);
    }

    @Override
    public List<PrivateRepositoryEntity> loadPackage() {
        List<PrivateRepositoryEntity> packages = new ArrayList<>();
        List<PrivateRepositoryEntity> processor = privateRepositoryRepository.findByType(DalaranConstants.PROCESSOR);
        List<PrivateRepositoryEntity> trigger = privateRepositoryRepository.findByType(DalaranConstants.TRIGGER);
        packages.addAll(trigger);
        packages.addAll(processor);
        return packages;
    }

    @Override
    public TriggerFlowReleasedEntity loadTriggerFlow(String triggerFlowId) {
        return releasedTriggerFlowRepository.findByVersionAndOriginId(version, triggerFlowId);
    }

    @Override
    public SubFlowReleasedEntity loadSubFlow(String subFlowId) {
        return releasedSubFlowRepository.findByVersionAndOriginId(version, subFlowId);
    }

    @Override
    public ModelReleasedEntity loadModel(String modelId) {
        return modelRepository.findByVersionAndOriginId(version, modelId);
    }

    @Override
    public ConnectorReleasedEntity loadConnector(String connectorId) {
        return connectorRepository.findByVersionAndOriginId(version, connectorId);
    }

    @Override
    public LimiterReleasedEntity loadLimiter(String limiterId) {
        return limiterRepository.findByVersionAndOriginId(version, limiterId);
    }

    @Override
    public ServiceReleasedEntity loadService(String serviceId) {
        return serviceRepository.findByVersionAndOriginId(version, serviceId);
    }

    @Override
    public AuthenticatorReleasedEntity loadAuthenticator(String authenticatorId) {
        return authenticatorRepository.findByVersionAndOriginId(version, authenticatorId);
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }
}
