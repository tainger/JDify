package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.console.model.StructureModel;
import io.terminus.dalaran.console.model.query.StructureQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.console.model.query.rst.type.StructureType;
import io.terminus.dalaran.console.service.StructureManagementService;
import io.terminus.dalaran.console.service.jpa.StructQueryService;
import io.terminus.dalaran.entity.StructureEntity;
import io.terminus.dalaran.repository.ModuleRepository;
import io.terminus.dalaran.repository.StructureRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by jingdi on 2019/3/29
 */
@Service
public class StructureManagementServiceImpl implements StructureManagementService {

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private StructQueryService structQueryService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public Long createStructure(StructureModel structureModel) {
        return structureRepository.save(buildEntity(structureModel)).getId();
    }

    @Override
    public void deleteStructure(Long structureId) {
        structureRepository.delete(structureId);
    }

    @Override
    public StructureModel updateStructure(StructureModel structureModel) {
        structureRepository.save(buildEntity(structureModel));
        return structureModel;
    }

    @Override
    public List<StructureModel> queryStructures(StructureQuery query) {
        List<StructureEntity> entities = structQueryService.query(query);
        List<StructureModel> models = new LinkedList<>();

        for (StructureEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<StructureModel> list() {
        List<StructureEntity> entities = structureRepository.findAll();
        List<StructureModel> models = new LinkedList<>();

        for (StructureEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<ModuleComponent> getComponents(Long moduleId) {
        List<ModuleComponent> components = new ArrayList<>();
        List<StructureType> types = structQueryService.getTypes(moduleId);
        for (StructureType structureType: types) {
            BodyModelType type = structureType.getType();
            List<ComponentInfo> componentInfos = structQueryService.getBasicInfo(type);
            ModuleComponent moduleComponent = new ModuleComponent();
            moduleComponent.setType(type.name());
            moduleComponent.setComponents(componentInfos);
            components.add(moduleComponent);
        }
        return components;
    }

    private StructureEntity buildEntity(StructureModel model) {
        StructureEntity structureEntity;
        Long id = model.getId();
        if (id == null) {
            structureEntity = new StructureEntity();
        } else {
            structureEntity = structureRepository.findOne(id);
        }
        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            structureEntity.setName(name);
        } else {
            structureEntity.setName("Dalaran Model");
        }
        structureEntity.setStructureSchema(JSON.toJSONString(model.getStructureSchema()));
        structureEntity.setType(model.getStructureType());
        structureEntity.setDescription(model.getDescription());
        structureEntity.setModuleId(model.getModuleId());
        structureEntity.setUpdatedAt(new Date());
        return structureEntity;
    }

    private StructureModel buildModel(StructureEntity entity) {
        StructureModel model = new StructureModel();
        model.setDescription(entity.getDescription());
        model.setModuleId(entity.getModuleId());
        model.setName(entity.getName());
        model.setStructureSchema(JSON.parseObject(entity.getStructureSchema(), Map.class));
        model.setStructureType(entity.getType());
        model.setId(entity.getId());
        return model;
    }
}
