package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.swagger.models.Swagger;
import io.terminus.dalaran.component.trigger.rest.RestConfig;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.ExportData;
import io.terminus.dalaran.console.model.api.ApiInfo;
import io.terminus.dalaran.console.model.api.ApiParameter;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.ExportService;
import io.terminus.dalaran.console.util.SwaggerUtils;
import io.terminus.dalaran.console.util.WordUtils;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.flow.FlowStatus;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.terminus.dalaran.DalaranConstants.MODEL_ROOT;

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

    private List<ApiInfo> getExportApiInfoList() {
        List<TriggerFlowEntity> restFlowList = triggerFlowRepository.findByStatusNotAndTriggerType(FlowStatus.Error, "http-rest-listener");
        return restFlowList.stream().map(flowEntity -> {
            RestConfig restConfig = JSON.parseObject(flowEntity.getTriggerConfig(), RestConfig.class);
            ApiInfo api = new ApiInfo();
            api.setName(flowEntity.getName());
            api.setDescription(flowEntity.getDescription());
            api.setPath(restConfig.getPath());
            api.setMethod(restConfig.getMethod());
            api.setInput(buildApiParam(flowEntity.getInModel(), api));
            api.setOutput(buildApiParam(flowEntity.getOutModel(), api));
            return api;
        }).collect(Collectors.toList());
    }

    private ApiParameter buildApiParam(Long modelId, ApiInfo api) {
        ModelField inModelRootField = getRootField(modelId);
        ApiParameter rootParam = new ApiParameter();
        rootParam.setType(inModelRootField.getType());
        rootParam.setDescription(inModelRootField.getDescription());
        if (!inModelRootField.getType().isBasicType()) {
            inModelRootField.getFields().forEach((name, subField) -> rootParam.getSubParameter().put(name, buildParameters(subField, 1, api)));
        }
        return rootParam;

    }

    private ApiParameter buildParameters(ModelField field, int level, ApiInfo api) {
        if (level > api.getParamLevel()) {
            api.setParamLevel(level);
        }
        ApiParameter param = new ApiParameter();
        param.setType(field.getType());
        param.setDescription(field.getDescription());
        switch (field.getType()) {
            case ARRAY: {
                if (field.getSubType().isBasicType()) {
                    ApiParameter subParam = new ApiParameter();
                    subParam.setType(field.getSubType());
                    subParam.setDescription(field.getDescription());
                    param.getSubParameter().put("", subParam);
                    break;
                }
            }
            case OBJECT: {
                field.getFields().forEach((subFieldName, subField) -> {
                    ApiParameter subParam = buildParameters(subField, level + 1, api);
                    param.getSubParameter().put(subFieldName, subParam);
                });
                break;
            }
        }
        return param;
    }

    private ModelField getRootField(Long modelId) {
        ModelEntity modelEntity = modelRepository.findOne(modelId);
        Class<? extends DalaranModelSchema> schemaType = converterContext.getSchemaType(modelEntity.getType());
        DalaranModelSchema modelSchema = JSON.parseObject(modelEntity.getModelSchema(), schemaType);
        ModelField rootField = modelSchema.getFields().get(MODEL_ROOT);
        return rootField;
    }

    // TODO 比较暴力, 但是需要重置 ID 自增, 否则 Json 内的依赖可能会有问题
    private void truncateTable() {
        Session session = entityManager.unwrap(Session.class);
        Map<String, ClassMetadata> hibernateMetadata = session.getSessionFactory().getAllClassMetadata();
        hibernateMetadata.values().stream().map(classMetadata -> ((AbstractEntityPersister) classMetadata).getTableName()).forEach(tableName -> {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        });
    }
}
