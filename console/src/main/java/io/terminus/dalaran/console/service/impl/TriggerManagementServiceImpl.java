package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.model.TriggerModel;
import io.terminus.dalaran.console.model.query.TriggerQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.console.model.query.rst.ComponentType;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.console.service.TriggerManagementService;
import io.terminus.dalaran.console.service.jpa.TriggerQueryService;
import io.terminus.dalaran.entity.TriggerEntity;
import io.terminus.dalaran.repository.FlowRepository;
import io.terminus.dalaran.repository.ModuleRepository;
import io.terminus.dalaran.repository.StructureRepository;
import io.terminus.dalaran.repository.TriggerRepository;
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
public class TriggerManagementServiceImpl implements TriggerManagementService {

    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private TriggerQueryService triggerQueryService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private FlowRepository flowRepository;

    @Override
    public void createTrigger(TriggerModel triggerModel) {
        triggerRepository.save(buildEntity(triggerModel));
    }

    @Override
    public void deleteTrigger(Long triggerId) {
        triggerRepository.delete(triggerId);
    }

    @Override
    public void updateTrigger(TriggerModel triggerModel) {
        triggerRepository.save(buildEntity(triggerModel));
    }

    @Override
    public List<TriggerModel> queryTriggers(TriggerQuery query) {
        List<TriggerEntity> entities = triggerQueryService.query(query);
        List<TriggerModel> models = new LinkedList<>();

        for (TriggerEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<TriggerModel> list() {
        List<TriggerEntity> entities = triggerRepository.findAll();
        List<TriggerModel> models = new LinkedList<>();

        for (TriggerEntity entity: entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<ModuleComponent> getComponents(Long moduleId) {
        List<ModuleComponent> components = new ArrayList<>();
        List<ComponentType> types = triggerQueryService.getTypes(moduleId);
        for (ComponentType componentType: types) {
            String type = componentType.getType();
            List<ComponentInfo> componentInfos = triggerQueryService.getBasicInfo(type);
            ModuleComponent moduleComponent = new ModuleComponent();
            moduleComponent.setType(type);
            moduleComponent.setComponents(componentInfos);
            components.add(moduleComponent);
        }
        return components;
    }

    private TriggerEntity buildEntity(TriggerModel model) {
        TriggerEntity triggerEntity = new TriggerEntity();

        triggerEntity.setFlowId(model.getFlowId());
        triggerEntity.setModuleId(model.getModuleId());
        triggerEntity.setInStructure(model.getInStructure());
        triggerEntity.setOutStructure(model.getOutStructure());
        triggerEntity.setConfig(JSON.toJSONString(model.getConfig()));
        triggerEntity.setDescription(model.getDescription());
        triggerEntity.setName(model.getName());
        triggerEntity.setType(model.getType());
        triggerEntity.setId(model.getId());

        return triggerEntity;
    }

    private TriggerModel buildModel(TriggerEntity entity) {
        TriggerModel triggerModel = new TriggerModel();
        triggerModel.setFlowId(entity.getFlowId());
        triggerModel.setModuleId(entity.getModuleId());
        triggerModel.setName(entity.getName());
        triggerModel.setType(entity.getType());
        triggerModel.setInStructure(entity.getInStructure());
        triggerModel.setOutStructure(entity.getOutStructure());
        triggerModel.setConfig(JSON.parseObject(entity.getConfig(), Map.class));
        return triggerModel;
    }
}
