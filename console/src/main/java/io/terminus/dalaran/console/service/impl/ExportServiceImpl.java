package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.predic8.wsdl.Definitions;
import io.swagger.models.Swagger;
import io.terminus.dalaran.component.http.trigger.model.ApiInfo;
import io.terminus.dalaran.component.http.trigger.utils.SwaggerUtils;
import io.terminus.dalaran.component.soap.trigger.model.SoapApiInfo;
import io.terminus.dalaran.component.soap.trigger.utils.WSDLUtils;
import io.terminus.dalaran.console.ExportData;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.ExportService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.DalaranTriggerApiDocExport;
import io.terminus.dalaran.core.component.DalaranTriggerWordDocExport;
import io.terminus.dalaran.core.context.DalaranComponentContext;
import io.terminus.dalaran.core.context.DalaranModelTypeContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.flow.TriggerFlow;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
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

    @Value("${terminus.dalaran.runtime-location}")
    private String runtimeLocation;

    // TODO 数据量暴多可能炸内存, 而且会涉及到清表, 所以事务也是个问题
    @Override
    @Transactional
    public void importAll(ExportData exportData) throws IOException {
        truncateTable();

        exportData.getModules().forEach(module -> module.setExist(true));
        exportData.getModels().forEach(model -> model.setExist(true));
        exportData.getTriggerFlows().forEach(triggerFlow -> triggerFlow.setExist(true));
        exportData.getSubFlows().forEach(subFlow -> subFlow.setExist(true));
        exportData.getServices().forEach(service -> service.setExist(true));
        exportData.getClients().forEach(client -> client.setExist(true));
        exportData.getConnectors().forEach(connector -> connector.setExist(true));
        exportData.getFunctions().forEach(function -> function.setExist(true));
        moduleRepository.saveAll(exportData.getModules());
        modelRepository.saveAll(exportData.getModels());
        triggerFlowRepository.saveAll(exportData.getTriggerFlows());
        subFlowRepository.saveAll(exportData.getSubFlows());
        serviceRepository.saveAll(exportData.getServices());
        functionRepository.saveAll(exportData.getFunctions());
        connectorRepository.saveAll(exportData.getConnectors());
        clientRepository.saveAll(exportData.getClients());
        propertyRepository.saveAll(exportData.getProperties());
        trantorRepository.saveAll(exportData.getTrantorEntities());

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
            Optional<ModuleEntity> moduleOptional = moduleRepository.findById(flowEntity.getModuleId());
            String moduleName;
            if (moduleOptional.isPresent()) {
                moduleName = moduleOptional.get().getName();
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
            ModuleEntity module = moduleRepository.findById(flowEntity.getModuleId()).get();
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            return new ApiInfo(module.getName(), triggerFlow);
        }).collect(Collectors.toList());
    }

    private List<ApiInfo> getExportApiInfoListNew() {
        List<TriggerFlowEntity> restFlowList = triggerFlowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, "http-rest-listener");
        List<ApiInfo> apiInfo = new ArrayList<>();
        restFlowList.stream().forEach(flowEntity -> {
            Optional<ModuleEntity> optional = moduleRepository.findById(flowEntity.getModuleId());
            ModuleEntity module = new ModuleEntity();
            if(optional!=null && optional.isPresent()) {
                module = optional.get();
            }
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            if(triggerFlow.getInModel()!=null && triggerFlow.getOutModel()!=null) {
                apiInfo.add(new ApiInfo(module.getName(), triggerFlow));
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
