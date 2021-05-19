package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.*;
import io.terminus.dalaran.console.service.jpa.ModuleQueryService;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.dto.ModuleDTO;
import io.terminus.dalaran.model.dto.ModuleDetailDTO;
import io.terminus.dalaran.model.dto.ModuleFlowDTO;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.model.query.ModuleQuery;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Service
@Transactional
public class ModuleManagementServiceImpl implements ModuleManagementService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModuleQueryService moduleQueryService;

    @Autowired
    private FlowManagementService flowManagementService;

    @Autowired
    private SubFlowManagementService subFlowManagementService;

    @Autowired
    private ModelManagementService modelManagementService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private LimiterService limiterService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private ClientManagementService clientService;

    @Autowired
    private ServiceManagement serviceManagement;

    @Autowired
    private TriggerFlowRepository flowRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private AlarmRuleService alarmRuleService;

    @Autowired
    private AuthenticatorService authenticatorService;

    @Autowired
    private AuthenticatorRepository authenticatorRepository;

    @Override
    public String createModule(ModuleDTO moduleModel) {
        ModuleEntity moduleEntity = buildEntity(moduleModel);
        setCreatedBy(moduleEntity);
        return moduleRepository.save(moduleEntity).getResourceKey();
    }

    @Override
    public void deleteModule(String moduleId) {
        ModuleEntity moduleEntity = moduleRepository.findByResourceKey(moduleId);
        moduleEntity.setExist(false);
        moduleRepository.save(moduleEntity);

        List<TriggerFlowEntity> triggerFlowEntityList = flowRepository.findByModuleIdAndIsExistTrue(moduleId);
        for (TriggerFlowEntity triggerFlowEntity : triggerFlowEntityList) {
            triggerFlowEntity.setExist(false);
            flowRepository.save(triggerFlowEntity);
        }

        List<ModelEntity> modelEntityList = modelRepository.findByModuleIdAndIsExistTrue(moduleId);
        for (ModelEntity modelEntity : modelEntityList) {
            modelEntity.setExist(false);
            modelRepository.save(modelEntity);
        }

        List<SubFlowEntity> subFlowEntityList = subFlowRepository.findByModuleIdAndIsExistTrue(moduleId);
        for (SubFlowEntity subFlowEntity : subFlowEntityList) {
            subFlowEntity.setExist(false);
            subFlowRepository.save(subFlowEntity);
        }

        List<ClientEntity> clientEntityList = clientRepository.findByModuleIdAndIsExistTrue(moduleId);
        for (ClientEntity clientEntity : clientEntityList) {
            clientEntity.setExist(false);
            clientRepository.save(clientEntity);
        }

        List<ServiceEntity> serviceEntityList = serviceRepository.findByModuleIdAndIsExistTrue(moduleId);
        for (ServiceEntity serviceEntity : serviceEntityList) {
            serviceEntity.setExist(false);
            serviceRepository.save(serviceEntity);
        }

        List<ConnectorEntity> connectorEntityList = connectorRepository.findByModuleIdAndIsExistTrue(moduleId);
        for (ConnectorEntity connectorEntity : connectorEntityList) {
            connectorEntity.setExist(false);
            connectorRepository.save(connectorEntity);
        }

        List<FunctionEntity> functionEntityList = functionRepository.findByModuleIdAndIsExistTrue(moduleId);
        for (FunctionEntity functionEntity : functionEntityList) {
            functionEntity.setExist(false);
            functionRepository.save(functionEntity);
        }

        List<AuthenticatorEntity> authenticatorEntityList = authenticatorRepository.findByModuleIdAndIsExistTrue(moduleId);
        for (AuthenticatorEntity authenticatorEntity : authenticatorEntityList) {
            authenticatorEntity.setExist(false);
            authenticatorRepository.save(authenticatorEntity);
        }
    }

    @Override
    public ModuleDTO updateModule(ModuleDTO moduleModel) {
        ModuleEntity moduleEntity = buildEntity(moduleModel);
        setUpdatedBy(moduleEntity);
        moduleRepository.save(moduleEntity);
        return moduleModel;
    }

    @Override
    public List<ModuleDTO> list() {
        List<ModuleEntity> entities = moduleRepository.findByIsExistTrue();
        List<ModuleDTO> models = new LinkedList<>();

        for (ModuleEntity entity : entities) {
            models.add(buildModule(entity));
        }

        return models;
    }

    @Override
    public List<ModuleDTO> queryModules(ModuleQuery query) {
        List<ModuleEntity> entities = moduleQueryService.query(query);
        List<ModuleDTO> models = new LinkedList<>();

        for (ModuleEntity entity : entities) {
            models.add(buildModule(entity));
        }

        return models;
    }

    @Override
    public ModuleDetailDTO getModuleDetail(String moduleId) {
        ModuleEntity moduleEntity = moduleRepository.findByResourceKey(moduleId);
        ModuleDetailDTO moduleDetail = new ModuleDetailDTO();
        moduleDetail.setId(moduleEntity.getResourceKey());
        moduleDetail.setName(moduleEntity.getName());
        moduleDetail.setDescription(moduleEntity.getDescription());
        // TODO to DTO
        moduleDetail.setDependencies(moduleEntity.getDependencies());
        moduleDetail.setFlows(flowManagementService.listBasicFlowInfoByModuleId(moduleId));
        moduleDetail.setSubFlows(subFlowManagementService.listBasicSubFlowInfoByModuleId(moduleId));
        moduleDetail.setModels(modelManagementService.listBasicInfoByModuleId(moduleId));
        moduleDetail.setConnectors(connectorService.listBasicInfoByModuleId(moduleId));
        moduleDetail.setServices(serviceManagement.listBasicInfoByModuleId(moduleId));
        moduleDetail.setFunctions(functionService.listBasicInfoByModuleId(moduleId));
        moduleDetail.setClients(clientService.listBasicInfoByModuleId(moduleId));
        moduleDetail.setLimiters(limiterService.listBasicInfoByModuleId(moduleId));
        moduleDetail.setAuthenticators(authenticatorService.listBasicInfoByModuleId(moduleId));
        return moduleDetail;
    }

    @Override
    public String getModuleName(@NotNull String moduleId) {
        ModuleEntity moduleEntity = moduleRepository.findByResourceKey(moduleId);
        if (moduleEntity == null) {
            return null;
        }
        return moduleEntity.getName();
    }

    @Override
    public List<ModuleFlowDTO> flowInfoList() {
        List<ModuleEntity> moduleEntities = moduleRepository.findByIsExistTrue();
        List<ModuleFlowDTO> moduleFlowDTOS = new ArrayList<>();
        for (ModuleEntity moduleEntity : moduleEntities) {
            ModuleFlowDTO moduleFlowDTO = new ModuleFlowDTO();
            moduleFlowDTO.setModuleName(moduleEntity.getName());
            moduleFlowDTO.setId(moduleEntity.getResourceKey());
            List<BasicFlowInfo> basicFlowInfos = flowManagementService.listBasicFlowInfoByModuleId(moduleEntity.getResourceKey());
            for (BasicFlowInfo basicFlowInfo : basicFlowInfos) {
                basicFlowInfo.setId(basicFlowInfo.getResourceKey());
            }
            moduleFlowDTO.setBasicFlowInfos(basicFlowInfos);
            moduleFlowDTOS.add(moduleFlowDTO);
        }
        return moduleFlowDTOS;
    }

    private ModuleEntity buildEntity(ModuleDTO module) {
        ModuleEntity moduleEntity;
        String resourceKey = module.getId();
        if (StringUtils.isBlank(resourceKey)) {
            moduleEntity = new ModuleEntity();
            resourceKey = GenerateKeyUtils.resourceKey();
        } else {
            moduleEntity = moduleRepository.findByResourceKey(resourceKey);
        }
        String name = module.getName();
        if (StringUtils.isNoneBlank(name)) {
            moduleEntity.setName(name);
        } else {
            moduleEntity.setName("Dalaran Module");
        }
        moduleEntity.setResourceKey(resourceKey);
        moduleEntity.setDependencies(module.getDependencies());
        moduleEntity.setDescription(module.getDescription());
        moduleEntity.setExist(true);
        return moduleEntity;
    }

    private ModuleDTO buildModule(ModuleEntity entity) {
        ModuleDTO module = new ModuleDTO();
        module.setId(entity.getResourceKey());
        module.setName(entity.getName());
        module.setExist(entity.isExist());
        module.setDependencies(entity.getDependencies());
        module.setDescription(entity.getDescription());
        return module;
    }

    private void setCreatedBy(ModuleEntity moduleEntity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            moduleEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private void setUpdatedBy(ModuleEntity moduleEntity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            moduleEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
    }
}
