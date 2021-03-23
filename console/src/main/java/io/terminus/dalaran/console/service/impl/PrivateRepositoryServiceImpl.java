package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.model.OSSObject;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.component.utils.DalaranFileUtils;
import io.terminus.dalaran.component.utils.OSSUtils;
import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.model.FlowTemplate;
import io.terminus.dalaran.console.model.TemplateData;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.PrivateRepositoryService;
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
import io.terminus.dalaran.model.market.MarketProcessor;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Collection<MarketResourceVersionDTO> listPrivateResource(PrivateRepositoryQuery query) {
        return null;
    }

    @Override
    public PrivateRepositoryDTO getResourceDetail(String id, String version) {
        PrivateRepositoryEntity entity = privateRepository.findByResourceKeyAndVersion(id, version);
        PrivateRepositoryDTO privateRepository = new PrivateRepositoryDTO();
        try {
            BeanUtils.copyProperties(privateRepository, entity);
            switch (entity.getType()) {
                case DalaranConstants.PROCESSOR:
                    privateRepository.setData(JSON.parseObject(entity.getData(), MarketProcessor.class));
                    break;
                case DalaranConstants.FLOW_TEMPLATE:
                case DalaranConstants.SUB_FLOW_TEMPLATE:
                    privateRepository.setData(JSON.parseObject(entity.getData(), FlowTemplate.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privateRepository;
    }

    @Override
    public BasicResponse publish(BasicResourceDTO basicResource) {
        PrivateRepositoryDTO privateResource = getResourceDetail(basicResource.getId(), basicResource.getVersion());
        HttpEntity<PrivateRepositoryDTO> request = new HttpEntity<>(privateResource);
        return restTemplate.postForObject(propertyService.getMarketHost() + propertyService.getMarketUpload(), request, BasicResponse.class);
    }

    @Override
    public BasicResponse install(PrivateRepositoryDTO privateRepositoryDTO) {
        try {
            resourceInstall(privateRepositoryDTO);
            PrivateRepositoryEntity entity = toEntity(privateRepositoryDTO);
            entity.setResourceKey(privateRepositoryDTO.getId());
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
            entity.setId(null);
            privateRepository.save(entity);
            return new BasicResponse(true, entity.getResourceKey());
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
                FlowTemplate flowTemplate = JSON.parseObject((String) privateRepositoryDTO.getData(), FlowTemplate.class);
                loadRelationResource(flowTemplate.getData());
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
        Map<String, MarketProcessor> resourceFile = templateData.getRelationPackage();
        // todo load processor
        if (MapUtils.isNotEmpty(resourceFile)) {
            for (Map.Entry<String, MarketProcessor> entityEntry: resourceFile.entrySet()) {
                PrivatePackageEntity privatePackageEntity = privatePackageRepository.findByResourceKeyAndVersion(entityEntry.getValue().getId(), entityEntry.getValue().getVersion());
                if (privatePackageEntity == null) {
                    PrivatePackageEntity entity = new PrivatePackageEntity();
                    BeanUtils.copyProperties(entity, entityEntry.getValue());

                    String fileUrl = entityEntry.getValue().getData().getFilePath();
                    OSSObject ossObject = OSSUtils.downloadByUrl(fileUrl, ossAccount);
                    entity.setFilePath(OSSUtils.upload(ossObject.getKey(), ossObject.getObjectContent(), ossAccount));

                    entity.setId(null);
                    privatePackageRepository.save(entity);
                    loadProcessor(entityEntry.getValue().getData().getFilePath());
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
