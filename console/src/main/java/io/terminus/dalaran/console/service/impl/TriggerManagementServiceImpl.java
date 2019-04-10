package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.model.TriggerModel;
import io.terminus.dalaran.console.model.query.TriggerQuery;
import io.terminus.dalaran.console.service.TriggerManagementService;
import io.terminus.dalaran.console.service.jpa.TriggerQueryService;
import io.terminus.dalaran.entity.FlowEntity;
import io.terminus.dalaran.entity.ModuleEntity;
import io.terminus.dalaran.entity.StructureEntity;
import io.terminus.dalaran.entity.TriggerEntity;
import io.terminus.dalaran.repository.FlowRepository;
import io.terminus.dalaran.repository.ModuleRepository;
import io.terminus.dalaran.repository.StructureRepository;
import io.terminus.dalaran.repository.TriggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private TriggerEntity buildEntity(TriggerModel model) {
        TriggerEntity triggerEntity = new TriggerEntity();
        ModuleEntity moduleEntity = moduleRepository.findOne(model.getModuleId());
        StructureEntity inStructure = structureRepository.findOne(model.getInStructure());
        StructureEntity outStructure = structureRepository.findOne(model.getOutStructure());
        FlowEntity flowEntity = flowRepository.findOne(model.getFlowId());

        triggerEntity.setFlow(flowEntity);
        triggerEntity.setModule(moduleEntity);
        triggerEntity.setInStructure(inStructure);
        triggerEntity.setOutStructure(outStructure);
        triggerEntity.setConfig(JSON.toJSONString(model.getConfig()));
        triggerEntity.setDescription(model.getDescription());
        triggerEntity.setName(model.getName());
        triggerEntity.setType(model.getType());
        triggerEntity.setId(model.getId());

        return triggerEntity;
    }

    private TriggerModel buildModel(TriggerEntity entity) {
        TriggerModel triggerModel = new TriggerModel();
        triggerModel.setFlowId(entity.getFlow().getId());
        triggerModel.setModuleId(entity.getModule().getId());
        triggerModel.setName(entity.getName());
        triggerModel.setType(entity.getType());
        triggerModel.setInStructure(entity.getInStructure().getId());
        triggerModel.setOutStructure(entity.getOutStructure().getId());
        triggerModel.setConfig(JSON.parseObject(entity.getConfig(), Map.class));
        return triggerModel;
    }
}
