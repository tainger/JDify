package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.swagger.models.Swagger;
import io.terminus.dalaran.component.trigger.rest.RestConfig;
import io.terminus.dalaran.component.trigger.rest.model.ApiInfo;
import io.terminus.dalaran.component.trigger.rest.utils.SwaggerUtils;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.ExportData;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.ExportService;
import io.terminus.dalaran.console.util.WordUtils;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.flow.FlowStatus;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Transient;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private DalaranConverterContext converterContext;

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

    // TODO 数据量暴多可能炸内存, 而且会涉及到清表, 所以事务也是个问题
    @Override
    @Transient
    public void importAll(ExportData exportData) {
        truncateTable();

        moduleRepository.save(exportData.getModules());
        modelRepository.save(exportData.getModels());
        triggerFlowRepository.save(exportData.getTriggerFlows());
        subFlowRepository.save(exportData.getSubFlows());
        serviceRepository.save(exportData.getServices());
        functionRepository.save(exportData.getFunctions());
        connectorRepository.save(exportData.getConnectors());
        clientRepository.save(exportData.getClients());
        propertyRepository.save(exportData.getProperties());
        trantorRepository.save(exportData.getTrantorEntities());

        // load test flow
        testFlowInitializer.loadResources();
    }

    // TODO 如果数据暴多可能内存会炸, 可以分页读逐渐序列化至磁盘, 最后读流输出回前端
    // TODO 但是导入一样可能会炸, 一样需要流处理, 太麻烦, 暂时也没那么多数据

    @Override
    public ExportData exportAll() {
        ExportData exportData = new ExportData();
        exportData.setModules(moduleRepository.findAll());
        exportData.setModels(modelRepository.findAll());
        exportData.setTriggerFlows(triggerFlowRepository.findAll());
        exportData.setSubFlows(subFlowRepository.findAll());
        exportData.setServices(serviceRepository.findAll());
        exportData.setFunctions(functionRepository.findAll());
        exportData.setConnectors(connectorRepository.findAll());
        exportData.setClients(clientRepository.findAll());
        exportData.setProperties(propertyRepository.findAll());
        exportData.setTrantorEntities(trantorRepository.findAll());
        return exportData;
    }

    @Override
    public Swagger exportSwagger() {
        List<ApiInfo> apiInfoList = getExportApiInfoList();
        return SwaggerUtils.buildSwagger(apiInfoList);
    }

    @Override
    public File exportWord() {
        List<ApiInfo> apiInfoList = getExportApiInfoList();
        return WordUtils.buildWordFile(apiInfoList);
    }

    // TODO 比较暴力, 但是需要重置 ID 自增, 否则 Json 内的依赖可能会有问题
    private void truncateTable() {
        entityManager.joinTransaction();
        Session session = entityManager.unwrap(Session.class);
        Map<String, ClassMetadata> hibernateMetadata = session.getSessionFactory().getAllClassMetadata();
        hibernateMetadata.values().stream().map(classMetadata -> ((AbstractEntityPersister) classMetadata).getTableName()).forEach(tableName -> {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        });
    }

    private List<ApiInfo> getExportApiInfoList() {
        List<TriggerFlowEntity> restFlowList = triggerFlowRepository.findByStatusNotAndTriggerType(FlowStatus.Error, "http-rest-listener");
        return restFlowList.stream().map(flowEntity -> {
            ModuleEntity module = moduleRepository.findOne(flowEntity.getModuleId());
            RestConfig restConfig = JSON.parseObject(flowEntity.getTriggerConfig(), RestConfig.class);
            DalaranModelSchema inSchema = getModelSchema(flowEntity.getInModel());
            DalaranModelSchema outSchema = getModelSchema(flowEntity.getOutModel());
            return new ApiInfo(module.getName(), restConfig, flowEntity, inSchema, outSchema);
        }).collect(Collectors.toList());
    }

    private DalaranModelSchema getModelSchema(Long modelId) {
        ModelEntity modelEntity = modelRepository.findOne(modelId);
        Class<? extends DalaranModelSchema> schemaType = converterContext.getSchemaType(modelEntity.getType());
        return JSON.parseObject(modelEntity.getModelSchema(), schemaType);
    }

}
