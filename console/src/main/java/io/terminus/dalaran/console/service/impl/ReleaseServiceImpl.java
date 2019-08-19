package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.*;
import io.terminus.dalaran.component.trigger.rest.RestConfig;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.ModuleEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.ExportData;
import io.terminus.dalaran.console.model.ReleaseRequestDTO;
import io.terminus.dalaran.console.model.dto.ReleaseRecordDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.ReleaseService;
import io.terminus.dalaran.core.context.DalaranConverterContext;
import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.released.*;
import io.terminus.dalaran.core.resource.repository.*;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.flow.FlowStatus;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static io.terminus.dalaran.DalaranConstants.MODEL_ROOT;

@Slf4j
@Service
@Transactional
public class ReleaseServiceImpl implements ReleaseService {

    @Autowired
    private DalaranConverterContext converterContext;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestFlowInitializer testFlowInitializer;

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

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
    private TriggerFlowReleasedRepository triggerFlowReleasedRepository;

    @Autowired
    private SubFlowReleasedRepository subFlowReleasedRepository;

    @Autowired
    private ModelReleasedRepository modelReleasedRepository;

    @Autowired
    private ConnectorReleasedRepository connectorReleasedRepository;

    @Autowired
    private PropertyReleasedRepository propertyReleasedRepository;

    @Autowired
    private FunctionReleasedRepository functionReleasedRepository;

    @Autowired
    private ClientReleasedRepository clientReleasedRepository;

    private final FlowConvertor flowConvertor = new FlowConvertor();

    @Override
    public ReleaseRecordDTO release(ReleaseRequestDTO requestDTO) {
        ReleaseRecordEntity enabledReleaseEntity = releaseRecordRepository.findByEnabledTrue();
        if (enabledReleaseEntity != null) {
            enabledReleaseEntity.setEnabled(false);
            releaseRecordRepository.save(enabledReleaseEntity);
        }

        ReleaseRecordEntity recordEntity = new ReleaseRecordEntity();
        recordEntity.setEnabled(true);
        recordEntity.setVersion(requestDTO.getVersion());
        recordEntity.setReleaseLog(requestDTO.getReleaseLog());
        recordEntity.setReleaseTime(new Date());
        // TODO 需要校验是否有误, 暂时没做
        recordEntity.setSuccessful(true);
        releaseRecordRepository.save(recordEntity);

        List<TriggerFlowReleasedEntity> releasedTriggerFlowEntities = toReleasedData(triggerFlowRepository.findAll(), TriggerFlowReleasedEntity.class, requestDTO.getVersion());
        triggerFlowReleasedRepository.save(releasedTriggerFlowEntities);

        List<SubFlowReleasedEntity> releasedSubFlowEntities = toReleasedData(subFlowRepository.findAll(), SubFlowReleasedEntity.class, requestDTO.getVersion());
        subFlowReleasedRepository.save(releasedSubFlowEntities);

        List<ModelReleasedEntity> releasedModelEntities = toReleasedData(modelRepository.findAll(), ModelReleasedEntity.class, requestDTO.getVersion());
        modelReleasedRepository.save(releasedModelEntities);

        List<ConnectorReleasedEntity> releasedConnectorEntities = toReleasedData(connectorRepository.findAll(), ConnectorReleasedEntity.class, requestDTO.getVersion());
        connectorReleasedRepository.save(releasedConnectorEntities);

        List<PropertyReleasedEntity> releasedPropertyEntities = toReleasedData(propertyRepository.findAll(), PropertyReleasedEntity.class, requestDTO.getVersion());
        propertyReleasedRepository.save(releasedPropertyEntities);

        List<FunctionReleasedEntity> releasedFunctionEntities = toReleasedData(functionRepository.findAll(), FunctionReleasedEntity.class, requestDTO.getVersion());
        functionReleasedRepository.save(releasedFunctionEntities);

        List<ClientReleasedEntity> releasedClientEntities = toReleasedData(clientRepository.findAll(), ClientReleasedEntity.class, requestDTO.getVersion());
        clientReleasedRepository.save(releasedClientEntities);

        return toDTO(recordEntity);
    }

    @Override
    public ReleaseRecordDTO rollback(String version) {
        ReleaseRecordEntity enabledReleaseEntity = releaseRecordRepository.findByEnabledTrue();
        if (enabledReleaseEntity != null) {
            enabledReleaseEntity.setEnabled(false);
            releaseRecordRepository.save(enabledReleaseEntity);
        }
        ReleaseRecordEntity nextReleaseRecord = releaseRecordRepository.findByVersion(version);
        if (nextReleaseRecord != null) {
            nextReleaseRecord.setEnabled(true);
            releaseRecordRepository.save(nextReleaseRecord);
        }
        return toDTO(nextReleaseRecord);
    }

    @Override
    public List<TriggerFlowDTO> listReleasedTriggerFlowDTO(String version) {
        return triggerFlowReleasedRepository.findByVersion(version).stream().map(flowConvertor::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<TriggerFlowReleasedEntity> listReleasedTriggerFlow(String version) {
        return triggerFlowReleasedRepository.findByVersion(version);
    }

    @Override
    public List<SubFlowReleasedEntity> listReleasedSubFlow(String version) {
        return subFlowReleasedRepository.findByVersion(version);
    }

    @Override
    public ModelReleasedEntity getReleasedModel(String version, Long modelId) {
        return modelReleasedRepository.findByVersionAndOriginId(version, modelId);
    }

    @Override
    public List<PropertyReleasedEntity> getReleasedProperty(String version) {
        return propertyReleasedRepository.findByVersion(version);
    }

    @Override
    public List<ReleaseRecordDTO> listReleaseRecordDTO() {
        return releaseRecordRepository.findAll(new Sort(Sort.Direction.DESC, "releaseTime"))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

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
        Swagger swagger = new Swagger();
        swagger.setPaths(new LinkedHashMap<>());
        List<TriggerFlowEntity> restFlowList = triggerFlowRepository.findByStatusNotAndTriggerType(FlowStatus.Error, "http-rest-listener");
        for (TriggerFlowEntity flowEntity : restFlowList) {
            ModuleEntity module = moduleRepository.findOne(flowEntity.getModuleId());

            RestConfig restConfig = JSON.parseObject(flowEntity.getTriggerConfig(), RestConfig.class);
            Path path = swagger.getPaths().computeIfAbsent(restConfig.getPath(), p -> new Path());
            Operation operation = new Operation();
            path.set(restConfig.getMethod().toString().toLowerCase(), operation);

            operation.setOperationId(flowEntity.getId().toString());
            operation.setSummary(flowEntity.getName());
            operation.setDescription(flowEntity.getDescription());
            operation.addTag(module.getName());
            operation.addConsumes("application/json");

            Response response = new Response();
            operation.addResponse("200", response);

            response.description("OK");

            response.setSchema(toSwaggerProperty(flowEntity.getOutModel()));
            if (restConfig.getMethod().isNoBody()) {
                operation.setParameters(toQueryParameter(flowEntity.getInModel()));
            } else {
                operation.addParameter(toBodyParameter(flowEntity.getInModel()));
            }
        }
        return swagger;
    }

    private List<Parameter> toQueryParameter(Long id) {
        ModelField rootField = getRootField(id);
        List<Parameter> parameters = new ArrayList<>();
        rootField.getFields().forEach((name, subField) -> {
            QueryParameter parameter = new QueryParameter();
            parameters.add(parameter);
            Property subFieldProperty = buildProperty(subField);
            if (subFieldProperty != null) {
                parameter.name(name).type(subFieldProperty.getType());
            }
        });
        return parameters;
    }

    private Parameter toBodyParameter(Long id) {
        ModelField rootField = getRootField(id);
        BodyParameter parameter = new BodyParameter();
        Model bodyModel = new ModelImpl();
        Map<String, Property> properties = new HashMap<>();
        rootField.getFields().forEach((name, subField) -> {
            properties.put(name, buildProperty(subField));
        });
        bodyModel.setProperties(properties);
        parameter.setSchema(bodyModel);
        return parameter;
    }


    private Property toSwaggerProperty(Long id) {
        ModelField rootField = getRootField(id);
        return buildProperty(rootField);
    }

    private Property buildProperty(ModelField field) {
        switch (field.getType()) {
            case INTEGER:
                return new LongProperty();
            case FLOAT:
                return new DoubleProperty();
            case DATE:
                return new DateProperty();
            case STRING:
                return new StringProperty();
            case BOOLEAN:
                return new BooleanProperty();
            case ARRAY: {
                // TODO 这个 SubType 很难受...
                ArrayProperty arrayProperty = new ArrayProperty();
                switch (field.getSubType()) {
                    case INTEGER:
                        arrayProperty.setItems(new LongProperty());
                        break;
                    case FLOAT:
                        arrayProperty.setItems(new DoubleProperty());
                        break;
                    case DATE:
                        arrayProperty.setItems(new DateProperty());
                        break;
                    case STRING:
                        arrayProperty.setItems(new StringProperty());
                        break;
                    case BOOLEAN:
                        arrayProperty.setItems(new BooleanProperty());
                        break;
                    case OBJECT: {
                        ObjectProperty property = new ObjectProperty();
                        field.getFields().forEach((name, subField) -> property.property(name, buildProperty(subField)));
                        arrayProperty.setItems(property);
                        break;
                    }
                }
                return arrayProperty;
            }
            case OBJECT: {
                ObjectProperty property = new ObjectProperty();
                field.getFields().forEach((name, subField) -> {
                    property.property(name, buildProperty(subField));
                });
                return property;
            }
        }
        return null;
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

    private <T extends ReleasedEntity, E extends BasicEntity> List<T> toReleasedData(List<E> data, Class<T> releasedType, String version) {
        return data.stream().map(entity -> {
            try {
                T releasedEntity = releasedType.newInstance();
                BeanUtils.copyProperties(entity, releasedEntity);
                releasedEntity.setId(null);
                releasedEntity.setOriginId(entity.getId());
                releasedEntity.setVersion(version);
                return releasedEntity;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            // TODO throw...
            return null;
        }).collect(Collectors.toList());
    }

    private ReleaseRecordDTO toDTO(ReleaseRecordEntity recordEntity) {
        ReleaseRecordDTO dto = new ReleaseRecordDTO();
        BeanUtils.copyProperties(recordEntity, dto);
        return dto;
    }
}
