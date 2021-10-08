package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.ModelImportMode;
import io.terminus.dalaran.component.foreach.ForEachConfig;
import io.terminus.dalaran.component.loopwhile.LoopWhileConfig;
import io.terminus.dalaran.component.multicast.ScatterGatherConfig;
import io.terminus.dalaran.component.retry.RetryConfig;
import io.terminus.dalaran.component.router.DalaranRouterConfig;
import io.terminus.dalaran.component.subflow.DalaranSubFlowConfig;
import io.terminus.dalaran.config.ProcessorInfo;
import io.terminus.dalaran.config.TriggerInfo;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.model.FlowTemplate;
import io.terminus.dalaran.console.model.TemplateData;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.PrivateRepositoryService;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import io.terminus.dalaran.console.service.jpa.PrivateResourceQueryService;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.core.component.config.*;
import io.terminus.dalaran.core.component.model.FunctionType;
import io.terminus.dalaran.core.component.model.MappingFunction;
import io.terminus.dalaran.core.component.model.MappingType;
import io.terminus.dalaran.core.component.model.SimpleMapping;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceLoader;
import io.terminus.dalaran.core.resource.entity.ModelAbstractEntity;
import io.terminus.dalaran.core.resource.entity.SubFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.basic.BasicFlowEntity;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.core.resource.property.PropertyService;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.core.resource.redis.RedisUtil;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.core.resource.repository.PrivateRepositoryRepository;
import io.terminus.dalaran.core.resource.repository.TriggerFlowReleasedRepository;
import io.terminus.dalaran.exception.flow.FlowNotExistException;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.ModelTargetType;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import io.terminus.dalaran.model.component.RoutesModel;
import io.terminus.dalaran.model.dto.*;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.model.dto.flow.BasicFlowInfoDTO;
import io.terminus.dalaran.model.dto.flow.BindAlarmRuleDTO;
import io.terminus.dalaran.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.model.flow.ValidateMessageLevel;
import io.terminus.dalaran.model.market.ResourceFile;
import io.terminus.dalaran.model.query.FlowQuery;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import io.terminus.dalaran.response.ResponseErrorMsg;
import io.terminus.dalaran.response.ResponseResult;
import io.terminus.draco.web.autoconfig.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import static io.terminus.dalaran.DalaranConstants.*;

@Slf4j
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
    private ConnectorRepository connectorRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private TestFlowInitializer testFlowInitializer;

    @Autowired
    private DalaranFlowBuilder flowBuilder;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private DalaranResourceLoader resourceLoader;

    @Autowired
    private ModelManagementService modelService;

    @Autowired
    private TriggerFlowReleasedRepository triggerFlowReleasedRepository;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TriggerFlowAlarmRuleRepository triggerFlowAlarmRuleRepository;

    @Autowired
    private PrivateRepositoryService privateRepositoryService;

    @Autowired
    private PrivateRepositoryRepository privateRepositoryRepository;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PrivateModelRepository privateModelRepository;

    @Autowired
    private PrivateConnectorRepository privateConnectorRepository;

    @Autowired
    private PrivateFunctionRepository privateFunctionRepository;

    @Autowired
    private PrivateServiceRepository privateServiceRepository;

    @Autowired
    private PrivateSubFlowRepository privateSubFlowRepository;

    @Autowired
    private PrivatePackageRepository privatePackageRepository;

    @Autowired
    private PrivateResourceQueryService privateResourceQueryService;

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
    public String saveFlow(TriggerFlowEntity flowEntity) {
        setFlowStatus(flowEntity);
        String id = flowRepository.save(flowEntity).getResourceKey();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        if (flowEntity.getStatus() != FlowStatus.Error) {
            testFlowInitializer.reloadTestTriggerFlow(id);
        }
        return id;
    }

    @Override
    public String createFlow(TriggerFlowDTO flowModel) {
        TriggerFlowEntity flowEntity = new TriggerFlowEntity();
        buildEntity(flowModel, flowEntity);
        setFlowStatus(flowEntity);
        setCreatedBy(flowEntity);
        flowEntity.setOnline(true);
        String id = flowRepository.save(flowEntity).getResourceKey();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        if (flowEntity.getStatus() != FlowStatus.Error) {
            testFlowInitializer.reloadTestTriggerFlow(id);
        }
        return id;
    }

    @Override
    public BasicResponse createFromTemplate(BasicResourceRequest template) {
        PrivateRepositoryEntity entity = privateResourceQueryService.findByResourceKeyAndVersion(template.getId(), template.getVersion()).get(0);
        TemplateData templateData = JSON.parseObject(entity.getData(), TemplateData.class);
        log.info("templateData: " + JSON.toJSONString(templateData));
        Map<String, String> resourceKeyMap = new HashMap<>();
        String id;
        try {
            copyResourceFromPrivateRepo(templateData, resourceKeyMap, template.getModuleId());
            switch (template.getType()) {
                case FLOW_TEMPLATE:
                    TriggerFlowEntity triggerFlowEntity = new TriggerFlowEntity();
                    triggerFlowEntity = (TriggerFlowEntity) buildFlowEntity(triggerFlowEntity, templateData, resourceKeyMap);
                    triggerFlowEntity.setCreatedFrom(entity.getResourceKey());
                    triggerFlowEntity.setModuleId(template.getModuleId());
                    triggerFlowEntity.setName(entity.getName());
                    triggerFlowEntity.setOnline(true);
                    triggerFlowEntity.setResourceKey(GenerateKeyUtils.resourceKey(propertyService.getTenantCode()));
                    triggerFlowEntity.setId(null);
                    log.info("now triggerFlowEntity: " + JSON.toJSONString(triggerFlowEntity));
                    id = flowRepository.save(triggerFlowEntity).getResourceKey();
                    break;
                case SUB_FLOW_TEMPLATE:
                default:
                    SubFlowEntity subFlowEntity = new SubFlowEntity();
                    subFlowEntity = (SubFlowEntity) buildFlowEntity(subFlowEntity, templateData, resourceKeyMap);
                    subFlowEntity.setCreatedFrom(entity.getResourceKey());
                    subFlowEntity.setModuleId(template.getModuleId());
                    subFlowEntity.setName(entity.getName());
                    subFlowEntity.setResourceKey(GenerateKeyUtils.resourceKey(propertyService.getTenantCode()));
                    subFlowEntity.setId(null);
                    id = subFlowRepository.save(subFlowEntity).getResourceKey();
            }
            return new BasicResponse(true, id);
        } catch (Exception e) {
            e.printStackTrace();
            return new BasicResponse(false, e.getMessage());
        }
    }

    private Object buildFlowEntity(BasicFlowEntity flowEntity, TemplateData templateData, Map<String, String> resourceKeyMap) throws Exception {
        BeanUtils.copyProperties(flowEntity, templateData);
        String oldConfig = JSON.toJSONString(flowEntity);
        log.info("old config: " + oldConfig);

        log.info("key map: " + JSON.toJSONString(resourceKeyMap));

//        BasicFlowEntity newFlowEntity = new BasicFlowEntity();
//
//        log.info("new config before1 : " + JSON.toJSONString(newFlowEntity));
//
//        BeanUtils.copyProperties(newFlowEntity, templateData);

        replaceResourceKey(flowEntity, resourceKeyMap);

        //临时过渡下。。
//        Map<String, String> newResourceKeyMap = new HashMap<>();
//        resourceKeyMap.forEach((k, v) -> {
//            newResourceKeyMap.put(":"+ k, ":" + v);
//        });

//        String newConfig = StringUtils.replaceEach(oldConfig, ArrayUtils.toStringArray(newResourceKeyMap.keySet().toArray()), ArrayUtils.toStringArray(newResourceKeyMap.values().toArray()));

        log.info("new config: " + JSON.toJSONString(flowEntity));
//        if (StringUtils.isBlank(templateData.getTriggerConfig()) && StringUtils.isBlank(templateData.getTriggerType())) {
//            flowEntity = JSON.parseObject(newConfig, SubFlowEntity.class);
//        } else {
//            flowEntity = JSON.parseObject(newConfig, TriggerFlowEntity.class);
//        }
        flowEntity.setResourceKey(GenerateKeyUtils.resourceKey(propertyService.getTenantCode()));
        flowEntity.setId(null);
        log.info("new flowEntity: " + JSON.toJSONString(flowEntity));
        return flowEntity;
    }

    private void copyResourceFromPrivateRepo(TemplateData templateData, Map<String, String> resourceKeyMap, String moduleId) throws Exception {
        Map<String, ModelEntity> models = templateData.getRelationModel();
        for (Map.Entry<String, ModelEntity> entityEntry : models.entrySet()) {
            if (modelRepository.findByResourceKey(entityEntry.getKey()) != null) {
                resourceKeyMap.put(entityEntry.getKey(), entityEntry.getKey());
                continue;
            }
            ModelEntity modelEntity = new ModelEntity();
            BeanUtils.copyProperties(modelEntity, entityEntry.getValue());
            String newResourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
            modelEntity.setResourceKey(newResourceKey);
            modelEntity.setCreatedFrom(entityEntry.getValue().getResourceKey());
            modelEntity.setModuleId(moduleId);
            modelEntity.setId(null);
            modelRepository.save(modelEntity);
            resourceKeyMap.put(entityEntry.getKey(), newResourceKey);
        }

        Map<String, ConnectorEntity> connectors = templateData.getRelationConnector();
        for (Map.Entry<String, ConnectorEntity> entityEntry : connectors.entrySet()) {
            if (connectorRepository.findByResourceKey(entityEntry.getKey()) != null) {
                resourceKeyMap.put(entityEntry.getKey(), entityEntry.getKey());
                continue;
            }
            ConnectorEntity connectorEntity = new ConnectorEntity();
            BeanUtils.copyProperties(connectorEntity, entityEntry.getValue());
            String newResourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
            connectorEntity.setResourceKey(newResourceKey);
            connectorEntity.setCreatedFrom(entityEntry.getValue().getResourceKey());
            connectorEntity.setModuleId(moduleId);
            connectorEntity.setId(null);
            connectorRepository.save(connectorEntity);
            resourceKeyMap.put(entityEntry.getKey(), newResourceKey);
        }

        Map<String, FunctionEntity> functions = templateData.getRelationFunction();
        for (Map.Entry<String, FunctionEntity> entityEntry : functions.entrySet()) {
            if (functionRepository.findByResourceKey(entityEntry.getKey()) != null) {
                resourceKeyMap.put(entityEntry.getKey(), entityEntry.getKey());
                continue;
            }
            FunctionEntity functionEntity = new FunctionEntity();
            BeanUtils.copyProperties(functionEntity, entityEntry.getValue());
            String newResourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
            functionEntity.setResourceKey(newResourceKey);
            functionEntity.setCreatedFrom(entityEntry.getValue().getResourceKey());
            functionEntity.setModuleId(moduleId);
            functionEntity.setId(null);
            functionRepository.save(functionEntity);
            resourceKeyMap.put(entityEntry.getKey(), newResourceKey);
        }

        Map<String, ServiceEntity> services = templateData.getRelationService();
        for (Map.Entry<String, ServiceEntity> entityEntry : services.entrySet()) {
            if (serviceRepository.findByResourceKey(entityEntry.getKey()) != null) {
                resourceKeyMap.put(entityEntry.getKey(), entityEntry.getKey());
                continue;
            }
            ServiceEntity serviceEntity = new ServiceEntity();
            BeanUtils.copyProperties(serviceEntity, entityEntry.getValue());
            String newResourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
            serviceEntity.setResourceKey(newResourceKey);
            serviceEntity.setCreatedFrom(entityEntry.getValue().getResourceKey());
            serviceEntity.setModuleId(moduleId);
            serviceEntity.setId(null);
            serviceRepository.save(serviceEntity);
            resourceKeyMap.put(entityEntry.getKey(), newResourceKey);
        }

        Map<String, SubFlowEntity> subflows = templateData.getRelationSubFlow();
        for (Map.Entry<String, SubFlowEntity> entityEntry : subflows.entrySet()) {
            if (subFlowRepository.findByResourceKey(entityEntry.getKey()) != null) {
                resourceKeyMap.put(entityEntry.getKey(), entityEntry.getKey());
                continue;
            }
            SubFlowEntity subflowEntity = new SubFlowEntity();
            BeanUtils.copyProperties(subflowEntity, entityEntry.getValue());

            String oldConfig = JSON.toJSONString(subflowEntity);
            String newConfig = StringUtils.replaceEach(oldConfig, ArrayUtils.toStringArray(resourceKeyMap.keySet().toArray()), ArrayUtils.toStringArray(resourceKeyMap.values().toArray()));

            subflowEntity = JSON.parseObject(newConfig, SubFlowEntity.class);

            String newResourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
            subflowEntity.setResourceKey(newResourceKey);
            subflowEntity.setCreatedFrom(entityEntry.getValue().getResourceKey());
            subflowEntity.setModuleId(moduleId);
            subflowEntity.setId(null);
            subFlowRepository.save(subflowEntity);
            resourceKeyMap.put(entityEntry.getKey(), newResourceKey);
        }
    }

    @Override
    public BasicResponse saveAsTemplate(BasicResourceRequest flow) {
        FlowTemplate flowTemplate = new FlowTemplate();
        if (CollectionUtils.isNotEmpty(privateResourceQueryService.findByResourceKeyAndVersion(flow.getId(), flow.getVersion()))) {
            return new BasicResponse(false, "Template is exist!");
        }

        BasicFlowEntity abstractEntity;
        switch (flow.getType()) {
            case FLOW_TEMPLATE:
                abstractEntity = resourceLoader.loadTriggerFlow(flow.getId());
                flowTemplate.setType(FLOW_TEMPLATE);
                break;
            case SUB_FLOW_TEMPLATE:
            default:
                abstractEntity = resourceLoader.loadSubFlow(flow.getId());
                flowTemplate.setType(SUB_FLOW_TEMPLATE);
        }
        TemplateData templateData = buildFlowTemplate(abstractEntity);
        flowTemplate.setVersion(flow.getVersion());
        flowTemplate.setId(flow.getId());
        flowTemplate.setData(templateData);
        flowTemplate.setName(abstractEntity.getName());
        flowTemplate.setTenantCode(propertyService.getTenantCode());
        return privateRepositoryService.saveTemplate(flowTemplate);
    }

    @Override
    public BasicResponse checkTemplateVersion(BasicResourceRequest flow) {
        if (CollectionUtils.isNotEmpty(privateResourceQueryService.findByResourceKeyAndVersion(flow.getId(), flow.getVersion()))) {
            return new BasicResponse(false, "version: " + flow.getVersion() + " is exist");
        }
        return new BasicResponse(true);
    }

    // TODO 很多重复内容 逻辑也比较尴尬, 各种 magic, 先测试一波, 有时间改改
    @Override
    public ImportFlowResult importFlow(ImportFlowDTO importInfo) {
        ImportFlowResult result = new ImportFlowResult();
        Map<String, Object> config = importInfo.getTriggerConfig();
        List<String> existModels = checkExistModels(importInfo);
        if (!existModels.isEmpty()) {
            result.setExistModels(existModels);
            result.setFlowId("");
            return result;
        }
        TriggerFlowDTO triggerFlowDTO = new TriggerFlowDTO();

        ModelDTO inModel = importInfo.getInModel();
        if (inModel != null) {
            inModel.setModuleId(importInfo.getModuleId());
            String modelId = importModel(inModel, importInfo.getImportMode());
            config.put("inModelId", modelId);
            triggerFlowDTO.setInModelId(modelId);
        }
        ModelDTO outModel = importInfo.getOutModel();
        if (outModel != null) {
            outModel.setModuleId(importInfo.getModuleId());
            String modelId = importModel(outModel, importInfo.getImportMode());
            config.put("outModelId", modelId);
            triggerFlowDTO.setOutModelId(modelId);
        }

        triggerFlowDTO.setTriggerType(importInfo.getTriggerType());
        triggerFlowDTO.setTriggerConfig(importInfo.getTriggerConfig());
        triggerFlowDTO.setModuleId(importInfo.getModuleId());
        triggerFlowDTO.setName(importInfo.getName());
        triggerFlowDTO.setDescription(importInfo.getDescription());

        List<ProcessorDTO> pipeline = new ArrayList<>();
        if (StringUtils.isNotBlank(importInfo.getProcessorType())) {
            ProcessorDTO processor = new ProcessorDTO();
            processor.setId(RandomStringUtils.randomAlphanumeric(6));

            processor.setType(importInfo.getProcessorType());
            processor.setGroup(importInfo.getProcessorGroup());
            processor.setVersion(importInfo.getProcessorVersion());
            processor.setConfig(importInfo.getProcessorConfig());
            ModelDTO processorInModel = importInfo.getProcessorInModel();
            if (processorInModel != null) {
                processorInModel.setModuleId(importInfo.getModuleId());
                String modelId = importModel(processorInModel, importInfo.getImportMode());
                processor.getConfig().put("inModelId", modelId);
                if (inModel != null) {
                    ProcessorDTO inMapper = new ProcessorDTO();
                    inMapper.setId(RandomStringUtils.randomAlphanumeric(6));
                    inMapper.setType("mapper-convert");
                    inMapper.setConfig(new HashMap<>());
                    inMapper.getConfig().put("inModelId", inModel.getId());
                    inMapper.getConfig().put("outModelId", modelId);
                    inMapper.getConfig().put("messageMapping", new HashMap<>());
                    pipeline.add(inMapper);
                }
            }
            pipeline.add(processor);
            ModelDTO processorOutModel = importInfo.getProcessorOutModel();
            if (processorOutModel != null) {
                processorOutModel.setModuleId(importInfo.getModuleId());
                String modelId = importModel(processorOutModel, importInfo.getImportMode());
                processor.getConfig().put("outModelId", modelId);
                if (outModel != null) {
                    ProcessorDTO outMapper = new ProcessorDTO();
                    outMapper.setId(RandomStringUtils.randomAlphanumeric(6));
                    outMapper.setType("mapper-convert");
                    outMapper.setConfig(new HashMap<>());
                    outMapper.getConfig().put("inModelId", modelId);
                    outMapper.getConfig().put("outModelId", outModel.getId());
                    outMapper.getConfig().put("messageMapping", new HashMap<>());
                    pipeline.add(outMapper);
                }
            }
        }
        triggerFlowDTO.setPipeline(pipeline);
        String flowId = createFlow(triggerFlowDTO);
        result.setFlowId(flowId);
        return result;
    }

    @Override
    public ImportProcessorResult importProcessor(ImportProcessorDTO importInfo) {
        ImportProcessorResult result = new ImportProcessorResult();
        Map<String, Object> config = importInfo.getProcessorConfig();
        List<String> existModels = checkExistModels(importInfo);
        if (!existModels.isEmpty()) {
            result.setExistModels(existModels);
            return result;
        }
        ModelDTO inModel = importInfo.getInModel();
        if (inModel != null) {
            inModel.setModuleId(importInfo.getModuleId());
            String modelId = importModel(inModel, importInfo.getImportMode());
            config.put("inModelId", modelId);
        }
        ModelDTO outModel = importInfo.getOutModel();
        if (outModel != null) {
            outModel.setModuleId(importInfo.getModuleId());
            String modelId = importModel(outModel, importInfo.getImportMode());
            config.put("outModelId", modelId);
        }
        ProcessorDTO processor = new ProcessorDTO();
        processor.setConfig(config);
        processor.setType(importInfo.getProcessorType());
        processor.setGroup(importInfo.getProcessorGroup());
        processor.setVersion(importInfo.getProcessorVersion());
        result.setProcessor(processor);
        return result;
    }

    private List<String> checkExistModels(ImportInfo importInfo) {
        List<String> existModels = new ArrayList<>();
        if (importInfo.getImportMode() != null) {
            return existModels;
        }
        ModelDTO inModel = importInfo.getInModel();
        if (inModel != null) {
            inModel.setModuleId(importInfo.getModuleId());
            ModelEntity inModelEntity = modelRepository.findByModuleIdAndResourceKey(importInfo.getModuleId(), inModel.getId());
            if (inModelEntity != null) {
                existModels.add(inModelEntity.getResourceKey());
            }
        }
        ModelDTO outModel = importInfo.getOutModel();
        if (outModel != null) {
            outModel.setModuleId(importInfo.getModuleId());
            ModelEntity outModelEntity = modelRepository.findByModuleIdAndResourceKey(importInfo.getModuleId(), outModel.getId());
            if (outModelEntity != null) {
                existModels.add(outModelEntity.getResourceKey());
            }
        }
        return existModels;
    }

    private String importModel(ModelDTO model, ModelImportMode importMode) {
        ModelEntity outModelEntity = modelRepository.findByModuleIdAndResourceKey(model.getModuleId(), model.getId());
        if (outModelEntity == null) {
            return modelService.createModel(model);
        } else if (importMode == ModelImportMode.Overwrite) {
            model.setId(outModelEntity.getResourceKey());
            modelService.updateModel(model);
            return model.getId();
        } else if (importMode == ModelImportMode.Rename) {
            String randomId = RandomStringUtils.randomAlphanumeric(6);
            model.setId(randomId);
            model.setName(model.getName() + "-" + randomId);
            return modelService.createModel(model);
        }
        return null;
    }

    @Override
    public void deleteFlow(String flowId) {
        TriggerFlowEntity flowEntity = flowRepository.findByResourceKey(flowId);
        flowEntity.setExist(false);
        flowRepository.save(flowEntity);
//        dalaranContext.removeFlow(FLOW_PREFIX + flowId);
//        dalaranContext.removeFlow(DALARAN_PROCESSOR + FLOW_PREFIX + flowId);
    }

    @Override
    public TriggerFlowDTO updateFlow(TriggerFlowDTO flowModel) throws FlowNotExistException {
        if (flowModel.getId() == null) {
            throw new FlowNotExistException();
        }
        TriggerFlowEntity flowEntity = flowRepository.findByResourceKey(flowModel.getId());
        buildEntity(flowModel, flowEntity);
        setFlowStatus(flowEntity);
        setUpdatedBy(flowEntity);
        flowRepository.save(flowEntity);
        // TODO 这里依赖 loader 有点怪 而且可以异步
        testFlowInitializer.reloadTestTriggerFlow(flowEntity.getResourceKey());
        return flowModel;
    }

    @Override
    public List<TriggerFlowDTO> list() {
        List<TriggerFlowEntity> entities = flowRepository.findByIsExistTrue();
        List<TriggerFlowDTO> models = new LinkedList<>();
        for (TriggerFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }
        return models;
    }

    @Override
    public List<TriggerFlowDTO> queryByProcessorIds(List<String> processorIds) {
        List<TriggerFlowEntity> entities = flowQueryService.queryByProcessorIds(processorIds);
        List<TriggerFlowDTO> models = new LinkedList<>();
        for (TriggerFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }
        return models;
    }

    @Override
    public List<BasicFlowInfo> listBasicFlowInfoByModuleId(String moduleId) {
        return flowQueryService.listBasicInfoByModuleId(moduleId);
    }

    @Nullable
    @Override
    public TriggerFlowDTO getById(String flowId) {
        TriggerFlowEntity flowEntity = flowRepository.findByResourceKey(flowId);
        return flowConvertor.toDTO(flowEntity);
    }

    @Nullable
    @Override
    public TriggerFlowDTO getByIdVersion(String flowId, String version) throws FlowNotExistException {
        TriggerFlowReleasedEntity triggerFlowReleasedEntity = triggerFlowReleasedRepository.findByVersionAndOriginId(version, flowId);
        if (triggerFlowReleasedEntity == null) {
            throw new FlowNotExistException();
        }
        return flowConvertor.releaseToDTO(triggerFlowReleasedEntity);
    }

    @Override
    public String copyFlow(CopyFlow copyFlow) throws FlowNotExistException {
        TriggerFlowEntity flowEntity = flowRepository.findByResourceKey(copyFlow.getId());

        TriggerFlowEntity newFlowEntity = new TriggerFlowEntity();
        try {
            BeanUtils.copyProperties(newFlowEntity, flowEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        newFlowEntity.setId(null);
        newFlowEntity.setName(copyFlow.getName());
        newFlowEntity.setResourceKey("copy_" + copyFlow.getId() + "_" + RandomStringUtils.random(4, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN"));
        flowRepository.save(newFlowEntity);
        return newFlowEntity.getResourceKey();
    }

    @Override
    public List<FlowValidation> validateFlow(TriggerFlowDTO model) {
        model.setId(null);
        TriggerFlowEntity entity = new TriggerFlowEntity();
        buildEntity(model, entity);
        return validateFlow(entity);
    }

    @Override
    public List<String> listTriggerOperations() {
        return getSoapOperations();
    }

    @Override
    public ResponseResult offline(TriggerFlowDTO flowDTO) {
        try {
            return isOnline(flowDTO, false);
        } catch (Exception e) {
            e.printStackTrace();
            return fail(ResponseErrorMsg.OFFLINE_FLOW_ERROR);
        }
    }

    @Override
    public ResponseResult online(TriggerFlowDTO flowDTO) {
        try {
            return isOnline(flowDTO, true);
        } catch (Exception e) {
            e.printStackTrace();
            return fail(ResponseErrorMsg.ONLINE_FLOW_ERROR);
        }
    }

    //todo 事务
    @Override
    public ResponseResult bindAlarm(BindAlarmRuleDTO bindAlarmRuleDto) {
        if (bindAlarmRuleDto.getFlowId() == null || bindAlarmRuleDto.getAlarmRuleId() == null) {
            return fail(ResponseErrorMsg.PARAM_IS_NULL);
        }
        try {
            TriggerFlowEntity triggerFlowEntity = flowRepository.findByResourceKey(bindAlarmRuleDto.getFlowId());
            if (triggerFlowEntity == null) {
                return fail(ResponseErrorMsg.FLOW_IS_NOT_EXIT);
            }

            //关联表
            TriggerFlowAlarmRuleEntity triggerFlowAlarmRuleEntity = triggerFlowAlarmRuleRepository.findByTriggerFlowIdAndIsExistTrue(bindAlarmRuleDto.getFlowId());
            if (triggerFlowAlarmRuleEntity == null) {
                triggerFlowAlarmRuleEntity = new TriggerFlowAlarmRuleEntity();
                triggerFlowAlarmRuleEntity.setAlarmRuleId(bindAlarmRuleDto.getAlarmRuleId());
                triggerFlowAlarmRuleEntity.setTriggerFlowId(bindAlarmRuleDto.getFlowId());
            } else {
                triggerFlowAlarmRuleEntity.setAlarmRuleId(bindAlarmRuleDto.getAlarmRuleId());
            }
            triggerFlowAlarmRuleEntity.setMonitor(bindAlarmRuleDto.getIsMonitor());
            triggerFlowAlarmRuleRepository.save(triggerFlowAlarmRuleEntity);

            //被监管就加入缓存
            if (bindAlarmRuleDto.getIsMonitor()) {
                redisService.persistKey(RedisUtil.getAlarmConfigKey(bindAlarmRuleDto.getFlowId()), bindAlarmRuleDto.getAlarmRuleId());
            } else {
                redisService.deleteKey(RedisUtil.getAlarmConfigKey(bindAlarmRuleDto.getFlowId()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return fail(e.getMessage());
        }
        return success();
    }

    @Override
    public Page<BasicFlowInfoDTO> listBasicInfo(FlowQuery flowQuery, Integer pageNumber, Integer pageSize) {
        Sort order = new Sort(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, order);
        Page<TriggerFlowEntity> triggerFlowEntities = flowRepository.findAll(buildSpecification(flowQuery), pageable);
        List<BasicFlowInfoDTO> models = new LinkedList<>();
        for (TriggerFlowEntity entity : triggerFlowEntities) {
            BasicFlowInfoDTO basicFlowInfoDTO = new BasicFlowInfoDTO();
            try {
                BeanUtils.copyProperties(basicFlowInfoDTO, entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            basicFlowInfoDTO.setId(entity.getResourceKey());
            basicFlowInfoDTO.setModuleName(moduleRepository.findByResourceKey(entity.getModuleId()).getName());
            models.add(basicFlowInfoDTO);
        }
        return new PageImpl<>(models, pageable, triggerFlowEntities.getTotalElements());
    }

    @Override
    public NodeFlowListDTO node(PipelineListDTO pipeline) {
        NodeFlowListDTO nodeFlowListDTO = new NodeFlowListDTO();
        List<NodeFlowDTO> nodeFlowDTOList = new ArrayList<>();
        List<NodeFlowDTO> routerList = new ArrayList<>();
        nodeFlowDTOList = buildNode(pipeline.getPipeline(), nodeFlowDTOList, routerList, false);
        nodeFlowListDTO.setPipeline(nodeFlowDTOList);
        return nodeFlowListDTO;
    }

    public List<NodeFlowDTO> buildNode(List<ProcessorDTO> pipeline, List<NodeFlowDTO> nodeFlowDTOList, List<NodeFlowDTO> routerList, boolean isRouter) {
        for (ProcessorDTO processorDTO : pipeline) {
            NodeFlowDTO nodeFlowDTO = new NodeFlowDTO();
            String json = JSONObject.toJSONString(processorDTO.getConfig());
            JSONObject jsonObject;
            if (json.startsWith("\"")) {
                jsonObject = JSONObject.parseObject(processorDTO.getConfig().toString());
            } else {
                jsonObject = JSONObject.parseObject(json);
            }
            if (jsonObject.containsKey("routes")) {
                Map<String, Object> routeConfig = new HashMap<>();
                List<RoutesModel> routesModelList = jsonObject.getJSONArray("routes").toJavaList(RoutesModel.class);
                List<NodeFlowListDTO> nodeFlowListDTOS = new ArrayList<>();
                for (RoutesModel routesModel : routesModelList) {
                    List<NodeFlowDTO> routerFlowList = new ArrayList<>();
                    NodeFlowListDTO nodeFlowListDTO = new NodeFlowListDTO();
                    String pipelineJson = JSONObject.toJSONString(routesModel);
                    JSONObject pipelineJsonObject;
                    if (json.startsWith("\"")) {
                        pipelineJsonObject = JSONObject.parseObject(routesModel.toString());
                    } else {
                        pipelineJsonObject = JSONObject.parseObject(pipelineJson);
                    }
                    if ((pipelineJsonObject.getJSONArray("pipeline")) != null) {
                        routerFlowList.addAll(buildNode((pipelineJsonObject.getJSONArray("pipeline")).toJavaList(ProcessorDTO.class), nodeFlowDTOList, routerList, true));
                        nodeFlowListDTO.setPipeline(routerFlowList);
                        nodeFlowListDTOS.add(nodeFlowListDTO);
                    }
                    routerList = new ArrayList<>();
                }
                routeConfig.put("routes", nodeFlowListDTOS);
                nodeFlowDTO.setConfig(routeConfig);
            }
            if (jsonObject.containsKey("pipeline")) {
                buildNode((jsonObject.getJSONArray("pipeline")).toJavaList(ProcessorDTO.class), nodeFlowDTOList, routerList,false);
            }
            if (jsonObject.containsKey("connectorId")) {
                if (StringUtils.isNotBlank(jsonObject.getString("connectorId"))) {
                    ConnectorEntity connectorEntity = connectorRepository.findByResourceKey(jsonObject.getString("connectorId"));
                    if (connectorEntity != null) {
                        NodeEntity nodeEntity = nodeRepository.findByResourceKey(connectorEntity.getNodeId());
                        if (nodeEntity != null) {
                            nodeFlowDTO.setName(nodeEntity.getName());
                            nodeFlowDTO.setId(nodeEntity.getResourceKey());
                            nodeFlowDTO.setCompany(nodeEntity.getCompany());
                            nodeFlowDTO.setApplication(nodeEntity.getApplication());
                            nodeFlowDTO.setSystem(nodeEntity.getSystems());
                            nodeFlowDTO.setIcon(nodeEntity.getIcon());
                            nodeFlowDTO.setIconColour(nodeEntity.getIconColour());
                            if (isRouter) {
                                routerList.add(nodeFlowDTO);
                            } else {
                                nodeFlowDTOList.add(nodeFlowDTO);
                            }
                        }
                    }
                }
            } else if (jsonObject.containsKey("serviceId")) {
                if (StringUtils.isNotBlank(jsonObject.getString("serviceId"))) {
                    ServiceEntity serviceEntity = serviceRepository.findByResourceKey(jsonObject.getString("serviceId"));
                    if (serviceEntity != null) {
                        NodeEntity nodeEntity = nodeRepository.findByResourceKey(serviceEntity.getNodeId());
                        if (nodeEntity != null) {
                            nodeFlowDTO.setName(nodeEntity.getName());
                            nodeFlowDTO.setId(nodeEntity.getResourceKey());
                            nodeFlowDTO.setCompany(nodeEntity.getCompany());
                            nodeFlowDTO.setApplication(nodeEntity.getApplication());
                            nodeFlowDTO.setSystem(nodeEntity.getSystems());
                            nodeFlowDTO.setIcon(nodeEntity.getIcon());
                            nodeFlowDTO.setIconColour(nodeEntity.getIconColour());
                            if (isRouter) {
                                routerList.add(nodeFlowDTO);
                            } else {
                                nodeFlowDTOList.add(nodeFlowDTO);
                            }
                        }
                    }
                }
            } else if (processorDTO.getType().equals("router") || processorDTO.getType().equals("error-catch")) {
                nodeFlowDTOList.add(nodeFlowDTO);
            }
        }
        if (routerList != null && routerList.size() > 0) {
            return routerList;
        } else {
            return nodeFlowDTOList;
        }
    }

    private Specification<TriggerFlowEntity> buildSpecification(FlowQuery flowQuery) {
        return (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (null != flowQuery.getModuleId()) {
                predicates.add(builder.equal(root.get("moduleId"), flowQuery.getModuleId()));
            }
            String fuzzySearch = flowQuery.getFuzzySearch();
            if (null != fuzzySearch) {
                Predicate searchName = builder.like(root.get("name"), "%" + fuzzySearch + "%");
                Predicate searchType = builder.like(root.get("triggerType"), "%" + fuzzySearch + "%");
                predicates.add(builder.or(searchName, searchType));
            }
            predicates.add(builder.equal(root.get("isExist"),true));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public ResponseResult isOnline(TriggerFlowDTO flowDTO, boolean isOnline) {
        String flowId = flowDTO.getId();
        if (flowId == null) {
            return fail(ResponseErrorMsg.FLOW_ID_NULL);
        }
        TriggerFlowEntity triggerFlowEntity = flowRepository.findByResourceKey(flowId);
        triggerFlowEntity.setOnline(isOnline);
        flowRepository.save(triggerFlowEntity);
        return success();
    }

    private void setFlowStatus(TriggerFlowEntity flowEntity) {
        FlowStatus flowStatus = null;
        for (FlowValidation flowValidation : validateFlow(flowEntity)) {
            if (flowValidation.getMessage().getLevel() == ValidateMessageLevel.Error) {
                flowEntity.setStatus(FlowStatus.Error);
                return;
            } else {
                flowStatus = FlowStatus.Warning;
            }
        }
        if (flowStatus == null) {
            flowStatus = FlowStatus.Available;
        }
        flowEntity.setStatus(flowStatus);
        flowEntity.setExist(true);
    }

    private List<FlowValidation> validateFlow(TriggerFlowEntity entity) {
        TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(entity);
        return flowBuilder.validateFlow(triggerFlow);
    }

    private void buildEntity(TriggerFlowDTO triggerFlow, TriggerFlowEntity flowEntity) {
        List<ProcessorEntity> pipeline = triggerFlow.getPipeline().stream().map(processor -> {
            ProcessorEntity processorEntity = new ProcessorEntity();
            processorEntity.setId(processor.getId());
            processorEntity.setGroup(processor.getGroup());
            processorEntity.setVersion(processor.getVersion());
            processorEntity.setType(processor.getType());
            processorEntity.setName(processor.getName());
            processorEntity.setConfig(JSON.toJSONString(processor.getConfig()));
            return processorEntity;
        }).collect(Collectors.toList());

        String name = triggerFlow.getName();
        if (StringUtils.isNoneBlank(name)) {
            flowEntity.setName(name);
        } else {
            flowEntity.setName("Dalaran Flow");
        }
        flowEntity.setTriggerType(triggerFlow.getTriggerType());
        flowEntity.setTriggerConfig(JSON.toJSONString(triggerFlow.getTriggerConfig()));
        flowEntity.setModuleId(triggerFlow.getModuleId());
        if (triggerFlow.getTriggerConfig().get("inModelId") != null) {
            flowEntity.setInModel(String.valueOf(triggerFlow.getTriggerConfig().get("inModelId")));
        }
        if (triggerFlow.getTriggerConfig().get("outModelId") != null) {
            flowEntity.setOutModel(String.valueOf(triggerFlow.getTriggerConfig().get("outModelId")));
        }
        flowEntity.setPipeline(pipeline);
        flowEntity.setTracing(triggerFlow.isTracing());
        flowEntity.setDescription(triggerFlow.getDescription());
        String resourceKey = triggerFlow.getId();
        if (StringUtils.isBlank(resourceKey)) {
            resourceKey = GenerateKeyUtils.resourceKey();
        }
        flowEntity.setResourceKey(resourceKey);
        flowEntity.setUpdatedAt(new Date());
    }

    private ModelDTO buildModelEntity(ModelEntity entity) {
        ModelDTO model = new ModelDTO();
        model.setId(entity.getResourceKey());
        model.setModelType(entity.getType());
        model.setModelSchema(JSON.parseObject(entity.getModelSchema(), Map.class));
        model.setName(entity.getName());
        model.setModuleId(entity.getModuleId());
        model.setDescription(entity.getDescription());
        return model;
    }

    private List<String> getSoapOperations() {
        List<TriggerFlowEntity> soapFlowList = flowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, "soap-listener");
        return soapFlowList.stream().map(flowEntity -> flowEntity.getName().trim()).collect(Collectors.toList());
    }

    private TemplateData buildFlowTemplate(BasicFlowEntity origin) {
        TemplateData templateData = new TemplateData();
        try {
            BeanUtils.copyProperties(templateData, origin);
            buildTemplateRelationResource(templateData, origin);
            saveRelationResource(templateData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return templateData;
    }

    private void saveRelationResource(TemplateData templateData) throws Exception {
        Map<String, ModelEntity> models = templateData.getRelationModel();
        if (MapUtils.isNotEmpty(models)) {
            for (ModelEntity entity : models.values()) {
                savePrivateResource(entity, DalaranConstants.MODEL);
            }
        }

        Map<String, ConnectorEntity> connectors = templateData.getRelationConnector();
        if (MapUtils.isNotEmpty(connectors)) {
            for (ConnectorEntity entity : connectors.values()) {
                savePrivateResource(entity, DalaranConstants.CONNECTOR);
            }
        }

        Map<String, ServiceEntity> services = templateData.getRelationService();
        if (MapUtils.isNotEmpty(services)) {
            for (ServiceEntity entity : services.values()) {
                savePrivateResource(entity, DalaranConstants.SERVICE);
            }
        }

        Map<String, PrivatePackageEntity> packages = templateData.getRelationPackage();
        if (MapUtils.isNotEmpty(packages)) {
            for (PrivatePackageEntity entity : packages.values()) {
                savePrivateResource(entity, DalaranConstants.PACKAGE);
            }
        }

        Map<String, FunctionEntity> functions = templateData.getRelationFunction();
        if (MapUtils.isNotEmpty(functions)) {
            for (FunctionEntity entity : functions.values()) {
                savePrivateResource(entity, DalaranConstants.FUNCTION);
            }
        }

        Map<String, SubFlowEntity> subflows = templateData.getRelationSubFlow();
        if (MapUtils.isNotEmpty(subflows)) {
            for (SubFlowEntity entity : subflows.values()) {
                savePrivateResource(entity, DalaranConstants.SUB_FLOW);
            }
        }
    }

    private void replaceResourceKey(BasicFlowEntity flowEntity, Map<String, String> keyMap) throws Exception {
        String inModelId = flowEntity.getInModel();
        flowEntity.setInModel(keyMap.get(inModelId));
        String outModelId = flowEntity.getOutModel();
        flowEntity.setOutModel(keyMap.get(outModelId));


        if (flowEntity instanceof TriggerFlowAbstractEntity) {
            TriggerInfo triggerInfo = dalaranContext.getDalaranComponentContext().getTriggerInfo(((TriggerFlowAbstractEntity) flowEntity).getTriggerType());
            Object config = resourceBuilder.buildConfig(((TriggerFlowAbstractEntity) flowEntity).getTriggerConfig(), triggerInfo.getConfigType());
            if (config instanceof ConnectorConfig) {
                ConnectorConfig connectorConfig = (ConnectorConfig) config;
                String connectorId = connectorConfig.getConnectorId();
                ((ConnectorConfig) config).setConnectorId(keyMap.get(connectorId));
            }

            if (config instanceof AllModelConfig) {
                AllModelConfig allModelConfig = (AllModelConfig) config;
                allModelConfig.setInModelId(keyMap.get(allModelConfig.getInModelId()));
                allModelConfig.setOutModelId(keyMap.get(allModelConfig.getOutModelId()));
            }

            ((TriggerFlowAbstractEntity) flowEntity).setTriggerConfig(JSON.toJSONString(config));
        }

        for (ProcessorEntity processorEntity : flowEntity.getPipeline()) {

            ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getGroup(), processorEntity.getType(), processorEntity.getVersion());
            Object processorConfig = resourceBuilder.buildConfig(processorEntity.getConfig(), processorInfo.getConfigType());
            replaceProcessorModel(processorConfig, keyMap);
            if (processorConfig instanceof ConnectorConfig) {
                ConnectorConfig connectorConfig = (ConnectorConfig) processorConfig;
                connectorConfig.setConnectorId(keyMap.get(connectorConfig.getConnectorId()));
            }
            if (processorConfig instanceof ServiceOperationConfig) {
                ServiceOperationConfig serviceOperationConfig = (ServiceOperationConfig) processorConfig;
                String serviceId = null;
                if (StringUtils.isNotBlank(serviceOperationConfig.getServiceId())) {
                    serviceId = serviceOperationConfig.getServiceId();
                    serviceOperationConfig.setServiceId(keyMap.get(serviceOperationConfig.getServiceId()));
                }
//                List<ModelEntity> serviceModels = modelRepository.findByTargetTypeAndTargetId(ModelTargetType.Service, serviceId);
//                serviceModels.forEach(modelEntity -> models.put(modelEntity.getResourceKey(), modelEntity));
            }

            if (processorConfig instanceof DalaranMapperConfig) {
                DalaranMapperConfig mapperConfig = (DalaranMapperConfig) processorConfig;

                mapperConfig.setInModelId(keyMap.get(mapperConfig.getInModelId()));
                mapperConfig.setOutModelId(keyMap.get(mapperConfig.getOutModelId()));

//                log.info("mapperConfig: " + JSON.toJSONString(mapperConfig));
//
//                Map<String, SimpleMapping> messageMapping = mapperConfig.getMessageMapping();
//                List<SimpleMapping> simpleMappings = new ArrayList<>(messageMapping.values());
//                List<SimpleMapping> noDestinationMappings = mapperConfig.getNoDestinationMappings();
//                log.info("noDestinationMappings: " + noDestinationMappings);
//                if (CollectionUtils.isNotEmpty(noDestinationMappings)) {
//                    simpleMappings.addAll(noDestinationMappings);
//                }
//                log.info("simpleMappings: " + simpleMappings.toString());
//                for (SimpleMapping simpleMapping : simpleMappings) {
//                    if (simpleMapping.getMappingType() == MappingType.FUNCTION) {
//                        MappingFunction mappingFunction = (MappingFunction) (simpleMapping.getValue());
//                        if (mappingFunction.getType() == FunctionType.STATIC) {
//                            continue;
//                        }
//                        String functionKey = mappingFunction.getId();
//                        if (functions.containsKey(functionKey)) {
//                            continue;
//                        }
//                        FunctionEntity entity = functionRepository.findByResourceKey(functionKey);
//                        functions.put(functionKey, entity);
//                    }
//                }
            }

            if (processorConfig instanceof DalaranRouterConfig) {
                DalaranRouterConfig dalaranRouterConfig = (DalaranRouterConfig) processorConfig;
                for (DalaranRouterConfig.Route route : dalaranRouterConfig.getRoutes()) {
                    replaceBranchResourceKey(route.getPipeline(), keyMap);
                }
            }

            if (processorConfig instanceof ScatterGatherConfig) {
                ScatterGatherConfig scatterGatherConfig = (ScatterGatherConfig) processorConfig;
                for (ScatterGatherConfig.Branch branch : scatterGatherConfig.getBranches()) {
                    replaceBranchResourceKey(branch.getPipeline(), keyMap);
                }
            }

            if (processorConfig instanceof RetryConfig) {
                RetryConfig retryConfig = (RetryConfig) processorConfig;
                replaceBranchResourceKey(retryConfig.getPipeline(), keyMap);
            }

            if (processorConfig instanceof LoopWhileConfig) {
                LoopWhileConfig loopWhileConfig = (LoopWhileConfig) processorConfig;
                replaceBranchResourceKey(loopWhileConfig.getPipeline(), keyMap);
            }

            if (processorConfig instanceof ForEachConfig) {
                ForEachConfig forEachConfig = (ForEachConfig) processorConfig;
                replaceBranchResourceKey(forEachConfig.getPipeline(), keyMap);
            }

            if (processorConfig instanceof ErrorCatchConfig) {
                ErrorCatchConfig errorCatchConfig = (ErrorCatchConfig) processorConfig;
                for (ErrorCatchConfig.Route route : errorCatchConfig.getRoutes()) {
                    replaceBranchResourceKey(route.getPipeline(), keyMap);
                }
            }

            if (processorConfig instanceof DalaranSubFlowConfig) {
                DalaranSubFlowConfig subFlowConfig = (DalaranSubFlowConfig) processorConfig;
                String subFlowId = subFlowConfig.getSubFlowId();
                subFlowConfig.setSubFlowId(keyMap.get(subFlowId));
                SubFlowAbstractEntity entity = resourceLoader.loadSubFlow(subFlowId);
                if (entity != null) {
                    replaceResourceKey(entity, keyMap);
                }
            }
            processorEntity.setConfig(JSON.toJSONString(processorConfig));
        }
    }

    private void buildTemplateRelationResource(TemplateData templateData, BasicFlowEntity origin) throws Exception {
        Map models = templateData.getRelationModel();
        Map connectors = templateData.getRelationConnector();
        Map services = templateData.getRelationService();
        Map packages = templateData.getRelationPackage();
        Map subFlows = templateData.getRelationSubFlow();
        Map functions = templateData.getRelationFunction();

        String inModelId = origin.getInModel();
        String outModelId = origin.getOutModel();
        if (StringUtils.isNotBlank(inModelId)) {
            ModelAbstractEntity abstractEntity = resourceLoader.loadModel(inModelId);
            models.put(inModelId, abstractEntity);
        }
        if (StringUtils.isNotBlank(outModelId)) {
            ModelAbstractEntity abstractEntity = resourceLoader.loadModel(outModelId);
            models.put(outModelId, abstractEntity);
        }

        if (origin instanceof TriggerFlowAbstractEntity) {
            TriggerInfo triggerInfo = dalaranContext.getDalaranComponentContext().getTriggerInfo(((TriggerFlowAbstractEntity) origin).getTriggerType());
            if (StringUtils.equalsIgnoreCase(triggerInfo.getDeveloper(), DalaranConstants.PARTNER)) {
                List<PrivateRepositoryEntity> privateRepositoryEntity = privateResourceQueryService.query(new PrivateRepositoryQuery(triggerInfo.getName(), DalaranConstants.TRIGGER));
                if (CollectionUtils.isNotEmpty(privateRepositoryEntity)) {
                    PrivateRepositoryEntity repositoryEntity = privateRepositoryEntity.get(0);
                    PrivatePackageEntity packageEntity = new PrivatePackageEntity();
                    BeanUtils.copyProperties(packageEntity, repositoryEntity);
                    ResourceFile resourceFile = JSON.parseObject(repositoryEntity.getData(), ResourceFile.class);
                    packageEntity.setFilePath(resourceFile.getFilePath());
                    packages.put(repositoryEntity.getResourceKey() + "#" + repositoryEntity.getVersion(), packageEntity);
                }
            }

            Object config = resourceBuilder.buildConfig(((TriggerFlowAbstractEntity) origin).getTriggerConfig(), triggerInfo.getConfigType());
            if (config instanceof ConnectorConfig) {
                ConnectorConfig connectorConfig = (ConnectorConfig) config;
                String connectorId = connectorConfig.getConnectorId();
                if (StringUtils.isNotBlank(connectorId)) {
                    connectors.put(connectorId, resourceLoader.loadConnector(connectorId));
                }
            }
        }

        for (ProcessorEntity processorEntity : origin.getPipeline()) {
            ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorEntity.getGroup(), processorEntity.getType(), processorEntity.getVersion());
            if (StringUtils.equalsIgnoreCase(processorInfo.getDeveloper(), DalaranConstants.PARTNER)) {
                List<PrivateRepositoryEntity> privateRepositoryEntity = privateResourceQueryService.query(new PrivateRepositoryQuery(processorInfo.getName(), DalaranConstants.PROCESSOR));
                if (CollectionUtils.isNotEmpty(privateRepositoryEntity)) {
                    PrivateRepositoryEntity repositoryEntity = privateRepositoryEntity.get(0);
                    PrivatePackageEntity packageEntity = new PrivatePackageEntity();
                    BeanUtils.copyProperties(packageEntity, repositoryEntity);
                    ResourceFile resourceFile = JSON.parseObject(repositoryEntity.getData(), ResourceFile.class);
                    packageEntity.setFilePath(resourceFile.getFilePath());
                    packages.put(repositoryEntity.getResourceKey() + "#" + repositoryEntity.getVersion(), packageEntity);
                }
            }

            Object processorConfig = resourceBuilder.buildConfig(processorEntity.getConfig(), processorInfo.getConfigType());
            parseProcessorModel(processorConfig, models);
            if (processorConfig instanceof ConnectorConfig) {
                ConnectorConfig connectorConfig = (ConnectorConfig) processorConfig;
                String connectorId = connectorConfig.getConnectorId();
                if (StringUtils.isNotBlank(connectorId)) {
                    connectors.put(connectorId, resourceLoader.loadConnector(connectorId));
                }
            }
            if (processorConfig instanceof ServiceOperationConfig) {
                ServiceOperationConfig serviceOperationConfig = (ServiceOperationConfig) processorConfig;
                String serviceId = null;
                if (StringUtils.isNotBlank(serviceOperationConfig.getServiceId())) {
                    serviceId = serviceOperationConfig.getServiceId();
                }
                if (StringUtils.isNotBlank(serviceId)) {
                    services.put(serviceId, resourceLoader.loadService(serviceId));
                }
                List<ModelEntity> serviceModels = modelRepository.findByTargetTypeAndTargetId(ModelTargetType.Service, serviceId);
                serviceModels.forEach(modelEntity -> models.put(modelEntity.getResourceKey(), modelEntity));
            }

            if (processorConfig instanceof DalaranMapperConfig) {
                DalaranMapperConfig mapperConfig = (DalaranMapperConfig) processorConfig;

                log.info("mapperConfig: " + JSON.toJSONString(mapperConfig));

                Map<String, SimpleMapping> messageMapping = mapperConfig.getMessageMapping();
                List<SimpleMapping> simpleMappings = new ArrayList<>(messageMapping.values());
                List<SimpleMapping> noDestinationMappings = mapperConfig.getNoDestinationMappings();
                log.info("noDestinationMappings: " + noDestinationMappings);
                if (CollectionUtils.isNotEmpty(noDestinationMappings)) {
                    simpleMappings.addAll(noDestinationMappings);
                }
                log.info("simpleMappings: " + simpleMappings.toString());
                for (SimpleMapping simpleMapping : simpleMappings) {
                    if (simpleMapping.getMappingType() == MappingType.FUNCTION) {
                        MappingFunction mappingFunction = (MappingFunction) (simpleMapping.getValue());
                        if (mappingFunction.getType() == FunctionType.STATIC) {
                            continue;
                        }
                        String functionKey = mappingFunction.getId();
                        if (functions.containsKey(functionKey)) {
                            continue;
                        }
                        FunctionEntity entity = functionRepository.findByResourceKey(functionKey);
                        functions.put(functionKey, entity);
                    }
                }
            }

            if (processorConfig instanceof DalaranRouterConfig) {
                DalaranRouterConfig dalaranRouterConfig = (DalaranRouterConfig) processorConfig;
                for (DalaranRouterConfig.Route route : dalaranRouterConfig.getRoutes()) {
                    parseBranchRelationResource(route.getPipeline(), templateData);
                }
            }

            if (processorConfig instanceof ScatterGatherConfig) {
                ScatterGatherConfig scatterGatherConfig = (ScatterGatherConfig) processorConfig;
                for (ScatterGatherConfig.Branch branch : scatterGatherConfig.getBranches()) {
                    parseBranchRelationResource(branch.getPipeline(), templateData);
                }
            }

            if (processorConfig instanceof RetryConfig) {
                RetryConfig retryConfig = (RetryConfig) processorConfig;
                parseBranchRelationResource(retryConfig.getPipeline(), templateData);
            }

            if (processorConfig instanceof LoopWhileConfig) {
                LoopWhileConfig loopWhileConfig = (LoopWhileConfig) processorConfig;
                parseBranchRelationResource(loopWhileConfig.getPipeline(), templateData);
            }

            if (processorConfig instanceof ForEachConfig) {
                ForEachConfig forEachConfig = (ForEachConfig) processorConfig;
                parseBranchRelationResource(forEachConfig.getPipeline(), templateData);
            }

            if (processorConfig instanceof ErrorCatchConfig) {
                ErrorCatchConfig errorCatchConfig = (ErrorCatchConfig) processorConfig;
                for (ErrorCatchConfig.Route route : errorCatchConfig.getRoutes()) {
                    parseBranchRelationResource(route.getPipeline(), templateData);
                }
            }

            if (processorConfig instanceof DalaranSubFlowConfig) {
                DalaranSubFlowConfig subFlowConfig = (DalaranSubFlowConfig) processorConfig;
                String subFlowId = subFlowConfig.getSubFlowId();
                SubFlowAbstractEntity entity = resourceLoader.loadSubFlow(subFlowId);
                if (entity != null) {
                    subFlows.put(subFlowId, entity);
                    buildTemplateRelationResource(templateData, entity);
                }
            }
        }
    }

    private void parseBranchRelationResource(List<ProcessorRouteInfo> processorRouteInfoList, TemplateData templateData) throws Exception {
        Map models = templateData.getRelationModel();
        Map connectors = templateData.getRelationConnector();
        Map services = templateData.getRelationService();
        Map packages = templateData.getRelationPackage();
        Map subFlows = templateData.getRelationSubFlow();
        Map functions = templateData.getRelationFunction();

        for (ProcessorRouteInfo processorRouteInfo : processorRouteInfoList) {
            ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorRouteInfo.getGroup(), processorRouteInfo.getType(), processorRouteInfo.getVersion());
            if (StringUtils.equalsIgnoreCase(processorInfo.getDeveloper(), DalaranConstants.PARTNER)) {
                List<PrivateRepositoryEntity> privateRepositoryEntity = privateResourceQueryService.query(new PrivateRepositoryQuery(processorInfo.getName(), DalaranConstants.PROCESSOR));
                if (CollectionUtils.isNotEmpty(privateRepositoryEntity)) {
                    PrivateRepositoryEntity repositoryEntity = privateRepositoryEntity.get(0);
                    PrivatePackageEntity packageEntity = new PrivatePackageEntity();
                    BeanUtils.copyProperties(packageEntity, repositoryEntity);
                    ResourceFile resourceFile = JSON.parseObject(repositoryEntity.getData(), ResourceFile.class);
                    packageEntity.setFilePath(resourceFile.getFilePath());
                    packages.put(repositoryEntity.getResourceKey() + "#" + repositoryEntity.getVersion(), packageEntity);
                }
            }

            Object processorConfig = resourceBuilder.buildConfig(processorRouteInfo.getConfig(), processorInfo.getConfigType());
            parseProcessorModel(processorConfig, models);
            if (processorConfig instanceof ConnectorConfig) {
                ConnectorConfig connectorConfig = (ConnectorConfig) processorConfig;
                String connectorId = connectorConfig.getConnectorId();
                if (StringUtils.isNotBlank(connectorId)) {
                    connectors.put(connectorId, resourceLoader.loadConnector(connectorId));
                }
            }
            if (processorConfig instanceof ServiceOperationConfig) {
                ServiceOperationConfig serviceOperationConfig = (ServiceOperationConfig) processorConfig;
                String serviceId = null;
                if (StringUtils.isNotBlank(serviceOperationConfig.getServiceId())) {
                    serviceId = serviceOperationConfig.getServiceId();
                }
                if (StringUtils.isNotBlank(serviceId)) {
                    services.put(serviceId, resourceLoader.loadService(serviceId));
                }
                List<ModelEntity> serviceModels = modelRepository.findByTargetTypeAndTargetId(ModelTargetType.Service, serviceId);
                serviceModels.forEach(modelEntity -> models.put(modelEntity.getResourceKey(), modelEntity));
            }
            if (processorConfig instanceof DalaranMapperConfig) {
                DalaranMapperConfig mapperConfig = (DalaranMapperConfig) processorConfig;
                Map<String, SimpleMapping> messageMapping = mapperConfig.getMessageMapping();
                List<SimpleMapping> simpleMappings = new ArrayList<>(messageMapping.values());
                List<SimpleMapping> noDestinationMappings = mapperConfig.getNoDestinationMappings();
                simpleMappings.addAll(noDestinationMappings);
                for (SimpleMapping simpleMapping : simpleMappings) {
                    if (simpleMapping.getMappingType() == MappingType.FUNCTION) {
                        MappingFunction mappingFunction = (MappingFunction) (simpleMapping.getValue());
                        if (mappingFunction.getType() == FunctionType.STATIC) {
                            continue;
                        }
                        String functionKey = mappingFunction.getId();
                        if (functions.containsKey(functionKey)) {
                            continue;
                        }
                        FunctionEntity entity = functionRepository.findByResourceKey(functionKey);
                        functions.put(functionKey, entity);
                    }
                }
            }

            if (processorConfig instanceof DalaranRouterConfig) {
                DalaranRouterConfig dalaranRouterConfig = (DalaranRouterConfig) processorConfig;
                for (DalaranRouterConfig.Route route : dalaranRouterConfig.getRoutes()) {
                    parseBranchRelationResource(route.getPipeline(), templateData);
                }
            }

            if (processorConfig instanceof ScatterGatherConfig) {
                ScatterGatherConfig scatterGatherConfig = (ScatterGatherConfig) processorConfig;
                for (ScatterGatherConfig.Branch branch : scatterGatherConfig.getBranches()) {
                    parseBranchRelationResource(branch.getPipeline(), templateData);
                }
            }

            if (processorConfig instanceof RetryConfig) {
                RetryConfig retryConfig = (RetryConfig) processorConfig;
                parseBranchRelationResource(retryConfig.getPipeline(), templateData);
            }

            if (processorConfig instanceof LoopWhileConfig) {
                LoopWhileConfig loopWhileConfig = (LoopWhileConfig) processorConfig;
                parseBranchRelationResource(loopWhileConfig.getPipeline(), templateData);
            }

            if (processorConfig instanceof ForEachConfig) {
                ForEachConfig forEachConfig = (ForEachConfig) processorConfig;
                parseBranchRelationResource(forEachConfig.getPipeline(), templateData);
            }

            if (processorConfig instanceof ErrorCatchConfig) {
                ErrorCatchConfig errorCatchConfig = (ErrorCatchConfig) processorConfig;
                for (ErrorCatchConfig.Route route : errorCatchConfig.getRoutes()) {
                    parseBranchRelationResource(route.getPipeline(), templateData);
                }
            }

            if (processorConfig instanceof DalaranSubFlowConfig) {
                DalaranSubFlowConfig subFlowConfig = (DalaranSubFlowConfig) processorConfig;
                String subFlowId = subFlowConfig.getSubFlowId();
                SubFlowAbstractEntity entity = resourceLoader.loadSubFlow(subFlowId);
                if (entity != null) {
                    subFlows.put(subFlowId, entity);
                    buildTemplateRelationResource(templateData, entity);
                }
            }
        }

    }



    private void replaceBranchResourceKey(List<ProcessorRouteInfo> processorRouteInfoList, Map<String, String> keyMap) throws Exception {

        for (ProcessorRouteInfo processorRouteInfo : processorRouteInfoList) {
            ProcessorInfo processorInfo = dalaranContext.getDalaranComponentContext().getProcessorInfo(processorRouteInfo.getGroup(), processorRouteInfo.getType(), processorRouteInfo.getVersion());
            Object processorConfig = resourceBuilder.buildConfig(processorRouteInfo.getConfig(), processorInfo.getConfigType());
            replaceProcessorModel(processorConfig, keyMap);
            if (processorConfig instanceof ConnectorConfig) {
                ConnectorConfig connectorConfig = (ConnectorConfig) processorConfig;
                connectorConfig.setConnectorId(keyMap.get(connectorConfig.getConnectorId()));
            }
            if (processorConfig instanceof ServiceOperationConfig) {
                ServiceOperationConfig serviceOperationConfig = (ServiceOperationConfig) processorConfig;
                String serviceId = null;
                if (StringUtils.isNotBlank(serviceOperationConfig.getServiceId())) {
                    serviceOperationConfig.setServiceId(keyMap.get(serviceOperationConfig.getServiceId()));
                }
//                List<ModelEntity> serviceModels = modelRepository.findByTargetTypeAndTargetId(ModelTargetType.Service, serviceId);
//                serviceModels.forEach(modelEntity -> models.put(modelEntity.getResourceKey(), modelEntity));
            }

            if (processorConfig instanceof DalaranMapperConfig) {
                DalaranMapperConfig mapperConfig = (DalaranMapperConfig) processorConfig;

                mapperConfig.setInModelId(keyMap.get(mapperConfig.getInModelId()));
                mapperConfig.setOutModelId(keyMap.get(mapperConfig.getOutModelId()));

//                log.info("mapperConfig: " + JSON.toJSONString(mapperConfig));
//
//                Map<String, SimpleMapping> messageMapping = mapperConfig.getMessageMapping();
//                List<SimpleMapping> simpleMappings = new ArrayList<>(messageMapping.values());
//                List<SimpleMapping> noDestinationMappings = mapperConfig.getNoDestinationMappings();
//                log.info("noDestinationMappings: " + noDestinationMappings);
//                if (CollectionUtils.isNotEmpty(noDestinationMappings)) {
//                    simpleMappings.addAll(noDestinationMappings);
//                }
//                log.info("simpleMappings: " + simpleMappings.toString());
//                for (SimpleMapping simpleMapping : simpleMappings) {
//                    if (simpleMapping.getMappingType() == MappingType.FUNCTION) {
//                        MappingFunction mappingFunction = (MappingFunction) (simpleMapping.getValue());
//                        if (mappingFunction.getType() == FunctionType.STATIC) {
//                            continue;
//                        }
//                        String functionKey = mappingFunction.getId();
//                        if (functions.containsKey(functionKey)) {
//                            continue;
//                        }
//                        FunctionEntity entity = functionRepository.findByResourceKey(functionKey);
//                        functions.put(functionKey, entity);
//                    }
//                }
            }

            if (processorConfig instanceof DalaranRouterConfig) {
                DalaranRouterConfig dalaranRouterConfig = (DalaranRouterConfig) processorConfig;
                for (DalaranRouterConfig.Route route : dalaranRouterConfig.getRoutes()) {
                    replaceBranchResourceKey(route.getPipeline(), keyMap);
                }
            }

            if (processorConfig instanceof ScatterGatherConfig) {
                ScatterGatherConfig scatterGatherConfig = (ScatterGatherConfig) processorConfig;
                for (ScatterGatherConfig.Branch branch : scatterGatherConfig.getBranches()) {
                    replaceBranchResourceKey(branch.getPipeline(), keyMap);
                }
            }

            if (processorConfig instanceof RetryConfig) {
                RetryConfig retryConfig = (RetryConfig) processorConfig;
                replaceBranchResourceKey(retryConfig.getPipeline(), keyMap);
            }

            if (processorConfig instanceof LoopWhileConfig) {
                LoopWhileConfig loopWhileConfig = (LoopWhileConfig) processorConfig;
                replaceBranchResourceKey(loopWhileConfig.getPipeline(), keyMap);
            }

            if (processorConfig instanceof ForEachConfig) {
                ForEachConfig forEachConfig = (ForEachConfig) processorConfig;
                replaceBranchResourceKey(forEachConfig.getPipeline(), keyMap);
            }

            if (processorConfig instanceof ErrorCatchConfig) {
                ErrorCatchConfig errorCatchConfig = (ErrorCatchConfig) processorConfig;
                for (ErrorCatchConfig.Route route : errorCatchConfig.getRoutes()) {
                    replaceBranchResourceKey(route.getPipeline(), keyMap);
                }
            }

            if (processorConfig instanceof DalaranSubFlowConfig) {
                DalaranSubFlowConfig subFlowConfig = (DalaranSubFlowConfig) processorConfig;
                String subFlowId = subFlowConfig.getSubFlowId();
                subFlowConfig.setSubFlowId(keyMap.get(subFlowId));
                SubFlowAbstractEntity entity = resourceLoader.loadSubFlow(subFlowId);
                if (entity != null) {
                    replaceResourceKey(entity, keyMap);
                }
            }
        }
    }




    private void parseProcessorModel(Object config, Map models) {
        if (config instanceof OutModelConfig) {
            OutModelConfig outModelConfig = (OutModelConfig) config;
            String outModelId = outModelConfig.getOutModelId();
            if (StringUtils.isBlank(outModelId)) {
                return;
            }
            models.put(outModelId, resourceLoader.loadModel(outModelId));
        }
        if (config instanceof AllModelConfig) {
            AllModelConfig allModelConfig = (AllModelConfig) config;
            String inModelId = allModelConfig.getInModelId();
            if (StringUtils.isNotBlank(inModelId)) {
                models.put(inModelId, resourceLoader.loadModel(inModelId));
            }
            String outModelId = allModelConfig.getOutModelId();
            if (StringUtils.isNotBlank(outModelId)) {
                models.put(outModelId, resourceLoader.loadModel(outModelId));
            }
        }
        if (config instanceof ImmutableInModelConfig) {
            ImmutableInModelConfig immutableInModelConfig = (ImmutableInModelConfig) config;
            String inModelId = immutableInModelConfig.getInModelId();
            if (StringUtils.isNotBlank(inModelId)) {
                models.put(inModelId, resourceLoader.loadModel(inModelId));
            }
            String outModelId = immutableInModelConfig.getOutModelId();
            if (StringUtils.isNotBlank(outModelId)) {
                models.put(outModelId, resourceLoader.loadModel(outModelId));
            }
        }
    }


    private void replaceProcessorModel(Object config, Map<String, String> keyMap) {
        if (config instanceof OutModelConfig) {
            OutModelConfig outModelConfig = (OutModelConfig) config;
            outModelConfig.setOutModelId(keyMap.get(outModelConfig.getOutModelId()));
        }
        if (config instanceof AllModelConfig) {
            AllModelConfig allModelConfig = (AllModelConfig) config;
            allModelConfig.setInModelId(keyMap.get(allModelConfig.getInModelId()));
            allModelConfig.setOutModelId(keyMap.get(allModelConfig.getOutModelId()));
        }
        if (config instanceof ImmutableInModelConfig) {
            ImmutableInModelConfig immutableInModelConfig = (ImmutableInModelConfig) config;
            immutableInModelConfig.setInModelId(keyMap.get(immutableInModelConfig.getInModelId()));
            immutableInModelConfig.setOutModelId(keyMap.get(immutableInModelConfig.getOutModelId()));
        }
    }

    private void savePrivateResource(Object origin, String type) throws Exception {
        switch (type) {
            case "model":
                PrivateModelEntity privateModelEntity = new PrivateModelEntity();
                BeanUtils.copyProperties(privateModelEntity, origin);
                if (privateModelRepository.findByResourceKey(privateModelEntity.getResourceKey()) != null) {
                    break;
                }
                privateModelEntity.setId(null);
                privateModelRepository.save(privateModelEntity);
                break;
            case "connector":
                PrivateConnectorEntity privateConnectorEntity = new PrivateConnectorEntity();
                BeanUtils.copyProperties(privateConnectorEntity, origin);
                if (privateConnectorRepository.findByResourceKey(privateConnectorEntity.getResourceKey()) != null) {
                    break;
                }
                privateConnectorEntity.setId(null);
                privateConnectorRepository.save(privateConnectorEntity);
                break;
            case "service":
                PrivateServiceEntity privateServiceEntity = new PrivateServiceEntity();
                BeanUtils.copyProperties(privateServiceEntity, origin);
                if (privateServiceRepository.findByResourceKey(privateServiceEntity.getResourceKey()) != null) {
                    break;
                }
                privateServiceEntity.setId(null);
                privateServiceRepository.save(privateServiceEntity);
                break;
            case "function":
                PrivateFunctionEntity privateFunctionEntity = new PrivateFunctionEntity();
                BeanUtils.copyProperties(privateFunctionEntity, origin);
                if (privateFunctionRepository.findByResourceKey(privateFunctionEntity.getResourceKey()) != null) {
                    break;
                }
                privateFunctionEntity.setId(null);
                privateFunctionRepository.save(privateFunctionEntity);
                break;
            case "subflow":
                PrivateSubFlowEntity privateSubFlowEntity = new PrivateSubFlowEntity();
                BeanUtils.copyProperties(privateSubFlowEntity, origin);
                if (privateSubFlowRepository.findByResourceKey(privateSubFlowEntity.getResourceKey()) != null) {
                    break;
                }
                privateSubFlowEntity.setId(null);
                privateSubFlowRepository.save(privateSubFlowEntity);
                break;
            case "package":
                PrivatePackageEntity privatePackageEntity = new PrivatePackageEntity();
                BeanUtils.copyProperties(privatePackageEntity, origin);
                if (privatePackageRepository.findByResourceKeyAndVersion(privatePackageEntity.getResourceKey(), privatePackageEntity.getVersion()) != null) {
                    break;
                }
                privatePackageEntity.setId(null);
                privatePackageRepository.save(privatePackageEntity);
                break;
        }
    }

    private void setCreatedBy(TriggerFlowEntity triggerFlowEntity) {
        if (UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null) {
            triggerFlowEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private void setUpdatedBy(TriggerFlowEntity triggerFlowEntity) {
        if (UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null) {
            triggerFlowEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private ResponseResult fail(String errorMsg) {
        ResponseResult result = new ResponseResult();
        result.setSuccess(false);
        result.setErrorMsg(errorMsg);
        return result;
    }

    private ResponseResult success() {
        ResponseResult result = new ResponseResult();
        result.setSuccess(true);
        return result;
    }

}
