package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.model.dto.ModuleDTO;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.ModuleManagementService;
import io.terminus.dalaran.console.service.jpa.ModuleQueryService;
import io.terminus.dalaran.entity.ModuleEntity;
import io.terminus.dalaran.repository.ModuleRepository;
import org.apache.commons.lang3.StringUtils;
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
    private ModelManagementService modelManagementService;

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

        for (ModuleEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<ModuleDTO> queryModules(ModuleQuery query) {
        List<ModuleEntity> entities = moduleQueryService.query(query);
        List<ModuleDTO> models = new LinkedList<>();

        for (ModuleEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<ModuleComponent> listModuleComponents(Long moduleId) {
        List<ModuleComponent> components = new LinkedList<>();
        components.addAll(flowManagementService.getComponents(moduleId));
        components.addAll(modelManagementService.getComponents(moduleId));
        return components;
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
