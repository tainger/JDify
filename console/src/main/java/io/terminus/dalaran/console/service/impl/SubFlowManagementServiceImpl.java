package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.console.model.UserContext;
import io.terminus.dalaran.console.repository.SubFlowRepository;
import io.terminus.dalaran.console.service.SubFlowManagementService;
import io.terminus.dalaran.console.service.jpa.SubFlowQueryService;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
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
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
    private SubFlowQueryService subFlowQueryService;

    @Autowired
    private TestFlowInitializer testFlowInitializer;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private DalaranFlowBuilder flowBuilder;

    private final FlowConvertor flowConvertor = new FlowConvertor();

    @Override
    public String createFlow(SubFlowDTO flowModel) {
        SubFlowEntity subFlowEntity = buildEntity(flowModel);
        setFlowStatus(subFlowEntity);
        setCreatedBy(subFlowEntity);
        subFlowEntity.setOnline(true);
        subFlowRepository.save(subFlowEntity);
        if (subFlowEntity.getStatus() != FlowStatus.Error) {
            testFlowInitializer.reloadTestSubFlow(subFlowEntity.getResourceKey());
        }
        return subFlowEntity.getResourceKey();
    }

    @Override
    public void deleteFlow(String flowId) {
        SubFlowEntity entity = subFlowRepository.findByResourceKey(flowId);
        entity.setExist(false);
        subFlowRepository.save(entity);
    }

    @Override
    public SubFlowDTO updateFlow(SubFlowDTO flowModel) {
        SubFlowEntity subFlowEntity = buildEntity(flowModel);
        setFlowStatus(subFlowEntity);
        setUpdatedBy(subFlowEntity);
        subFlowRepository.save(subFlowEntity);
        if (subFlowEntity.getStatus() != FlowStatus.Error) {
            testFlowInitializer.reloadTestSubFlow(subFlowEntity.getResourceKey());
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
    public SubFlowDTO getById(String flowId) {
        SubFlowEntity flowEntity = subFlowRepository.findByResourceKey(flowId);
        if (flowEntity == null) {
            return null;
        }
        return flowConvertor.toDTO(flowEntity);
    }

    @Override
    public String copyFlow(CopyFlow copyFlow) {
        SubFlowEntity flowEntity = subFlowRepository.findByResourceKey(copyFlow.getId());
        if (flowEntity == null) {
            return null;
        }
        SubFlowEntity newFlowEntity = new SubFlowEntity();
        try {
            BeanUtils.copyProperties(newFlowEntity, flowEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        newFlowEntity.setId(null);
        newFlowEntity.setName(copyFlow.getName());
        newFlowEntity.setResourceKey("copy_" + copyFlow.getId() + "_" + RandomStringUtils.random(4, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN"));
        subFlowRepository.save(newFlowEntity);
        return newFlowEntity.getResourceKey();
    }

    @Override
    public List<FlowValidation> validateFlow(SubFlowDTO model) {
        SubFlowEntity entity = buildEntity(model);
        return validateFlow(entity);
    }

    @Override
    public List<BasicFlowInfo> listBasicSubFlowInfoByModuleId(String moduleId) {
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
        String resourceKey = model.getId();
        if (StringUtils.isNotBlank(resourceKey)) {
            flowEntity = subFlowRepository.findByResourceKey(resourceKey);
        } else {
            flowEntity = new SubFlowEntity();
            resourceKey = GenerateKeyUtils.resourceKey();
        }
        List<ProcessorEntity> pipeline = model.getPipeline().stream().map(processor -> {
            ProcessorEntity processorEntity = new ProcessorEntity();
            try {
                BeanUtils.copyProperties(processorEntity, processor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            processorEntity.setConfig(JSON.toJSONString(processor.getConfig()));
            return processorEntity;
        }).collect(Collectors.toList());

        String name = model.getName();
        if (StringUtils.isNoneBlank(name)) {
            flowEntity.setName(name);
        } else {
            flowEntity.setName("Dalaran Flow");
        }
        flowEntity.setResourceKey(resourceKey);
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
