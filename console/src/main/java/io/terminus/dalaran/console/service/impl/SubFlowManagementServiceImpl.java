package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.SubFlowRepository;
import io.terminus.dalaran.console.service.SubFlowManagementService;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.flow.FlowStatus;
import io.terminus.dalaran.core.flow.model.FlowValidation;
import io.terminus.dalaran.core.flow.model.SubFlow;
import io.terminus.dalaran.core.flow.model.TriggerFlow;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubFlowManagementServiceImpl implements SubFlowManagementService {

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private TestFlowInitializer testFlowInitializer;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private DalaranFlowBuilder flowBuilder;

    private final FlowConvertor flowConvertor = new FlowConvertor();

    @Override
    public Long createFlow(SubFlowDTO flowModel) {
        SubFlowEntity subFlowEntity = buildEntity(flowModel);
        setFlowStatus(subFlowEntity);
        subFlowRepository.save(subFlowEntity);
        testFlowInitializer.reloadTestSubFlow(subFlowEntity.getId());
        return subFlowEntity.getId();
    }

    @Override
    public void deleteFlow(Long flowId) {
        subFlowRepository.delete(flowId);
    }

    @Override
    public SubFlowDTO updateFlow(SubFlowDTO flowModel) {
        SubFlowEntity subFlowEntity = buildEntity(flowModel);
        setFlowStatus(subFlowEntity);
        subFlowRepository.save(subFlowEntity);
        testFlowInitializer.reloadTestSubFlow(subFlowEntity.getId());
        return flowConvertor.toDTO(subFlowEntity);
    }

    @Override
    public List<SubFlowDTO> queryFlows(FlowQuery query) {
        // TODO
        return null;
    }

    @Override
    public List<SubFlowDTO> list() {
        List<SubFlowEntity> entities = subFlowRepository.findAll();
        List<SubFlowDTO> models = new LinkedList<>();
        for (SubFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }
        return models;
    }

    @Nullable
    @Override
    public SubFlowDTO getById(Long flowId) {
        SubFlowEntity flowEntity = subFlowRepository.findOne(flowId);
        if (flowEntity == null) {
            return null;
        }
        return flowConvertor.toDTO(flowEntity);
    }

    @Override
    public Long copyFlow(Long id) {
        // TODO
        return null;
    }

    @Override
    public List<FlowValidation> validateFlow(SubFlowDTO model) {
        SubFlowEntity entity = buildEntity(model);
        return validateFlow(entity);
    }

    private void setFlowStatus(SubFlowEntity flowEntity) {
        FlowStatus flowStatus;
        if (validateFlow(flowEntity).isEmpty()) {
            flowStatus = FlowStatus.Available;
        } else {
            flowStatus = FlowStatus.Error;
        }
        flowEntity.setStatus(flowStatus);
    }

    private List<FlowValidation> validateFlow(SubFlowEntity entity) {
        SubFlow subFlow = resourceBuilder.buildSubFlow(entity);
        return flowBuilder.validateFlow(subFlow);
    }

    private SubFlowEntity buildEntity(SubFlowDTO model) {
        SubFlowEntity flowEntity;
        Long id = model.getId();
        if (id != null) {
            flowEntity = subFlowRepository.findOne(id);
        } else {
            flowEntity = new SubFlowEntity();
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
        flowEntity.setModuleId(model.getModuleId());
        flowEntity.setInModel(model.getInModelId());
        flowEntity.setOutModel(model.getOutModelId());
        flowEntity.setPipeline(pipeline);
        flowEntity.setDescription(model.getDescription());

        return flowEntity;
    }
}
