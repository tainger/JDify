package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.service.*;
import io.terminus.dalaran.console.service.jpa.ModuleQueryService;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.dto.ModuleDTO;
import io.terminus.dalaran.model.dto.ModuleDetailDTO;
import io.terminus.dalaran.model.query.ModuleQuery;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    private FunctionService functionService;

    @Autowired
    private ClientManagementService clientService;

    @Autowired
    private ServiceManagement serviceManagement;

    @Override
    public Long createModule(ModuleDTO moduleModel) {
        ModuleEntity moduleEntity = buildEntity(moduleModel);
        setCreatedBy(moduleEntity);
        return moduleRepository.save(moduleEntity).getId();
    }

    @Override
    public void deleteModule(Long moduleId) {
        moduleRepository.deleteById(moduleId);
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
        List<ModuleEntity> entities = moduleRepository.findAll();
        List<ModuleDTO> models = new LinkedList<>();

        for (ModuleEntity entity : entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<ModuleDTO> queryModules(ModuleQuery query) {
        List<ModuleEntity> entities = moduleQueryService.query(query);
        List<ModuleDTO> models = new LinkedList<>();

        for (ModuleEntity entity : entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public ModuleDetailDTO getModuleDetail(Long moduleId) {
        Optional<ModuleEntity> moduleEntityOptional = moduleRepository.findById(moduleId);
        if (!moduleEntityOptional.isPresent()) {
            return null;
        }
        ModuleEntity moduleEntity = moduleEntityOptional.get();
        ModuleDetailDTO moduleDetail = new ModuleDetailDTO();
        moduleDetail.setId(moduleEntity.getId());
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
        return moduleDetail;
    }

    @Override
    public String getModuleName(@NotNull Long moduleId) {
        Optional<ModuleEntity> optional = moduleRepository.findById(moduleId);
        ModuleEntity entity = new ModuleEntity();
        if(optional!=null && optional.isPresent()) {
            entity = optional.get();
        }
        if (entity == null) {
            return null;
        }
        return entity.getName();
    }

    private ModuleEntity buildEntity(ModuleDTO model) {
        ModuleEntity moduleEntity;
        Long id = model.getId();
        if (id == null) {
            moduleEntity = new ModuleEntity();
        } else {
            moduleEntity = moduleRepository.findById(id).get();
        }
        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            moduleEntity.setName(name);
        } else {
            moduleEntity.setName("Dalaran Module");
        }
        moduleEntity.setDependencies(model.getDependencies());
        moduleEntity.setDescription(model.getDescription());
        return moduleEntity;
    }

    private ModuleDTO buildModel(ModuleEntity entity) {
        ModuleDTO moduleModel = new ModuleDTO();
        moduleModel.setId(entity.getId());
        moduleModel.setName(entity.getName());
        moduleModel.setDependencies(entity.getDependencies());
        moduleModel.setDescription(entity.getDescription());
        return moduleModel;
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
