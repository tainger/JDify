package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.VersionUpdateService;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class VersionUpdateServiceImpl implements VersionUpdateService {

    @Autowired
    private TriggerFlowRepository flowRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Override
    public void cleanOldData() {
        List<ModuleEntity> moduleEntityList = moduleRepository.findAll();
        moduleEntityList.forEach(moduleEntity -> {
            if (StringUtils.isNotBlank(moduleEntity.getResourceKey())) {
                return;
            }
            moduleEntity.setResourceKey(String.valueOf(moduleEntity.getId()));
            moduleRepository.save(moduleEntity);
        });

        List<ModelEntity> modelEntityList = modelRepository.findAll();
        modelEntityList.forEach(modelEntity -> {
            if (StringUtils.isNotBlank(modelEntity.getResourceKey())) {
                return;
            }
            modelEntity.setResourceKey(String.valueOf(modelEntity.getId()));
            modelRepository.save(modelEntity);
        });

        List<ConnectorEntity> connectorEntityList = connectorRepository.findAll();
        connectorEntityList.forEach(connectorEntity -> {
            if (StringUtils.isNotBlank(connectorEntity.getResourceKey())) {
                return;
            }
            connectorEntity.setResourceKey(String.valueOf(connectorEntity.getId()));
            connectorRepository.save(connectorEntity);
        });

        List<FunctionEntity> functionEntityList = functionRepository.findAll();
        functionEntityList.forEach(functionEntity -> {
            if (StringUtils.isNotBlank(functionEntity.getResourceKey())) {
                return;
            }
            functionEntity.setResourceKey(String.valueOf(functionEntity.getId()));
            functionRepository.save(functionEntity);
        });

        List<ServiceEntity> serviceEntityList = serviceRepository.findAll();
        serviceEntityList.forEach(serviceEntity -> {
            if (StringUtils.isNotBlank(serviceEntity.getResourceKey())) {
                return;
            }
            serviceEntity.setResourceKey(String.valueOf(serviceEntity.getId()));
            serviceRepository.save(serviceEntity);
        });

        List<SubFlowEntity> subFlowEntityList = subFlowRepository.findAll();
        subFlowEntityList.forEach(subFlowEntity -> {
            if (StringUtils.isNotBlank(subFlowEntity.getResourceKey())) {
                return;
            }
            subFlowEntity.setResourceKey(String.valueOf(subFlowEntity.getId()));
            subFlowRepository.save(subFlowEntity);
        });

        List<TriggerFlowEntity> triggerFlowEntityList = flowRepository.findAll();
        triggerFlowEntityList.forEach(triggerFlowEntity -> {
            if (StringUtils.isNotBlank(triggerFlowEntity.getResourceKey())) {
                return;
            }
            triggerFlowEntity.setResourceKey(String.valueOf(triggerFlowEntity.getId()));
            triggerFlowEntity.setOnline(true);
            flowRepository.save(triggerFlowEntity);
        });
    }
}
