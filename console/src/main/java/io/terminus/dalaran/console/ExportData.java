package io.terminus.dalaran.console;

import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import lombok.Data;

import java.util.List;

@Data
public class ExportData {
    private List<ModuleEntity> modules;
    private List<ModelEntity> models;
    private List<TriggerFlowEntity> triggerFlows;
    private List<SubFlowEntity> subFlows;
    private List<ServiceEntity> services;
    private List<FunctionEntity> functions;
    private List<ConnectorEntity> connectors;
    private List<ClientEntity> clients;
    private List<PropertyEntity> properties;
    private List<TrantorEntity> trantorEntities;
    private List<AuthenticatorEntity> authenticatorEntities;
    private List<LimiterEntity> limiterEntities;
    private List<PrivateRepositoryEntity> privateRepositoryEntities;
}
