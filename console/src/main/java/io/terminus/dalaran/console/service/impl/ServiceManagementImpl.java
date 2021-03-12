package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.soap.trigger.model.SoapApiInfo;
import io.terminus.dalaran.config.ServiceInfo;
import io.terminus.dalaran.console.ServiceDetail;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.ServiceEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.repository.ServiceRepository;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.ServiceManagement;
import io.terminus.dalaran.console.service.jpa.model.QueryServiceInfo;
import io.terminus.dalaran.console.util.ResourceKeyUtils;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.context.DalaranServiceContext;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelTargetType;
import io.terminus.dalaran.model.ServiceOperationModel;
import io.terminus.dalaran.model.component.ServiceOperation;
import io.terminus.dalaran.model.dto.ModelDTO;
import io.terminus.dalaran.model.dto.ServiceDTO;
import io.terminus.dalaran.model.dto.basic.BasicServiceInfo;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceManagementImpl implements ServiceManagement {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private DalaranServiceContext serviceContext;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ModelManagementService modelManagementService;

    @Autowired
    private TriggerFlowRepository triggerFlowRepository;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Override
    @Transactional
    public String create(ServiceDTO serviceDTO) {
        ServiceDetail serviceDetail = toEntity(serviceDTO);
        ServiceEntity entity = serviceDetail.getEntity();
        setCreatedBy(entity);
        String serviceId = serviceRepository.save(entity).getResourceKey();
        serviceDetail.setServiceId(serviceId);
        createModels(serviceDetail);
        return serviceId;
    }

    @Override
    public ServiceDTO update(ServiceDTO serviceDTO) {
        ServiceDetail serviceDetail = toEntity(serviceDTO);
        ServiceEntity entity = serviceDetail.getEntity();
        serviceDetail.setServiceId(serviceDTO.getId());
        setUpdatedBy(entity);
        serviceRepository.save(entity);
        createModels(serviceDetail);
        return toDTO(entity);
    }

    @Override
    public void delete(String serviceId) {
        ServiceEntity entity = serviceRepository.findByResourceKey(serviceId);
        entity.setExist(false);
        serviceRepository.save(entity);
    }

    @Override
    public ServiceDTO detail(String serviceId) {
        return toDTO(serviceRepository.findByResourceKey(serviceId));
    }

    @Override
    public List<ServiceDTO> list() {
        return serviceRepository.findByIsExistTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ServiceOperation> listOperation(String serviceId) {
        ServiceEntity entity = serviceRepository.findByResourceKey(serviceId);
        ServiceInfo serviceInfo = serviceContext.getServiceInfo(entity.getType());
        Object serviceConfig = JSON.parseObject(entity.getServiceConfig(), serviceInfo.getServiceConfigType());
        return serviceContext.getService(entity.getType()).operations(serviceConfig);
    }

    @Override
    public List<BasicServiceInfo> listBasicInfoByModuleId(String moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryServiceInfo> criteriaQuery = builder.createQuery(QueryServiceInfo.class);
        Root<ServiceEntity> root = criteriaQuery.from(ServiceEntity.class);
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("type"), root.get("name"), root.get("isExist"))
                .where(builder.equal(root.get("moduleId"), moduleId), builder.equal(root.get("isExist"),true));
        List<QueryServiceInfo> services = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicServiceInfo> basicServices = new ArrayList<>();
        services.forEach(service -> {
            BasicServiceInfo basicService = new BasicServiceInfo();
            try {
                BeanUtils.copyProperties(basicService, service);
                basicService.setId(service.getResourceKey());
                basicServices.add(basicService);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicServices;
    }

    private ServiceDTO toDTO(ServiceEntity entity) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(entity.getResourceKey());
        dto.setModuleId(entity.getModuleId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setImportConfig(JSON.parseObject(entity.getImportConfig(), Map.class));
        dto.setServiceConfig(JSON.parseObject(entity.getServiceConfig(), Map.class));
        return dto;
    }

    private ServiceDetail toEntity(ServiceDTO dto) {
        ServiceDetail serviceDetail = new ServiceDetail();
        String type = dto.getType();
        String moduleId = dto.getModuleId();
        serviceDetail.setModuleId(moduleId);
        ServiceEntity entity;
        String resourceKey = dto.getId();
        if (StringUtils.isBlank(resourceKey)) {
            entity = new ServiceEntity();
            resourceKey = ResourceKeyUtils.generateKey();
        } else {
            entity = serviceRepository.findByResourceKey(resourceKey);
        }
        entity.setResourceKey(resourceKey);
        entity.setModuleId(moduleId);
        entity.setName(dto.getName());
        entity.setType(type);
        entity.setDescription(dto.getDescription());
        String importConfigJson = JSON.toJSONString(dto.getImportConfig());
        entity.setImportConfig(importConfigJson);

        Class importConfigType = serviceContext.getServiceInfo(type).getImportConfigType();
        Object importConfig = JSON.parseObject(importConfigJson, importConfigType);
        DalaranService dalaranService = serviceContext.getService(dto.getType());
        Object serviceConfig = dalaranService.importConfig(importConfig);
        entity.setServiceConfig(JSON.toJSONString(serviceConfig));
        entity.setExist(true);
        serviceDetail.setEntity(entity);
        serviceDetail.setDalaranService(dalaranService);
        serviceDetail.setImportConfig(importConfig);
        serviceDetail.setServiceConfig(serviceConfig);
        return serviceDetail;
    }

    private void createModels(ServiceDetail serviceDetail) {
        Map<String, String> models = new HashMap<>();
        ServiceEntity entity = serviceDetail.getEntity();
        DalaranService dalaranService = serviceDetail.getDalaranService();
        Object serviceConfig = serviceDetail.getServiceConfig();
        Object importConfig = serviceDetail.getImportConfig();
        String moduleId = serviceDetail.getModuleId();
        String serviceId = serviceDetail.getServiceId();
        List<ServiceOperation> operations = dalaranService.operations(serviceConfig);
        operations.forEach(operation -> {
            ServiceOperationModel operationModel = dalaranService.buildOperationModel(importConfig, operation);
            String inModelId = buildModel(operationModel.getInModel(), operationModel.getInputName(), moduleId, serviceId, models);
            String outModelId = buildModel(operationModel.getOutModel(), operationModel.getOutputName(), moduleId, serviceId, models);
            operation.setInModelId(String.valueOf(inModelId));
            operation.setOutModelId(String.valueOf(outModelId));
        });
        entity.setServiceConfig(JSON.toJSONString(serviceConfig));
        serviceRepository.save(entity);
    }

    private String buildModel(MessageModel messageModel, String modelName, String moduleId, String serviceId, Map<String, String> models) {
        if (models.containsKey(modelName)) {
            return models.get(modelName);
        }
        ModelDTO model = new ModelDTO();
        model.setName(modelName);
        model.setModuleId(moduleId);
        model.setTargetId(serviceId);
        model.setTargetType(ModelTargetType.Service);
        model.setModelType(messageModel.getModelType());
        model.setModelSchema(JSON.parseObject(JSON.toJSONString(messageModel.getModelSchema()), Map.class));

        ModelEntity entity = modelManagementService.getByNameAndServiceId(modelName, serviceId);
        if (entity == null) {
            String id = modelManagementService.createModel(model);
            models.put(modelName, id);
            return id;
        } else {
            String id = entity.getResourceKey();
            model.setId(id);
            modelManagementService.updateModel(model);
            return id;
        }
    }

    private List<SoapApiInfo> getExportSoapListeners() {
        List<TriggerFlowEntity> soapFlowList = triggerFlowRepository.findByStatusNotAndTriggerTypeAndIsExistTrue(FlowStatus.Error, "soap-listener");
        return soapFlowList.stream().map(flowEntity -> {
            TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(flowEntity);
            return new SoapApiInfo(triggerFlow);
        }).collect(Collectors.toList());
    }

    private void setCreatedBy(ServiceEntity serviceEntity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            serviceEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private void setUpdatedBy(ServiceEntity serviceEntity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            serviceEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
    }
}
