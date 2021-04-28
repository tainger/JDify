package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.predic8.wsdl.Definitions;
import io.swagger.models.Swagger;
import io.terminus.dalaran.SourceType;
import io.terminus.dalaran.component.foreach.ForEachConfig;
import io.terminus.dalaran.component.http.trigger.model.ApiInfo;
import io.terminus.dalaran.component.http.trigger.utils.SwaggerUtils;
import io.terminus.dalaran.component.loopwhile.LoopWhileConfig;
import io.terminus.dalaran.component.multicast.ScatterGatherConfig;
import io.terminus.dalaran.component.retry.RetryConfig;
import io.terminus.dalaran.component.service.soap.SoapServiceConfig;
import io.terminus.dalaran.component.soap.trigger.model.SoapApiInfo;
import io.terminus.dalaran.component.soap.trigger.utils.WSDLUtils;
import io.terminus.dalaran.component.subflow.DalaranSubFlowConfig;
import io.terminus.dalaran.console.ExportData;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.entity.ServiceEntity;
import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.FlowResourceCollector;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.ExportService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerApiDocExport;
import io.terminus.dalaran.core.component.DalaranTriggerWordDocExport;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ServiceOperationConfig;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.core.context.support.DefaultDalaranServiceContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.component.ProcessorRouteInfo;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.model.soap.model.SoapOperationConfig;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private DalaranModelTypeContext converterContext;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestFlowInitializer testFlowInitializer;

    @Autowired
    private TrantorRepository trantorRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TriggerFlowRepository triggerFlowRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private DalaranComponentContext componentContext;

    @Autowired
    private ModelManagementService modelManagementService;

    @Autowired
    private AuthenticatorRepository authenticatorRepository;

    @Value("${terminus.dalaran.runtime-location}")
    private String runtimeLocation;

    private FlowResourceCollector flowsCollector;

    @Autowired
    private LimiterRepository limiterRepository;

    @Autowired
    private DalaranComponentContext dalaranComponentContext;

    @Autowired
    private DefaultDalaranServiceContext defaultDalaranServiceContext;

    // TODO 数据量暴多可能炸内存, 而且会涉及到清表, 所以事务也是个问题
    @Override
    @Transactional
    public void importAll(ExportData exportData) throws IOException {
        truncateTable();
        if(null != exportData.getModules()){
            exportData.getModules().forEach(module -> module.setExist(true));
            moduleRepository.saveAll(exportData.getModules());
        }

        if(null != exportData.getModels()) {
            exportData.getModels().forEach(model -> model.setExist(true));
            modelRepository.saveAll(exportData.getModels());
        }

        if(null != exportData.getTriggerFlows()) {
            exportData.getTriggerFlows().forEach(triggerFlow -> triggerFlow.setExist(true));
            triggerFlowRepository.saveAll(exportData.getTriggerFlows());
        }

        if(null !=  exportData.getSubFlows()) {
            exportData.getSubFlows().forEach(subFlow -> subFlow.setExist(true));
            subFlowRepository.saveAll(exportData.getSubFlows());
        }

        if(null !=  exportData.getServices()) {
            exportData.getServices().forEach(service -> service.setExist(true));
            serviceRepository.saveAll(exportData.getServices());
        }

        if(null !=   exportData.getClients()) {
            exportData.getClients().forEach(client -> client.setExist(true));
            clientRepository.saveAll(exportData.getClients());
        }

        if(null !=   exportData.getConnectors()) {
            exportData.getConnectors().forEach(connector -> connector.setExist(true));
            connectorRepository.saveAll(exportData.getConnectors());
        }

        if(null != exportData.getFunctions()) {
            exportData.getFunctions().forEach(function -> function.setExist(true));
            functionRepository.saveAll(exportData.getFunctions());
        }

        if(null != exportData.getAuthenticatorEntities()) {
            exportData.getAuthenticatorEntities().forEach(authenticatorEntity -> authenticatorEntity.setExist(true));
            authenticatorRepository.saveAll(exportData.getAuthenticatorEntities());
        }

        if(null !=  exportData.getLimiterEntities()) {
            exportData.getLimiterEntities().forEach(limiterEntity -> limiterEntity.setExist(true));
            limiterRepository.saveAll(exportData.getLimiterEntities());
        }
        // load test flow
        testFlowInitializer.start();
    }

    // TODO 如果数据暴多可能内存会炸, 可以分页读逐渐序列化至磁盘, 最后读流输出回前端
    // TODO 但是导入一样可能会炸, 一样需要流处理, 太麻烦, 暂时也没那么多数据

    @Override
    public ExportData exportAll() {
        ExportData exportData = new ExportData();
        exportData.setModules(moduleRepository.findByIsExistTrue());
        exportData.setModels(modelRepository.findByIsExistTrue());
        exportData.setTriggerFlows(triggerFlowRepository.findByIsExistTrue());
        exportData.setSubFlows(subFlowRepository.findByIsExistTrue());
        exportData.setServices(serviceRepository.findByIsExistTrue());
        exportData.setFunctions(functionRepository.findByIsExistTrue());
        exportData.setConnectors(connectorRepository.findByIsExistTrue());
        exportData.setClients(clientRepository.findByIsExistTrue());
        exportData.setProperties(propertyRepository.findAll());
        exportData.setTrantorEntities(trantorRepository.findAll());
        return exportData;
    }

    @Override
    public Swagger exportSwagger() {
        List<ApiInfo> apiInfoList = getExportApiInfoListNew();
        apiInfoList.forEach(apiInfo -> {
            Object inExample = JSON.parseObject(modelManagementService.buildSwaggerDataTemplate(apiInfo.getInSchema().getModelSchema(), apiInfo.getInSchema().getModelType()).getData());
            Object outExample = JSON.parseObject(modelManagementService.buildSwaggerDataTemplate(apiInfo.getOutSchema().getModelSchema(), apiInfo.getOutSchema().getModelType()).getData());
            apiInfo.setInExample(inExample);
            apiInfo.setOutExample(outExample);
        });
        return SwaggerUtils.buildSwagger(apiInfoList);
    }

    @Override
    public File exportWord() {
        String triggerType = "http-rest-listener";
        DalaranTrigger trigger = componentContext.getTrigger(triggerType);
        if (trigger instanceof DalaranTriggerWordDocExport) {
            Map<String, List<TriggerFlow>> moduleTriggerFlowList = buildModuleTriggerFlowList(triggerType);
            return ((DalaranTriggerWordDocExport) trigger).exportWord(moduleTriggerFlowList);
        }
        return null;
    }

    @Override
    public File exportWordDocs(String triggerType) {
        DalaranTrigger trigger = componentContext.getTrigger(triggerType);
        if (trigger instanceof DalaranTriggerWordDocExport) {
            Map<String, List<TriggerFlow>> moduleTriggerFlowList = buildModuleTriggerFlowList(triggerType);
            return ((DalaranTriggerWordDocExport) trigger).exportWord(moduleTriggerFlowList);
        }
        return null;
    }

    @Override
    public Object exportApiDocs(String triggerType) {
        DalaranTrigger trigger = componentContext.getTrigger(triggerType);
        if (trigger instanceof DalaranTriggerApiDocExport) {
            Map<String, List<TriggerFlow>> moduleTriggerFlowList = buildModuleTriggerFlowList(triggerType);
            return ((DalaranTriggerApiDocExport) trigger).exportApiDoc(moduleTriggerFlowList);
        }
        return null;
    }

    @Override
    public ExportData exportFlow(String ids) {
        flowsCollector = new FlowResourceCollector();
        List<String> idList = Arrays.asList(ids.split(","));
        List<TriggerFlowEntity> triggerFlowEntities = triggerFlowRepository.findByResourceKeyIn(idList);
        for (TriggerFlowEntity triggerFlowEntity : triggerFlowEntities) {
            collectTriggerResourceKey(triggerFlowEntity);
            //basic Info
            collectBaseInfoResourceKey(triggerFlowEntity);
            //pipeLine
            List<ProcessorEntity> pipeline = triggerFlowEntity.getPipeline();
            for (ProcessorEntity processorEntity : pipeline) {
                collectProcessorResourceKey(processorEntity);
            }
        }
        ExportData exportData = buildExportData(flowsCollector);
        exportData.setTriggerFlows(triggerFlowEntities);
        return exportData;
    }

    private ExportData buildExportData(FlowResourceCollector flowResourceCollector) {
        ExportData exportData = new ExportData();
        Map<String, Set<String>> resourceKeyCollector = flowResourceCollector.getResourceKeyCollector();
        for (Map.Entry<String, Set<String>> entry : resourceKeyCollector.entrySet()) {
            switch (entry.getKey()) {
                case SourceType.SERVICE:
                    exportData.setServices(serviceRepository.findByResourceKeyIn(new ArrayList<>(entry.getValue())));
                    break;
                case SourceType.AUTHENTICATOR:
                    exportData.setAuthenticatorEntities(authenticatorRepository.findByResourceKeyIn(new ArrayList<>(entry.getValue())));
                    break;

                case SourceType.CLIENT:
                    exportData.setClients(clientRepository.findByResourceKeyIn(new ArrayList<>(entry.getValue())));
                    break;

                case SourceType.CONNECTOR:
                    exportData.setConnectors(connectorRepository.findByResourceKeyIn(new ArrayList<>(entry.getValue())));
                    break;

                case SourceType.FUNCTION:
                    exportData.setFunctions(functionRepository.findByResourceKeyIn(new ArrayList<>(entry.getValue())));
                    break;

                case SourceType.LIMITER:
                    exportData.setLimiterEntities(limiterRepository.findByResourceKeyIn(new ArrayList<>(entry.getValue())));
                    break;

                case SourceType.MODEL:
                    exportData.setModels(modelRepository.findByResourceKeyIn(new ArrayList<>(entry.getValue())));
                    break;

                case SourceType.MODULE:
                    exportData.setModules(moduleRepository.findByResourceKeyIn(new ArrayList<>(entry.getValue())));
                    break;

                case SourceType.SUB_FLOW:
                    exportData.setSubFlows(subFlowRepository.findByResourceKeyIn(new ArrayList<>(entry.getValue())));
                    break;
            }
        }
        return exportData;
    }

    private void collectProcessorResourceKey(ProcessorEntity processorEntity) {
        String type = processorEntity.getType();
        String processorEntityConfig = processorEntity.getConfig();

        if ("scatter-gather".equals(type)) {
            ScatterGatherConfig scatterGatherConfig = JSONObject.parseObject(processorEntityConfig, ScatterGatherConfig.class);
            List<ScatterGatherConfig.Branch> branches = scatterGatherConfig.getBranches();
            for (ScatterGatherConfig.Branch branch : branches) {
                List<ProcessorRouteInfo> branchPipeline = branch.getPipeline();
                for (ProcessorRouteInfo processorRouteInfo : branchPipeline) {
                    ProcessorEntity branchProcessor = new ProcessorEntity();
                    BeanUtils.copyProperties(processorRouteInfo, branchProcessor);
                    collectProcessorResourceKey(branchProcessor);
                }
            }
        }

        if ("sub-flow".equals(type)) {
            DalaranSubFlowConfig dalaranSubFlowConfig = JSONObject.parseObject(processorEntityConfig, DalaranSubFlowConfig.class);
            String subFlowId = dalaranSubFlowConfig.getSubFlowId();
            SubFlowEntity subFlowEntity = subFlowRepository.findByResourceKey(subFlowId);
            List<ProcessorEntity> pipeline = subFlowEntity.getPipeline();
            for (ProcessorEntity subProcessorEntity : pipeline) {
                collectProcessorResourceKey(subProcessorEntity);
            }
        }

        if ("loop-while".equals(type)) {
            LoopWhileConfig loopWhileConfig = JSONObject.parseObject(processorEntityConfig, LoopWhileConfig.class);
            List<ProcessorRouteInfo> loopWhilePipeline = loopWhileConfig.getPipeline();
            for (ProcessorRouteInfo processorRouteInfo : loopWhilePipeline) {
                ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
                BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
                collectProcessorResourceKey(loopWhileProcessorEntity);
            }
        }

        if ("service".equals(type)) {
            ServiceOperationConfig serviceOperationConfig = JSONObject.parseObject(processorEntityConfig, ServiceOperationConfig.class);
            String serviceId = serviceOperationConfig.getServiceId();
            ServiceEntity service = serviceRepository.findByResourceKey(serviceId);
            String serviceConfig = service.getServiceConfig();
            collectServiceResourceKey(serviceConfig);
        }

        if ("foreach".equals(type)) {
            ForEachConfig forEachConfig = JSONObject.parseObject(processorEntityConfig, ForEachConfig.class);
            List<ProcessorRouteInfo> pipeline = forEachConfig.getPipeline();
            for (ProcessorRouteInfo processorRouteInfo : pipeline) {
                ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
                BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
                collectProcessorResourceKey(loopWhileProcessorEntity);
            }
        }

        if ("retry".equals(type)) {
            RetryConfig retryConfig = JSONObject.parseObject(processorEntityConfig, RetryConfig.class);
            List<ProcessorRouteInfo> pipeline = retryConfig.getPipeline();
            for (ProcessorRouteInfo processorRouteInfo : pipeline) {
                ProcessorEntity loopWhileProcessorEntity = new ProcessorEntity();
                BeanUtils.copyProperties(processorRouteInfo, loopWhileProcessorEntity);
                collectProcessorResourceKey(loopWhileProcessorEntity);
            }
        }
        collectProcessResourceKey(processorEntity);
    }

    private void collectServiceResourceKey(String config) {
        SoapServiceConfig soapServiceConfig = JSONObject.parseObject(config, SoapServiceConfig.class);
        List<SoapOperationConfig> configs = soapServiceConfig.getConfigs();
        for (SoapOperationConfig soapOperationConfig : configs) {
            String inModelId = soapOperationConfig.getInModelId();
            String outModelId = soapOperationConfig.getOutModelId();
            flowsCollector.collect(SourceType.MODEL, inModelId);
            flowsCollector.collect(SourceType.MODEL, outModelId);
        }
    }

    private void collectProcessResourceKey(ProcessorEntity processorEntity) {
        String type = processorEntity.getType();
        String processorClassName = getProcessorClassName(type);
        String processorEntityConfig = processorEntity.getConfig();
        Class<?> configClass = null;
        try {
            configClass = Class.forName(processorClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Object object = JSONObject.parseObject(processorEntityConfig, configClass);
        List<Field> fields = new ArrayList<>();
        while (null != configClass) {
            List<Field> fieldList = Arrays.asList(configClass.getDeclaredFields());
            fields.addAll(fieldList);
            configClass = configClass.getSuperclass();
        }
        for (Field declaredField : fields) {
            ConfigFieldInfo configFieldInfo = declaredField.getDeclaredAnnotation(ConfigFieldInfo.class);
            if (configFieldInfo == null) {
                continue;
            }
            String sourceType = configFieldInfo.sourceType();
            if (StringUtils.EMPTY.equals(sourceType)) {
                continue;
            }
            String resourceKey = null;
            try {
                declaredField.setAccessible(true);
                resourceKey = (String) declaredField.get(object);
                flowsCollector.collect(sourceType, resourceKey);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


    }

    private void collectBaseInfoResourceKey(TriggerFlowEntity triggerFlowEntity) {
        String moduleId = triggerFlowEntity.getModuleId();
        flowsCollector.collect(SourceType.MODULE, moduleId);
    }

    public void collectTriggerResourceKey(TriggerFlowEntity triggerFlowEntity) {
        String triggerType = triggerFlowEntity.getTriggerType();
        String triggerConfig = triggerFlowEntity.getTriggerConfig();
        String triggerClassName = getTriggerClassName(triggerType);
        Class<?> configClass = null;
        try {
            configClass = Class.forName(triggerClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Object object = JSONObject.parseObject(triggerConfig, configClass);
        List<Field> fields = new ArrayList<>();
        while (null != configClass) {
            List<Field> fieldList = Arrays.asList(configClass.getDeclaredFields());
            fields.addAll(fieldList);
            configClass = configClass.getSuperclass();
        }
        for (Field declaredField : fields) {
            ConfigFieldInfo configFieldInfo = declaredField.getDeclaredAnnotation(ConfigFieldInfo.class);
            if (configFieldInfo == null) {
                continue;
            }
            String sourceType = configFieldInfo.sourceType();
            if (!StringUtils.EMPTY.equals(sourceType)) {
                String resourceKey = null;
                try {
                    declaredField.setAccessible(true);
                    resourceKey = (String) declaredField.get(object);
                    if(StringUtils.isEmpty(resourceKey)){
                        continue;
                    }
                    flowsCollector.collect(sourceType, resourceKey);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String getTriggerClassName(String triggerType) {
        return dalaranComponentContext.getTriggerConfigMap().get(triggerType);
    }


    public String getProcessorClassName(String processorType) {
        return dalaranComponentContext.getProcessorConfigMap().get(processorType);
    }

    public String getSoapConfigClassName(String soapType) {
        return defaultDalaranServiceContext.getConfigClassMap().get(soapType);
    }

//    public void collectComponent(Object object) {
//        if(object instanceof AllModelConfig) {
//            AllModelConfig allModelConfig = (AllModelConfig) object;
//            String inModelId = allModelConfig.getInModelId();
//            String outModelId = allModelConfig.getOutModelId();
//            flowsCollector.getModelIds().add(inModelId);
//            flowsCollector.getModelIds().add(outModelId);
//        }
//
//        if(object instanceof OutModelConfig) {
//            OutModelConfig outModelConfig = (OutModelConfig) object;
//            String outModelId = outModelConfig.getOutModelId();
//            flowsCollector.getModelIds().add(outModelId);
//        }
//
//        if(object instanceof InModelConfig) {
//            InModelConfig inModelConfig = (InModelConfig) object;
//            String inModelId= inModelConfig.getInModelId();
//            flowsCollector.getModelIds().add(inModelId);
//        }
//
//        if(object instanceof AuthenticatorConfig) {
//            AuthenticatorConfig authenticatorConfig = (AuthenticatorConfig) object;
//            String authenticatorId = authenticatorConfig.getAuthenticatorId();
//            flowsCollector.getAuthenticatorIds().add(authenticatorId);
//        }
//        if(object instanceof LimiterConfig) {
//            LimiterConfig limiterConfig = (LimiterConfig) object;
//            String limiterId = limiterConfig.getLimiterId();
//            flowsCollector.getLimiterIds().add(limiterId);
//        }
//    }

    @Override

    public Definitions exportWSDL() {
        List<SoapApiInfo> soapApiList = getExportSoapListeners();
        return WSDLUtils.buildDefinitions(soapApiList, runtimeLocation);
    }

    @Override
    public Definitions exportOperationWSDL(String operation) {
        SoapApiInfo apiInfo = getApiInfoByOperation(operation);
        return WSDLUtils.getOperationDefinitions(apiInfo, runtimeLocation);
    }

    // TODO 比较暴力, 但是需要重置 ID 自增, 否则 Json 内的依赖可能会有问题
    private void truncateTable() {
        MetamodelImplementor metaMode = (MetamodelImplementor) entityManager.getMetamodel();
        for (EntityPersister entityPersister : metaMode.entityPersisters().values()) {
            String tableName = ((AbstractEntityPersister) entityPersister).getTableName();
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }
    }

    private Map<String, List<TriggerFlow>> buildModuleTriggerFlowList(String triggerType) {
        List<TriggerFlowEntity> restFlowList = triggerFlowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, triggerType);
        Map<String, List<TriggerFlow>> moduleTriggerFlowList = new HashMap<>();
        for (TriggerFlowEntity flowEntity : restFlowList) {
            ModuleEntity moduleEntity = moduleRepository.findByResourceKey(flowEntity.getModuleId());
            String moduleName;
            if (moduleEntity != null) {
                moduleName = moduleEntity.getName();
            } else {
                moduleName = "unknown";
            }
            List<TriggerFlow> triggerFlowList = moduleTriggerFlowList.computeIfAbsent(moduleName, module -> new ArrayList<>());
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            triggerFlowList.add(triggerFlow);
        }
        return moduleTriggerFlowList;
    }

    private List<ApiInfo> getExportApiInfoList() {
        List<TriggerFlowEntity> restFlowList = triggerFlowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, "http-rest-listener");
        return restFlowList.stream().map(flowEntity -> {
            ModuleEntity module = moduleRepository.findByResourceKey(flowEntity.getModuleId());
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            return new ApiInfo(module.getName(), triggerFlow);
        }).collect(Collectors.toList());
    }

    private List<ApiInfo> getExportApiInfoListNew() {
        List<TriggerFlowEntity> restFlowList = triggerFlowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, "http-rest-listener");
        List<ApiInfo> apiInfo = new ArrayList<>();
        restFlowList.stream().forEach(flowEntity -> {
            ModuleEntity moduleEntity = moduleRepository.findByResourceKey(flowEntity.getModuleId());
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            if (triggerFlow.getInModel() != null && triggerFlow.getOutModel() != null) {
                apiInfo.add(new ApiInfo(moduleEntity.getName(), triggerFlow));
            }
        });
        return apiInfo;
    }

    private List<SoapApiInfo> getExportSoapListeners() {
        List<TriggerFlowEntity> soapFlowList = triggerFlowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, "soap-listener");
        return soapFlowList.stream().map(flowEntity -> {
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            return new SoapApiInfo(triggerFlow);
        }).collect(Collectors.toList());
    }

    private SoapApiInfo getApiInfoByOperation(String operation) {
        List<TriggerFlowEntity> soapFlowList = triggerFlowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, "soap-listener");
        Optional<SoapApiInfo> soapApiInfo = soapFlowList.stream().filter(triggerFlowEntity ->
                StringUtils.equals(triggerFlowEntity.getName().trim(), operation)
        ).findFirst().map(flowEntity -> {
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            return new SoapApiInfo(triggerFlow);
        });
        return soapApiInfo.orElse(null);
    }
}
