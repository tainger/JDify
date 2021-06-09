package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.SourceType;
import io.terminus.dalaran.component.foreach.ForEachConfig;
import io.terminus.dalaran.component.loopwhile.LoopWhileConfig;
import io.terminus.dalaran.component.multicast.ScatterGatherConfig;
import io.terminus.dalaran.component.retry.RetryConfig;
import io.terminus.dalaran.component.service.soap.SoapServiceConfig;
import io.terminus.dalaran.component.service.swagger.SwaggerOperationConfig;
import io.terminus.dalaran.component.service.swagger.SwaggerServiceConfig;
import io.terminus.dalaran.component.subflow.DalaranSubFlowConfig;
import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.config.ServiceInfo;
import io.terminus.dalaran.config.TriggerInfo;
import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.VersionUpdateService;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.config.*;
import io.terminus.dalaran.core.component.model.FunctionType;
import io.terminus.dalaran.core.component.model.MappingFunction;
import io.terminus.dalaran.core.component.model.MappingType;
import io.terminus.dalaran.core.component.model.SimpleMapping;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.context.support.DefaultDalaranServiceContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import io.terminus.dalaran.model.soap.model.SoapOperationConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
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

    @Autowired
    private DalaranComponentContext dalaranComponentContext;

    @Autowired
    private DefaultDalaranServiceContext defaultDalaranServiceContext;

    @Autowired
    private DalaranResourceBuilder dalaranResourceBuilder;

    @Autowired
    private DalaranServiceContext serviceContext;

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
            String serviceConfig = serviceEntity.getServiceConfig();
            String type = serviceEntity.getType();
            ServiceInfo serviceInfo = serviceContext.getServiceInfo(type);
            Class serviceConfigType = serviceInfo.getServiceConfigType();
            Object serviceConfigObject = JSONObject.parseObject(serviceConfig, serviceConfigType);
            JSONObject jsonObject = JSONObject.parseObject(serviceConfig);
            JSONArray jsonArray = (JSONArray) jsonObject.get("configs");
            if (serviceConfigObject instanceof SwaggerServiceConfig) {
                SwaggerServiceConfig swaggerServiceConfig = (SwaggerServiceConfig) serviceConfigObject;
                List<SwaggerOperationConfig> configs = swaggerServiceConfig.getConfigs();
                int i = 0;
                for (SwaggerOperationConfig config : configs) {
                    i++;
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                    config.setInModelId(String.valueOf(jsonObjectItem.get("inModelId")));
                    config.setOutModelId(String.valueOf(jsonObjectItem.get("outModelId")));
                }
            }

            if (serviceConfigObject instanceof SoapOperationConfig) {
                SoapServiceConfig soapServiceConfig = (SoapServiceConfig) serviceConfigObject;
                List<SoapOperationConfig> configs = soapServiceConfig.getConfigs();
                int i = 0;
                for (SoapOperationConfig config : configs) {
                    i++;
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                    config.setInModelId(String.valueOf(jsonObjectItem.get("inModelId")));
                    config.setOutModelId(String.valueOf(jsonObjectItem.get("outModelId")));
                }
            }

            serviceEntity.setServiceConfig(JSONObject.toJSONString(serviceConfigObject));
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
        triggerFlowEntityList.forEach((item) -> {
            log.error("第{}条流程", item.getId());
            handleTriggerFlow(item);
            flowRepository.save(item);
        });
    }

    private void handleTriggerFlow(TriggerFlowEntity flowEntity) {
        flowEntity.setResourceKey(String.valueOf(flowEntity.getId()));
        flowEntity.setOnline(true);
        List<ProcessorEntity> processorEntities = flowEntity.getPipeline();
        handleTriggerConfig(flowEntity);
        for (ProcessorEntity processorEntity : processorEntities) {
            handleProcessor(processorEntity);
        }
        flowEntity.setPipeline(processorEntities);
    }

    private Class getProcessorConfig(String processorType) {
        return dalaranComponentContext.getProcessorConfigMap().get(processorType);
    }

    private Class getTriggerType(String triggerType) {
        return dalaranComponentContext.getTriggerConfigMap().get(triggerType);
    }


    private void handleProcessor(ProcessorEntity processorEntity) {
        String processorEntityType = processorEntity.getType();
        Class processorConfigType = getProcessorConfig(processorEntityType);
        String processorEntityConfig = processorEntity.getConfig();
        Object processorConfig = JSONObject.parseObject(processorEntityConfig, processorConfigType);
        Map<String, Object> jsonObject = JSONObject.parseObject(processorEntityConfig, Map.class);

        if (processorConfig instanceof RetryConfig) {
            List<ProcessorRouteInfo> pipeline = ((RetryConfig) processorConfig).getPipeline();
            for (ProcessorRouteInfo processorRouteInfo : pipeline) {
                ProcessorEntity transformProcessorEntity = new ProcessorEntity();
                BeanUtils.copyProperties(processorRouteInfo, transformProcessorEntity);
                handleProcessor(transformProcessorEntity);
            }
        }

        if (processorConfig instanceof ScatterGatherConfig) {
            List<ScatterGatherConfig.Branch> branches = ((ScatterGatherConfig) processorConfig).getBranches();
            for (ScatterGatherConfig.Branch branch : branches) {
                List<ProcessorRouteInfo> branchPipeline = branch.getPipeline();
                if(CollectionUtils.isNotEmpty(branchPipeline)) {
                    for (ProcessorRouteInfo processorRouteInfo : branchPipeline) {
                        ProcessorEntity branchProcessor = new ProcessorEntity();
                        BeanUtils.copyProperties(processorRouteInfo, branchProcessor);
                        handleProcessor(branchProcessor);
                    }
                }
            }
        }

        if (processorConfig instanceof DalaranSubFlowConfig) {
            String subFlowId = ((DalaranSubFlowConfig) processorConfig).getSubFlowId();
            SubFlowEntity subFlowEntity = subFlowRepository.findByResourceKey(subFlowId);
            List<ProcessorEntity> pipeline = subFlowEntity.getPipeline();
            if(CollectionUtils.isNotEmpty(pipeline)) {
                for (ProcessorEntity subProcessorEntity : pipeline) {
                    handleProcessor(subProcessorEntity);
                }
            }
        }

        if (processorConfig instanceof LoopWhileConfig) {
            LoopWhileConfig loopWhileConfig = (LoopWhileConfig) processorConfig;
            List<ProcessorRouteInfo> loopWhilePipeline = loopWhileConfig.getPipeline();
            if(CollectionUtils.isNotEmpty(loopWhilePipeline)) {
                for (ProcessorRouteInfo processorRouteInfo : loopWhilePipeline) {
                    ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
                    BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
                    handleProcessor(loopWhileProcessorEntity);
                }
            }
        }

        if (processorConfig instanceof ServiceOperationConfig) {
            ServiceOperationConfig serviceOperationConfig = ((ServiceOperationConfig) processorConfig);
            String serviceId = String.valueOf(jsonObject.get("serviceId"));
            serviceOperationConfig.setServiceId(serviceId);
        }

        if (processorConfig instanceof ForEachConfig) {
            List<ProcessorRouteInfo> pipeline = ((ForEachConfig) processorConfig).getPipeline();
            if(CollectionUtils.isNotEmpty(pipeline)) {
                for (ProcessorRouteInfo processorRouteInfo : pipeline) {
                    ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
                    BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
                    handleProcessor(loopWhileProcessorEntity);
                }
            }

        }

        if (processorConfig instanceof RetryConfig) {
            List<ProcessorRouteInfo> pipeline = ((RetryConfig) processorConfig).getPipeline();
            if(CollectionUtils.isNotEmpty(pipeline)) {
                for (ProcessorRouteInfo processorRouteInfo : pipeline) {
                    ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
                    BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
                    handleProcessor(loopWhileProcessorEntity);
                }
            }
        }


        if (processorConfig instanceof DalaranMapperConfig) {

            DalaranMapperConfig dalaranMapperConfig = (DalaranMapperConfig) processorConfig;
            HashMap<String, SimpleMapping> messageMappingMap = dalaranMapperConfig.getMessageMapping();
            JSONObject messageMappingMapJsonObject = (JSONObject) jsonObject.get("messageMapping");
            if(null != messageMappingMap && null != messageMappingMapJsonObject && !messageMappingMap.isEmpty()&& !messageMappingMapJsonObject.isEmpty()) {
                for (Map.Entry<String, SimpleMapping> entry : messageMappingMap.entrySet()) {
                    String key = entry.getKey();
                    SimpleMapping simpleMapping = entry.getValue();
                    if (simpleMapping.getMappingType() == MappingType.FUNCTION) {
                        MappingFunction mappingFunction = (MappingFunction) simpleMapping.getValue();
                        if (mappingFunction.getType() == FunctionType.STATIC) {
                            continue;
                        }
                        JSONObject simpleMappingJSONObject = (JSONObject) messageMappingMapJsonObject.get(key);
                        mappingFunction.setId(String.valueOf(simpleMappingJSONObject.get("id")));
                    }
                }
            }

            List<SimpleMapping> noDestinationMappings = dalaranMapperConfig.getNoDestinationMappings();
            if (CollectionUtils.isNotEmpty(noDestinationMappings)) {
                JSONArray noDestinationMappingsJsonObject = (JSONArray) jsonObject.get("noDestinationMappings");
                int i = 0;
                for (SimpleMapping simpleMapping : noDestinationMappings) {
                    i++;
                    if (simpleMapping.getMappingType() == MappingType.FUNCTION) {
                        MappingFunction mappingFunction = (MappingFunction) (simpleMapping.getValue());
                        if (mappingFunction.getType() == FunctionType.STATIC) {
                            continue;
                        }
                        JSONObject simpleMappingJSONObject = (JSONObject) noDestinationMappingsJsonObject.get(i);
                        String id = String.valueOf(simpleMappingJSONObject.get("id"));
                        mappingFunction.setId(id);
                    }

                }
            }
        }

        if (processorConfig instanceof AllModelConfig) {
            AllModelConfig allModelConfig = (AllModelConfig) processorConfig;
            allModelConfig.setInModelId(String.valueOf(jsonObject.get("inModelId")));
            allModelConfig.setOutModelId(String.valueOf(jsonObject.get("outModelId")));
        }

        if (processorConfig instanceof InModelConfig) {
            InModelConfig inModelConfig = (InModelConfig) processorConfig;
            inModelConfig.setInModelId(String.valueOf(jsonObject.get("inModelId")));
        }

        if (processorConfig instanceof OutModelConfig) {
            OutModelConfig outModelConfig = (OutModelConfig) processorConfig;
            outModelConfig.setOutModelId(String.valueOf(jsonObject.get("outModelId")));
        }
        if (processorConfig instanceof ConnectorConfig) {
            ConnectorConfig connector = (ConnectorConfig) processorConfig;
            connector.setConnectorId(String.valueOf(jsonObject.get("connectorId")));
        }


        if (processorConfig instanceof AuthenticatorConfig) {
            AuthenticatorConfig authenticatorConfig = (AuthenticatorConfig) processorConfig;
            authenticatorConfig.setAuthenticatorId(String.valueOf(jsonObject.get("authenticatorId")));
        }

        if (processorConfig instanceof LimiterConfig) {
            LimiterConfig limiterConfig = (LimiterConfig) processorConfig;
            limiterConfig.setLimiterId(String.valueOf(jsonObject.get("limitId")));
        }
        processorEntityConfig = JSONObject.toJSONString(processorConfig);
        processorEntity.setConfig(processorEntityConfig);
    }

    private void handleTriggerConfig(TriggerFlowEntity flowEntity) {
        String triggerConfig = flowEntity.getTriggerConfig();
        String triggerType = flowEntity.getTriggerType();
        Class triggerTypeClass = getTriggerType(triggerType);
        Object configObject = JSONObject.parseObject(triggerConfig, triggerTypeClass);
        Map map = JSONObject.parseObject(triggerConfig, Map.class);
        if (configObject instanceof AllModelConfig) {
            AllModelConfig allModelConfig = (AllModelConfig) configObject;
            allModelConfig.setInModelId(String.valueOf(map.get("inModelId")));
            allModelConfig.setOutModelId(String.valueOf(map.get("outModelId")));
        }

        if (configObject instanceof InModelConfig) {
            InModelConfig inModelConfig = (InModelConfig) configObject;
            inModelConfig.setInModelId(String.valueOf(map.get("inModelId")));
        }

        if (configObject instanceof OutModelConfig) {
            OutModelConfig outModelConfig = (OutModelConfig) configObject;
            outModelConfig.setOutModelId(String.valueOf(map.get("outModelId")));
        }
        if (configObject instanceof ConnectorConfig) {
            ConnectorConfig connector = (ConnectorConfig) configObject;
            connector.setConnectorId(String.valueOf(map.get("connectorId")));
        }


        if (configObject instanceof AuthenticatorConfig) {
            AuthenticatorConfig authenticatorConfig = (AuthenticatorConfig) configObject;
            authenticatorConfig.setAuthenticatorId(String.valueOf(map.get("authenticatorId")));
        }

        if (configObject instanceof LimiterConfig) {
            LimiterConfig limiterConfig = (LimiterConfig) configObject;
            limiterConfig.setLimiterId(String.valueOf(map.get("limitId")));
        }
        flowEntity.setTriggerConfig(JSONObject.toJSONString(configObject));
    }

}
