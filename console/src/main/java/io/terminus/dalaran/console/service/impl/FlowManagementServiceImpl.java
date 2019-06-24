package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.dto.flow.BasicFlowInfo;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.repository.ModuleRepository;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.FlowStatus;
import io.terminus.dalaran.core.flow.model.FlowValidation;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
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
    private TestFlowInitializer testFlowInitializer;

    @Autowired
    private DalaranFlowBuilder flowBuilder;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

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
        setFlowStatus(flowEntity);
        Long id = flowRepository.save(flowEntity).getId();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        testFlowInitializer.reloadTestTriggerFlow(flowEntity.getId());
        return id;
    }

    @Override
    public Long createFlow(TriggerFlowDTO flowModel) {
        TriggerFlowEntity flowEntity = buildEntity(flowModel);
        setFlowStatus(flowEntity);
        Long id = flowRepository.save(flowEntity).getId();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        testFlowInitializer.reloadTestTriggerFlow(flowEntity.getId());
        return id;
    }

    @Override
    public void deleteFlow(Long flowId) {
        flowRepository.delete(flowId);
    }

    @Override
    public TriggerFlowDTO updateFlow(TriggerFlowDTO flowModel) {
        TriggerFlowEntity flowEntity = buildEntity(flowModel);
        setFlowStatus(flowEntity);
        flowRepository.save(flowEntity);
        // TODO 这里依赖 loader 有点怪 而且可以异步
        testFlowInitializer.reloadTestTriggerFlow(flowEntity.getId());
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

    @Override
    public Long copyFlow(Long id, String name) {
        TriggerFlowEntity flowEntity = flowRepository.findOne(id);
        if (flowEntity == null) {
            return null;
        }
        TriggerFlowEntity newFlowEntity = new TriggerFlowEntity();

        BeanUtils.copyProperties(flowEntity, newFlowEntity);
        newFlowEntity.setId(null);
        newFlowEntity.setName(name);
        flowRepository.save(newFlowEntity);
        return newFlowEntity.getId();
    }

    @Override
    public List<FlowValidation> validateFlow(TriggerFlowDTO model) {
        TriggerFlowEntity entity = buildEntity(model);
        return validateFlow(entity);
    }

    private void setFlowStatus(TriggerFlowEntity flowEntity) {
        FlowStatus flowStatus;
        if (validateFlow(flowEntity).isEmpty()) {
            flowStatus = FlowStatus.Available;
        } else {
            flowStatus = FlowStatus.Error;
        }
        flowEntity.setStatus(flowStatus);
    }

    private List<FlowValidation> validateFlow(TriggerFlowEntity entity) {
        TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(entity);
        return flowBuilder.validateFlow(triggerFlow);
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
            processorEntity.setConfig(JSON.toJSONString(processor.getConfig()));
            return processorEntity;
        }).collect(Collectors.toList());

        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            flowEntity.setName(name);
        } else {
            flowEntity.setName("Dalaran Flow");
        }
        flowEntity.setTriggerType(model.getTriggerType());
        flowEntity.setTriggerConfig(JSON.toJSONString(model.getTriggerConfig()));
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
