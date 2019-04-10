package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.model.ModuleModel;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.service.ModuleManagementService;
import io.terminus.dalaran.console.service.jpa.ModuleQueryService;
import io.terminus.dalaran.entity.ModuleEntity;
import io.terminus.dalaran.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Service
public class ModuleManagementServiceImpl implements ModuleManagementService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModuleQueryService moduleQueryService;

    @Override
    public void createModule(ModuleModel moduleModel) {
        moduleRepository.save(buildEntity(moduleModel));
    }

    @Override
    public void deleteModule(Long moduleId) {
        moduleRepository.delete(moduleId);
    }

    @Override
    public void updateModule(ModuleModel moduleModel) {
        moduleRepository.save(buildEntity(moduleModel));
    }

    @Override
    public List<ModuleModel> list() {
        List<ModuleEntity> entities = moduleRepository.findAll();
        List<ModuleModel> models = new LinkedList<>();

        for (ModuleEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<ModuleModel> queryModules(ModuleQuery query) {
        List<ModuleEntity> entities = moduleQueryService.query(query);
        List<ModuleModel> models = new LinkedList<>();

        for (ModuleEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    private ModuleEntity buildEntity(ModuleModel model) {
        ModuleEntity moduleEntity = new ModuleEntity();
        moduleEntity.setId(model.getId());
        moduleEntity.setName(model.getName());
        moduleEntity.setDependencies(model.getDependencies());
        moduleEntity.setDescription(model.getDescription());
        return moduleEntity;
    }

    private ModuleModel buildModel(ModuleEntity entity) {
        ModuleModel moduleModel = new ModuleModel();
        moduleModel.setId(entity.getId());
        moduleModel.setName(entity.getName());
        moduleModel.setDependencies(entity.getDependencies());
        moduleModel.setDescription(entity.getDescription());
        return moduleModel;
    }
}
