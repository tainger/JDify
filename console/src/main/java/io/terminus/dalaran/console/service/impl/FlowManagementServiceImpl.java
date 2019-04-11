package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.console.model.FlowModel;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import io.terminus.dalaran.entity.FlowEntity;
import io.terminus.dalaran.entity.ModuleEntity;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.repository.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class FlowManagementServiceImpl implements FlowManagementService {

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private FlowQueryService flowQueryService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public List<FlowModel> queryFlows(FlowQuery query) {
        List<FlowEntity> entities = flowQueryService.query(query);
        List<FlowModel> models = new LinkedList<>();
        for (FlowEntity entity : entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public void saveFlow(FlowEntity flowEntity) {
        flowRepository.save(flowEntity);
    }

    @Override
    public void createFlow(FlowModel flowModel) {
        flowRepository.save(buildEntity(flowModel));
    }

    @Override
    public void deleteFlow(Long flowId) {
        flowRepository.delete(flowId);
    }

    @Override
    public void updateFlow(FlowModel flowModel) {
        flowRepository.save(buildEntity(flowModel));
    }

    @Override
    public List<FlowModel> list() {
        List<FlowEntity> entities = flowRepository.findAll();
        List<FlowModel> models = new LinkedList<>();
        for (FlowEntity entity : entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Override
    public List<FlowModel> queryByProcessorIds(List<Long> processorIds) {
        List<FlowEntity> entities = flowQueryService.queryByProcessorIds(processorIds);
        List<FlowModel> models = new LinkedList<>();
        for (FlowEntity entity : entities) {
            models.add(buildModel(entity));
        }

        return models;
    }

    @Nullable
    @Override
    public FlowModel getById(Long flowId) {
        FlowEntity flowEntity = flowRepository.findOne(flowId);
        if (flowEntity == null) {
            return null;
        }
        return buildModel(flowEntity);
    }

    private FlowEntity buildEntity(FlowModel model) {
        FlowEntity flowEntity = new FlowEntity();
        ModuleEntity moduleEntity = moduleRepository.findOne(model.getModuleId());
        List<ProcessorEntity> processors = new LinkedList<>();
        for (Long id : model.getProcessorIds()) {
            processors.add(processorRepository.findOne(id));
        }

        flowEntity.setName(model.getName());
        flowEntity.setModule(moduleEntity);
        flowEntity.setProcessors(model.getProcessorIds());
        flowEntity.setDescription(model.getDescription());
        flowEntity.setMaxRetry(model.getMaxRetry());
        flowEntity.setRetryable(model.getRetryable());
        flowEntity.setRetryDelay(model.getRetryDelay());
        flowEntity.setProperties(model.getPropertyIds());

        return flowEntity;
    }

    private FlowModel buildModel(FlowEntity entity) {
        FlowModel flowModel = new FlowModel();
        flowModel.setId(entity.getId());
        flowModel.setName(entity.getName());
        flowModel.setModuleId(entity.getModule().getId());
        flowModel.setMaxRetry(entity.getMaxRetry());
        flowModel.setProcessorIds(entity.getProcessors());
        flowModel.setPropertyIds(entity.getProperties());
        flowModel.setRetryable(entity.getRetryable());
        flowModel.setRetryDelay(entity.getRetryDelay());
        flowModel.setDescription(entity.getDescription());
        return flowModel;
    }
}
