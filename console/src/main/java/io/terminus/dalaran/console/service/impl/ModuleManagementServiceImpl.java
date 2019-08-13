package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.ModuleEntity;
import io.terminus.dalaran.console.model.dto.ModuleDTO;
import io.terminus.dalaran.console.model.dto.ModuleDetailDTO;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.repository.ModuleRepository;
import io.terminus.dalaran.console.service.*;
import io.terminus.dalaran.console.service.jpa.ModuleQueryService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    private FunctionService functionService;

    @Autowired
    private ClientManagementService clientService;

    @Autowired
    private ServiceManagement serviceManagement;

    @Override
    public Long createModule(ModuleDTO moduleModel) {
        return moduleRepository.save(buildEntity(moduleModel)).getId();
    }

    @Override
    public void deleteModule(Long moduleId) {
        moduleRepository.delete(moduleId);
    }

    @Override
    public ModuleDTO updateModule(ModuleDTO moduleModel) {
        moduleRepository.save(buildEntity(moduleModel));
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
        ModuleEntity moduleEntity = moduleRepository.findOne(moduleId);
        if (moduleEntity == null) {
            return null;
        }
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
        ModuleEntity entity = moduleRepository.findOne(moduleId);
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
            moduleEntity = moduleRepository.findOne(id);
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
}
