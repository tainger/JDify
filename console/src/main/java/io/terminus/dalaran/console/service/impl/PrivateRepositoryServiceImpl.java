package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.model.OSSObject;
import io.terminus.dalaran.component.utils.DalaranFileUtils;
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
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.property.PropertyService;
import io.terminus.dalaran.core.resource.repository.PrivateRepositoryRepository;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.market.model.MarketResourceVersionDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import io.terminus.dalaran.model.dto.ResourceGroupDTO;
import io.terminus.dalaran.model.market.MarketProcessor;
import io.terminus.dalaran.model.market.ResourceFile;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

import static io.terminus.dalaran.DalaranConstants.*;

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
    private OSSAccount ossAccount;

    @Autowired
    private MarketResourceLoader marketResourceLoader;

    @Autowired
    private PrivatePackageRepository privatePackageRepository;

    @Autowired
    private PrivateResourceQueryService privateResourceQueryService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Collection<MarketResourceVersionDTO> listPrivateResource(PrivateRepositoryQuery query) {
        List<PrivateRepositoryEntity> entities =  privateResourceQueryService.query(query);
        Map<String, List<BasicResourceDTO>> resourceMap = new HashMap<>();
        for (PrivateRepositoryEntity entity: entities) {
            String resourceKey = entity.getResourceKey();
            BasicResourceDTO basicResource = new BasicResourceDTO();
            try {
                BeanUtils.copyProperties(basicResource, entity);
                basicResource.setId(resourceKey);

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
    public List<ResourceGroupDTO> listResourceGroup() {
        ResponseEntity<List<ResourceGroupDTO>> responseEntity = restTemplate.exchange(
                propertyService.getMarketHost() + propertyService.getResourceGroup(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ResourceGroupDTO>>() {});
        return responseEntity.getBody();
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
                    MarketProcessor marketProcessor = JSON.parseObject(entity.getData(), MarketProcessor.class);
                    String openUrl = OSSUtils.getFileUrl(marketProcessor.getData().getFilePath(), ossAccount);
                    marketProcessor.getData().setFilePath(openUrl);
                    privateRepositoryDTO.setData(marketProcessor);
                    break;
                case FLOW_TEMPLATE:
                case SUB_FLOW_TEMPLATE:
                    FlowTemplate flowTemplate = JSON.parseObject(entity.getData(), FlowTemplate.class);
                    Map<String, PrivatePackageEntity> packages = flowTemplate.getData().getRelationPackage();
                    if (MapUtils.isNotEmpty(packages)) {
                        packages.values().forEach(value -> {
                            String url = OSSUtils.getFileUrl(value.getFilePath(), ossAccount);
                            value.setFilePath(url);
                        });
                    }
                    privateRepositoryDTO.setData(packages);
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
        return new BasicResponse(false);
    }

    @Override
    public BasicResponse install(PrivateRepositoryDTO privateRepositoryDTO) {
        try {
            resourceInstall(privateRepositoryDTO);
            PrivateRepositoryEntity entity = toEntity(privateRepositoryDTO);
            entity.setResourceKey(privateRepositoryDTO.getId());
            entity.setOrigin(MARKET);
            entity.setId(null);
            privateRepository.save(entity);
            return new BasicResponse(true, entity.getResourceKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BasicResponse(false);
    }

    @Override
    public BasicResponse saveTemplate(FlowTemplate flowTemplate) {
        try {
            PrivateRepositoryEntity entity = flowTemplateToEntity(flowTemplate);
            entity.setResourceKey(flowTemplate.getId());
            entity.setOrigin(PRIVATE);
            entity.setId(null);
            privateRepository.save(entity);
            return new BasicResponse(true, entity.getResourceKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BasicResponse(false);
    }

    @Override
    public BasicResponse localResourceUpload(MultipartFile file, BasicResourceDTO basicResource) {
        try {
            File local = io.terminus.dalaran.console.util.FileUtils.transfer(file);
            String filePath = OSSUtils.upload(local, ossAccount);
            ResourceFile resourceFile = new ResourceFile(filePath);
            marketResourceLoader.loadProcessor(local);
            PrivateRepositoryEntity entity = new PrivateRepositoryEntity();
            BeanUtils.copyProperties(entity, basicResource);
            String resourceKey = basicResource.getId();
            if (StringUtils.isBlank(resourceKey)) {
                resourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
            }
            entity.setResourceKey(resourceKey);
            entity.setData(JSON.toJSONString(resourceFile));
            entity.setId(null);
            privateRepository.save(entity);
            return new BasicResponse(true, resourceKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BasicResponse(false);
    }

    private PrivateRepositoryEntity flowTemplateToEntity(FlowTemplate flowTemplate) throws Exception {
        String resourceKey = flowTemplate.getId();
        if (StringUtils.isBlank(resourceKey)) {
            resourceKey = GenerateKeyUtils.resourceKey(propertyService.getTenantCode());
        }
        PrivateRepositoryEntity entity = new PrivateRepositoryEntity();
        BeanUtils.copyProperties(entity, flowTemplate);
        entity.setData(JSON.toJSONString(flowTemplate));
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

    private void resourceInstall(PrivateRepositoryDTO privateRepositoryDTO) throws Exception {
        switch (privateRepositoryDTO.getType()) {
            case PROCESSOR:
                MarketProcessor marketProcessor = JSON.parseObject((String)privateRepositoryDTO.getData(), MarketProcessor.class);
                PrivatePackageEntity privatePackageEntity = privatePackageRepository.findByResourceKeyAndVersion(marketProcessor.getId(), marketProcessor.getVersion());
                if (privatePackageEntity != null) {
                    return;
                }

                PrivatePackageEntity entity = new PrivatePackageEntity();
                BeanUtils.copyProperties(entity, marketProcessor);

                String fileUrl = marketProcessor.getData().getFilePath();
                OSSObject ossObject = OSSUtils.downloadByUrl(fileUrl, ossAccount);
                entity.setFilePath(OSSUtils.upload(ossObject.getKey(), ossObject.getObjectContent(), ossAccount));
                entity.setResourceKey(marketProcessor.getId());
                entity.setId(null);
                privatePackageRepository.save(entity);

                OSSObject object = OSSUtils.downloadByUrl(fileUrl, ossAccount);
                File file = DalaranFileUtils.createFile(object.getKey());
                FileUtils.copyToFile(object.getObjectContent(), file);
                marketResourceLoader.loadProcessor(file);
                break;
            case FLOW_TEMPLATE:
            case SUB_FLOW_TEMPLATE:
                FlowTemplate templateData = JSON.parseObject((String) privateRepositoryDTO.getData(), FlowTemplate.class);
                loadRelationResource(templateData.getData());
                break;
        }
    }

    private void loadRelationResource(TemplateData templateData) throws Exception {
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
                    BeanUtils.copyProperties(privateFunctionEntity, entityEntry.getKey());
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
                    OSSObject ossObject = OSSUtils.downloadByUrl(fileUrl, ossAccount);
                    entity.setFilePath(OSSUtils.upload(ossObject.getKey(), ossObject.getObjectContent(), ossAccount));

                    entity.setId(null);
                    privatePackageRepository.save(entity);
                    loadProcessor(entityEntry.getValue().getFilePath());
                }
            }
        }
    }

    private void loadProcessor(String fileUrl) throws Exception {
        OSSObject ossObject = OSSUtils.downloadByUrl(fileUrl, ossAccount);
        File file = DalaranFileUtils.createFile(ossObject.getKey());
        FileUtils.copyToFile(ossObject.getObjectContent(), file);
        marketResourceLoader.loadProcessor(file);
    }
}
