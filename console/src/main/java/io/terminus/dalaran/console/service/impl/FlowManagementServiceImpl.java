package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.console.TestFlowLoader;
import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.dto.flow.BasicFlowInfo;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import io.terminus.dalaran.entity.ModelEntity;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.flow.TriggerFlowEntity;
import io.terminus.dalaran.repository.ModelRepository;
import io.terminus.dalaran.repository.ModuleRepository;
import io.terminus.dalaran.repository.PropertyRepository;
import io.terminus.dalaran.repository.TriggerFlowRepository;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlowManagementServiceImpl implements FlowManagementService {

    @Autowired
    private TriggerFlowRepository flowRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private FlowQueryService flowQueryService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private TestFlowLoader testFlowLoader;

    private final Gson gson = new Gson();
    private final FlowConvertor flowConvertor = new FlowConvertor();

    @Override
    public List<TriggerFlowDTO> queryFlows(FlowQuery query) {
        List<TriggerFlowEntity> entities = flowQueryService.query(query);
        List<TriggerFlowDTO> models = new LinkedList<>();
        for (TriggerFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }
        return models;
    }

    @Override
    public Long saveFlow(TriggerFlowEntity flowEntity) {
        Long id = flowRepository.save(flowEntity).getId();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        testFlowLoader.loadTriggerFlow(flowEntity);
        return id;
    }

    @Override
    public Long createFlow(TriggerFlowDTO flowModel) {
        TriggerFlowEntity flowEntity = buildEntity(flowModel);
        Long id = flowRepository.save(flowEntity).getId();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        testFlowLoader.loadTriggerFlow(flowEntity);
        return id;
    }

    @Override
    public void deleteFlow(Long flowId) {
        flowRepository.delete(flowId);
    }

    @Override
    public TriggerFlowDTO updateFlow(TriggerFlowDTO flowModel) {
        TriggerFlowEntity flowEntity = buildEntity(flowModel);
        flowRepository.save(flowEntity);
        // TODO 这里依赖 loader 有点怪 而且可以异步
        testFlowLoader.loadTriggerFlow(flowEntity);
        return flowModel;
    }

    @Override
    public List<TriggerFlowDTO> list() {
        List<TriggerFlowEntity> entities = flowRepository.findAll();
        List<TriggerFlowDTO> models = new LinkedList<>();
        for (TriggerFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }

        return models;
    }

    @Override
    public List<TriggerFlowDTO> queryByProcessorIds(List<Long> processorIds) {
        List<TriggerFlowEntity> entities = flowQueryService.queryByProcessorIds(processorIds);
        List<TriggerFlowDTO> models = new LinkedList<>();
        for (TriggerFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }

        return models;
    }

    @Override
    public List<BasicFlowInfo> listBasicFlowInfoByModuleId(Long moduleId) {
        return flowQueryService.listBasicInfoByModuleId(moduleId);
    }

    @Nullable
    @Override
    public TriggerFlowDTO getById(Long flowId) {
        TriggerFlowEntity flowEntity = flowRepository.findOne(flowId);
        if (flowEntity == null) {
            return null;
        }
        return flowConvertor.toDTO(flowEntity);
    }

    private TriggerFlowEntity buildEntity(TriggerFlowDTO model) {
        TriggerFlowEntity flowEntity;
        Long id = model.getId();
        if (id != null) {
            flowEntity = flowRepository.findOne(id);
        } else {
            flowEntity = new TriggerFlowEntity();
        }

        List<ProcessorEntity> pipeline = model.getPipeline().stream().map(processor -> {
            ProcessorEntity processorEntity = new ProcessorEntity();
            processorEntity.setId(processor.getId());
            processorEntity.setType(processor.getType());
            processorEntity.setName(processor.getName());
            processorEntity.setConfig(gson.toJson(processor.getConfig()));
            return processorEntity;
        }).collect(Collectors.toList());

        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            flowEntity.setName(name);
        } else {
            flowEntity.setName("Dalaran Flow");
        }
        flowEntity.setTriggerType(model.getTriggerType());
        flowEntity.setTriggerConfig(gson.toJson(model.getTriggerConfig()));
        flowEntity.setModuleId(model.getModuleId());
        flowEntity.setInModel(model.getInModelId());
        flowEntity.setOutModel(model.getOutModelId());
        flowEntity.setPipeline(pipeline);
        flowEntity.setDescription(model.getDescription());

        return flowEntity;
    }

    private ModelDTO buildModelEntity(ModelEntity entity) {
        ModelDTO model = new ModelDTO();
        model.setId(entity.getId());
        model.setModelType(entity.getType());
        model.setModelSchema(JSON.parseObject(entity.getModelSchema(), Map.class));
        model.setName(entity.getName());
        model.setModuleId(entity.getModuleId());
        model.setDescription(entity.getDescription());
        return model;
    }
}
