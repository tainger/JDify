package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.model.ProcessorModel;
import io.terminus.dalaran.console.model.query.ProcessorQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.console.model.query.rst.ComponentType;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.console.service.ProcessorManagementService;
import io.terminus.dalaran.console.service.jpa.ProcessorQueryService;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.repository.ModuleRepository;
import io.terminus.dalaran.repository.ProcessorRepository;
import io.terminus.dalaran.repository.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
@Service
public class ProcessorManagementServiceImpl implements ProcessorManagementService {

    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private ProcessorQueryService processorQueryService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private StructureRepository structureRepository;

    @Override
    public void createProcessor(ProcessorModel processorModel) {
        processorRepository.save(buildEntity(processorModel));
    }

    @Override
    public void deleteProcessor(Long processorId) {
        processorRepository.delete(processorId);
    }

    @Override
    public void updateProcessor(ProcessorModel processorModel) {
        processorRepository.save(buildEntity(processorModel));
    }

    @Override
    public List<ProcessorModel> queryProcessors(ProcessorQuery query) {
        List<ProcessorEntity> entities = processorQueryService.query(query);
        List<ProcessorModel> models = new LinkedList<>();

        for (ProcessorEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<ProcessorModel> list() {
        List<ProcessorEntity> entities = processorRepository.findAll();
        List<ProcessorModel> models = new LinkedList<>();

        for (ProcessorEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<ModuleComponent> getComponents(Long moduleId) {
        List<ModuleComponent> components = new ArrayList<>();
        List<ComponentType> types = processorQueryService.getTypes(moduleId);
        for (ComponentType componentType: types) {
            String type = componentType.getType();
            List<ComponentInfo> componentInfos = processorQueryService.getBasicInfo(type);
            ModuleComponent moduleComponent = new ModuleComponent();
            moduleComponent.setType(type);
            moduleComponent.setComponents(componentInfos);
            components.add(moduleComponent);
        }
        return components;
    }

    private ProcessorEntity buildEntity(ProcessorModel model) {
        ProcessorEntity processorEntity = new ProcessorEntity();
        processorEntity.setModuleId(model.getModuleId());
        processorEntity.setInStructure(model.getInStructure());
        processorEntity.setOutStructure(model.getOutStructure());
        processorEntity.setType(model.getType());
        processorEntity.setName(model.getName());
        processorEntity.setDescription(model.getDescription());
        processorEntity.setConfig(JSON.toJSONString(model.getConfig()));
        processorEntity.setId(model.getId());
        return processorEntity;
    }

    private ProcessorModel buildModel(ProcessorEntity entity) {
        ProcessorModel processorModel = new ProcessorModel();
        processorModel.setId(entity.getId());
        processorModel.setModuleId(entity.getModuleId());
        processorModel.setInStructure(entity.getInStructure());
        processorModel.setOutStructure(entity.getOutStructure());
        processorModel.setName(entity.getName());
        processorModel.setDescription(entity.getDescription());
        processorModel.setType(entity.getType());
        processorModel.setConfig(JSON.parseObject(entity.getConfig(), Map.class));
        return processorModel;
    }
}
