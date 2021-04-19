package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.io.ByteSource;
import io.terminus.dalaran.component.utils.DalaranFileUtils;
import io.terminus.dalaran.component.utils.HttpUtils;
import io.terminus.dalaran.component.utils.OSSUtils;
import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.model.FlowTemplate;
import io.terminus.dalaran.console.model.TemplateData;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.PrivateRepositoryService;
import io.terminus.dalaran.console.service.jpa.PrivateResourceQueryService;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.core.market.MarketResourceLoader;
import io.terminus.dalaran.core.oss.OSSAccount;
import io.terminus.dalaran.core.resource.entity.*;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.property.PropertyService;
import io.terminus.dalaran.core.resource.repository.PrivateRepositoryRepository;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.market.model.MarketResourceVersionDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.ResourceUploadRequest;
import io.terminus.dalaran.model.dto.*;
import io.terminus.dalaran.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.model.market.ResourceFile;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import io.terminus.dalaran.model.query.ResourceQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.terminus.dalaran.DalaranConstants.*;

@Slf4j
@Service
public class PrivateRepositoryServiceImpl implements PrivateRepositoryService {

    @Autowired
    private PrivateRepositoryRepository privateRepository;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PrivateModelRepository privateModelRepository;

    @Autowired
    private PrivateConnectorRepository privateConnectorRepository;

    @Autowired
    private PrivateServiceRepository privateServiceRepository;

    @Autowired
    private PrivateFunctionRepository privateFunctionRepository;

    @Autowired
    private PrivateSubFlowRepository privateSubFlowRepository;

    @Autowired
    private TriggerFlowRepository triggerFlowRepository;

    @Autowired
    private SubFlowRepository subFlowRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ConnectorRepository connectorRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private OSSAccount ossAccount;

    @Autowired
    private MarketResourceLoader marketResourceLoader;

    @Autowired
    private PrivatePackageRepository privatePackageRepository;

    @Autowired
    private PrivateResourceQueryService privateResourceQueryService;

    private final RestTemplate restTemplate = new RestTemplate();

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Collection<MarketResourceVersionDTO> listPrivateResource(PrivateRepositoryQuery query) {
        List<PrivateRepositoryEntity> entities =  privateResourceQueryService.query(query);
        Map<String, List<BasicResourceDTO>> resourceMap = new HashMap<>();
        for (PrivateRepositoryEntity entity: entities) {
            String resourceKey = entity.getResourceKey();
            BasicResourceDTO basicResource = new BasicResourceDTO();
            try {
                BeanUtils.copyProperties(basicResource, entity);
                basicResource.setUpdateAt(format.format(entity.getUpdatedAt()));
                basicResource.setId(resourceKey);
                basicResource.setLabel(entity.getLabel());

                List<BasicResourceDTO> resourceList = resourceMap.get(resourceKey);
                if (CollectionUtils.isEmpty(resourceList)) {
                    resourceList = new ArrayList<>();
                    resourceMap.put(resourceKey, resourceList);
                }
                resourceList.add(basicResource);
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        List<MarketResourceVersionDTO> versionResourceList = new ArrayList<>();
        resourceMap.forEach((key, resources) -> {
            MarketResourceVersionDTO versionResource = new MarketResourceVersionDTO();
            if (CollectionUtils.isNotEmpty(resources)) {
                Map<String, BasicResourceDTO> versions = new HashMap<>();
                resources.forEach(resource -> versions.put(resource.getVersion(), resource));
                versionResource.setVersions(versions);
                BasicResourceDTO lastResource = resources.get(0);
                try {
                    BeanUtils.copyProperties(versionResource, lastResource);
                    versionResource.setLastVersion(lastResource.getVersion());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                versionResourceList.add(versionResource);
            }
        });
        return versionResourceList;
    }

    @Override
    public Page<MarketResourceVersionDTO> pagingPrivateResource(PrivateRepositoryQuery query, Integer pageNumber, Integer pageSize) {
        Page<PrivateRepositoryEntity> entities = privateResourceQueryService.paging(query, pageNumber, pageSize);
        Map<String, List<BasicResourceDTO>> resourceMap = new HashMap<>();
        for (PrivateRepositoryEntity entity: entities) {
            List<PrivateRepositoryEntity> privateRepositoryList = privateRepository.findByResourceKey(entity.getResourceKey());
            List<BasicResourceDTO> basicResourceList = new ArrayList<>();
            for (PrivateRepositoryEntity repositoryEntity: privateRepositoryList) {
                BasicResourceDTO basicResource = new BasicResourceDTO();
                try {
                    BeanUtils.copyProperties(basicResource, repositoryEntity);
                    basicResource.setUpdateAt(format.format(repositoryEntity.getUpdatedAt()));
                    basicResource.setId(repositoryEntity.getResourceKey());
                    basicResource.setLabel(repositoryEntity.getLabel());
                    basicResourceList.add(basicResource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            resourceMap.put(entity.getResourceKey(), basicResourceList);
        }

        List<MarketResourceVersionDTO> versionResourceList = new ArrayList<>();
        resourceMap.forEach((key, resources) -> {
            MarketResourceVersionDTO versionResource = new MarketResourceVersionDTO();
            if (CollectionUtils.isNotEmpty(resources)) {
                Map<String, BasicResourceDTO> versions = new HashMap<>();
                resources.forEach(resource -> versions.put(resource.getVersion(), resource));
                versionResource.setVersions(versions);
                BasicResourceDTO lastResource = resources.get(0);
                try {
                    BeanUtils.copyProperties(versionResource, lastResource);
                    versionResource.setLastVersion(lastResource.getVersion());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                versionResourceList.add(versionResource);
            }
        });
        return new PageImpl<>(versionResourceList, PageRequest.of(pageNumber - 1, pageSize), entities.getTotalElements());
    }

    @Override
    public List<ResourceGroupDTO> listResourceGroup() {
        List<ResourceGroupDTO> responseEntity = restTemplate.getForObject(
                propertyService.getMarketHost() + propertyService.getResourceGroup(),
                List.class);
        return responseEntity;
    }

    @Override
    public PrivateRelationResource listPrivateRelationResource() throws Exception {
        PrivateRelationResource privateRelationResource = new PrivateRelationResource();
        List<SubFlowDTO> subFlows = privateRelationResource.getSubFlows();
        List<SubFlowAbstractEntity> subFlowAbstractEntityList = new ArrayList<>();
        List<PrivateSubFlowEntity> privateSubFlowEntityList = privateSubFlowRepository.findAll();
        if (CollectionUtils.isNotEmpty(privateSubFlowEntityList)) {
            subFlowAbstractEntityList.addAll(privateSubFlowEntityList);
        }
        List<SubFlowEntity> subFlowEntityList = subFlowRepository.findAll();
        if (CollectionUtils.isNotEmpty(subFlowEntityList)) {
            subFlowAbstractEntityList.addAll(subFlowEntityList);
        }
        for (SubFlowAbstractEntity subFlowEntity : subFlowAbstractEntityList) {
            SubFlowDTO subFlowDTO = new SubFlowDTO();
            BeanUtils.copyProperties(subFlowDTO, subFlowEntity);
            subFlowDTO.setId(subFlowEntity.getResourceKey());
            List<ProcessorDTO> pipeline = new ArrayList<>();
            for (ProcessorEntity processorEntity : subFlowEntity.getPipeline()) {
                ProcessorDTO processor = new ProcessorDTO();
                processor.setGroup(processorEntity.getGroup());
                processor.setVersion(processorEntity.getVersion());
                processor.setId(processorEntity.getId());
                processor.setType(processorEntity.getType());
                processor.setName(processorEntity.getName());
                processor.setConfig(JSON.parseObject(processorEntity.getConfig(), Map.class));
                pipeline.add(processor);
            }
            subFlowDTO.setPipeline(pipeline);
            subFlows.add(subFlowDTO);
        }

        List<ModelDTO> models = privateRelationResource.getModels();
        List<ModelAbstractEntity> modelAbstractEntityList = new ArrayList<>();
        List<PrivateModelEntity> privateModelEntityList = privateModelRepository.findAll();
        if (CollectionUtils.isNotEmpty(privateModelEntityList)) {
            modelAbstractEntityList.addAll(privateModelEntityList);
        }
        List<ModelEntity> modelEntityList = modelRepository.findAll();
        if (CollectionUtils.isNotEmpty(modelEntityList)) {
            modelAbstractEntityList.addAll(modelEntityList);
        }
        for (ModelAbstractEntity modelEntity : modelAbstractEntityList) {
            ModelDTO modelDTO = new ModelDTO();
//            BeanUtils.copyProperties(modelDTO, modelEntity);
            modelDTO.setName(modelEntity.getName());
            modelDTO.setModelType(modelEntity.getType());
            modelDTO.setId(modelEntity.getResourceKey());
            modelDTO.setTargetId(modelEntity.getTargetId());
            modelDTO.setTargetType(modelEntity.getTargetType());
            modelDTO.setModelSchema(JSON.parseObject(modelEntity.getModelSchema(), Map.class));
            modelDTO.setId(modelEntity.getResourceKey());
            models.add(modelDTO);
        }

        List<ConnectorDTO> connectors = privateRelationResource.getConnectors();
        List<ConnectorAbstractEntity> connectorAbstractEntityList = new ArrayList<>();
        List<PrivateConnectorEntity> privateConnectorEntityList = privateConnectorRepository.findAll();
        if (CollectionUtils.isNotEmpty(privateConnectorEntityList)) {
            connectorAbstractEntityList.addAll(privateConnectorEntityList);
        }
        List<ConnectorEntity> connectorEntityList = connectorRepository.findAll();
        if (CollectionUtils.isNotEmpty(connectorEntityList)) {
            connectorAbstractEntityList.addAll(connectorEntityList);
        }
        for (ConnectorAbstractEntity connectorEntity : connectorAbstractEntityList) {
            ConnectorDTO connectorDTO = new ConnectorDTO();
//            BeanUtils.copyProperties(connectorDTO, connectorEntity);
            connectorDTO.setName(connectorEntity.getName());
            connectorDTO.setConnectorType(connectorEntity.getConnectorType());
            connectorDTO.setDescription(connectorEntity.getDescription());
            connectorDTO.setConfig(JSON.parseObject(connectorEntity.getConfig(), Map.class));
            connectorDTO.setId(connectorEntity.getResourceKey());
            connectors.add(connectorDTO);
        }

        List<ServiceDTO> services = privateRelationResource.getServices();
        List<ServiceAbstractEntity> serviceAbstractEntityList = new ArrayList<>();
        List<PrivateServiceEntity> privateServiceEntityList = privateServiceRepository.findAll();
        if (CollectionUtils.isNotEmpty(privateServiceEntityList)) {
            serviceAbstractEntityList.addAll(privateServiceEntityList);
        }
        List<ServiceEntity> serviceEntityList = serviceRepository.findAll();
        if (CollectionUtils.isNotEmpty(serviceEntityList)) {
            serviceAbstractEntityList.addAll(serviceEntityList);
        }
        for (ServiceAbstractEntity serviceEntity : serviceAbstractEntityList) {
            ServiceDTO serviceDTO = new ServiceDTO();
//            BeanUtils.copyProperties(serviceDTO, serviceEntity);
            serviceDTO.setName(serviceEntity.getName());
            serviceDTO.setType(serviceEntity.getType());
            serviceDTO.setImportConfig(JSON.parseObject(serviceEntity.getImportConfig(), Map.class));
            serviceDTO.setServiceConfig(JSON.parseObject(serviceEntity.getServiceConfig(), Map.class));
            serviceDTO.setId(serviceEntity.getResourceKey());
            services.add(serviceDTO);
        }

        List<FunctionDTO> functions = privateRelationResource.getFunctions();
        List<FunctionAbstractEntity> functionAbstractEntityList = new ArrayList<>();
        List<PrivateFunctionEntity> privateFunctionEntityList = privateFunctionRepository.findAll();
        if (CollectionUtils.isNotEmpty(privateFunctionEntityList)) {
            functionAbstractEntityList.addAll(privateFunctionEntityList);
        }
        List<FunctionEntity> functionEntityList = functionRepository.findAll();
        if (CollectionUtils.isNotEmpty(functionEntityList)) {
            functionAbstractEntityList.addAll(functionEntityList);
        }
        for (FunctionAbstractEntity functionEntity : functionAbstractEntityList) {
            FunctionDTO functionDTO = new FunctionDTO();
            BeanUtils.copyProperties(functionDTO, functionEntity);
            functionDTO.setId(functionEntity.getResourceKey());
            functions.add(functionDTO);
        }

        return privateRelationResource;
    }

    @Override
    public PrivateRepositoryDTO getResourceDetail(String id, String version) {
        PrivateRepositoryDTO privateRepositoryDTO = new PrivateRepositoryDTO();
        try {
            PrivateRepositoryEntity entity = privateRepository.findByResourceKeyAndVersion(id, version);
            BeanUtils.copyProperties(privateRepositoryDTO, entity);
            privateRepositoryDTO.setId(entity.getResourceKey());
            switch (entity.getType()) {
                case PROCESSOR:
                    ResourceFile resourceFile = JSON.parseObject(entity.getData(), ResourceFile.class);
                    String openUrl = OSSUtils.getFileUrl(resourceFile.getFilePath(), ossAccount);
                    resourceFile.setFilePath(openUrl);
                    privateRepositoryDTO.setData(resourceFile);
                    break;
                case FLOW_TEMPLATE:
                case SUB_FLOW_TEMPLATE:
                    TemplateData templateData = JSON.parseObject(entity.getData(), TemplateData.class);
                    Map<String, PrivatePackageEntity> packages = templateData.getRelationPackage();
                    if (MapUtils.isNotEmpty(packages)) {
                        packages.values().forEach(value -> {
                            log.info("value: " + value.toString());
                            String url = OSSUtils.getFileUrl(value.getFilePath(), ossAccount);
                            value.setFilePath(url);
                            log.info("url: " + url);
                        });
                    }
                    privateRepositoryDTO.setData(templateData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Get Resource Detail Error! ");
        }
        return privateRepositoryDTO;
    }

    @Override
    public BasicResponse publish(BasicResourceDTO basicResource) {
        try {
            PrivateRepositoryDTO privateResource = getResourceDetail(basicResource.getId(), basicResource.getVersion());
            BeanUtils.copyProperties(privateResource, basicResource);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PrivateRepositoryDTO> request = new HttpEntity<>(privateResource, headers);
            return restTemplate.postForObject(propertyService.getMarketHost() + propertyService.getMarketUpload(), request, BasicResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BasicResponse(false, "发布失败");
    }

    @Override
    public BasicResponse install(PrivateRepositoryDTO privateRepositoryDTO) {
        try {
            PrivateRepositoryEntity entity;
            List<PrivateRepositoryEntity> entities = privateResourceQueryService.findByResourceKeyAndVersion(privateRepositoryDTO.getId(), privateRepositoryDTO.getVersion());
            if (CollectionUtils.isNotEmpty(entities)) {
                return new BasicResponse(false, "资源已存在");
            } else {
                entity = toEntity(privateRepositoryDTO);
                entity.setId(null);
            }
            resourceInstall(privateRepositoryDTO, entity);
            entity.setResourceKey(privateRepositoryDTO.getId());
            entity.setOrigin(MARKET);
            entity.setData(JSON.toJSONString(privateRepositoryDTO.getData()));
            privateRepository.save(entity);
            return new BasicResponse(true, entity.getResourceKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BasicResponse(false, "下载失败");
    }

    @Override
    public BasicResponse saveTemplate(FlowTemplate flowTemplate) {
        try {
            PrivateRepositoryEntity entity = flowTemplateToEntity(flowTemplate);
            entity.setResourceKey(flowTemplate.getId());
            entity.setOrigin(PRIVATE);
            entity.setTenantCode(propertyService.getTenantCode());
            entity.setId(null);
            privateRepository.save(entity);
            return new BasicResponse(true, entity.getResourceKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BasicResponse(false, "保存失败");
    }

    @Override
    public BasicResponse localResourceUpload(MultipartFile file, String name, String version, String resourceGroup) {
        try {
            List<PrivateRepositoryEntity> privateRepositoryEntityList;
            PrivateRepositoryQuery privateRepositoryQuery = new PrivateRepositoryQuery(name, version, PROCESSOR);
            privateRepositoryEntityList = privateResourceQueryService.query(privateRepositoryQuery);
            if (CollectionUtils.isNotEmpty(privateRepositoryEntityList)) {
                return new BasicResponse(false, "resource is exist, " + name + ", " + version);
            }

            String resourceKey = null;
            privateRepositoryQuery.setVersion(null);
            PrivateRepositoryEntity privateRepositoryEntity = privateRepository.findByNameAndType(name, PROCESSOR);
            if (privateRepositoryEntity != null) {
                resourceKey = privateRepositoryEntity.getResourceKey();
            }

            File local = io.terminus.dalaran.console.util.FileUtils.transfer(file);
            String filePath = OSSUtils.upload(local, ossAccount);
            ResourceFile resourceFile = new ResourceFile(filePath);
            marketResourceLoader.install(local, PRIVATE, version);
            if (StringUtils.isBlank(resourceKey)) {
                resourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
            }
            PrivateRepositoryEntity entity = new PrivateRepositoryEntity();
            entity.setName(name);
            entity.setVersion(version);
            entity.setResourceGroup(resourceGroup);
            entity.setResourceKey(resourceKey);
            entity.setData(JSON.toJSONString(resourceFile));
            entity.setId(null);
            entity.setTenantCode(propertyService.getTenantCode());
            entity.setType(PROCESSOR);
            entity.setOrigin(PRIVATE);
            privateRepository.save(entity);
            return new BasicResponse(true, resourceKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BasicResponse(false, "上传失败");
    }

    @Override
    public BasicResponse localResourceUpload(ResourceUploadRequest resourceUploadRequest) {
        String name = resourceUploadRequest.getName();
        String version = resourceUploadRequest.getVersion();
        String resourceGroup = resourceUploadRequest.getResourceGroup();
        String type = resourceUploadRequest.getType();
        if (StringUtils.isBlank(type)) {
            type = PROCESSOR;
        }
        try {
            List<PrivateRepositoryEntity> privateRepositoryEntityList;
            PrivateRepositoryQuery privateRepositoryQuery = new PrivateRepositoryQuery(name, version, PROCESSOR);
            privateRepositoryEntityList = privateResourceQueryService.query(privateRepositoryQuery);
            if (CollectionUtils.isNotEmpty(privateRepositoryEntityList)) {
                return new BasicResponse(false, "resource is exist, " + name + ", " + version);
            }

            String resourceKey = null;
            privateRepositoryQuery.setVersion(null);
            PrivateRepositoryEntity privateRepositoryEntity = privateRepository.findByNameAndType(name, type);
            if (privateRepositoryEntity != null) {
                resourceKey = privateRepositoryEntity.getResourceKey();
            }

            String filePath = resourceUploadRequest.getFilePath();
            File local = OSSUtils.downloadByPath(resourceUploadRequest.getFilePath(), ossAccount);
            ResourceFile resourceFile = new ResourceFile(filePath);
            marketResourceLoader.install(local, PRIVATE, version);
            if (StringUtils.isBlank(resourceKey)) {
                resourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
            }
            PrivateRepositoryEntity entity = new PrivateRepositoryEntity();
            entity.setName(name);
            entity.setVersion(version);
            entity.setResourceGroup(resourceGroup);
            entity.setResourceKey(resourceKey);
            entity.setData(JSON.toJSONString(resourceFile));
            entity.setId(null);
            entity.setTenantCode(propertyService.getTenantCode());
            entity.setType(type);
            entity.setOrigin(PRIVATE);
            privateRepository.save(entity);
            return new BasicResponse(true, resourceKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BasicResponse(false, "上传失败");    }

    @Override
    public BasicResponse delete(BasicResourceRequest request) {
        try {
            List<PrivateRepositoryEntity> entities = privateResourceQueryService.findByResourceKeyAndVersion(request.getId(), request.getVersion());
            if (CollectionUtils.isEmpty(entities)) {
                return new BasicResponse(true);
            }
            PrivateRepositoryEntity entity = entities.get(0);
            String flowName = checkResourceDependency(entity);
            if (StringUtils.isNotBlank(flowName)) {
                return new BasicResponse(false, "该资源已经被其他流程依赖，删除失败. 流程名：" + flowName);
            }

            entity.setExist(false);
            if (StringUtils.equalsIgnoreCase(entity.getType(), PROCESSOR) || StringUtils.equalsIgnoreCase(entity.getType(), TRIGGER)) {
                marketResourceLoader.uninstall(entity.getOrigin(), entity.getName(), entity.getVersion());
            }

            if (StringUtils.equalsIgnoreCase(entity.getOrigin(), PRIVATE)) {
                privateRepository.save(entity);
                return new BasicResponse(true, "删除成功");
            }

            ResourceQuery resourceQuery = new ResourceQuery(entity.getResourceKey(), entity.getVersion(), propertyService.getTenantCode());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ResourceQuery> httpEntity = new HttpEntity<>(resourceQuery, headers);
            restTemplate.postForObject(propertyService.getMarketHost() + propertyService.getDeleteTenantResourceRelation(), httpEntity, BasicResponse.class);
            privateRepository.save(entity);
            return new BasicResponse(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BasicResponse(false, "删除失败");
    }

    private String checkResourceDependency(PrivateRepositoryEntity entity) {
        if (StringUtils.equalsIgnoreCase(entity.getType(), FLOW_TEMPLATE) || StringUtils.equalsIgnoreCase(entity.getType(), SUB_FLOW_TEMPLATE)) {
            return null;
        }
        String resourceType = entity.getName();
        List<TriggerFlowEntity> triggerFlowEntityList = triggerFlowRepository.findByIsExistTrue();
        for (TriggerFlowEntity flowEntity : triggerFlowEntityList) {
            if (StringUtils.contains(flowEntity.getTriggerConfig(), resourceType) || StringUtils.contains(JSON.toJSONString(flowEntity.getPipeline()), resourceType)) {
                return flowEntity.getName();
            }
        }

        List<SubFlowEntity> subFlowEntityList = subFlowRepository.findByIsExistTrue();
        for (SubFlowEntity subFlowEntity : subFlowEntityList) {
            if (StringUtils.contains(JSON.toJSONString(subFlowEntity.getPipeline()), resourceType)) {
                return subFlowEntity.getName();
            }
        }

        return null;
    }

    private PrivateRepositoryEntity flowTemplateToEntity(FlowTemplate flowTemplate) throws Exception {
        String resourceKey = flowTemplate.getId();
        Map<String, Object> triggerConfig = JSON.parseObject(flowTemplate.getData().getTriggerConfig(), Map.class);
        if (MapUtils.isNotEmpty(triggerConfig)) {
            Map<String, Object> newTriggerConfig = new HashMap<>();
            newTriggerConfig.put("inModelId", triggerConfig.get("inModelId"));
            newTriggerConfig.put("outModelId", triggerConfig.get("outModelId"));
            flowTemplate.getData().setTriggerConfig(JSON.toJSONString(newTriggerConfig));
        }

        if (StringUtils.isBlank(resourceKey)) {
            resourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
        }
        PrivateRepositoryEntity entity = new PrivateRepositoryEntity();
        BeanUtils.copyProperties(entity, flowTemplate);
        entity.setData(JSON.toJSONString(flowTemplate.getData()));
        entity.setResourceKey(resourceKey);
        return entity;
    }

    private PrivateRepositoryEntity toEntity(PrivateRepositoryDTO privateRepository) throws Exception {
        PrivateRepositoryEntity entity = new PrivateRepositoryEntity();
        BeanUtils.copyProperties(entity, privateRepository);
        entity.setResourceKey(privateRepository.getId());
        entity.setData((String)privateRepository.getData());
        return entity;
    }

    private void resourceInstall(PrivateRepositoryDTO privateRepositoryDTO, PrivateRepositoryEntity privateRepositoryEntity) throws Exception {
        switch (privateRepositoryDTO.getType()) {
            case PROCESSOR:
                ResourceFile resourceFile = JSON.parseObject((String)privateRepositoryDTO.getData(), ResourceFile.class);

                PrivatePackageEntity entity = privatePackageRepository.findByResourceKeyAndVersion(privateRepositoryDTO.getId(), privateRepositoryDTO.getVersion());
                if (entity == null) {
                    entity = new PrivatePackageEntity();
                    BeanUtils.copyProperties(entity, privateRepositoryDTO);
                    entity.setId(null);
                }

                String fileUrl = resourceFile.getFilePath();
                log.info("fileUrl: " + fileUrl);
                byte[] content = HttpUtils.get(fileUrl);
                if (content == null) {
                    log.info("file url: " + fileUrl + " get content null.");
                    return;
                }
                log.info("file size: " + content.length);

                String fileKey = OSSUtils.upload(  System.currentTimeMillis() + fileUrl.hashCode() + ".jar", content, ossAccount);
                privateRepositoryEntity.setData(JSON.toJSONString(new ResourceFile(fileKey)));
                entity.setFilePath(fileKey);
                entity.setResourceKey(privateRepositoryDTO.getId());
                privatePackageRepository.save(entity);

                File file = OSSUtils.downloadByPath(fileKey, ossAccount);
                log.info("temp file: " + file.getName() + ", size: " + file.getTotalSpace());
                marketResourceLoader.install(file, MARKET, entity.getVersion());
                break;
            case FLOW_TEMPLATE:
            case SUB_FLOW_TEMPLATE:
                TemplateData templateData = JSON.parseObject((String) privateRepositoryDTO.getData(), TemplateData.class);
                loadRelationResource(templateData);
                privateRepositoryDTO.setData(templateData);
                break;
        }
    }

    private void loadRelationResource(TemplateData templateData) throws Exception {
        List<ProcessorEntity> processorEntityList = templateData.getPipeline();
        for (ProcessorEntity processorEntity: processorEntityList) {
            if (StringUtils.isNotBlank(processorEntity.getGroup()) && StringUtils.equalsIgnoreCase(processorEntity.getGroup(), PRIVATE)) {
                processorEntity.setGroup(MARKET);
            }
        }
        Map<String, ModelEntity> models =  templateData.getRelationModel();
        if (MapUtils.isNotEmpty(models)) {
            for (Map.Entry<String, ModelEntity> entityEntry: models.entrySet()) {
                List<PrivateModelEntity> modelEntity = privateModelRepository.findByResourceKey(entityEntry.getKey());
                if (CollectionUtils.isEmpty(modelEntity)) {
                    PrivateModelEntity privateModelEntity = new PrivateModelEntity();
                    BeanUtils.copyProperties(privateModelEntity, entityEntry.getValue());
                    privateModelEntity.setId(null);
                    privateModelRepository.save(privateModelEntity);
                }
            }
        }
        Map<String, ConnectorEntity> connectors = templateData.getRelationConnector();
        if (MapUtils.isNotEmpty(connectors)) {
            for (Map.Entry<String, ConnectorEntity> entityEntry: connectors.entrySet()) {
                List<PrivateConnectorEntity> connectorEntities = privateConnectorRepository.findByResourceKey(entityEntry.getKey());
                if (CollectionUtils.isEmpty(connectorEntities)) {
                    PrivateConnectorEntity privateConnectorEntity = new PrivateConnectorEntity();
                    BeanUtils.copyProperties(privateConnectorEntity, entityEntry.getValue());
                    privateConnectorEntity.setId(null);
                    privateConnectorRepository.save(privateConnectorEntity);
                }
            }
        }
        Map<String, FunctionEntity> functions = templateData.getRelationFunction();
        if (MapUtils.isNotEmpty(functions)) {
            for (Map.Entry<String, FunctionEntity> entityEntry: functions.entrySet()) {
                List<PrivateFunctionEntity> functionEntities = privateFunctionRepository.findByResourceKey(entityEntry.getKey());
                if (CollectionUtils.isEmpty(functionEntities)) {
                    PrivateFunctionEntity privateFunctionEntity = new PrivateFunctionEntity();
                    BeanUtils.copyProperties(privateFunctionEntity, entityEntry.getValue());
                    privateFunctionEntity.setId(null);
                    privateFunctionRepository.save(privateFunctionEntity);
                }
            }
        }
        Map<String, ServiceEntity> services = templateData.getRelationService();
        if (MapUtils.isNotEmpty(services)) {
            for (Map.Entry<String, ServiceEntity> entityEntry: services.entrySet()) {
                List<PrivateServiceEntity> serviceEntities = privateServiceRepository.findByResourceKey(entityEntry.getKey());
                if (CollectionUtils.isEmpty(serviceEntities)) {
                    PrivateServiceEntity privateServiceEntity = new PrivateServiceEntity();
                    BeanUtils.copyProperties(privateServiceEntity, entityEntry.getValue());
                    privateServiceEntity.setId(null);
                    privateServiceRepository.save(privateServiceEntity);
                }
            }
        }
        Map<String, SubFlowEntity> subFlows = templateData.getRelationSubFlow();
        if (MapUtils.isNotEmpty(subFlows)) {
            for (Map.Entry<String, SubFlowEntity> entityEntry: subFlows.entrySet()) {
                List<PrivateSubFlowEntity> subFlowEntities = privateSubFlowRepository.findByResourceKey(entityEntry.getKey());
                if (CollectionUtils.isEmpty(subFlowEntities)) {
                    PrivateSubFlowEntity privateSubFlowEntity = new PrivateSubFlowEntity();
                    BeanUtils.copyProperties(privateSubFlowEntity, entityEntry.getValue());
                    privateSubFlowEntity.setId(null);
                    privateSubFlowRepository.save(privateSubFlowEntity);
                }
            }
        }
        Map<String, PrivatePackageEntity> resourceFile = templateData.getRelationPackage();
        // todo load processor
        if (MapUtils.isNotEmpty(resourceFile)) {
            for (Map.Entry<String, PrivatePackageEntity> entityEntry: resourceFile.entrySet()) {
                PrivatePackageEntity privatePackageEntity = privatePackageRepository.findByResourceKeyAndVersion(entityEntry.getValue().getResourceKey(), entityEntry.getValue().getVersion());
                if (privatePackageEntity == null) {
                    PrivatePackageEntity entity = new PrivatePackageEntity();
                    BeanUtils.copyProperties(entity, entityEntry.getValue());

                    String fileUrl = entityEntry.getValue().getFilePath();
                    byte[] content = HttpUtils.get(fileUrl);
                    if (content == null) {
                        log.info("file url: " + fileUrl + " get content null.");
                        continue;
                    }
                    entity.setFilePath(OSSUtils.upload(System.currentTimeMillis() + fileUrl.hashCode() + ".jar", content, ossAccount));
                    entity.setId(null);
                    privatePackageRepository.save(entity);
                    loadProcessor(entityEntry.getValue().getFilePath(), MARKET, entity.getVersion());
                }
            }
        }
    }

    private void loadProcessor(String fileUrl, String group, String version) throws Exception {
        byte[] content = HttpUtils.get(fileUrl);
        if (content == null) {
            log.info("file url: " + fileUrl + " get content null.");
            return;
        }
        File file = DalaranFileUtils.createFile(System.currentTimeMillis() + fileUrl.hashCode() + "");
        FileUtils.copyToFile(ByteSource.wrap(content).openStream(), file);
        marketResourceLoader.install(file, group, version);
    }
}
