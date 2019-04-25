package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.DalaranLoader;
import io.terminus.dalaran.console.model.FlowModel;
import io.terminus.dalaran.console.model.ProcessorModel;
import io.terminus.dalaran.console.model.StructureModel;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.model.query.ProcessorQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.console.model.query.rst.ModuleComponent;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import io.terminus.dalaran.console.service.jpa.ProcessorQueryService;
import io.terminus.dalaran.entity.FlowEntity;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.StructureEntity;
import io.terminus.dalaran.repository.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FlowManagementServiceImpl implements FlowManagementService {

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private ProcessorQueryService processorQueryService;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private FlowQueryService flowQueryService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private DalaranLoader dalaranLoader;

    private String DALARAN_FLOW = "dalaran-flow";

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
    public Long saveFlow(FlowEntity flowEntity) {
        Long id = flowRepository.save(flowEntity).getId();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        dalaranLoader.loadTestFlow(flowEntity);
        return id;
    }

    @Override
    public Long createFlow(FlowModel flowModel) {
        FlowEntity flowEntity = buildEntity(flowModel);
        Long id = flowRepository.save(flowEntity).getId();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        dalaranLoader.loadTestFlow(flowEntity);
        return id;
    }

    @Override
    public void deleteFlow(Long flowId) {
        flowRepository.delete(flowId);
    }

    @Override
    public FlowModel updateFlow(FlowModel flowModel) {
        FlowEntity flowEntity = buildEntity(flowModel);
        flowRepository.save(flowEntity);
        // TODO 这里依赖 loader 有点怪 而且可以异步
        dalaranLoader.loadTestFlow(flowEntity);
        return flowModel;
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

    @Override
    public List<ModuleComponent> getComponents(Long moduleId) {
        List<ModuleComponent> components = new ArrayList<>();
        ModuleComponent component = new ModuleComponent();
        List<ComponentInfo> componentInfos = flowQueryService.getBasicInfo(moduleId);
        component.setType(DALARAN_FLOW);
        component.setComponents(componentInfos);
        components.add(component);
        return components;
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
        FlowEntity flowEntity;
        Long id = model.getId();
        if (id != null) {
            flowEntity = flowRepository.findOne(id);
        } else {
            flowEntity = new FlowEntity();
        }

//        List<ProcessorEntity> processors = new LinkedList<>();
//        for (Long processorId : model.getProcessorIds()) {
//            processors.add(processorRepository.findOne(processorId));
//        }

        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            flowEntity.setName(name);
        } else {
            flowEntity.setName("Dalaran Flow");
        }
        flowEntity.setModuleId(model.getModuleId());
        flowEntity.setInStructure(model.getInStructure().getId());
        flowEntity.setOutStructure(model.getOutStructure().getId());
        flowEntity.setProcessors(model.getProcessorIds());
        flowEntity.setDescription(model.getDescription());

        if (CollectionUtils.isNotEmpty(model.getPropertyIds())) {
            flowEntity.setProperties(model.getPropertyIds());
        } else {
            flowEntity.setProperties(new ArrayList<>());
        }
        return flowEntity;
    }

    private FlowModel buildModel(FlowEntity entity) {
        FlowModel flowModel = new FlowModel();
        flowModel.setId(entity.getId());
        flowModel.setName(entity.getName());
        flowModel.setModuleId(entity.getModuleId());
        flowModel.setInStructure(buildStructureEntity(structureRepository.findOne(entity.getInStructure())));
        flowModel.setOutStructure(buildStructureEntity(structureRepository.findOne(entity.getOutStructure())));
        flowModel.setProcessorIds(entity.getProcessors());
        flowModel.setPropertyIds(entity.getProperties());
        flowModel.setDescription(entity.getDescription());

        Set<ProcessorModel> processors = new HashSet<>();
        ProcessorQuery processorQuery = new ProcessorQuery();
        processorQuery.setProcessorIds(entity.getProcessors());
        for (ProcessorEntity processorEntity : processorQueryService.query(processorQuery)) {
            ProcessorModel processorModel = new ProcessorModel();
            processorModel.setId(processorEntity.getId());
            processorModel.setModuleId(processorEntity.getModuleId());
            processorModel.setName(processorEntity.getName());
            processorModel.setDescription(processorEntity.getDescription());
            processorModel.setType(processorEntity.getType());
            processorModel.setConfig(JSON.parseObject(processorEntity.getConfig(), Map.class));
            processors.add(processorModel);
        }
        flowModel.setProcessors(processors);
        return flowModel;
    }

    private StructureModel buildStructureEntity(StructureEntity entity) {
        StructureModel model = new StructureModel();
        model.setId(entity.getId());
        model.setStructureType(entity.getType());
        model.setStructureSchema(JSON.parseObject(entity.getStructureSchema(), Map.class));
        model.setName(entity.getName());
        model.setModuleId(entity.getModuleId());
        model.setDescription(entity.getDescription());
        return model;
    }
}
