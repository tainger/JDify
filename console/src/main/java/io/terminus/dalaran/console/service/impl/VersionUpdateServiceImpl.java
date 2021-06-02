package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.SourceType;
import io.terminus.dalaran.component.foreach.ForEachConfig;
import io.terminus.dalaran.component.loopwhile.LoopWhileConfig;
import io.terminus.dalaran.component.multicast.ScatterGatherConfig;
import io.terminus.dalaran.component.retry.RetryConfig;
import io.terminus.dalaran.component.subflow.DalaranSubFlowConfig;
import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.VersionUpdateService;
import io.terminus.dalaran.core.component.config.ServiceOperationConfig;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class VersionUpdateServiceImpl implements VersionUpdateService {

    @Autowired
    private TriggerFlowRepository flowRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private DalaranContext dalaranContext;

    @Override
    @Transactional
    public void cleanOldData() {
        List<ModuleEntity> moduleEntityList = moduleRepository.findAll();
        moduleEntityList.forEach(moduleEntity -> {
            if (StringUtils.isNotBlank(moduleEntity.getResourceKey())) {
                return;
            }
            moduleEntity.setResourceKey(String.valueOf(moduleEntity.getId()));
            moduleRepository.save(moduleEntity);
        });

        List<ModelEntity> modelEntityList = modelRepository.findAll();
        modelEntityList.forEach(modelEntity -> {
            if (StringUtils.isNotBlank(modelEntity.getResourceKey())) {
                return;
            }
            modelEntity.setResourceKey(String.valueOf(modelEntity.getId()));
            modelRepository.save(modelEntity);
        });

        List<ConnectorEntity> connectorEntityList = connectorRepository.findAll();
        connectorEntityList.forEach(connectorEntity -> {
            if (StringUtils.isNotBlank(connectorEntity.getResourceKey())) {
                return;
            }
            connectorEntity.setResourceKey(String.valueOf(connectorEntity.getId()));
            connectorRepository.save(connectorEntity);
        });

        List<FunctionEntity> functionEntityList = functionRepository.findAll();
        functionEntityList.forEach(functionEntity -> {
            if (StringUtils.isNotBlank(functionEntity.getResourceKey())) {
                return;
            }
            functionEntity.setResourceKey(String.valueOf(functionEntity.getId()));
            functionRepository.save(functionEntity);
        });

        List<ServiceEntity> serviceEntityList = serviceRepository.findAll();
        serviceEntityList.forEach(serviceEntity -> {
            if (StringUtils.isNotBlank(serviceEntity.getResourceKey())) {
                return;
            }
            serviceEntity.setResourceKey(String.valueOf(serviceEntity.getId()));
            serviceRepository.save(serviceEntity);
        });

        List<SubFlowEntity> subFlowEntityList = subFlowRepository.findAll();
        subFlowEntityList.forEach(subFlowEntity -> {
            if (StringUtils.isNotBlank(subFlowEntity.getResourceKey())) {
                return;
            }
            subFlowEntity.setResourceKey(String.valueOf(subFlowEntity.getId()));
            subFlowRepository.save(subFlowEntity);
        });

        List<TriggerFlowEntity> triggerFlowEntityList = flowRepository.findAll();
        triggerFlowEntityList.forEach(this::handleTriggerFlow);
    }

    private void handleTriggerFlow(TriggerFlowEntity flowEntity) {
        flowEntity.setResourceKey(String.valueOf(flowEntity.getId()));
        flowEntity.setOnline(true);
        List<ProcessorEntity> processorEntities = flowEntity.getPipeline();
        for (ProcessorEntity processorEntity : processorEntities) {
//            handleProcessor(processorEntity);
        }

        handleTriggerConfig(flowEntity);
    }

    private void handleTriggerConfig(TriggerFlowEntity flowEntity) {
        String triggerConfig = flowEntity.getTriggerConfig();
        Map map = JSONObject.parseObject(triggerConfig, Map.class);
        if (map.containsKey("inModelId")) {
            map.put("inModelId", String.valueOf(map.get("inModelId")));
        }
        if (map.containsKey("outModelId")) {
            map.put("outModelId", String.valueOf(map.get("outModelId")));
        }
        if (map.containsKey("connectorId")) {
            map.put("connectorId", String.valueOf(map.get("connectorId")));
        }
        if (map.containsKey("limiterId")) {
            map.put("limiterId", String.valueOf(map.get("limiterId")));
        }
        flowEntity.setTriggerConfig(JSONObject.toJSONString(map));
    }

}
//
//    private void handleProcessor(ProcessorEntity processorEntity) {
//
//
//        String type = processorEntity.getType();
//        String processorEntityConfig = processorEntity.getConfig();
//
//
//        if ("scatter-gather".equals(type)) {
//            ScatterGatherConfig scatterGatherConfig = JSONObject.parseObject(processorEntityConfig, ScatterGatherConfig.class);
//            List<ScatterGatherConfig.Branch> branches = scatterGatherConfig.getBranches();
//            for (ScatterGatherConfig.Branch branch : branches) {
//                List<ProcessorRouteInfo> branchPipeline = branch.getPipeline();
//                for (ProcessorRouteInfo processorRouteInfo : branchPipeline) {
//                    ProcessorEntity branchProcessor = new ProcessorEntity();
//                    BeanUtils.copyProperties(processorRouteInfo, branchProcessor);
//                    handleProcessor(branchProcessor);
//                }
//            }
//        }
//
//        if ("sub-flow".equals(type)) {
//            DalaranSubFlowConfig dalaranSubFlowConfig = JSONObject.parseObject(processorEntityConfig, DalaranSubFlowConfig.class);
//            String subFlowId = dalaranSubFlowConfig.getSubFlowId();
//            SubFlowEntity subFlowEntity = subFlowRepository.findByResourceKey(subFlowId);
//            List<ProcessorEntity> pipeline = subFlowEntity.getPipeline();
//            for (ProcessorEntity subProcessorEntity : pipeline) {
//                handleProcessor(subProcessorEntity);
//            }
//        }
//
//        if ("loop-while".equals(type)) {
//            LoopWhileConfig loopWhileConfig = JSONObject.parseObject(processorEntityConfig, LoopWhileConfig.class);
//            List<ProcessorRouteInfo> loopWhilePipeline = loopWhileConfig.getPipeline();
//            for (ProcessorRouteInfo processorRouteInfo : loopWhilePipeline) {
//                ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
//                BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
//                handleProcessor(loopWhileProcessorEntity);
//            }
//        }
//
//        if ("service".equals(type)) {
//            ServiceOperationConfig serviceOperationConfig = JSONObject.parseObject(processorEntityConfig, ServiceOperationConfig.class);
//            String serviceId = serviceOperationConfig.getServiceId();
//            ServiceEntity service = serviceRepository.findByResourceKey(serviceId);
//            String serviceConfig = service.getServiceConfig();
//            //handle
//            Map<String, Object> map = JSONObject.parseObject(serviceConfig, Map.class);
//            map.get("");
//        }
//
//
//        if ("foreach".equals(type)) {
//            ForEachConfig forEachConfig = JSONObject.parseObject(processorEntityConfig, ForEachConfig.class);
//            List<ProcessorRouteInfo> pipeline = forEachConfig.getPipeline();
//            for (ProcessorRouteInfo processorRouteInfo : pipeline) {
//                ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
//                BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
//                collectProcessorResourceKey(loopWhileProcessorEntity);
//            }
//        }
//
//        if ("retry".equals(type)) {
//            RetryConfig retryConfig = JSONObject.parseObject(processorEntityConfig, RetryConfig.class);
//            List<ProcessorRouteInfo> pipeline = retryConfig.getPipeline();
//            for (ProcessorRouteInfo processorRouteInfo : pipeline) {
//                ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
//                BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
//                collectProcessorResourceKey(loopWhileProcessorEntity);
//            }
//        }
//
//    }
//
//    private void collectProcessorResourceKey(ProcessorEntity processorEntity) {
//        String type = processorEntity.getType();
//        String processorEntityConfig = processorEntity.getConfig();
//
//        if ("scatter-gather".equals(type)) {
//            ScatterGatherConfig scatterGatherConfig = JSONObject.parseObject(processorEntityConfig, ScatterGatherConfig.class);
//            List<ScatterGatherConfig.Branch> branches = scatterGatherConfig.getBranches();
//            for (ScatterGatherConfig.Branch branch : branches) {
//                List<ProcessorRouteInfo> branchPipeline = branch.getPipeline();
//                for (ProcessorRouteInfo processorRouteInfo : branchPipeline) {
//                    ProcessorEntity branchProcessor = new ProcessorEntity();
//                    BeanUtils.copyProperties(processorRouteInfo, branchProcessor);
//                    collectProcessorResourceKey(branchProcessor);
//                }
//            }
//        }
//
//        if ("sub-flow".equals(type)) {
//            DalaranSubFlowConfig dalaranSubFlowConfig = JSONObject.parseObject(processorEntityConfig, DalaranSubFlowConfig.class);
//            String subFlowId = dalaranSubFlowConfig.getSubFlowId();
//            SubFlowEntity subFlowEntity = subFlowRepository.findByResourceKey(subFlowId);
//            List<ProcessorEntity> pipeline = subFlowEntity.getPipeline();
//            for (ProcessorEntity subProcessorEntity : pipeline) {
//                collectProcessorResourceKey(subProcessorEntity);
//            }
//        }
//
//        if ("loop-while".equals(type)) {
//            LoopWhileConfig loopWhileConfig = JSONObject.parseObject(processorEntityConfig, LoopWhileConfig.class);
//            List<ProcessorRouteInfo> loopWhilePipeline = loopWhileConfig.getPipeline();
//            for (ProcessorRouteInfo processorRouteInfo : loopWhilePipeline) {
//                ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
//                BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
//                collectProcessorResourceKey(loopWhileProcessorEntity);
//            }
//        }
//
//        if ("service".equals(type)) {
//            ServiceOperationConfig serviceOperationConfig = JSONObject.parseObject(processorEntityConfig, ServiceOperationConfig.class);
//            String serviceId = serviceOperationConfig.getServiceId();
//            ServiceEntity service = serviceRepository.findByResourceKey(serviceId);
//            String serviceConfig = service.getServiceConfig();
//            collectServiceResourceKey(serviceConfig);
//        }
//
//        if ("foreach".equals(type)) {
//            ForEachConfig forEachConfig = JSONObject.parseObject(processorEntityConfig, ForEachConfig.class);
//            List<ProcessorRouteInfo> pipeline = forEachConfig.getPipeline();
//            for (ProcessorRouteInfo processorRouteInfo : pipeline) {
//                ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
//                BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
//                collectProcessorResourceKey(loopWhileProcessorEntity);
//            }
//        }
//
//        if ("retry".equals(type)) {
//            RetryConfig retryConfig = JSONObject.parseObject(processorEntityConfig, RetryConfig.class);
//            List<ProcessorRouteInfo> pipeline = retryConfig.getPipeline();
//            for (ProcessorRouteInfo processorRouteInfo : pipeline) {
//                ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
//                BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
//                collectProcessorResourceKey(loopWhileProcessorEntity);
//            }
//        }
//
//        String processorType = processorEntity.getType();
//        ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getGroup(),processorType,processorEntity.getVersion());
//        if (StringUtils.equalsIgnoreCase(processorInfo.getOrigin(), DalaranConstants.PARTNER)) {
//            List<PrivateRepositoryEntity> privateRepositoryEntity = privateResourceQueryService.query(new PrivateRepositoryQuery(processorInfo.getName(), DalaranConstants.PROCESSOR));
//            if (CollectionUtils.isNotEmpty(privateRepositoryEntity)) {
//                PrivateRepositoryEntity repositoryEntity = privateRepositoryEntity.get(0);
//                flowsCollector.collect(SourceType.PRIVATE_REPOSITORY, repositoryEntity.getResourceKey() + "#" + repositoryEntity.getVersion());
//            }
//        }
//        collectProcessResourceKey(processorEntity);
//    }
//}
