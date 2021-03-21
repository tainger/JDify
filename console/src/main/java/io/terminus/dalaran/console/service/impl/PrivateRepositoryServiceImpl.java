package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.entity.*;
import io.terminus.dalaran.console.model.FlowTemplate;
import io.terminus.dalaran.console.model.TemplateData;
import io.terminus.dalaran.console.repository.*;
import io.terminus.dalaran.console.service.PrivateRepositoryService;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.property.PropertyService;
import io.terminus.dalaran.core.resource.repository.PrivateRepositoryRepository;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.market.model.MarketResourceVersionDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import io.terminus.dalaran.model.market.MarketProcessor;
import io.terminus.dalaran.model.market.ResourceFile;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
            FlowTemplate flowTemplate = JSON.parseObject(entity.getData(), FlowTemplate.class);
            privateRepository.setData(flowTemplate);
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
                // todo load processor
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
        Map<String, ResourceFile> resourceFile = templateData.getRelationPackage();
        // todo load processor
    }

}
