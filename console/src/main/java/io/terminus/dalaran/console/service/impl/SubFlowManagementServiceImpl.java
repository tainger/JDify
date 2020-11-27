package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.console.repository.SubFlowRepository;
import io.terminus.dalaran.console.service.SubFlowManagementService;
import io.terminus.dalaran.console.service.jpa.SubFlowQueryService;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.model.dto.CopyFlow;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.flow.SubFlow;
import io.terminus.dalaran.model.flow.ValidateMessageLevel;
import io.terminus.dalaran.model.query.FlowQuery;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubFlowManagementServiceImpl implements SubFlowManagementService {

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private SubFlowQueryService subFlowQueryService;

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
        setCreatedBy(subFlowEntity);
        subFlowRepository.save(subFlowEntity);
        if (subFlowEntity.getStatus() != FlowStatus.Error) {
            testFlowInitializer.reloadTestSubFlow(subFlowEntity.getId());
        }
        return subFlowEntity.getId();
    }

    @Override
    public void deleteFlow(Long flowId) {
        Optional<SubFlowEntity> optional = subFlowRepository.findById(flowId);
        SubFlowEntity subFlowEntity = optional.get();
        subFlowEntity.setExist(false);
        subFlowRepository.save(subFlowEntity);
    }

    @Override
    public SubFlowDTO updateFlow(SubFlowDTO flowModel) {
        SubFlowEntity subFlowEntity = buildEntity(flowModel);
        setFlowStatus(subFlowEntity);
        setUpdatedBy(subFlowEntity);
        subFlowRepository.save(subFlowEntity);
        if (subFlowEntity.getStatus() != FlowStatus.Error) {
            testFlowInitializer.reloadTestSubFlow(subFlowEntity.getId());
        }
        return flowConvertor.toDTO(subFlowEntity);
    }

    @Override
    public List<SubFlowDTO> queryFlows(FlowQuery query) {
        // TODO
        return null;
    }

    @Override
    public List<SubFlowDTO> list() {
        List<SubFlowEntity> entities = subFlowRepository.findByIsExistTrue();
        List<SubFlowDTO> models = new LinkedList<>();
        for (SubFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }
        return models;
    }

    @Nullable
    @Override
    public SubFlowDTO getById(Long flowId) {
        SubFlowEntity flowEntity = subFlowRepository.findById(flowId).get();
        if (flowEntity == null) {
            return null;
        }
        return flowConvertor.toDTO(flowEntity);
    }

    @Override
    public Long copyFlow(CopyFlow copyFlow) {
        SubFlowEntity flowEntity = subFlowRepository.findById(copyFlow.getId()).get();
        if (flowEntity == null) {
            return null;
        }
        SubFlowEntity newFlowEntity = new SubFlowEntity();

        BeanUtils.copyProperties(flowEntity, newFlowEntity);
        newFlowEntity.setId(null);
        newFlowEntity.setName(copyFlow.getName());
        subFlowRepository.save(newFlowEntity);
        return newFlowEntity.getId();
    }

    @Override
    public List<FlowValidation> validateFlow(SubFlowDTO model) {
        SubFlowEntity entity = buildEntity(model);
        return validateFlow(entity);
    }

    @Override
    public List<BasicFlowInfo> listBasicSubFlowInfoByModuleId(Long moduleId) {
        return subFlowQueryService.listBasicInfoByModuleId(moduleId);
    }

    private void setFlowStatus(SubFlowEntity flowEntity) {
        FlowStatus flowStatus = FlowStatus.Available;
        for (FlowValidation flowValidation : validateFlow(flowEntity)) {
            if (flowValidation.getMessage().getLevel() == ValidateMessageLevel.Error) {
                flowStatus = FlowStatus.Error;
                break;
            }
            if (flowStatus == FlowStatus.Available) {
                flowStatus = FlowStatus.Warning;
            }
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
            flowEntity = subFlowRepository.findById(id).get();
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
        flowEntity.setExist(true);

        return flowEntity;
    }

    private void setCreatedBy(SubFlowEntity subFlowEntity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            subFlowEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private void setUpdatedBy(SubFlowEntity subFlowEntity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            subFlowEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
    }
}
